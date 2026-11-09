/**
 * One customer's order for one MenuItem.
 * UML: Order 0..* ── 1 MenuItem, Order 0..1 ── 1 Customer
 *
 * Valid transitions (anything else should throw IllegalMoveException):
 *   TAKEN --advancePrep (last unit)--> READY --markServed--> SERVED --markPaid--> PAID
 *   TAKEN or READY --cancel--> CANCELLED
 */
public class Order {

    private final MenuItem item;
    private final Customer customer;
    private int unitsDone;
    private OrderStatus status;

    public Order(MenuItem item, Customer customer) {
        this.item = item;
        this.customer = customer;
        this.unitsDone = 0;
        this.status = OrderStatus.TAKEN;
    }

    /**
     * Chef does one preparation unit.
     * When unitsDone reaches item.getPrepUnits(), status becomes READY.
     * @throws IllegalMoveException if the order is not TAKEN (already ready, served, cancelled...)
     */
    public void advancePrep() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isReady() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * @throws IllegalMoveException if the order is not READY
     * Note: the "finished this turn can't be served this turn" rule is handled
     * by turn order (Waiter acts before Chef), not here.
     */
    public void markServed() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /** @throws IllegalMoveException if the order is not SERVED */
    public void markPaid() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /** Called when the customer abandons. Only an unfinished order (TAKEN/READY) can be cancelled. */
    public void cancel() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * The amount the customer pays, in cents.
     * Stage 1: just the item's price.
     * (Stage 2 VIP discount / Stage 3 Happy Hour will change this — don't add them yet.)
     */
    public int billCents() {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public MenuItem getItem() {
        return item;
    }

    public Customer getCustomer() {
        return customer;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public int getUnitsDone() {
        return unitsDone;
    }
}
