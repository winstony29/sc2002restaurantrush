/**
 * One dish on the menu (Salad, Burger, Pasta).
 * Immutable: once created, its name, price and preparation units never change.
 */
public class MenuItem {

    private final String name;
    private final int priceCents;   // money is stored as integer cents, e.g. $9.00 -> 900
    private final int prepUnits;    // how many Chef turns this dish needs

    public MenuItem(String name, int priceCents, int prepUnits) {
        // TODO: validate arguments (name not null/blank, priceCents >= 0, prepUnits >= 1)
        //       and throw IllegalArgumentException if invalid
        this.name = name;
        this.priceCents = priceCents;
        this.prepUnits = prepUnits;
    }

    public String getName() {
        return name;
    }

    public int getPriceCents() {
        return priceCents;
    }

    public int getPrepUnits() {
        return prepUnits;
    }

    @Override
    public String toString() {
        // TODO: return something readable for the CLI, e.g. "Pasta ($18.00, 2 units)"
        return name;
    }
}
