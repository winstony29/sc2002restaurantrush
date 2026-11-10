
/**
 * Represents a critic customer in the restaurant game.
 * Critics lose satisfaction faster, choose the most expensive dish
 * and rate the restaurant based on serving delay.
 */
public class CriticCustomer extends Customer {

    public CriticCustomer(int id) {
        super(id);
    }

    // Critics lose 12 satisfaction points per turn while waiting.
    @Override
    protected int waitingLoss() {
        return 12;
    }

    // Choose the most expensive item on the menu.
    @Override
    public MenuItem chooseItem(Menu menu) {
        if (menu == null || menu.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "The menu must contain at least one item.");
        }

        MenuItem mostExpensive = menu.getItem(0);
        for (MenuItem item : menu.getItems()) {
            if (item.getPriceCents() > mostExpensive.getPriceCents()) {
                mostExpensive = item;
            }
        }

        return mostExpensive;
    }

    // Rate the service based on how many turns passed since the order was ready.
    @Override
    public Rating rateService(Order order, int currentTurn) {
        int readyTurn = order.getReadyTurn();

        if (readyTurn < 0 || currentTurn < readyTurn) {
            return Rating.NEUTRAL;
        }

        int delay = currentTurn - readyTurn;

        if (delay <= 1) {
            return Rating.GOOD;
        }

        if (delay == 2) {
            return Rating.NEUTRAL;
        }

        return Rating.BAD;
    }
}
