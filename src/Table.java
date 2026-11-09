/**
 * A table in the restaurant. Holds at most one customer.
 * UML: Table 0..1 ── 0..1 Customer
 *
 * Rule from the brief: a table stays occupied until its customer pays or leaves.
 */
public class Table {

    private final int tableId;
    private Customer customer;   // null when the table is free

    public Table(int tableId) {
        this.tableId = tableId;
        this.customer = null;
    }

    public boolean isFree() {
        // TODO: a table is free when no customer is sitting at it
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Seat a customer here.
     * @throws IllegalMoveException if the table is already occupied
     */
    public void seat(Customer c) {
        // TODO: 1. if not free -> throw new IllegalMoveException("Table " + tableId + " is occupied")
        //       2. store the customer
        // Think: should Table also call c.seatAt(this), or should Waiter do both?
        //        Pick ONE place so the link isn't set twice. Agree with whoever writes Waiter/Customer.
        throw new UnsupportedOperationException("TODO");
    }

    /** Free the table (called when the customer pays or leaves). */
    public void clear() {
        // TODO: remove the customer reference
        throw new UnsupportedOperationException("TODO");
    }

    public int getTableId() {
        return tableId;
    }

    public Customer getCustomer() {
        return customer;
    }
}
