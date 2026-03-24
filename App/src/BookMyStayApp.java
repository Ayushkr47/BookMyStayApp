import java.util.*;

/**
 * BookMyStayApp
 * Demonstrates thread-safe booking using synchronization
 * to prevent race conditions and double booking
 *
 * @author Roger
 * @version 10.0
 */

// -------------------- INVENTORY (SHARED RESOURCE) --------------------
class RoomInventory {

    private HashMap<String, Integer> availabilityMap = new HashMap<>();

    public RoomInventory() {
        availabilityMap.put("Single Room", 2);
        availabilityMap.put("Double Room", 1);
    }

    // synchronized critical section
    public synchronized boolean allocateRoom(String type) {

        int available = availabilityMap.getOrDefault(type, 0);

        if (available > 0) {
            availabilityMap.put(type, available - 1);
            return true;
        }
        return false;
    }

    public void display() {
        System.out.println("\nFinal Inventory:");
        for (String t : availabilityMap.keySet()) {
            System.out.println(t + " → " + availabilityMap.get(t));
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

// -------------------- SHARED QUEUE --------------------
class BookingQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    // synchronized enqueue
    public synchronized void add(Reservation r) {
        queue.offer(r);
    }

    // synchronized dequeue
    public synchronized Reservation getNext() {
        return queue.poll();
    }
}

// -------------------- BOOKING PROCESSOR (THREAD) --------------------
class BookingProcessor extends Thread {

    private BookingQueue queue;
    private RoomInventory inventory;

    public BookingProcessor(String name, BookingQueue queue, RoomInventory inventory) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    public void run() {

        while (true) {

            Reservation r;

            // safely get next request
            synchronized (queue) {
                r = queue.getNext();
            }

            if (r == null) break;

            // simulate processing delay (race condition scenario)
            try { Thread.sleep(100); } catch (InterruptedException e) {}

            // critical section: allocation
            boolean success = inventory.allocateRoom(r.getRoomType());

            if (success) {
                System.out.println(getName() + " CONFIRMED → "
                        + r.getGuestName() + " (" + r.getRoomType() + ")");
            } else {
                System.out.println(getName() + " FAILED → "
                        + r.getGuestName() + " (" + r.getRoomType() + ")");
            }
        }
    }
}

// -------------------- MAIN --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();

        // Simulate multiple guest requests
        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("Bob", "Single Room"));
        queue.add(new Reservation("Charlie", "Single Room")); // should fail
        queue.add(new Reservation("David", "Double Room"));
        queue.add(new Reservation("Eve", "Double Room"));     // should fail

        // Multiple threads (concurrent users)
        BookingProcessor t1 = new BookingProcessor("Thread-1", queue, inventory);
        BookingProcessor t2 = new BookingProcessor("Thread-2", queue, inventory);

        // Start threads
        t1.start();
        t2.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {}

        inventory.display();

        System.out.println("\nNo double booking occurred. State is consistent.");
    }
}