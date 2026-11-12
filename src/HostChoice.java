/**
 * Stage 3: the manager's decision for the Host this turn.
 * The Host can only seat (one queued customer at a free table), or wait.
 * A null customer means the Host waits (Game checks choice.customer() != null).
 *
 * Same style as WaiterChoice / ChefChoice: built by RestaurantCLI, applied by Game.
 */
public record HostChoice(Customer customer, Table table) {
 
    public static HostChoice seat(Customer c, Table t) {
        // TODO
        throw new UnsupportedOperationException("TODO");
    }
 
    public static HostChoice waitTurn() {
        // TODO: the "Host waits" choice
        throw new UnsupportedOperationException("TODO");
    }
}
 