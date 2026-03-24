import java.util.*;

/**
 * BookMyStayApp
 * Demonstrates booking history tracking and reporting
 * using List (ordered storage) without external persistence
 *
 * @author Roger
 * @version 7.0
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

    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// -------------------- QUEUE --------------------
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void add(Reservation r) { queue.offer(r); }
    public Reservation next() { return queue.poll(); }
    public boolean isEmpty() { return queue.isEmpty(); }
}

// -------------------- BOOKING HISTORY --------------------
class BookingHistory {

    private List<Reservation> history = new ArrayList<>();

    // Store confirmed reservation
    public void add(Reservation r) {
        history.add(r);
    }

    // Retrieve all bookings
    public List<Reservation> getAll() {
        return history;
    }
}

// -------------------- BOOKING SERVICE --------------------
class BookingService {

    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();
    private int counter = 1;

    public void processQueue(BookingQueue queue, RoomInventory inventory, BookingHistory history) {

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

                    String reservationId = "RES-" + (100 + counter);
                    r.setReservationId(reservationId);

                    // Store in history
                    history.add(r);

                    System.out.println("Confirmed: " + r);
                }

            } else {
                System.out.println("Failed: " + r.getGuestName()
                        + " → " + type + " (No availability)");
            }
        }
    }
}

// -------------------- REPORT SERVICE --------------------
class BookingReportService {

    // Display all bookings
    public void displayAll(BookingHistory history) {
        System.out.println("\n=== Booking History ===");

        for (Reservation r : history.getAll()) {
            System.out.println(r);
        }
    }

    // Generate summary report
    public void generateSummary(BookingHistory history) {

        System.out.println("\n=== Booking Summary Report ===");

        HashMap<String, Integer> countMap = new HashMap<>();

        for (Reservation r : history.getAll()) {
            String type = r.getRoomType();
            countMap.put(type, countMap.getOrDefault(type, 0) + 1);
        }

        for (String type : countMap.keySet()) {
            System.out.println(type + " → " + countMap.get(type) + " bookings");
        }

        System.out.println("Total Bookings: " + history.getAll().size());
    }
}

// -------------------- MAIN --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService();
        BookingReportService reportService = new BookingReportService();

        // Add booking requests
        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Double Room"));
        queue.add(new Reservation("Charlie", "Suite Room"));
        queue.add(new Reservation("David", "Single Room"));

        // Process bookings
        bookingService.processQueue(queue, inventory, history);

        // Admin views reports (read-only)
        reportService.displayAll(history);
        reportService.generateSummary(history);

        System.out.println("\nHistory preserved. Reporting completed.");
    }
}