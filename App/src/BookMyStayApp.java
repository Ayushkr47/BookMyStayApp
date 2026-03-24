import java.util.HashMap;

/**
 * BookMyStayApp
 * Demonstrates centralized room inventory management using HashMap
 *
 * @author Roger
 * @version 2.0
 */

class RoomInventory {

    private HashMap<String, Integer> availabilityMap;

    // Constructor initializes inventory
    public RoomInventory() {
        availabilityMap = new HashMap<>();

        availabilityMap.put("Single Room", 5);
        availabilityMap.put("Double Room", 3);
        availabilityMap.put("Suite Room", 2);
    }

    // Get availability
    public int getAvailability(String roomType) {
        return availabilityMap.getOrDefault(roomType, 0);
    }

    // Update availability
    public void updateAvailability(String roomType, int newCount) {
        if (availabilityMap.containsKey(roomType)) {
            availabilityMap.put(roomType, newCount);
        } else {
            System.out.println("Room type not found.");
        }
    }

    // Display inventory
    public void displayInventory() {
        System.out.println("=== BookMyStay Room Inventory ===");
        for (String roomType : availabilityMap.keySet()) {
            System.out.println(roomType + " → Available: " + availabilityMap.get(roomType));
        }
    }
}

public class BookMyStayApp {

    public static void main(String[] args) {

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();

        // Display initial inventory
        inventory.displayInventory();

        System.out.println("\nUpdating availability...\n");

        // Update values
        inventory.updateAvailability("Single Room", 4);
        inventory.updateAvailability("Suite Room", 1);

        // Display updated inventory
        inventory.displayInventory();

        System.out.println("\nBookMyStayApp terminated.");
    }
}