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

    private static final int VICTORY_MIN_PAID = 4;
    private static final int VICTORY_MIN_REVENUE_CENTS = 4500;
    private static final int VICTORY_MIN_AVG_SATISFACTION = 60;

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
        turn++;
        for (Staff s : staff) {
            s.resetTurn();
        }
        processArrival();
        cli.displayTurnStart(this);
        applyManagerChoices();
        processEatingAndPayment();
        processWaitingAndAbandonment();
        cli.displayTurnEnd(this);
    }

    // Step 1: the scheduled customer joins the waiting queue.
    private void processArrival() {
        for (int arrivalTurn : arrivalTurns) {
            if (arrivalTurn == turn) {
                Customer c = new Customer(customers.size() + 1);
                customers.add(c);
                waitingQueue.add(c);
            }
        }
    }

    // Step 2-3: Waiter choice first, then Chef choice, each retried until legal.
    private void applyManagerChoices() {
        // Waiter first, so a dish finished by the Chef this turn cannot be served until next turn.
        retryUntilValid(() -> applyWaiterChoice(cli.readWaiterChoice(this)));
        retryUntilValid(() -> applyChefChoice(cli.readChefChoice(this)));
    }

    // The single error route: runs the move, and on IllegalMoveException shows why and asks again.
    private void retryUntilValid(Runnable move) {
        while (true) {
            try {
                move.run();
                return;
            } catch (IllegalMoveException e) {
                cli.displayError(e.getMessage());
            }
        }
    }

    private void applyWaiterChoice(WaiterChoice choice) {
        switch (choice.kind()) {
            case SEAT -> {
                waiter.seat(choice.customer(), choice.table());
                waitingQueue.remove(choice.customer()); // only reached if seat() did not throw
            }
            case TAKE_ORDER -> orders.add(waiter.takeOrder(choice.customer(), menu));
            case SERVE -> waiter.serve(choice.customer().getOrder(), turn);
            case WAIT -> { }
        }
    }

    private void applyChefChoice(ChefChoice choice) {
        switch (choice.kind()) {
            case COOK -> chef.cook(choice.table().getCustomer().getOrder());
            case WAIT -> { }
        }
    }

    // Step 4: served customers finish eating; Cashier.collect() charges at most one.
    private void processEatingAndPayment() {
        for (Customer c : customers) {
            if (c.getStatus() == CustomerStatus.SERVED && c.getServedTurn() < turn) {
                c.markReadyToPay(turn);
            }
        }
        Customer payer = nextCustomerToPay();
        if (payer != null) {
            revenueCents += cashier.collect(payer);
            paidSatisfactions.add(payer.getSatisfaction());
        }
    }

    // Oldest READY_TO_PAY customer who became ready on an earlier turn, or null.
    private Customer nextCustomerToPay() {
        Customer next = null;
        for (Customer c : customers) { // arrival order breaks ties
            if (c.getStatus() == CustomerStatus.READY_TO_PAY && c.getReadyToPayTurn() < turn
                    && (next == null || c.getReadyToPayTurn() < next.getReadyToPayTurn())) {
                next = c;
            }
        }
        return next;
    }

    // Step 5: unserved waiting/seated customers lose satisfaction; those at 0 abandon.
    private void processWaitingAndAbandonment() {
        for (Customer c : customers) {
            CustomerStatus s = c.getStatus();
            if (s == CustomerStatus.WAITING || s == CustomerStatus.SEATED || s == CustomerStatus.ORDERED) {
                c.loseSatisfactionForWaiting();
                if (c.hasGivenUp()) {
                    abandon(c);
                }
            }
        }
    }

    // Cancels the customer's unfinished order, frees their table, removes them from the queue.
    private void abandon(Customer c) {
        if (c.getOrder() != null) {
            c.getOrder().cancel();
        }
        if (c.getTable() != null) {
            c.getTable().clear();
        }
        waitingQueue.remove(c);
        c.leave();
    }

    public boolean isOver() {
        return turn >= maxTurns;
    }

    // >= 4 paid AND revenue >= $45.00 AND average paid satisfaction >= 60.
    public boolean isVictory() {
        return getPaidCount() >= VICTORY_MIN_PAID
                && revenueCents >= VICTORY_MIN_REVENUE_CENTS
                && averageSatisfaction().orElse(0) >= VICTORY_MIN_AVG_SATISFACTION;
    }

    // Empty when no one has paid yet (displayed as N/A).
    public OptionalDouble averageSatisfaction() {
        return paidSatisfactions.stream().mapToInt(Integer::intValue).average();
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
        return (int) customers.stream().filter(c -> c.getStatus() == CustomerStatus.LEFT).count();
    }

    public List<Table> getFreeTables() {
        return tables.stream().filter(Table::isFree).toList();
    }

    public List<Customer> getCustomersToOrder() {
        return customers.stream().filter(c -> c.getStatus() == CustomerStatus.SEATED).toList();
    }

    public List<Order> getOrdersToServe() {
        return orders.stream().filter(o -> o.getStatus() == OrderStatus.READY).toList();
    }

    public List<Order> getOrdersToCook() {
        return orders.stream().filter(o -> o.getStatus() == OrderStatus.TAKEN).toList();
    }
}
