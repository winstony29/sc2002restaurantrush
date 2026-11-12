
public class HappyHour {
    private static final int DURATION_TURNS = 2;
    private int activatedTurn = -1;

    public void activate(int turn) {
        if (isUsed()) {
            throw new IllegalMoveException("Happy Hour can only be activated once.");
        }
        if (turn < 1) {
            throw new IllegalMoveException("Activation turn must be positive.");
        }
        activatedTurn = turn;
    }

    public boolean isUsed() {
        return activatedTurn != -1;
    }

    public boolean isActive(int turn) {
        return remainingTurns(turn) > 0;
    }

    public int remainingTurns(int turn) {
        if (!isUsed() || turn < activatedTurn) {
            return 0;
        }
        return Math.max(0, DURATION_TURNS - (turn - activatedTurn));
    }

    public boolean wasActiveOn(int turn) {
        return isActive(turn);
    }

    public int discountOn(int placedTurn, int cents) {
        if (wasActiveOn(placedTurn)) {
            return (int) ((cents * 80L + 50) / 100);
        }
        return cents;
    }
}
