For Stage 3, I need templates for: 

ComboMeal extends MenuItem: built from two items so it works anywhere a MenuItem does. Price = 90% of both prices, rounded to the cent (int cents); prep units = sum of both. Name "<a> + <b> combo". Null-check before super() using static helpers.

HostChoice record (Customer, Table) with seat(c, t) and waitTurn(). A null customer means the Host waits.

Order: add a final placedTurn (must be >= 1) via Order(item, customer, placedTurn) plus getPlacedTurn(), and remove the 2-arg constructor. Cashier uses placedTurn to lock the Happy Hour price, so Order doesn't calculate the bill.

RestaurantCLI (boundary, no game rules): readHostChoice(g), askActivateHappyHour(g) (y/n), displayHappyHour(g), displayRecovery(c, newValue), displayReview(c, rating, reputation), plus a Happy Hour status line and Host tasks on the turn screen.

There’s a stage-1 tag but no stage-2 tag yet. I’ll check the current CLI and what your teammates have started on the Stage 3 branches, so the CLI template uses their method names.

Restaurant_Rush_UML_Stage3_design_preview.png
if it helps, this is our UML diagram

Matching a class outline against the template's fields.
Thanks, that helps. Your diagram shows HappyHour.remainingTurns(turn) and isActive(turn), so the Happy Hour display can use those. ComboMeal matches what I’ve written. Now the Order changes:

I’ve written templates for your four Stage 3 items, following your diagram and the names your teammates’ Stage 3 code already uses. Rules and hints are in comments, and the logic is left as // TODOs for you. They compile together with the current stage3/game and stage3/staff branches. I used a placeholder HappyHour, since nobody has written it yet.

ComboMeal extends MenuItem. super(...) is called with comboName, comboPrice and comboUnits, which are static helpers that each check for null first. The comments explain why they must be static, and warn that integer division truncates instead of rounding.
HostChoice is a record with seat(c, t) and waitTurn().
Order has a final placedTurn, the 3-argument constructor (the 2-argument one is gone) and getPlacedTurn(). I also replaced the outdated “Stage 1: just the item’s price” comment. The constructor contains a placeholder line, this.placedTurn = 0;, only so the file compiles, because Java requires a final field to be assigned. Replace it with the parameter, or every order will look as if it was placed on turn 0 and Happy Hour discounts won’t apply.
RestaurantCLI has stubs for readHostChoice, askActivateHappyHour, displayHappyHour, displayRecovery and displayReview. There are also TODO markers in displayTurnStart() for the Happy Hour status line, which must show the remaining duration, and for a Host line under “what your staff can do”.
Things to raise with your group:

There’s a stage-1 tag but no stage-2 tag. Tag the current main as stage-2 before any Stage 3 branch is merged. Otherwise there’s no Stage 2 version to compare against.
Prices with fractions of a cent are already possible. For a VIP ordering the Salad + Pasta combo during Happy Hour, 2430 → 2187 → 2187 × 0.8 = 1749.6 cents. HappyHour.discountOn() (and VIPCustomer.applyDiscount()) must round to the nearest cent instead of truncating. Tell whoever writes HappyHour.
Customer.recoverSatisfaction() doesn’t exist on main. The Stage 3 Game calls it and your diagram shows it, so someone needs to add it, with the cap at 100.
Diagram gaps: HostChoice (along with WaiterChoice/ChefChoice) still isn’t on the diagram, and IllegalMoveException still has no parent class shown. ComboMeal’s private static helpers can be left off.
Order
Code
·JAVA
Restaurantcli
Code
·JAVA
Combomeal
Code
·JAVA
Hostchoice
Code
·JAVA
WaiterChoice uses "Kind", but HostChoice uses null. Is that inconsistent or would a Kind enum or Optional be safer

Yes, it’s inconsistent. Your other two choice records say explicitly what kind of choice they are, while HostChoice relies on a convention that a null customer means “wait”. The difference matters in practice, not just in style.

The risk with null: if the CLI ever builds HostChoice.seat(null, table) by mistake, for example because askCustomer returned null at the end of a replay file, Game would quietly treat it as “Host waits” instead of reporting a bug. With a Kind, a seat choice without a customer goes to Host.seat(), which throws “Customer or table cannot be null”, so the bug shows up.

The three options:

Option	For	Against
Kind enum (SEAT, WAIT), like ChefChoice	Consistent with the other two records. Intent is explicit. Game uses the same switch shape for all three.	A slightly larger record, and you need to change Game.applyHostChoice() and the UML.
Optional<Customer>	Makes “might be empty” visible in the type.	Java’s Optional is designed for return values, not fields or record components, and many style guides and IDE warnings discourage that. It also doesn’t cover the table, which must be present when a customer is.
Keep null, but hide it behind isWait()	The smallest change.	Still a different pattern from the other two records, and seat() should still reject null so mistakes aren’t silent.
I’d use the Kind enum:

java
public record HostChoice(Kind kind, Customer customer, Table table) {
    public enum Kind { SEAT, WAIT }
    // seat(c, t) -> SEAT;  waitTurn() -> WAIT with nulls
}
Game then gets switch (choice.kind()) { case SEAT -> host.seat(...); case WAIT -> { } }, the same as applyChefChoice().

What it costs: the stage3/game branch currently does if (choice.customer() != null), so tell the teammate writing Game. Also update the template’s comment that says “a null customer means the Host waits”, and add HostChoice to the diagram with its Kind.

You can also use this as a small, concrete design point in your report: all three choice records follow one pattern, so the reader learns it once.

My UML shows ComboMeal both extending MenuItem and aggregating two MenuItems. Can i use both inheritance and composition together, or should I pick one

You can use both, and here they do different jobs. Combining them like this is a recognised design called the Composite pattern.

Inheritance (ComboMeal extends MenuItem) means a combo is a menu item. Order, Chef, Cashier, Menu and the CLI accept it wherever a MenuItem goes, with no changes and no instanceof checks. That’s the Liskov substitution principle working for you, and it’s why Stage 3 needs so few changes to existing classes.
Aggregation (first, second) means a combo has two menu items. That’s where its name, price and preparation units come from, and it lets the CLI show what’s inside.
If you used only one:

With only inheritance, a combo would just be a MenuItem with hand-typed numbers. You’d lose the link to its components, and the rule “90% of both prices, prep = sum” would live outside the class.
With only composition (a separate class that isn’t a MenuItem), Order would need to accept “a MenuItem or a ComboMeal”. That means changing Order, Chef and Cashier, or adding a shared interface, which is far more work.
Notation checks for your diagram:

A hollow diamond (aggregation) is the right symbol, not a filled one (composition). Salad and Pasta exist on the menu independently of any combo, and one MenuItem can be part of many combos, so the combo doesn’t own their lifecycle. You’ve drawn it that way. Just make sure the multiplicity reads ComboMeal ◇── 2 MenuItem.
first and second appear as attributes and as the aggregation line. That’s the same duplication as elsewhere on your diagram. Keep one, preferably the line, with role names first/second or the multiplicity 2.
Annotate it as your “inheritance + composition together” example. The brief asks for annotated examples of encapsulation, polymorphism and composition, and this one class shows two of them.
One consequence to decide on: because a ComboMeal is a MenuItem, new ComboMeal(someCombo, salad) would also compile, giving a combo inside a combo. The brief says “two existing menu items”. Either allow it (the maths still works) or reject it in requireBoth(). Whichever you choose, mention it in the report.