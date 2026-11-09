public record ChefChoice(Kind kind, Order order) {
 
    public enum Kind { COOK, WAIT }
 
    public static ChefChoice cook(Order o) {
        return new ChefChoice(Kind.COOK, o);
    }
 
    public static ChefChoice waitTurn() {
        return new ChefChoice(Kind.WAIT, null);
    }
}