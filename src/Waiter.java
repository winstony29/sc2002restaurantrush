package restrush.sc2002restaurantrush.src;

public class Waiter extends Staff {

    public void seat(Customer c, Table t) {
        requireAvailable();

        // Check customer is waiting
        // Check table is available
        // Seat customer at table

        markActed();
    }

    public Order takeOrder(Customer c, Menu m) {
        requireAvailable();

        // Check customer is seated
        // Check customer has no active order
        // Create order using menu
        // Associate order with customer

        markActed();

        // Return created order
        return null; // Placeholder
    }

    public void serve(Order o, int turn) {
        requireAvailable();

        // Check order status is READY
        // Update order status to SERVED
        // Update customer's status

        markActed();
    }
}
