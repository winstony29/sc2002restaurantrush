/**
 * Represents a customer in the restaurant game.
 * Tracks their satisfaction, order, table and current status.
 */

public class Customer {
    // Customer ID is final as it should not change after creation.
    private final int id;
    private int satisfaction;
    private CustomerStatus status;
    private Table table;
    private Order order;
    private int servedTurn;
    private int readyToPayTurn;

    public Customer(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Customer ID must be positive.");
        }

        this.id = id;
        this.satisfaction = 100;
        this.status = CustomerStatus.WAITING;

        // A value of -1 means the turn has not been set yet.
        this.servedTurn = -1;
        this.readyToPayTurn = -1;
    }

    public int getId() {
        return id;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Table getTable() {
        return table;
    }

    public Order getOrder() {
        return order;
    }

    public int getServedTurn() {
        return servedTurn;
    }

    public int getReadyToPayTurn() {
        return readyToPayTurn;
    }

    public void seatAt(Table t) {
        requireStatus(CustomerStatus.WAITING, "Only a waiting customer can be seated.");
        if (t == null) {
            throw new IllegalMoveException("A table is required to seat the customer.");
        }

        table = t;
        status = CustomerStatus.SEATED;
    }

    public void placeOrder(Order o) {
        requireStatus(CustomerStatus.SEATED, "Only a seated customer can place an order.");
        if (o == null || o.getCustomer() != this) {
            throw new IllegalMoveException("The order must belong to this customer.");
        }

        order = o;
        status = CustomerStatus.ORDERED;
    }

    public void markServed(int turn) {
        requireStatus(CustomerStatus.ORDERED,
                "A customer must place an order before being served.");
        if (turn <= 0) {
            throw new IllegalMoveException("The served turn must be positive.");
        }
        if (order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalMoveException("The order must be marked as served first.");
        }

        servedTurn = turn;
        status = CustomerStatus.SERVED;
    }

    public void markReadyToPay(int turn) {
        requireStatus(CustomerStatus.SERVED,
                "A customer must be served before being ready to pay.");
        if (turn <= servedTurn) {
            throw new IllegalMoveException("The ready-to-pay turn must be after the served turn.");
        }

        readyToPayTurn = turn;
        status = CustomerStatus.READY_TO_PAY;
    }

    public void markPaid() {
        requireStatus(CustomerStatus.READY_TO_PAY,
                "A customer must be ready to pay before payment.");

        status = CustomerStatus.PAID;
    }

    public void leave() {
        if (!hasGivenUp()) {
            throw new IllegalMoveException(
                    "A customer can only leave if they have given up.");
        }

        status = CustomerStatus.LEFT;
    }

    // Customers lose satisfaction while waiting for a table or food.
    public void loseSatisfactionForWaiting() {
        if (status == CustomerStatus.WAITING
                || status == CustomerStatus.SEATED
                || status == CustomerStatus.ORDERED) {
            satisfaction = Math.max(0, satisfaction - 8);
        }
    }

    public boolean hasGivenUp() {
        return satisfaction == 0
                && (status == CustomerStatus.WAITING
                    || status == CustomerStatus.SEATED
                    || status == CustomerStatus.ORDERED);
    }

    // Use the customer ID to make menu selection consistent across runs.
    public MenuItem chooseItem(Menu menu) {
        if (menu == null || menu.getItems().isEmpty()) {
            throw new IllegalArgumentException("The menu must contain at least one item.");
        }

        int index = (id - 1) % menu.getItems().size();
        return menu.getItem(index);
    }

    private void requireStatus(CustomerStatus expectedStatus, String message) {
        if (status != expectedStatus) {
            throw new IllegalMoveException(message);
        }
    }
}
