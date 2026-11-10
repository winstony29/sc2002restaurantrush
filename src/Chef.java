


/**
 * Chef is responsible for preparing customer orders.
 * Each successful cooking action advances an order's
 * preparation progress.
 */
public class Chef extends Staff {
    // argument changed from stage 1 to include turn 
    public void cook(Order o, int turn) {
        requireAvailable();

        // Check that the order exists
        if (o == null) {
            throw new IllegalMoveException(
                "No order provided."
            );
        }

        // Only orders taken by the Waiter can be cooked
        if (o.getStatus() != OrderStatus.TAKEN) {
            throw new IllegalMoveException(
                "Order cannot be cooked in its current state."
            );
        }

        // Advance preparation by one unit
        o.advancePrep(turn);

        // Mark Chef as having acted successfully
        markActed();
    }
}
