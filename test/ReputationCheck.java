
public class ReputationCheck {
    private static final int REGULAR = 8;
    private static final int VIP = 5;
    private static final int CRITIC = 12;
 
    private static int passed = 0;
    private static int failed = 0;
 
    public static void main(String[] args) {
        System.out.println("ReputationCheck");
        System.out.println();
 
        // NEUTRAL leaves every customer type's loss unchanged
        check("NEUTRAL Regular", REGULAR, Reputation.NEUTRAL.adjustLoss(REGULAR));
        check("NEUTRAL VIP", VIP, Reputation.NEUTRAL.adjustLoss(VIP));
        check("NEUTRAL Critic", CRITIC, Reputation.NEUTRAL.adjustLoss(CRITIC));
 
        // GOOD makes waiting 2 cheaper
        check("GOOD Regular", 6, Reputation.GOOD.adjustLoss(REGULAR));
        check("GOOD VIP", 3, Reputation.GOOD.adjustLoss(VIP));
        check("GOOD Critic", 10, Reputation.GOOD.adjustLoss(CRITIC));
 
        // BAD makes waiting 2 dearer
        check("BAD Regular", 10, Reputation.BAD.adjustLoss(REGULAR));
        check("BAD VIP", 7, Reputation.BAD.adjustLoss(VIP));
        check("BAD Critic", 14, Reputation.BAD.adjustLoss(CRITIC));
 
        // The adjusted loss never drops below 1
        check("GOOD on base 3 stays at 1", 1, Reputation.GOOD.adjustLoss(3));
        check("GOOD on base 2 is clamped to 1", 1, Reputation.GOOD.adjustLoss(2));
        check("GOOD on base 1 is clamped to 1", 1, Reputation.GOOD.adjustLoss(1));
        check("NEUTRAL on base 1 stays 1", 1, Reputation.NEUTRAL.adjustLoss(1));
        check("BAD on base 1 becomes 3", 3, Reputation.BAD.adjustLoss(1));
 
        // Exactly three reputations, and NEUTRAL exists for the starting state
        check("number of reputations", 3, Reputation.values().length);
        check("valueOf(\"NEUTRAL\")", Reputation.NEUTRAL, Reputation.valueOf("NEUTRAL"));
 
        System.out.println();
        System.out.println(passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }
 
    private static void check(String name, Object expected, Object actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("  PASS  " + name);
        } else {
            failed++;
            System.out.println("  FAIL  " + name + ": expected " + expected + " but got " + actual);
        }
    }
}
