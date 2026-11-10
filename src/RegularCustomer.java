/**
 * Represents a regular customer in the restaurant game.
 * Regular customers lose 8 satisfaction points per turn while waiting.
 */
public class RegularCustomer extends Customer {

    public RegularCustomer(int id) {
        super(id);
    }

    // Return the base waiting penalty for regular customers.
    @Override
    protected int waitingLoss() {
        return 8;
    }
}
