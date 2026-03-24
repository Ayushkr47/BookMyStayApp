import java.util.*;

/**
 * BookMyStayApp
 * Demonstrates validation + custom exception handling (fail-fast design)
 *
 * @author Roger
 * @version 8.0
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

        // Guard: prevent negative inventory
        if (current <= 0) {
            throw new InvalidBookingException("No available rooms for: " + type);
        }

        availabilityMap.put(type, current - 1);
    }

    public boolean isValidRoomType(String type) {
        return availabilityMap.containsKey(type);
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

    public String toString() {
        return guestName + " → " + roomType + " | ID: " + reservationId;
    }
}

// -------------------- QUEUE --------------------
class BookingQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void add(Reservation r) { queue.offer(r); }
    public Reservation next() { return queue.poll(); }
    public boolean isEmpty() { return queue.isEmpty(); }
}

// -------------------- VALIDATOR --------------------
class InvalidBookingValidator {

    public void validate(Reservation r, RoomInventory inventory) throws InvalidBookingException {

        // Validate guest name
        if (r.getGuestName() == null || r.getGuestName().trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type
        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + r.getRoomType());
        }

        // Validate availability
        if (inventory.getAvailability(r.getRoomType()) <= 0) {
            throw new InvalidBookingException("Room not available: " + r.getRoomType());
        }
    }
}

// -------------------- BOOKING SERVICE --------------------
class BookingService {

    private HashMap<String, Set<String>> allocatedRooms = new HashMap<>();
    private int counter = 1;
    private InvalidBookingValidator validator = new InvalidBookingValidator();

    public void processQueue(BookingQueue queue, RoomInventory inventory) {

        System.out.println("=== Booking Processing (With Validation) ===");

        while (!queue.isEmpty()) {

            Reservation r = queue.next();

            try {
                // Fail-fast validation
                validator.validate(r, inventory);

                String type = r.getRoomType();

                // Generate unique room ID
                String roomId = type.substring(0, 2).toUpperCase() + "-" + counter++;
                allocatedRooms.putIfAbsent(type, new HashSet<>());

                if (!allocatedRooms.get(type).contains(roomId)) {

                    allocatedRooms.get(type).add(roomId);

                    // Safe inventory update
                    inventory.decrement(type);

                    r.setReservationId("RES-" + (100 + counter));

                    System.out.println("Confirmed: " + r + " | Room ID: " + roomId);
                }

            } catch (InvalidBookingException e) {
                // Graceful failure handling
                System.out.println("Booking Failed for " + r.getGuestName() + ": " + e.getMessage());
            }
        }

        System.out.println("\nSystem remains stable after handling all requests.");
    }
}

// -------------------- MAIN --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingQueue queue = new BookingQueue();
        BookingService service = new BookingService();

        // Test cases (valid + invalid)
        queue.add(new Reservation("Alice", "Single Room"));
        queue.add(new Reservation("", "Double Room"));          // invalid name
        queue.add(new Reservation("Bob", "Deluxe Room"));       // invalid type
        queue.add(new Reservation("Charlie", "Suite Room"));
        queue.add(new Reservation("David", "Suite Room"));      // exceeds availability

        // Process bookings safely
        service.processQueue(queue, inventory);
    }
}