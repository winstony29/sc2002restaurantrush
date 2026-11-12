/**
 * Stage 3: a combo made of two existing menu items.
 * Extends MenuItem, so a ComboMeal works anywhere a MenuItem does
 * (Order, Chef, Cashier and the CLI need no changes to handle it).
 *
 *   name       = "<a> + <b> combo"            e.g. "Salad + Pasta combo"
 *   price      = 90% of a + b, rounded to the nearest cent (int cents)
 *                e.g. (900 + 1800) * 0.9 = 2430 -> $24.30
 *   prep units = a's units + b's units        e.g. 1 + 2 = 3
 */
public class ComboMeal extends MenuItem {

    private static final int COMBO_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        // super(...) must be the FIRST statement in a constructor, so the null
        // checks can't come before it. Instead, each argument is computed by a
        // static helper that checks for null first; it runs before super() starts.
        super(comboName(first, second), comboPrice(first, second), comboUnits(first, second));
        this.first = first;
        this.second = second;
    }

    // ---- static helpers: run BEFORE the object exists, so they must be static
    //      (they can't use fields or this).

    /** Throws IllegalArgumentException if either item is null. */
    private static void requireBoth(MenuItem a, MenuItem b) {
        // TODO
        // Optional: should a combo of a combo be allowed? The brief says "two existing
        // menu items". If you forbid it, decide how without an instanceof chain.
    }

    private static String comboName(MenuItem a, MenuItem b) {
        requireBoth(a, b);
        // TODO: "<a> + <b> combo"
        throw new UnsupportedOperationException("TODO");
    }

    private static int comboPrice(MenuItem a, MenuItem b) {
        requireBoth(a, b);
        // TODO: 90% of (a + b) in cents, ROUNDED to the nearest cent.
        //   Careful: int division truncates, e.g. 2431 * 90 / 100 = 2187 (2187.9 cut off).
        //   Either Math.round(...) with a double, or add half the divisor before dividing.
        //   Use COMBO_PERCENT, not a magic number.
        throw new UnsupportedOperationException("TODO");
    }

    private static int comboUnits(MenuItem a, MenuItem b) {
        requireBoth(a, b);
        // TODO
        throw new UnsupportedOperationException("TODO");
    }

    public MenuItem getFirst() {
        return first;
    }

    public MenuItem getSecond() {
        return second;
    }
}