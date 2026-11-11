import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Queue;
import java.util.function.IntFunction;

// «control» Owns the restaurant state and runs the 18-turn game loop.
public class Game {
    private final RestaurantCLI cli;
    private int turn;
    private final int maxTurns = 18;
    // "A reproducible base-setting run must include at least one VIP and one Critic.
    //  You may replace two of the regular arrivals; there must still be six arrivals."
    // "Different behaviour must be dispatched through the customer objects, not through
    //  a chain of type checks in the turn controller."
    // This map is the only place Game names a customer type.
    private final Map<Integer, IntFunction<Customer>> arrivals = Map.of(
            1, RegularCustomer::new,
            4, VIPCustomer::new,
            7, CriticCustomer::new,
            10, RegularCustomer::new,
            13, RegularCustomer::new,
            16, RegularCustomer::new);
    private Reputation reputation = Reputation.NEUTRAL;
    // "Can be activated once per game, at the start of a turn. It lasts for that turn and the next."
    private final HappyHour happyHour = new HappyHour();
    private final List<Table> tables = new ArrayList<>();
    private final Queue<Customer> waitingQueue = new ArrayDeque<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final Menu menu;
    private final Host host;
    private final Waiter waiter;
    private final Chef chef;
    private final Cashier cashier;
    private final List<Staff> staff = new ArrayList<>();
    private int revenueCents;
    private final List<Integer> paidSatisfactions = new ArrayList<>();

    private static final int VICTORY_MIN_PAID = 4;
    private static final int VICTORY_MIN_REVENUE_CENTS = 4500;
    private static final int VICTORY_MIN_AVG_SATISFACTION = 60;
    // "During each active turn, customers still waiting for a table or food recover 15
    //  satisfaction at turn start (maximum 100)." The cap is applied by Customer.
    private static final int HAPPY_HOUR_RECOVERY = 15;

    // Base setting: 2 tables, Salad/Burger/Pasta menu plus two combos, one Host, Waiter, Chef and Cashier.
    public Game(RestaurantCLI cli) {
        this.cli = cli;
        MenuItem salad = new MenuItem("Salad", 900, 1);
        MenuItem burger = new MenuItem("Burger", 1500, 1);
        MenuItem pasta = new MenuItem("Pasta", 1800, 2);
        // "A customer chooses a combo according to a rule you document."
        // No new rule: customers still pick menu item (id - 1) mod menu.size(), so with five
        // items customer 4 picks Salad + Pasta and customer 5 picks Burger + Pasta.
        // The base items keep their positions so earlier choices do not shift.
        menu = new Menu(List.of(salad, burger, pasta,
                new ComboMeal(salad, pasta),
                new ComboMeal(burger, pasta)));
        tables.add(new Table(1));
        tables.add(new Table(2));
        host = new Host();
        waiter = new Waiter();
        chef = new Chef();
        cashier = new Cashier();
        staff.addAll(List.of(host, waiter, chef, cashier));
    }

    // Loops runTurn() until isOver(), then shows the result.
    public void runGame() {
        while (!isOver()) {
            runTurn();
        }
        cli.displayResult(this);
    }

    // One turn: reset staff, arrival, start screen, Happy Hour offer and recovery,
    // manager choices, eating/payment, waiting/abandonment.
    public void runTurn() {
        if (isOver()) {
            throw new IllegalStateException("The game is over");
        }
        turn++;
        for (Staff s : staff) {
            s.resetTurn();
        }
        processArrival();
        cli.displayTurnStart(this);
        offerHappyHour();
        applyHappyHourRecovery();
        applyManagerChoices();
        processEatingAndPayment();
        processWaitingAndAbandonment();
        cli.displayTurnEnd(this);
    }

    // Step 1: the scheduled customer joins the waiting queue.
    private void processArrival() {
        IntFunction<Customer> arrival = arrivals.get(turn);
        if (arrival != null) {
            Customer c = arrival.apply(customers.size() + 1);
            customers.add(c);
            waitingQueue.add(c);
        }
    }

    // "Can be activated once per game, at the start of a turn." Only asked while unused.
    private void offerHappyHour() {
        if (!happyHour.isUsed() && cli.askActivateHappyHour(this)) {
            happyHour.activate(turn);
            cli.displayHappyHour(this);
        }
    }

    // "During each active turn, customers still waiting for a table or food recover 15
    //  satisfaction at turn start (maximum 100)." Customers who have left are not touched.
    private void applyHappyHourRecovery() {
        if (!happyHour.isActive(turn)) {
            return;
        }
        for (Customer c : customers) {
            if (isWaitingForService(c)) {
                c.recoverSatisfaction(HAPPY_HOUR_RECOVERY);
                cli.displayRecovery(c, c.getSatisfaction());
            }
        }
    }

    // Step 2-3: Host, then Waiter, then Chef, each retried until legal.
    private void applyManagerChoices() {
        // "The Host seats at most one queued customer per turn, in addition to the Waiter's task."
        retryUntilValid(() -> applyHostChoice(cli.readHostChoice(this)));
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

    private void applyHostChoice(HostChoice choice) {
        if (choice.customer() != null) { // a null customer means the Host waits
            host.seat(choice.customer(), choice.table());
            waitingQueue.remove(choice.customer()); // only reached if seat() did not throw
        }
    }

    private void applyWaiterChoice(WaiterChoice choice) {
        switch (choice.kind()) {
            case SEAT -> {
                waiter.seat(choice.customer(), choice.table());
                waitingQueue.remove(choice.customer()); // only reached if seat() did not throw
            }
            // The turn locks the Happy Hour price: "The price is locked when the order is taken."
            case TAKE_ORDER -> orders.add(waiter.takeOrder(choice.customer(), menu, turn));
            case SERVE -> {
                Order o = choice.customer() == null ? null : choice.customer().getOrder();
                waiter.serve(o, turn);
                // Critic: "A distinct order preference or reaction to service that you define and document"
                // This is our definition of the Critic's reaction to service which we decided on. See DESIGN_NOTES.md for details.
                // Only reached if serve() did not throw; the customer decides its own rating.
                Rating r = choice.customer().rateService(o, turn);
                if (r != Rating.NONE) {
                    reputation = Reputation.valueOf(r.name());
                    cli.displayReview(choice.customer(), r, reputation);
                }
            }
            case WAIT -> { }
        }
    }

    private void applyChefChoice(ChefChoice choice) {
        switch (choice.kind()) {
            // Critic: "A distinct order preference or reaction to service that you define and document"
            // The turn lets the Order record when it became READY, which the Critic's rating uses.
            case COOK -> chef.cook(choice.order(), turn);
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
            // "Apply the VIP discount first, then Happy Hour."
            revenueCents += cashier.collect(payer, happyHour);
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
            if (isWaitingForService(c)) {
                // "Different behaviour must be dispatched through the customer objects, not through
                //  a chain of type checks in the turn controller."
                // Each customer computes its own loss; Game only passes the current reputation in.
                c.loseSatisfactionForWaiting(reputation);
                if (c.hasGivenUp()) {
                    abandon(c);
                }
            }
        }
    }

    // Still waiting for a table or food: WAITING, SEATED or ORDERED.
    private static boolean isWaitingForService(Customer c) {
        CustomerStatus s = c.getStatus();
        return s == CustomerStatus.WAITING || s == CustomerStatus.SEATED || s == CustomerStatus.ORDERED;
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

    public Reputation getReputation() {
        return reputation;
    }

    public Host getHost() {
        return host;
    }

    public HappyHour getHappyHour() {
        return happyHour;
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
