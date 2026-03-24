import java.io.*;
import java.util.*;

/**
 * BookMyStayApp
 * Demonstrates persistence & recovery using serialization
 *
 * @author Roger
 * @version 11.0
 */

// -------------------- INVENTORY --------------------
class RoomInventory implements Serializable {
    private static final long serialVersionUID = 1L;

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

    public void display() {
        System.out.println("\n=== Inventory ===");
        for (String t : availabilityMap.keySet()) {
            System.out.println(t + " → " + availabilityMap.get(t));
        }
    }

    public HashMap<String, Integer> getMap() {
        return availabilityMap;
    }

    public void setMap(HashMap<String, Integer> map) {
        this.availabilityMap = map;
    }
}

// -------------------- RESERVATION --------------------
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String guestName;
    private String roomType;
    private String reservationId;

    public Reservation(String guestName, String roomType, String reservationId) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.reservationId = reservationId;
    }

    public String getRoomType() { return roomType; }

    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType;
    }
}

// -------------------- BOOKING HISTORY --------------------
class BookingHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }

    public void display() {
        System.out.println("\n=== Booking History ===");
        for (Reservation r : history) {
            System.out.println(r);
        }
    }
}

// -------------------- SYSTEM STATE (SNAPSHOT) --------------------
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    RoomInventory inventory;
    BookingHistory history;

    public SystemState(RoomInventory inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }
}

// -------------------- PERSISTENCE SERVICE --------------------
class PersistenceService {

    private static final String FILE_NAME = "bookmystay.dat";

    // Save state to file
    public void save(SystemState state) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(state);
            System.out.println("\nState saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    // Load state from file
    public SystemState load() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            System.out.println("State restored from file.");
            return (SystemState) in.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("No saved state found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("Corrupted data. Starting with clean state.");
        }
        return null;
    }
}

// -------------------- MAIN --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        PersistenceService persistence = new PersistenceService();

        // Try to restore previous state
        SystemState state = persistence.load();

        RoomInventory inventory;
        BookingHistory history;

        if (state != null) {
            inventory = state.inventory;
            history = state.history;
        } else {
            inventory = new RoomInventory();
            history = new BookingHistory();
        }

        // Simulate new bookings
        System.out.println("\n=== Processing New Bookings ===");

        if (inventory.getAvailability("Single Room") > 0) {
            inventory.decrement("Single Room");
            history.add(new Reservation("Alice", "Single Room", "RES-101"));
        }

        if (inventory.getAvailability("Double Room") > 0) {
            inventory.decrement("Double Room");
            history.add(new Reservation("Bob", "Double Room", "RES-102"));
        }

        // Display current state
        history.display();
        inventory.display();

        // Save state before shutdown
        persistence.save(new SystemState(inventory, history));

        System.out.println("\nSystem shutdown complete. Restart to verify recovery.");
    }
}