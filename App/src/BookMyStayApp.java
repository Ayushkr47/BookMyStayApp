import java.util.*;

/**
 * BookMyStayApp
 * Demonstrates safe booking confirmation with:
 * - FIFO queue processing
 * - Unique room ID allocation using Set
 * - Inventory synchronization using HashMap
 *
 * @author Roger
 * @version 5.0
 */

// -------------------- INVENTORY --------------------
class RoomInventory {

    private HashMap<String, Integer> availabilityMap;

    public RoomInventory() {
        availabilityMap = new HashMap<>();
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

    public void display() {
        System.out.println("\n=== Current Inventory ===");
        for (String type : availabilityMap.keySet()) {
            System.out.println(type + " → " + availabilityMap.get(type));
        }
    }
}

// -------------------- RESERVATION --------------------
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}

// -------------------- QUEUE --------------------
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void add(Reservation r) {
        queue.offer(r);
    }

    public Reservation next() {
        return queue.poll(); // FIFO
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// -------------------- BOOKING SERVICE --------------------
class BookingService {

    // Map room type → allocated room IDs
    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();

    // Counter for generating unique IDs
    private int counter = 1;

    public void processQueue(BookingQueue queue, RoomInventory inventory) {

        System.out.println("\n=== Processing Bookings ===");

        while (!queue.isEmpty()) {

            Reservation r = queue.next();
            String type = r.getRoomType();

            // Check availability
            if (inventory.getAvailability(type) > 0) {

                // Generate unique room ID
                String roomId = type.substring(0, 2).toUpperCase() + "-" + counter++;

                // Ensure set exists
                allocatedRooms.putIfAbsent(type, new HashSet<>());

                // Check uniqueness (Set prevents duplicates)
                if (!allocatedRooms.get(type).contains(roomId)) {

                    // Add to allocated set
                    allocatedRooms.get(type).add(roomId);

                    // Update inventory immediately
                    inventory.decrement(type);

                    // Confirm booking
                    System.out.println("Confirmed: " + r.getGuestName()
                            + " → " + type + " | Room ID: " + roomId);
                }

            } else {
                System.out.println("Failed: " + r.getGuestName()
                        + " → " + type + " (No availability)");
            }
        }
    }

    public void displayAllocations() {
        System.out.println("\n=== Allocated Rooms ===");
        for (String type : allocatedRooms.keySet()) {
            System.out.println(type + " → " + allocatedRooms.get(type));
        }
    }
}

// -------------------- MAIN --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        // Initialize components
        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingService service = new BookingService();

        // Add booking requests (FIFO)
        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Single Room"));
        queue.add(new Reservation("Charlie", "Single Room")); // should fail
        queue.add(new Reservation("David", "Double Room"));
        queue.add(new Reservation("Eve", "Suite Room"));

        // Process bookings
        service.processQueue(queue, inventory);

        // Show results
        service.displayAllocations();
        inventory.display();

        System.out.println("\nAll bookings processed safely.");
    }
}