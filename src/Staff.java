package restrush.sc2002restaurantrush.src;

/**
 * Abstract superclass for all restaurant staff.
 * Handles the shared rule that each staff member
 * can only perform one action per turn.
 */
public abstract class Staff {

    private boolean actedThisTurn;

    // Constructor
    public Staff() {
        this.actedThisTurn = false;
    }

    /**
     * Checks whether the staff member can act.
     * Throws an exception if they have already acted.
     */
    protected void requireAvailable() {
        if (actedThisTurn) {
            throw new IllegalMoveException(
                "Staff member has already acted this turn."
            );
        }
    }

    /**
     * Marks the staff member as having acted.
     */
    protected void markActed() {
        actedThisTurn = true;
    }

    /**
     * Resets the staff member's action status
     * at the beginning of a new turn.
     */
    public void resetTurn() {
        actedThisTurn = false;
    }

}

