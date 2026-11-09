/**
 * Thrown when the manager makes an invalid choice
 * (e.g. seating someone at an occupied table, serving an order that isn't READY).
 *
 * The CLI catches it, shows getMessage() to the player, and asks again
 * WITHOUT using up the staff member's task for the turn.
 *
 * Unchecked (extends RuntimeException) so every method doesn't need "throws".
 * If your group prefers a checked exception, change to "extends Exception"
 * and add "throws IllegalMoveException" where needed. Update the UML either way.
 */
public class IllegalMoveException extends RuntimeException {

    public IllegalMoveException(String reason) {
        super(reason);
    }
}
