package restrush.sc2002restaurantrush.src;


public class Waiter extends Staff {

    /**
     * Seats a waiting customer at an available table.
     */
    public void seat(Customer c, Table t) {
        requireAvailable();

        if (c == null || t == null) {
            throw new IllegalMoveException(
                "Customer or table cannot be null."
            );
        }

        if (c.getStatus() != CustomerStatus.WAITING) {
            throw new IllegalMoveException(
                "Customer is not waiting to be seated."
            );
        }

        if (!t.isFree()) {
            throw new IllegalMoveException(
                "Table is already occupied."
            );
        }

        // Seat the customer
        t.seat(c);
        c.assignTable(t);

        markActed();
    }

    /**
     * Takes an order from a seated customer.
     */
    public Order takeOrder(Customer c, Menu m) {
        requireAvailable();

        if (c == null || m == null) {
            throw new IllegalMoveException(
                "Customer or menu cannot be null."
            );
        }

        if (c.getStatus() != CustomerStatus.SEATED) {
            throw new IllegalMoveException(
                "Customer must be seated to order."
            );
        }

        if (c.getOrder() != null) {
            throw new IllegalMoveException(
                "Customer already has an order."
            );
        }

        // Customer chooses an item from the menu
        MenuItem item = c.chooseItem(m);

        if (item == null) {
            throw new IllegalMoveException(
                "No valid menu item selected."
            );
        }

        // Create and assign the order
        Order order = new Order(item, c);
        c.placeOrder(order);

        markActed();

        return order;
    }

    /**
     * Serves a ready order to its customer.
     */
    public void serve(Order o, int turn) {
        requireAvailable();

        if (o == null) {
            throw new IllegalMoveException(
                "Order cannot be null."
            );
        }

        if (o.getStatus() != OrderStatus.READY) {
            throw new IllegalMoveException(
                "Order is not ready to be served."
            );
        }

        Customer c = o.getCustomer();

        if (c == null) {
            throw new IllegalMoveException(
                "Order has no associated customer."
            );
        }

        if (c.getStatus() != CustomerStatus.ORDERED) {
            throw new IllegalMoveException(
                "Customer is not waiting for an order."
            );
        }

        // Serve order and update customer status
        o.markServed();
        c.markServed(turn);

        markActed();
    }
}

