/**
 * Stage 3: a combo made of two existing menu items.
 * Extends MenuItem, so a ComboMeal works anywhere a MenuItem does
 * (Order, Chef, Cashier and the CLI need no changes to handle it).
 *
 *   name       = "<first> + <second> combo"            e.g. "Salad + Pasta combo"
 *   price      = 90% of a + b, rounded to the nearest cent (int cents)
 *                e.g. (900 + 1800) * 0.9 = 2430 -> $24.30
 *   prep units = a's units + b's units        e.g. 1 + 2 = 3
 */
public class ComboMeal extends MenuItem {

    private static final int COMBO_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        super(comboName(first, second), comboPrice(first, second), comboUnits(first, second));
        this.first = first;
        this.second = second;
    }



    private static String comboName(MenuItem first, MenuItem second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("A combo needs two menu items");
        }
        return first.getName() + " + " + second.getName() + " combo";
    }

    private static int comboPrice(MenuItem first, MenuItem second) {
    
        int sum = first.getPriceCents() + second.getPriceCents();
        return (sum * COMBO_PERCENT + 50) / 100;
    }

    private static int comboUnits(MenuItem first, MenuItem second) {
        return first.getPrepUnits() + second.getPrepUnits();
    }

    public MenuItem getFirst() {
        return first;
    }

    public MenuItem getSecond() {
        return second;
    }
}