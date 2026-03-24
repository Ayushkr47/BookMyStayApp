import java.util.*;

/**
 * BookMyStayApp
 * Demonstrates extensible add-on services using Map + List
 * without modifying core booking or inventory logic.
 *
 * @author Roger
 * @version 6.0
 */

// -------------------- INVENTORY --------------------
class RoomInventory {
    private HashMap<String, Integer> availabilityMap = new HashMap<>();

    public RoomInventory() {
        availabilityMap.put("Single Room", 2);
        availabilityMap.put("Double Room", 1);
        availabilityMap.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, 0);
    }

    public void decrement(String type) {
        availabilityMap.put(type, getAvailability(type) - 1);
    }
}

// -------------------- RESERVATION --------------------
class Reservation {
    private String guestName;
    private String roomType;
    private String reservationId;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void setReservationId(String id) { this.reservationId = id; }
    public String getReservationId() { return reservationId; }
}

// -------------------- QUEUE --------------------
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void add(Reservation r) { queue.offer(r); }
    public Reservation next() { return queue.poll(); }
    public boolean isEmpty() { return queue.isEmpty(); }
}

// -------------------- BOOKING SERVICE --------------------
class BookingService {

    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();
    private int counter = 1;

    public void processQueue(BookingQueue queue, RoomInventory inventory) {

        System.out.println("=== Booking Confirmation ===");

        while (!queue.isEmpty()) {

            Reservation r = queue.next();
            String type = r.getRoomType();

            if (inventory.getAvailability(type) > 0) {

                String roomId = type.substring(0, 2).toUpperCase() + "-" + counter++;
                allocatedRooms.putIfAbsent(type, new HashSet<>());

                if (!allocatedRooms.get(type).contains(roomId)) {

                    allocatedRooms.get(type).add(roomId);
                    inventory.decrement(type);

                    // Assign reservation ID
                    String reservationId = "RES-" + (100 + counter);
                    r.setReservationId(reservationId);

                    System.out.println("Confirmed: " + r.getGuestName()
                            + " → " + type + " | Room ID: " + roomId
                            + " | Reservation ID: " + reservationId);
                }

            } else {
                System.out.println("Failed: " + r.getGuestName()
                        + " → " + type + " (No availability)");
            }
        }
    }
}

// -------------------- ADD-ON SERVICE --------------------
class AddOnService {
    private String name;
    private double cost;

    public AddOnService(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public double getCost() { return cost; }

    public String toString() {
        return name + " (₹" + cost + ")";
    }
}

// -------------------- ADD-ON SERVICE MANAGER --------------------
class AddOnServiceManager {

    // Map: Reservation ID → List of Services
    private HashMap<String, List<AddOnService>> serviceMap = new HashMap<>();

    // Add service to reservation
    public void addService(String reservationId, AddOnService service) {
        serviceMap.putIfAbsent(reservationId, new ArrayList<>());
        serviceMap.get(reservationId).add(service);
    }

    // Calculate total add-on cost
    public double calculateTotal(String reservationId) {
        double total = 0;

        List<AddOnService> services = serviceMap.get(reservationId);
        if (services != null) {
            for (AddOnService s : services) {
                total += s.getCost();
            }
        }
        return total;
    }

    // Display services
    public void displayServices(String reservationId) {
        System.out.println("\nServices for " + reservationId + ":");

        List<AddOnService> services = serviceMap.get(reservationId);
        if (services == null || services.isEmpty()) {
            System.out.println("No add-on services selected.");
            return;
        }

        for (AddOnService s : services) {
            System.out.println("- " + s);
        }

        System.out.println("Total Add-On Cost: ₹" + calculateTotal(reservationId));
    }
}

// -------------------- MAIN --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingService bookingService = new BookingService();
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Create reservations
        Reservation r1 = new Reservation("Alice", "Single Room");
        Reservation r2 = new Reservation("Bob", "Double Room");

        queue.add(r1);
        queue.add(r2);

        // Process bookings (core logic)
        bookingService.processQueue(queue, inventory);

        // Add optional services (no impact on booking/inventory)
        serviceManager.addService(r1.getReservationId(), new AddOnService("Breakfast", 200));
        serviceManager.addService(r1.getReservationId(), new AddOnService("Airport Pickup", 500));

        serviceManager.addService(r2.getReservationId(), new AddOnService("Extra Bed", 300));

        // Display add-ons
        serviceManager.displayServices(r1.getReservationId());
        serviceManager.displayServices(r2.getReservationId());

        System.out.println("\nAdd-ons handled independently. Core system unchanged.");
    }
}