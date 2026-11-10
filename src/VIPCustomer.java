/**
 * Represents a VIP customer in the restaurant game.
 * VIP customers lose less satisfaction while waiting and receive a 10% discount.
 */
public class VIPCustomer extends Customer {

    public VIPCustomer(int id) {
        super(id);
    }

    // VIP customers lose 5 satisfaction points per turn while waiting.
    @Override
    protected int waitingLoss() {
        return 5;
    }

    // Apply a 10% discount
    @Override
    public int applyDiscount(int cents) {
        return cents * 90 / 100;
    }
}
