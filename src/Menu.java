import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The restaurant's menu: an ordered list of MenuItems.
 * UML: Menu ◇── 1..* MenuItem (aggregation)
 */
public class Menu {

    private final List<MenuItem> items;

    public Menu(List<MenuItem> items) {
        // TODO: reject a null or empty list (the UML says 1..* items)
        // Defensive copy, so outside code can't change our list later (encapsulation)
        this.items = new ArrayList<>(items);
    }

    /** Read-only view: callers can look at the items but not add/remove them. */
    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * @param index position in the menu, starting at 0
     * @return the item at that position
     */
    public MenuItem getItem(int index) {
        // TODO: if index is out of range, throw an exception with a clear message
        //       (decide: IllegalArgumentException or IllegalMoveException?)
        return items.get(index);
    }

    // TODO (optional): a static factory for the base setting, e.g.
    // public static Menu baseMenu() {
    //     return new Menu(List.of(
    //         new MenuItem("Salad", 900, 1),
    //         new MenuItem("Burger", 1500, 1),
    //         new MenuItem("Pasta", 1800, 2)));
    // }
}
