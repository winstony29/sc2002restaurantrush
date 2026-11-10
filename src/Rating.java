/**
 * Stage 2: a Critic's verdict on the food quality of ONE dish.
 *
 * Food quality = how long the dish sat READY before it was served
 * (servedTurn - readyTurn):
 *   1 turn  (served as soon as the rules allow) -> GOOD
 *   2 turns                                     -> NEUTRAL
 *   3+ turns                                    -> BAD
 *
 * The minimum possible is 1, because a dish finished this turn
 * can only be served next turn at the earliest.
 */
public enum Rating {
    NONE,
    GOOD,
    NEUTRAL,
    BAD;
 
}
 
