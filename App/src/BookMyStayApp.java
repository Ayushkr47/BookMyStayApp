import java.util.HashMap;

/**
 * BookMyStayApp
 * Demonstrates read-only search over centralized inventory
 *
 * @author Roger
 * @version 3.0
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
        availabilityMap.put("Suite Room", 0); // unavailable example
    }

    // Read-only access
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }
}

// -------------------- SEARCH SERVICE (READ-ONLY) --------------------
class SearchService {

    public void searchAvailableRooms(RoomInventory inventory, Room[] rooms) {

        System.out.println("=== Available Rooms (BookMyStay) ===");

        for (Room room : rooms) {

            int available = inventory.getAvailability(room.getType());

            // Defensive check: show only available rooms
            if (available > 0) {
                room.displayDetails();
                System.out.println("Available: " + available + "\n");
            }
        }
    }
}

// -------------------- APPLICATION ENTRY --------------------
public class BookMyStayApp {

    public static void main(String[] args) {

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Create room objects (domain)
        Room[] rooms = {
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        };

        // Search service (read-only)
        SearchService searchService = new SearchService();

        // Guest initiates search
        searchService.searchAvailableRooms(inventory, rooms);

        System.out.println("Search completed. System state unchanged.");
    }
}