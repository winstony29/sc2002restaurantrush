import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;
import java.util.Scanner;
import java.util.Set;
import java.util.StringJoiner;
 
/**
 * «boundary» All console input and output. Holds no game rules: it turns
 * what the manager types into a WaiterChoice / ChefChoice and prints the
 * game state. Whether a move is legal is decided by Staff, Customer, Table
 * and Order, which throw IllegalMoveException; Game catches it, calls
 * displayError(reason) and asks again.
 *
 * The CLI re-prompts on its own only when input cannot form a choice at all
 * (not a number, unknown table or customer id, customer has no order yet).
 *
 * Uses these Game getters: getTurn, getMaxTurns, getRevenueCents, getMenu,
 * getWaitingQueue, getTables, getCustomers, getOrders, getFreeTables,
 * getCustomersToOrder, getOrdersToServe, getOrdersToCook, getPaidCount,
 * getServedCount, getUnhappyDepartures, averageSatisfaction, isVictory.
 */
public class RestaurantCLI {
 
    private static final String RULE = "=".repeat(64);
    private static final String THIN = "-".repeat(64);
 
    private final Scanner in;
    // display memory only: welcome shown, which customers have been announced
    private boolean welcomed = false;
    private final Set<Integer> announced = new HashSet<>();
 
    public RestaurantCLI() {
        this(new Scanner(System.in));
    }
 
    /** Lets tests or replay files feed input, e.g. new Scanner(new File("victory-run.txt")). */
    public RestaurantCLI(Scanner in) {
        this.in = in;
    }
 
    // ------------------------------------------------------------------ input
 
    public WaiterChoice readWaiterChoice(Game g) {
        while (true) {
            System.out.println();
            System.out.println("  WAITER - choose one task");
            System.out.println("    1) Seat a waiting customer");
            System.out.println("    2) Take an order");
            System.out.println("    3) Serve a ready order");
            System.out.println("    0) Wait");
            Integer pick = readInt("  > ");
            if (pick == null) {
                return WaiterChoice.waitTurn(); // input ended
            }
            switch (pick) {
                case 1 -> {
                    System.out.println("    Waiting: " + customerIds(g.getWaitingQueue())
                            + "   |   Free tables: " + tableIds(g.getFreeTables()));
                    Customer c = askCustomer(g, "    Customer id (blank to go back): ");
                    if (c == null) {
                        continue;
                    }
                    Table t = askTable(g, "    Table id (blank to go back): ");
                    if (t == null) {
                        continue;
                    }
                    return WaiterChoice.seat(c, t);
                }
                case 2 -> {
                    System.out.println("    Ready to order: " + customerIds(g.getCustomersToOrder()));
                    Customer c = askCustomer(g, "    Customer id (blank to go back): ");
                    if (c == null) {
                        continue;
                    }
                    return WaiterChoice.takeOrder(c);
                }
                case 3 -> {
                    System.out.println("    Ready to serve: " + orderList(g.getOrdersToServe()));
                    Customer c = askCustomer(g, "    Customer id (blank to go back): ");
                    if (c == null) {
                        continue;
                    }
                    return WaiterChoice.serve(c);
                }
                case 0 -> {
                    return WaiterChoice.waitTurn();
                }
                default -> System.out.println("    Please enter 0, 1, 2 or 3.");
            }
        }
    }
 
    public ChefChoice readChefChoice(Game g) {
        while (true) {
            System.out.println();
            System.out.println("  CHEF - choose one task");
            System.out.println("    1) Cook one unit of an order");
            System.out.println("    0) Wait");
            Integer pick = readInt("  > ");
            if (pick == null) {
                return ChefChoice.waitTurn(); // input ended
            }
            switch (pick) {
                case 1 -> {
                    System.out.println("    To cook: " + orderList(g.getOrdersToCook()));
                    Customer c = askCustomer(g, "    Customer id (blank to go back): ");
                    if (c == null) {
                        continue;
                    }
                    // Input problem, not a game rule: there is no order to put in the choice.
                    if (c.getOrder() == null) {
                        System.out.println("    Customer " + c.getId() + " has not ordered yet.");
                        continue;
                    }
                    return ChefChoice.cook(c.getOrder());
                }
                case 0 -> {
                    return ChefChoice.waitTurn();
                }
                default -> System.out.println("    Please enter 0 or 1.");
            }
        }
    }
 
    // ------------------------------------------------------------ Stage 3 input

    /**
     * Stage 3: ask what the Host does this turn: seat one queued customer at a
     * free table, or wait. The Host only seats, so this is a short version of
     * the SEAT branch in readWaiterChoice.
     *
     * Hints:
     *  - If nobody is waiting or no table is free, you could skip the question
     *    and return HostChoice.waitTurn() straight away (fewer pointless prompts).
     *  - Reuse askCustomer / askTable; blank input ("go back") can mean wait.
     *  - Input ended (askCustomer returns null at end of a replay file) -> wait,
     *    like the other read methods.
     *  - Don't check whether the seat is legal here: Host.seat() throws
     *    IllegalMoveException and Game calls displayError and asks again.
     */
    public HostChoice readHostChoice(Game g) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Stage 3: offered at the start of a turn while Happy Hour is still unused
     * (Game only calls this if !happyHour.isUsed()).
     * Read y / n; re-prompt on anything else. Input ended -> false.
     * Tip: say what it does (20% off orders taken while active, +15 satisfaction
     * for waiting customers, lasts this turn and next, once per game).
     */
    public boolean askActivateHappyHour(Game g) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    // ----------------------------------------------------------- Stage 3 output

    /** Stage 3: confirm Happy Hour has started and how many turns it lasts (g.getHappyHour().remainingTurns(g.getTurn())). */
    public void displayHappyHour(Game g) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Stage 3: one line per customer who recovered satisfaction during Happy Hour,
     * e.g. "c3 (VIP) recovers to 87". Game passes the value after recovery (capped at 100).
     */
    public void displayRecovery(Customer c, int newValue) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Stage 2 Critic, shown when a rating happens,
     * e.g. "Critic c4 rates the food GOOD. Reputation is now GOOD."
     * Tip: say what the reputation means for waiting customers
     *      (lossChange: lose 2 less / 2 more per turn) so the player sees the effect.
     *      Decide what to show for Rating.NONE, or check Game never calls this with NONE.
     */
    public void displayReview(Customer c, Rating rating, Reputation reputation) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /** Called by Game when a staff action throws IllegalMoveException. */
    public void displayError(String reason) {
        System.out.println();
        System.out.println("  ! Not allowed: " + reason);
        System.out.println("    Choose again.");
    }
 
    // ----------------------------------------------------------------- output
 
    /**
     * Brief §3.1: turn number, revenue, average satisfaction of paid customers
     * (N/A if nobody has paid), waiting queue, occupied tables, each customer's
     * satisfaction, order status, and the staff tasks available.
     */
    public void displayTurnStart(Game g) {
        if (!welcomed) {
            welcomed = true;
            displayWelcome(g);
        }
        System.out.println();
        System.out.println(RULE);
        System.out.println("  TURN " + g.getTurn() + " of " + g.getMaxTurns());
        System.out.println(RULE);
        announceArrivals(g);
 
        System.out.println();
        System.out.println("  Revenue " + money(g.getRevenueCents()) + "   |   Paid " + g.getPaidCount()
                + "   |   Avg satisfaction " + average(g));
        System.out.println("  Reputation " + g.getReputation());
        // TODO (Stage 3): Happy Hour status line. Brief: "Display its remaining duration".
        //   active    -> e.g. "Happy Hour ON: 20% off new orders, 2 turns left"  (remainingTurns(turn))
        //   not used  -> e.g. "Happy Hour available (once per game)"
        //   used up   -> e.g. "Happy Hour used"
        //   Use g.getHappyHour().isActive(g.getTurn()) / isUsed(): ask, don't recalculate the rules.

        System.out.println();
        System.out.println("  WAITING");
        List<Customer> waiting = g.getWaitingQueue();
        if (waiting.isEmpty()) {
            System.out.println("    nobody");
        }
        for (Customer c : waiting) {
            System.out.printf("    %-13s satisfaction %d%n", label(c), c.getSatisfaction());
        }
 
        System.out.println();
        System.out.println("  TABLES");
        for (Table t : g.getTables()) {
            Customer c = t.getCustomer();
            if (c == null) {
                System.out.println("    Table " + t.getTableId() + "   free");
            } else {
                String dish = c.getOrder() == null ? "-" : c.getOrder().getItem().getName();
                System.out.printf("    Table %d   %-13s %-13s %-21s satisfaction %d%n",
                        t.getTableId(), label(c), c.getStatus(), dish, c.getSatisfaction());
            }
        }
 
        System.out.println();
        System.out.println("  KITCHEN");
        boolean any = false;
        for (Order o : g.getOrders()) {
            if (o.getStatus() == OrderStatus.TAKEN) {
                System.out.printf("    %-13s %-21s cooking %d/%d%n", label(o.getCustomer()),
                        o.getItem().getName(), o.getUnitsDone(), o.getItem().getPrepUnits());
                any = true;
            } else if (o.getStatus() == OrderStatus.READY) {
                String since = o.getReadyTurn() > 0 ? " (ready since turn " + o.getReadyTurn() + ")" : "";
                System.out.printf("    %-13s %-21s READY to serve%s%n", label(o.getCustomer()),
                        o.getItem().getName(), since);
                any = true;
            }
        }
        if (!any) {
            System.out.println("    no open orders");
        }
 
        System.out.println();
        System.out.println("  WHAT YOUR STAFF CAN DO");
        // TODO (Stage 3): a "Host" line before the Waiter, from a private hostTasks(g) helper
        //   like waiterTasks: "seat c3, c5 (free tables: 2)" or "nothing to do".
        //   The seat part of waiterTasks is the same, so consider sharing it.
        System.out.println("    Waiter   " + waiterTasks(g));
        System.out.println("    Chef     " + chefTasks(g));
        System.out.println(THIN);
    }
 
    /**
     * Brief §3.5: served count, unhappy departures, revenue,
     * average satisfaction of paid customers (N/A if none), turns used.
     */
    public void displayTurnEnd(Game g) {
        System.out.println();
        System.out.println(THIN);
        System.out.println("  END OF TURN " + g.getTurn() + " of " + g.getMaxTurns());
        System.out.println("  Revenue " + money(g.getRevenueCents()) + "   |   Served " + g.getServedCount()
                + "   |   Paid " + g.getPaidCount() + "   |   Left unhappy " + g.getUnhappyDepartures());
        System.out.println("  Avg satisfaction " + average(g) + "   |   Reputation " + g.getReputation()
                + "   |   Turns used " + g.getTurn() + " of " + g.getMaxTurns());
        System.out.println(THIN);
    }
 
    /** After the last turn: the totals and VICTORY or DEFEAT. */
    public void displayResult(Game g) {
        System.out.println();
        System.out.println(RULE);
        System.out.println("  GAME OVER");
        System.out.println(RULE);
        System.out.println("  Revenue:            " + money(g.getRevenueCents()));
        System.out.println("  Customers served:   " + g.getServedCount());
        System.out.println("  Customers paid:     " + g.getPaidCount());
        System.out.println("  Customers who left: " + g.getUnhappyDepartures());
        System.out.println("  Avg satisfaction:   " + average(g));
        System.out.println();
        System.out.println(g.isVictory() ? "  RESULT: VICTORY!" : "  RESULT: DEFEAT");
        System.out.println(RULE);
    }
 
    // ------------------------------------------------------- arrivals and help
 
    /** Shown once before the first turn: the goal, what each staff member does, and the menu. */
    private void displayWelcome(Game g) {
        System.out.println();
        System.out.println(RULE);
        System.out.println("  WELCOME TO RESTAURANT RUSH");
        System.out.println(RULE);
        System.out.println();
        System.out.println("  You run the restaurant for " + g.getMaxTurns() + " turns. To win, after the last turn");
        System.out.println("  you need at least 4 paying customers, $45.00 in revenue, and an");
        System.out.println("  average satisfaction of 60 or more among the customers who paid.");
        System.out.println("  Customers waiting for a table or food lose satisfaction every turn,");
        System.out.println("  and leave without paying if it reaches 0.");
        System.out.println();
        System.out.println("  STAFF (each does at most one task per turn)");
        System.out.println("    Waiter    seats a customer, takes an order, or serves a READY dish");
        System.out.println("    Chef      cooks one prep unit of one order");
        System.out.println("    Cashier   works by itself: one payment per turn, oldest first");
        System.out.println();
        System.out.println("  MENU (customers choose their own food)");
        for (MenuItem m : g.getMenu().getItems()) {
            System.out.printf("    %-21s %7s   %d prep unit(s)%n",
                    m.getName(), money(m.getPriceCents()), m.getPrepUnits());
        }
        System.out.println();
        pause();
    }
 
    /** Announces each newcomer once. */
    private void announceArrivals(Game g) {
        for (Customer c : g.getCustomers()) {
            if (announced.add(c.getId())) {
                System.out.println();
                System.out.println("  New arrival: " + label(c) + " joins the waiting queue.");
            }
        }
    }
 
    /** Waits for Enter so the player can read the welcome. Scripted runs can override it. */
    protected void pause() {
        System.out.print("    < press Enter to continue >");
        if (in.hasNextLine()) {
            in.nextLine();
        }
    }
 
    // ---------------------------------------------------------------- helpers
 
    /** Reads one whole number; re-prompts on bad input. Returns null if input has ended. */
    private Integer readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNextLine()) {
                return null;
            }
            String line = in.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("    Please enter a whole number.");
            }
        }
    }
 
    /** Like readInt, but a blank line means "go back" and returns null. Accepts "c3", "t2" or "3". */
    private Integer readIdOrBlank(String prompt) {
        while (true) {
            System.out.print(prompt);
            if (!in.hasNextLine()) {
                return null;
            }
            String line = in.nextLine().trim();
            if (line.isEmpty()) {
                return null;
            }
            if (line.startsWith("c") || line.startsWith("C") || line.startsWith("t") || line.startsWith("T")) {
                line = line.substring(1);
            }
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("    Please enter a number, or leave blank to go back.");
            }
        }
    }
 
    private Table askTable(Game g, String prompt) {
        while (true) {
            Integer id = readIdOrBlank(prompt);
            if (id == null) {
                return null;
            }
            for (Table t : g.getTables()) {
                if (t.getTableId() == id) {
                    return t;
                }
            }
            System.out.println("    There is no table " + id + ".");
        }
    }
 
    private Customer askCustomer(Game g, String prompt) {
        while (true) {
            Integer id = readIdOrBlank(prompt);
            if (id == null) {
                return null;
            }
            for (Customer c : g.getCustomers()) {
                if (c.getId() == id) {
                    return c;
                }
            }
            System.out.println("    There is no customer " + id + ".");
        }
    }
 
    // the lists come from Game, so the CLI only words them
    private static String waiterTasks(Game g) {
        List<String> tasks = new ArrayList<>();
        if (!g.getWaitingQueue().isEmpty() && !g.getFreeTables().isEmpty()) {
            tasks.add("seat " + customerIds(g.getWaitingQueue()) + " (free tables: " + tableIds(g.getFreeTables()) + ")");
        }
        if (!g.getCustomersToOrder().isEmpty()) {
            tasks.add("take order from " + customerIds(g.getCustomersToOrder()));
        }
        if (!g.getOrdersToServe().isEmpty()) {
            tasks.add("serve " + orderList(g.getOrdersToServe()));
        }
        return tasks.isEmpty() ? "nothing to do" : String.join("; ", tasks);
    }
 
    private static String chefTasks(Game g) {
        return g.getOrdersToCook().isEmpty() ? "nothing to cook" : "cook " + orderList(g.getOrdersToCook());
    }
 
    private static String average(Game g) {
        OptionalDouble avg = g.averageSatisfaction();
        return avg.isPresent() ? String.format("%.1f", avg.getAsDouble()) : "N/A";
    }
 
    /**
     * "c2 VIP", "c3 Critic", "c1 Regular". Display only: the type comes from
     * the subclass name (VIPCustomer -> VIP), so a new customer type shows up
     * here with no change to the CLI. A plain Customer is shown as just "c1".
     */
    private static String label(Customer c) {
        String type = c.getClass().getSimpleName();
        if (type.endsWith("Customer")) {
            type = type.substring(0, type.length() - "Customer".length());
        }
        return type.isEmpty() ? "c" + c.getId() : "c" + c.getId() + " " + type;
    }
 
    private static String customerIds(List<Customer> list) {
        StringJoiner sj = new StringJoiner(", ");
        for (Customer c : list) {
            sj.add(label(c));
        }
        return sj.length() == 0 ? "none" : sj.toString();
    }
 
    private static String tableIds(List<Table> list) {
        StringJoiner sj = new StringJoiner(", ");
        for (Table t : list) {
            sj.add(String.valueOf(t.getTableId()));
        }
        return sj.length() == 0 ? "none" : sj.toString();
    }
 
    private static String orderList(List<Order> list) {
        StringJoiner sj = new StringJoiner(", ");
        for (Order o : list) {
            sj.add(label(o.getCustomer()) + " " + o.getItem().getName());
        }
        return sj.length() == 0 ? "none" : sj.toString();
    }
 
    /** 1830 -> "$18.30". Money is stored in cents; always show 2 decimals. */
    private static String money(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }
}