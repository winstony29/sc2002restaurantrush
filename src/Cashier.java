
/**
 * Cashier handles customer payments and frees
 * the occupied table after successful payment.
 */
public class Cashier extends Staff {

    public int collect(Customer c) {
        requireAvailable();

        // Validate customer
        if (c == null) {
            throw new IllegalMoveException(
                "No customer provided."
            );
        }

        if (c.getStatus() != CustomerStatus.READY_TO_PAY) {
            throw new IllegalMoveException(
                "Customer is not ready to pay."
            );
        }

        // Retrieve customer's order and table
        Order o = c.getOrder();
        Table t = c.getTable();

        if (o == null) {
            throw new IllegalMoveException(
                "Customer has no order."
            );
        }

        if (t == null) {
            throw new IllegalMoveException(
                "Customer is not assigned to a table."
            );
        }

        // Calculate bill (changed since stage 1)
        int bill = c.applyDiscount(o.basePriceCents());

        // Complete payment
        o.markPaid();
        c.markPaid();

        // Free the table
        t.clear();

        // Mark successful action
        markActed();

        return bill;
    }
}
