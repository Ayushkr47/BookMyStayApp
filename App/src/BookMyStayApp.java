import java.util.*;

/**
 * BookMyStayApp
 * Demonstrates safe cancellation with rollback using Stack (LIFO)
 *
 * @author Roger
 * @version 9.0
 */

// -------------------- CUSTOM EXCEPTION --------------------
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// -------------------- INVENTORY --------------------
class RoomInventory {
    private HashMap<String, Integer> availabilityMap = new HashMap<>();

    public RoomInventory() {
        availabilityMap.put("Single Room", 2);
        availabilityMap.put("Double Room", 1);
        availabilityMap.put("Suite Room", 1);
    }

    public int getAvailability(String type) {
        return availabilityMap.getOrDefault(type, -1);
    }

    public void decrement(String type) throws InvalidBookingException {
        int current = getAvailability(type);
        if (current <= 0) {
            throw new InvalidBookingException("No availability for " + type);
        }
        availabilityMap.put(type, current - 1);
    }

    public void increment(String type) {
        availabilityMap.put(type, getAvailability(type) + 1);
    }

    public boolean isValidRoomType(String type) {
        return availabilityMap.containsKey(type);
    }

    public void display() {
        System.out.println("\n=== Inventory ===");
        for (String t : availabilityMap.keySet()) {
            System.out.println(t + " → " + availabilityMap.get(t));
        }
    }
}

// -------------------- RESERVATION --------------------
class Reservation {
    private String guestName;
    private String roomType;
    private String reservationId;
    private boolean active = true;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public String getReservationId() { return reservationId; }
    public boolean isActive() { return active; }

    public void setReservationId(String id) { this.reservationId = id; }
    public void cancel() { this.active = false; }

    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType + " | " + (active ? "ACTIVE" : "CANCELLED");
    }
}

// -------------------- BOOKING HISTORY --------------------
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) { history.add(r); }

    public Reservation findById(String id) {
        for (Reservation r : history) {
            if (r.getReservationId().equals(id)) return r;
        }
        return null;
    }

    public void display() {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : history) {
            System.out.println(r);
        }
    }
}

// -------------------- BOOKING SERVICE --------------------
class BookingService {
    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();
    private int counter = 1;

    public void book(Reservation r, RoomInventory inventory, BookingHistory history)
            throws InvalidBookingException {

        String type = r.getRoomType();

        if (!inventory.isValidRoomType(type)) {
            throw new InvalidBookingException("Invalid room type");
        }

        inventory.decrement(type);

        String roomId = type.substring(0, 2).toUpperCase() + "-" + counter++;
        allocatedRooms.putIfAbsent(type, new HashSet<>());
        allocatedRooms.get(type).add(roomId);

        r.setReservationId("RES-" + (100 + counter));
        history.add(r);

        System.out.println("Booked: " + r + " | Room ID: " + roomId);
    }
}

// -------------------- CANCELLATION SERVICE --------------------
class CancellationService {

    // Stack for rollback (LIFO)
    private Stack<String> rollbackStack = new Stack<>();

    public void cancel(String reservationId, BookingHistory history, RoomInventory inventory)
            throws InvalidBookingException {

        Reservation r = history.findById(reservationId);

        // Validation
        if (r == null) {
            throw new InvalidBookingException("Reservation not found: " + reservationId);
        }

        if (!r.isActive()) {
            throw new InvalidBookingException("Reservation already cancelled: " + reservationId);
        }

        // Record rollback (room type used as identifier here)
        rollbackStack.push(r.getRoomType());

        // Restore inventory
        inventory.increment(r.getRoomType());

        // Mark as cancelled
        r.cancel();

        System.out.println("Cancelled: " + reservationId + " | Room released");

        // Show rollback order
        System.out.println("Rollback Stack (latest first): " + rollbackStack);
    }
}

// -------------------- MAIN --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingHistory history = new BookingHistory();
        BookingService bookingService = new BookingService();
        CancellationService cancelService = new CancellationService();

        try {
            // Create bookings
            Reservation r1 = new Reservation("Alice", "Single Room");
            Reservation r2 = new Reservation("Bob", "Double Room");

            bookingService.book(r1, inventory, history);
            bookingService.book(r2, inventory, history);

            history.display();
            inventory.display();

            // Perform cancellation
            cancelService.cancel(r1.getReservationId(), history, inventory);

            // Invalid cancellation (already cancelled)
            cancelService.cancel(r1.getReservationId(), history, inventory);

        } catch (InvalidBookingException e) {
            System.out.println("Error: " + e.getMessage());
        }

        history.display();
        inventory.display();

        System.out.println("\nSystem state restored safely after cancellation.");
    }
}