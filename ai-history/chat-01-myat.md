Im starting on a new OOP project, you can find the details here

I’ve read both files. You’re building Restaurant Rush, a turn-based restaurant management game for the command line, written in Java. It’s due Sunday 15 Nov, 11:59 PM SGT, which is 6 days from now.

The game. The player is the manager. There are 18 turns, 2 tables, and one each of Waiter, Chef and Cashier. Six customers arrive, one at the start of turns 1, 4, 7, 10, 13 and 16. Customers pick their own food from Salad ($9, 1 unit), Burger ($15, 1 unit) and Pasta ($18, 2 units). Each turn, the manager gives the Waiter one task (seat a customer, take an order, or serve a dish) and tells the Chef which order to work on. Waiting customers lose satisfaction each turn and leave when it hits 0. To win, by the end of turn 18 you need at least 4 paid customers, at least $45 in revenue, and an average satisfaction of 60 or more.

Three stages, each tagged as a runnable version in Git:

stage-1: regular customers and the full turn loop, with both a win and a loss ending.
stage-2: VIP customers (lose 5 satisfaction per turn, 10% off) and Critic customers (lose 12 per turn, plus a special behaviour you define). The different behaviour has to come from the customer classes themselves (polymorphism), not from type checks in the controller.
stage-3: a Host who seats one extra customer per turn, combo meals (90% of the two items’ total price), and a once-per-game Happy Hour (20% off, +15 satisfaction for waiting customers, applied after the VIP discount).

Marks: class diagram 25, sequence diagram 20, design discussion 15, code 20, development process 20. So 60% of the marks are for design and documentation, not code.

Things to watch:

The two documents disagree on class diagrams. The assignment sheet asks for one final diagram matching Stage 3. The briefing slides ask for one per stage (3 in total). Doing all three is the safe choice.
Your AI chats are graded evidence. Every prompt and reply must go in your own Git branch, for example ai-history/stage-1/chat-01.md, with the full conversation, not a summary. That includes this chat.
Build the stages one at a time, as if you didn’t know what comes next. Stage 1 must contain no Stage 2 or 3 code. For each change between stages, the README has to say which files were added or changed and why.
Store money as integer cents. Keep game rules out of the user-interface code.
The turn order is strict. The Waiter acts before the Chef. A dish that becomes ready this turn can’t be served until next turn. A customer becomes ready to pay one turn after being served and pays the turn after that, at the earliest. The worked Pasta example (served turn 4, pays turn 6) is a good first test case.

Where do you want to start? I could sketch the Stage 1 class design (and the hooks that keep Stages 2 and 3 small changes), write the Stage 1 code, or plan how to split the 6 days across your group.

actually my group has already discussed the UML diagram for stage 1, over here.

The diagram is in good shape. The turn logic is all in the control class, the user-interface class only reads and displays, money is stored in cents, and putting loseSatisfactionForWaiting() on Customer means VIP and Critic customers can change it later. I’d fix a few things before you start coding.

Fix these (the code can’t match the diagram otherwise):

WaiterChoice and ChefChoice are missing. readWaiterChoice() returns a WaiterChoice and applyWaiterChoice() takes one, but neither class is drawn. Add them, probably as small classes or an enum with a target field.
IllegalMoveException isn’t connected to anything. Show that it extends RuntimeException (or Exception). Add dashed dependency arrows from Staff/Game, which throw it, and from Game/RestaurantCLI, which catch it. It’s how you meet the rule that an invalid choice explains itself and doesn’t use up the staff task. So also make sure requireAvailable() is checked before markActed() in every staff method.
Some things are drawn twice. Game lists tables, orders, menu, waiter, … as attributes and draws association lines to the same classes. The usual UML convention is one or the other. Since the class diagram is worth 25 marks and part of that is notation, keep the lines and remove the matching attributes. The CLI and Game also have both an association and a «uses» dependency. Keep just one.
staff: List<Staff> plus separate waiter/chef/cashier fields store the same objects twice. That’s fine in code, but the diagram should explain why. For example, the list is used to call resetTurn() on everyone each turn.

Design points that will affect the 15 marks for design discussion:

Game does almost everything. It has 14 fields and about 30 methods covering the turn loop, restaurant state, revenue and the queries the CLI needs. That will be hard to defend under the single-responsibility principle, and it grows with every stage. One option is to move tables, queue, customers and orders into a Restaurant class, and revenue and the paid satisfactions into a small Ledger (or Stats) class. Game then just runs the six turn steps.
Getters like getCustomers() and getOrders() return your internal lists. If they return unmodifiable views, they make a good encapsulation annotation, which the final diagram has to include.
Staff only shares the “one task per turn” check. That’s a legitimate reason for inheritance, but it isn’t polymorphism, because each subclass has different methods. Your polymorphism example will come from customer types in Stage 2 (and the Host fitting under Staff in Stage 3 is a nice extension example).
Note the food-choice rule on the diagram (seeded random or a fixed rotation), since chooseItem() has to be reproducible.

Changes Stages 2 and 3 will need. Leave these out of Stage 1. Each one is a small, justified change you can describe in the README.

Order.billCents() uses only the menu item. Stage 2 VIPs need the customer to apply a discount.
Stage 3 Happy Hour locks the price when the order is taken, so Order will need to store a priceCents set at creation instead of calculating it at payment.
A ComboMeal has to be used wherever a MenuItem is, so MenuItem will probably become a parent class or abstract class with SingleItem and ComboMeal beneath it.

If your group agrees with these changes, I can write the Stage 1 code from the diagram next. I’d keep it to the class and method names you’ve already chosen so the diagram and code stay in sync.

Yes. The CLI’s job becomes a simple pattern: show the options, read a number, and build a choice object. It never has to work out whether a move is legal. It just packages what the player picked, and Game decides.

Here’s a sketch of readWaiterChoice() (it uses the readInt helper suggested in your template’s TODO):

java
public WaiterChoice readWaiterChoice(Game g) {
    System.out.println("Waiter: 1) Seat  2) Take order  3) Serve  0) Wait");
    int action = readInt("Choose action: ", 0, 3);

    switch (action) {
        case 1: {
            Customer c = pick("Customer to seat", g.getWaitingQueue());
            Table t = pick("Table", g.getFreeTables());
            return WaiterChoice.seat(c, t);
        }
        case 2:
            return WaiterChoice.takeOrder(pick("Customer to take order from", g.getCustomersToOrder()));
        case 3:
            return WaiterChoice.serve(pick("Order to serve", g.getOrdersToServe()));
        default:
            return WaiterChoice.waitTurn();
    }
}

readChefChoice() is even shorter: list g.getOrdersToCook(), and return ChefChoice.cook(order) or ChefChoice.waitTurn().

Why it gets easier:

Each method has one clear return value. It doesn’t need several output parameters or a long list of codes.
No rule checking in the CLI. If the player picks a full table or an order that isn’t ready, Game throws IllegalMoveException. The game loop then calls your displayError() and asks again.
The input handling can be shared. Two small private helpers do most of the work:
readInt(prompt, min, max) keeps asking until the input is a valid number in range.
pick(label, list) prints a numbered list and returns the chosen item. It needs to be generic (<T> T pick(String label, List<T> items)) so the same method works for Customer, Table and Order. If the list is empty, it should say so instead of asking.

One thing to decide: if the player chooses “Serve” when nothing is ready, pick gets an empty list. You can either show only the actions that are currently possible, or let pick print “Nothing to serve” and re-ask for an action. The first gives the player a cleaner screen. It also meets the brief’s requirement to display the staff tasks available at the start of each turn.

would i need methods like askCustomer, custimerIds, orderList etc? to display on the CLI

Mostly not. One generic helper replaces most of them. Methods like askCustomer(), askTable() and askOrder() would each do the same thing: print a numbered list, read a number, and return the chosen item. Only the type of item and the text would differ, so you can write that once.

One generic picker:

java
private <T> T pick(String label, List<T> items, Function<T, String> describe) {
    if (items.isEmpty()) {
        System.out.println("No options available for: " + label);
        return null;   // caller treats null as "go back / wait"
    }
    System.out.println(label + ":");
    for (int i = 0; i < items.size(); i++) {
        System.out.println("  " + (i + 1) + ") " + describe.apply(items.get(i)));
    }
    int n = readInt("Choose: ", 1, items.size());
    return items.get(n - 1);
}

The third argument, a Function<T, String>, says how to turn each item into a line of text. Put import java.util.function.Function; at the top of the file. You’d call it like this:

java
Customer c = pick("Customer to seat", g.getWaitingQueue(), this::describeCustomer);
Order o    = pick("Order to serve",   g.getOrdersToServe(), this::describeOrder);

this::describeCustomer is a method reference. It passes your describeCustomer method into pick so pick can call it on each item.

The helpers you do need, for formatting only:

java
private String describeCustomer(Customer c) {
    return "Customer " + c.getId() + " (satisfaction " + c.getSatisfaction() + ", " + c.getStatus() + ")";
}

private String describeTable(Table t) {
    return t.isFree() ? "Table " + t.getTableId() + " (free)"
                      : "Table " + t.getTableId() + " – " + describeCustomer(t.getCustomer());
}

private String describeOrder(Order o) { ... }   // the one from earlier

These also do double duty: displayTurnStart() reuses them to print the queue, the tables and the orders. Your CLI then needs only:

the six public methods from the UML,
four or five private helpers: readInt, pick, formatCents, and the describe… methods.

You don’t need customerIds() or orderList(). The lists already come from Game’s getters (getWaitingQueue(), getOrdersToServe(), …), and the CLI shouldn’t build or filter its own. Deciding which orders can be served is a game rule, so it belongs in Game.

Private helpers don’t have to be on the class diagram, since it focuses on each class’s public operations. Leaving them off keeps the RestaurantCLI box readable.