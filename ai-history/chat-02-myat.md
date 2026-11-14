Yes, I have the Stage 2 rules from your brief:

Customer	Satisfaction lost per waiting turn	Bill	Special behaviour
Regular	8	Full price	none
VIP	5	10% off	none
Critic	12	Full price	Something your group defines and documents: an order preference or a reaction to service

Rules that come with it:

Every customer type follows the same flow: queue → seated → order → READY → SERVED → eat → pay.
The game controller must not check customer types. No if (c instanceof VIPCustomer) chains in Game. Each customer object handles its own behaviour, which is where your polymorphism marks come from.
You need a reproducible base-setting run with at least 1 VIP and 1 Critic. You can replace two of the regular arrivals, but there must still be 6 arrivals.
Stage 2 must be playable on its own, with the same win and loss targets (4 paid customers, $45 revenue, average satisfaction 60).

What this means for your design:

VIPCustomer and CriticCustomer extend Customer. Each one overrides small methods, for example getWaitingLoss(), which returns 8, 5 or 12, and applyDiscount(int cents). That might mean adding these methods to Customer and having loseSatisfactionForWaiting() call getWaitingLoss().
Your Order.billCents() changes from return item.getPriceCents(); to return customer.applyDiscount(item.getPriceCents());. That’s a small change with a clear reason, which is exactly the kind of evidence the README needs for the Stage 1 → 2 comparison. VIP prices come out to exact cents ($8.10, $13.50, $16.20), so no rounding issues yet.
The CLI should show each customer’s type, e.g. [VIP] Customer 2. Use a method like c.getTypeLabel() that each subclass overrides. Don’t put instanceof checks in the CLI either.
Game needs an arrival schedule with customer types, e.g. Regular, VIP, Regular, Critic, Regular, Regular. That’s a small setup change, which the brief says is expected.

Ideas for the Critic’s behaviour (pick one and document it):

Order preference: always orders the most expensive dish (Pasta), so the kitchen has to spend 2 turns on it.
Reaction to service: if served within 2 turns of being seated, gains +10 satisfaction. Otherwise loses an extra 10 when served.
Harsh review: their satisfaction at payment counts twice in the average.

Option 1 is the simplest and makes the Critic a real decision for the manager.

Before you start, check that Stage 1 is finished and tagged as stage-1 on a runnable commit. Stage 2 has to build on that saved version, and the comparison between the two tags is part of the marks. Is that done? And which Stage 2 classes are you responsible for this time?

Yes, because your design gives each constant its own lossChange value. Your UML already shows - Reputation(lossChange: int) and - lossChange: int. The constructor is how GOOD, NEUTRAL and BAD each get their own value.

java
public enum Reputation {
    GOOD(-2),
    NEUTRAL(0),
    BAD(2);            // the semicolon is needed once fields or methods follow

    private final int lossChange;

    Reputation(int lossChange) {
        this.lossChange = lossChange;
    }

    // adjustLoss(int base) goes here and uses lossChange
}

How enum constructors differ from normal ones:

You never call it yourself. GOOD(-2) calls it, and Java creates exactly those three objects when the enum loads. new Reputation(-2) won’t compile anywhere.
It’s always private. You can write private or leave it out. Either way it can’t be public, which is why your UML correctly shows it with -.
lossChange should be final, because a reputation’s effect never changes. The restaurant moves to a different constant instead.

If you’d chosen a plain enum with no fields, like your Rating, you’d need no constructor. adjustLoss() would then need a switch on this to find the adjustment. The field version avoids that, because each constant already carries its own adjustment.

in Order.java, should i keep the old advancePrep()? i think it is still needed

I’d remove it. Unless something else in your group’s code still calls it, there’s no reason to keep it. In the code I’ve seen, the only caller is Chef.cook(), and your Stage 2 diagram already changes that to cook(o, turn), which calls advancePrep(turn).

Keeping it would cause a real bug, not just leave unused code. If anything called the old version, an order would become READY with readyTurn still at -1. The Critic would then calculate servedTurn − (−1), for example 5 − (−1) = 6 turns, and give a BAD rating to a dish served on time. The mistake wouldn’t show up as an error, just as a wrong rating and a worse reputation for every later customer.

What about Stage 1? You don’t need to keep old methods for that. Stage 1 is preserved at the stage-1 tag, and each stage only has to run on its own. Changing a method’s signature is a normal part of extending a design. Mention it in the README’s Stage 1 → 2 section, for example: “Order.advancePrep() now takes the turn so the Critic can rate food quality; Chef.cook() updated to pass it.”

To check nothing else uses it: delete the old method and compile. Any remaining caller will show up as a compile error. Alternatively, search the project for advancePrep( in VS Code (Ctrl/Cmd+Shift+F).

If you had a specific caller in mind that I haven’t seen, tell me which one and we can look at whether it should pass the turn as well. Once it’s removed, also take advancePrep(): void off the class diagram, so the diagram matches the code.

what about Chef.cook(o)?

It changes the same way. In Stage 1, Chef.cook(o) is the only caller of advancePrep(). Once advancePrep() needs the turn, cook() has to receive the turn too so it can pass it on. Your Stage 2 diagram already shows this as cook(o: Order, turn: int).

Chef.java, written by your teammate, needs two small edits:

java
public void cook(Order o, int turn) {      // was cook(Order o)
    ...
    o.advancePrep(turn);                   // was o.advancePrep()
    ...
}

Game.java, in applyChefChoice():

java
case COOK -> chef.cook(choice.order(), turn);   // was chef.cook(choice.order())

Game already has turn, because it passes it to waiter.serve(..., turn) the same way, so the new call follows the existing pattern.

Don’t keep the old cook(o) either, for the same reason as before: it would call a method that no longer exists, or an order could become READY without recording its ready turn. Changes like this pass through several classes: Game passes the turn to Chef, which passes it to Order. Changing all of them in the same commit keeps the project compiling at every step.

Since Chef and Game aren’t yours, tell those teammates about the change, or make it together, so nobody’s work breaks. In the README, list it under the Stage 1 → 2 changes as “Chef.cook() and Game.applyChefChoice() now pass the current turn so Order can record when it became READY”.