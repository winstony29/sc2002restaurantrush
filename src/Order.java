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
    private int unitsDone = 0;
    private OrderStatus status;
    private int readyTurn = -1; 

    public Order(MenuItem item, Customer customer) {
        if (item == null || customer == null) {
            throw new IllegalArgumentException("An order needs an item and a customer");
        }
        this.item = item;
        this.customer = customer;
        this.status = OrderStatus.TAKEN;
    }

    /**
     * Chef does one preparation unit.
     * When unitsDone reaches item.getPrepUnits(), status becomes READY.
     * @throws IllegalMoveException if the order is not TAKEN (already ready, served, cancelled...)
     */
    public void advancePrep(int turn) {
        if (status != OrderStatus.TAKEN) {
            throw new IllegalMoveException(describe() + " does not need cooking (it is " + status + ")");
        }
        unitsDone++;
        if (unitsDone >= item.getPrepUnits()) {
            status = OrderStatus.READY;
            readyTurn = turn;
        }
    }

    public void advancePrep() {
        advancePrep(-1);
    }

    public boolean isReady() {
        return status == OrderStatus.READY;
    }

    /**
     * @throws IllegalMoveException if the order is not READY
     * Note: the "finished this turn can't be served this turn" rule is handled
     * by turn order (Waiter acts before Chef), not here.
     */
    public void markServed() {
        if (status != OrderStatus.READY) {
            throw new IllegalMoveException(describe() + " is not ready to serve (it is " + status + ")");
        }
        status = OrderStatus.SERVED;
    }

    /** @throws IllegalMoveException if the order is not SERVED */
    public void markPaid() {
        if (status != OrderStatus.SERVED) {
            throw new IllegalMoveException(describe() + " cannot be paid (it is " + status + ")");
        }
        status = OrderStatus.PAID;
    }

    /** Called when the customer abandons. Only an unfinished order (TAKEN/READY) can be cancelled. */
    public void cancel() {
        if (status != OrderStatus.TAKEN && status != OrderStatus.READY) {
            throw new IllegalMoveException(describe() + " cannot be cancelled (it is " + status + ")");
        }
        status = OrderStatus.CANCELLED;
    }

    /**
     * The amount the customer pays, in cents.
     * Stage 1: just the item's price.
     * (Stage 2 VIP discount / Stage 3 Happy Hour will change this — don't add them yet.)
     */

    public int basePriceCents() {
        return item.getPriceCents();
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

    public int getReadyTurn() {
        return readyTurn;
    }

    private String describe() {
        return "Order for customer " + customer.getId() + " (" + item.getName() + ")";
    }

    @Override
    public String toString() {
        return describe() + " - " + status
                + (status == OrderStatus.TAKEN ? " " + unitsDone + "/" + item.getPrepUnits() : "");
    }
}
