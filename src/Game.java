import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Queue;

// «control» Owns the restaurant state and runs the 18-turn game loop.
public class Game {
    private final RestaurantCLI cli;
    private int turn;
    private final int maxTurns = 18;
    private final int[] arrivalTurns = {1, 4, 7, 10, 13, 16};
    private final List<Table> tables = new ArrayList<>();
    private final Queue<Customer> waitingQueue = new ArrayDeque<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final Menu menu;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;
    private final List<Staff> staff = new ArrayList<>();
    private int revenueCents;
    private final List<Integer> paidSatisfactions = new ArrayList<>();

    // Base setting: 2 tables, Salad/Burger/Pasta menu, one Waiter, Chef and Cashier.
    public Game(RestaurantCLI cli) {
        this.cli = cli;
        menu = new Menu(List.of(
                new MenuItem("Salad", 900, 1),
                new MenuItem("Burger", 1500, 1),
                new MenuItem("Pasta", 1800, 2)));
        tables.add(new Table(1));
        tables.add(new Table(2));
        waiter = new Waiter();
        chef = new Chef();
        cashier = new Cashier();
        staff.addAll(List.of(waiter, chef, cashier));
    }

    // Loops runTurn() until isOver(), then shows the result.
    public void runGame() {
        throw new UnsupportedOperationException("TODO");
    }

    // One turn: reset staff, arrival, start screen, manager choices, eating/payment, waiting/abandonment.
    public void runTurn() {
        throw new UnsupportedOperationException("TODO");
    }

    // Step 1: the scheduled customer joins the waiting queue.
    private void processArrival() {
        throw new UnsupportedOperationException("TODO");
    }

    // Step 2-3: Waiter choice first, then Chef choice, each retried until legal.
    private void applyManagerChoices() {
        throw new UnsupportedOperationException("TODO");
    }

    // The single error route: runs the move, and on IllegalMoveException shows why and asks again.
    private void retryUntilValid(Runnable move) {
        throw new UnsupportedOperationException("TODO");
    }

    private void applyWaiterChoice(WaiterChoice choice) {
        throw new UnsupportedOperationException("TODO");
    }

    private void applyChefChoice(ChefChoice choice) {
        throw new UnsupportedOperationException("TODO");
    }

    // Step 4: served customers finish eating; Cashier.collect() charges at most one.
    private void processEatingAndPayment() {
        throw new UnsupportedOperationException("TODO");
    }

    // Oldest READY_TO_PAY customer who became ready on an earlier turn, or null.
    private Customer nextCustomerToPay() {
        throw new UnsupportedOperationException("TODO");
    }

    // Step 5: unserved waiting/seated customers lose satisfaction; those at 0 abandon.
    private void processWaitingAndAbandonment() {
        throw new UnsupportedOperationException("TODO");
    }

    // Cancels the customer's unfinished order, frees their table, removes them from the queue.
    private void abandon(Customer c) {
        throw new UnsupportedOperationException("TODO");
    }

    public boolean isOver() {
        throw new UnsupportedOperationException("TODO");
    }

    // >= 4 paid AND revenue >= $45.00 AND average paid satisfaction >= 60.
    public boolean isVictory() {
        throw new UnsupportedOperationException("TODO");
    }

    // Empty when no one has paid yet (displayed as N/A).
    public OptionalDouble averageSatisfaction() {
        throw new UnsupportedOperationException("TODO");
    }

    public int getRevenueCents() {
        return revenueCents;
    }

    public int getTurn() {
        return turn;
    }

    public int getMaxTurns() {
        return maxTurns;
    }

    public List<Customer> getWaitingQueue() {
        return List.copyOf(waitingQueue);
    }

    public List<Table> getTables() {
        return List.copyOf(tables);
    }

    public List<Customer> getCustomers() {
        return List.copyOf(customers);
    }

    public List<Order> getOrders() {
        return List.copyOf(orders);
    }

    public Menu getMenu() {
        return menu;
    }

    public int getPaidCount() {
        return paidSatisfactions.size();
    }

    public int getServedCount() {
        // A served customer never loses satisfaction, so can never become LEFT.
        return (int) customers.stream()
                .filter(c -> c.getStatus() == CustomerStatus.SERVED
                        || c.getStatus() == CustomerStatus.READY_TO_PAY
                        || c.getStatus() == CustomerStatus.PAID)
                .count();
    }

    public int getUnhappyDepartures() {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Table> getFreeTables() {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Customer> getCustomersToOrder() {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Order> getOrdersToServe() {
        throw new UnsupportedOperationException("TODO");
    }

    public List<Order> getOrdersToCook() {
        throw new UnsupportedOperationException("TODO");
    }
}
