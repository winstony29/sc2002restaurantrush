/**
 * Lifecycle of an Order.
 *
 * TAKEN     -> order placed, kitchen still preparing (unitsDone < prepUnits)
 * READY     -> all preparation units done, waiting for the Waiter to serve
 * SERVED    -> on the customer's table
 * PAID      -> Cashier has collected payment
 * CANCELLED -> customer left (satisfaction hit 0) before the order was finished
 */
public enum OrderStatus {
    TAKEN,
    READY,
    SERVED,
    PAID,
    CANCELLED
}
