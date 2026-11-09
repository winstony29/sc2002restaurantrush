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
        return customer == null;
    }

    /**
     * Seat a customer here.
     * @throws IllegalMoveException if the table is already occupied
     */
    public void seat(Customer c) {
         if (c == null) {
            throw new IllegalArgumentException("Cannot seat a null customer");
        }
        if (!isFree()) {
            throw new IllegalMoveException("Table " + tableId
                    + " is already taken by customer " + customer.getId());
        }
        this.customer = c;
    }

    /** Free the table (called when the customer pays or leaves). */
    public void clear() {
        this.customer = null;
    }

    public int getTableId() {
        return tableId;
    }

    public Customer getCustomer() {
        return customer;
    }

     @Override
    public String toString() {
        return "Table " + tableId + (isFree() ? " (free)" : " (customer " + customer.getId() + ")");
    }
}
