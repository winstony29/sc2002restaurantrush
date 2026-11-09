import java.util.Scanner;

/**
 * «boundary» — the only class that talks to the player.
 * It READS choices and DISPLAYS state. It must not contain game rules
 * (no checking whether a move is legal, no changing satisfaction, etc.).
 *
 * Depends on classes written by teammates: Game, Customer, WaiterChoice, ChefChoice.
 * Agree on their method names before filling in the TODOs.
 */
public class RestaurantCLI {

    private final Scanner in;

    public RestaurantCLI() {
        this(new Scanner(System.in));
    }

    /** Lets tests or replay files feed input, e.g. new Scanner(new File("victory-run.txt")). */
    public RestaurantCLI(Scanner in) {
        this.in = in;
    }

    /**
     * Ask the manager what the Waiter should do this turn.
     * Options: seat a queued customer at a free table, take an order, serve a READY order, or wait.
     * Use g.getWaitingQueue(), g.getFreeTables(), g.getCustomersToOrder(), g.getOrdersToServe()
     * to list ONLY the options that exist right now.
     *
     * Keep asking until the input is a valid number/format (bad typing is a CLI problem).
     * Whether the MOVE is legal is checked by Game/staff, which throw IllegalMoveException.
     */
    public WaiterChoice readWaiterChoice(Game g) {
        // TODO: print options, read a line, parse it, build a WaiterChoice
        //       (agree with your teammate on what WaiterChoice looks like)
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Ask the manager which order the Chef should work on (or wait).
     * Use g.getOrdersToCook() to list the options.
     */
    public ChefChoice readChefChoice(Game g) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /** Shows why a move failed, e.g. "Invalid move: Table 2 is occupied. Choose again." */
    public void displayError(String reason) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Required by brief §3.1: turn number, revenue, average satisfaction of paid customers
     * (N/A if nobody has paid), waiting queue, occupied tables, each customer's satisfaction,
     * order status, and the staff tasks available.
     */
    public void displayTurnStart(Game g) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /**
     * Required by brief §3.5: served count, unhappy departures, revenue,
     * average satisfaction of paid customers (N/A if none), turns used.
     */
    public void displayTurnEnd(Game g) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    /** After turn 18: VICTORY or DEFEAT, and which of the three targets were met. */
    public void displayResult(Game g) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    // ---- helpers (private: not part of the UML's public interface) ----

    /** 1830 -> "$18.30". Money is stored in cents; always show 2 decimals. */
    private static String formatCents(int cents) {
        return String.format("$%d.%02d", cents / 100, cents % 100);
    }

    // TODO: a helper like "private int readInt(String prompt, int min, int max)"
    //       that re-asks on non-numbers or out-of-range input will save a lot of repetition.
}
