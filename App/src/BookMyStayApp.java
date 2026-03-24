import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * BookMyStayApp
 * Demonstrates booking request intake using a FIFO Queue
 * (no inventory mutation at this stage)
 *
 * @author Roger
 * @version 4.0
 */

// -------------------- DOMAIN MODEL --------------------
abstract class Room {
    protected String type;
    protected int beds;
    protected double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() { return type; }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

class SingleRoom extends Room {
    public SingleRoom() { super("Single Room", 1, 1000); }
}

class DoubleRoom extends Room {
    public DoubleRoom() { super("Double Room", 2, 2000); }
}

class SuiteRoom extends Room {
    public SuiteRoom() { super("Suite Room", 3, 5000); }
}

// -------------------- INVENTORY (STATE HOLDER) --------------------
class RoomInventory {

    private HashMap<String, Integer> availabilityMap;

    public RoomInventory() {
        availabilityMap = new HashMap<>();
        availabilityMap.put("Single Room", 5);
        availabilityMap.put("Double Room", 3);
        availabilityMap.put("Suite Room", 2);
    }

    // Read-only access only
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }
}

// -------------------- RESERVATION (REQUEST OBJECT) --------------------
class Reservation {
    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }

    public void displayRequest() {
        System.out.println("Guest: " + guestName + " requested " + roomType);
    }
}

// -------------------- BOOKING REQUEST QUEUE --------------------
class BookingQueue {

    private Queue<Reservation> queue;

    public BookingQueue() {
        queue = new LinkedList<>();
    }

    // Add request (enqueue)
    public void addRequest(Reservation reservation) {
        queue.offer(reservation);
        System.out.println("Request added to queue: " + reservation.getGuestName());
    }

    // Display all queued requests (no processing yet)
    public void displayQueue() {
        System.out.println("\n=== Booking Request Queue (FIFO Order) ===");
        for (Reservation r : queue) {
            r.displayRequest();
        }
    }
}

// -------------------- APPLICATION ENTRY --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        // Inventory exists but is NOT modified here
        RoomInventory inventory = new RoomInventory();

        // Booking request queue (FIFO)
        BookingQueue bookingQueue = new BookingQueue();

        // Guests submit booking requests
        bookingQueue.addRequest(new Reservation("Alice", "Single Room"));
        bookingQueue.addRequest(new Reservation("Bob", "Double Room"));
        bookingQueue.addRequest(new Reservation("Charlie", "Suite Room"));
        bookingQueue.addRequest(new Reservation("David", "Single Room"));

        // Show queued requests in arrival order
        bookingQueue.displayQueue();

        System.out.println("\nAll requests stored in arrival order.");
        System.out.println("No inventory changes performed.");
        System.out.println("Ready for allocation phase.");
    }
}