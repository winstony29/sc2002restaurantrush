
public class HappyHourTest {
    public static void main(String[] args) {
        testActivationAndDuration();
        testDiscount();
        testRecoveryBeforeService();
        testRecoveryAfterServiceOrDeparture();
        testNegativeRecovery();
        System.out.println("All Happy Hour and recovery tests passed!");
    }

    private static void testActivationAndDuration() {
        HappyHour hh = new HappyHour();

        check(!hh.isUsed(), "Happy Hour starts unused");
        check(!hh.isActive(1), "Unused Happy Hour is inactive");
        check(hh.remainingTurns(1) == 0, "Unused Happy Hour has no remaining turns");

        expectThrows(IllegalMoveException.class, () -> hh.activate(0),
                "Reject activation on turn zero");
        expectThrows(IllegalMoveException.class, () -> hh.activate(-1),
                "Reject activation on a negative turn");

        check(!hh.isUsed(), "Invalid activation does not use Happy Hour");

        hh.activate(4);

        check(hh.isUsed(), "Successful activation marks Happy Hour as used");
        check(!hh.isActive(3) && hh.remainingTurns(3) == 0,
                "Inactive before the activation turn");
        check(hh.isActive(4) && hh.remainingTurns(4) == 2,
                "Two turns remaining on activation");
        check(hh.isActive(5) && hh.remainingTurns(5) == 1,
                "One turn remaining on the following turn");
        check(!hh.isActive(6) && hh.remainingTurns(6) == 0,
                "Inactive after the two-turn window");

        expectThrows(IllegalMoveException.class, () -> hh.activate(7),
                "Reject a second activation even after Happy Hour ends");

        check(hh.wasActiveOn(4) && hh.wasActiveOn(5) && !hh.wasActiveOn(7),
                "Rejected activation preserves the original window");
    }

    private static void testDiscount() {
        HappyHour hh = new HappyHour();

        check(hh.discountOn(4, 1000) == 1000, "No discount before activation");

        hh.activate(4);

        check(hh.discountOn(3, 1000) == 1000, "Full price before Happy Hour");
        check(hh.discountOn(4, 1000) == 800, "20% off on the activation turn");
        check(hh.discountOn(5, 1000) == 800, "20% off on the second active turn");
        check(hh.discountOn(4, 999) == 799, "799.2 cents rounds down to 799");
        check(hh.discountOn(5, 997) == 798, "797.6 cents rounds up to 798");
        check(hh.discountOn(6, 1000) == 1000, "Full price after Happy Hour");
    }

    private static void testRecoveryBeforeService() {
        Customer c = new RegularCustomer(1);

        checkRecovery(c, CustomerStatus.WAITING);

        c.seatAt(new Table(1));
        checkRecovery(c, CustomerStatus.SEATED);

        Order order = new Order(new MenuItem("Salad", 900, 1), c);
        c.placeOrder(order);
        checkRecovery(c, CustomerStatus.ORDERED);

        c.recoverSatisfaction(100);
        check(c.getSatisfaction() == 100, "Recovery caps satisfaction at 100");
    }

    private static void checkRecovery(Customer c, CustomerStatus expectedStatus) {
        check(c.getStatus() == expectedStatus, "Customer is " + expectedStatus);

        c.loseSatisfactionForWaiting(Reputation.NEUTRAL);
        int before = c.getSatisfaction();

        c.recoverSatisfaction(3);

        check(c.getSatisfaction() == before + 3,
                "Recovery applies in " + expectedStatus);
    }

    private static void testRecoveryAfterServiceOrDeparture() {
        Customer c = new RegularCustomer(1);
        c.loseSatisfactionForWaiting(Reputation.NEUTRAL);
        c.seatAt(new Table(1));

        Order order = new Order(new MenuItem("Salad", 900, 1), c);
        c.placeOrder(order);

        order.advancePrep(1);
        order.markServed();
        c.markServed(2);

        checkNoRecovery(c, CustomerStatus.SERVED);

        c.markReadyToPay(3);
        checkNoRecovery(c, CustomerStatus.READY_TO_PAY);

        order.markPaid();
        c.markPaid();
        checkNoRecovery(c, CustomerStatus.PAID);

        Customer departed = new RegularCustomer(2);
        for (int turn = 0; turn < 13; turn++) {
            departed.loseSatisfactionForWaiting(Reputation.NEUTRAL);
        }

        departed.leave();
        checkNoRecovery(departed, CustomerStatus.LEFT);
    }

    private static void checkNoRecovery(Customer c, CustomerStatus expectedStatus) {
        check(c.getStatus() == expectedStatus, "Customer is " + expectedStatus);

        int before = c.getSatisfaction();
        check(before < 100, "Recovery check starts below the cap");

        c.recoverSatisfaction(15);

        check(c.getSatisfaction() == before,
                "Recovery has no effect in " + expectedStatus);
    }

    private static void testNegativeRecovery() {
        Customer c = new RegularCustomer(1);
        c.loseSatisfactionForWaiting(Reputation.NEUTRAL);

        int before = c.getSatisfaction();

        expectThrows(IllegalArgumentException.class, () -> c.recoverSatisfaction(-1),
                "Reject negative recovery");

        check(c.getSatisfaction() == before,
                "Rejected recovery leaves satisfaction unchanged");
    }

    private static void expectThrows(
            Class<? extends RuntimeException> expected,
            Runnable action,
            String name) {

        try {
            action.run();
        } catch (RuntimeException e) {
            if (!expected.isInstance(e)) {
                throw new AssertionError(
                        name + " threw " + e.getClass().getSimpleName(), e);
            }
            System.out.println(name + " - PASS");
            return;
        }

        throw new AssertionError(
                name + " did not throw " + expected.getSimpleName());
    }

    private static void check(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError(name + " - FAILED");
        }
        System.out.println(name + " - PASS");
    }
}
