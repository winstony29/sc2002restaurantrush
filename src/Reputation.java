/**
 * Represents the restaurant's overall reputation.
 * Reputation affects how much satisfaction customers lose while waiting.
 */
public enum Reputation {
    GOOD(-2),
    NEUTRAL(0),
    BAD(2);

    private final int waitingAdjustment;

    Reputation(int waitingAdjustment) {
        this.waitingAdjustment = waitingAdjustment;
    }

    // Apply the reputation adjustment, ensuring the penalty is at least 1.
    public int adjustLoss(int base) {
        return Math.max(1, base + waitingAdjustment);
    }
}
