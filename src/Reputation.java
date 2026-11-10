/**
 * Stage 2: the restaurant's reputation, set by the most recent Critic rating.
 * It changes how much satisfaction EVERY waiting customer loses per turn.
 *
 *   GOOD    -> each customer type's loss - 2
 *   NEUTRAL -> unchanged (the game starts here)
 *   BAD     -> each customer type's loss + 2
 *   The adjusted loss is never below 1.
 */
public enum Reputation {
    GOOD(-2),
    NEUTRAL(0),
    BAD(2);
 
    private static final int MIN_LOSS = 1;

    private final int lossChange;

    Reputation(int lossChange) {
        this.lossChange = lossChange;
    }
   
    public int adjustLoss(int base) {
       return Math.max(MIN_LOSS, base + lossChange);
    }

}