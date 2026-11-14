Implement Customer Classes
==========================

You:
🖼️ [Uploaded image: image(20261109-074430).png — PNG, 268 KB]
I'm implementing Customer.java and CustomerStatus.java for my SC2002 restaurant management game. Follow the UML diagram.
For Customer.java, use encapsulation by keeping the attributes private and providing getters instead of allowing them to be directly changed. Each customer should have: customer ID, satisfaction level, status, assigned Table and Order, servedTurn and readyToPayTurn. The customer ID should be final since it should not change after the customer is created.
Initialise the satisfaction score to 100, customer status to WAITING, and servedTurn and readyToPayTurn to -1. The -1 shows that they have not been set yet, include that as a comment.
For CustomerStatus.java, define the valid states which are: WAITING, SEATED, ORDERED, SERVED, READY_TO_PAY, PAID and LEFT.


ChatGPT:
Customer.java
public class Customer {
    private final int id;
    private int satisfaction;
    private CustomerStatus status;
    private Table table;
    private Order order;
    private int servedTurn;
    private int readyToPayTurn;

    public Customer(int id) {
        this.id = id;
        this.satisfaction = 100;
        this.status = CustomerStatus.WAITING;

        // A value of -1 means the turn has not been set yet.
        this.servedTurn = -1;
        this.readyToPayTurn = -1;
    }

    public int getId() {
        return id;
    }

    public int getSatisfaction() {
        return satisfaction;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public Table getTable() {
        return table;
    }

    public Order getOrder() {
        return order;
    }

    public int getServedTurn() {
        return servedTurn;
    }

    public int getReadyToPayTurn() {
        return readyToPayTurn;
    }
}

CustomerStatus.java
public enum CustomerStatus {
    WAITING,
    SEATED,
    ORDERED,
    SERVED,
    READY_TO_PAY,
    PAID,
    LEFT
}

table and order start as null, matching the UML’s 0..1 multiplicity. A new customer starts in WAITING and gets a table and an order later in the game.


You:
Now lets implement the customer action methods in Customer.java based on the UML.

seatAt(Table t): Assign a table and change status from WAITING to SEATED.
placeOrder(Order o): Assign an order and change status from SEATED to ORDERED.
markServed(int turn): Record the turn and change status from ORDERED to SERVED.
markReadyToPay(int turn): Record the turn and change status from SERVED to READY_TO_PAY.
markPaid(): Change status from READY_TO_PAY to PAID.
leave(): Change status to LEFT.
Make sure each method checks the current status before changing it, and throw an IllegalMoveException if the action isnt allowed.
Keep the existing code unchanged and dont implement the satisfaction methods yet.



ChatGPT:
Add these methods inside Customer.java, before the closing brace. I used the game’s waiting stages for leave(), so customers can leave while WAITING, SEATED, or ORDERED.
public void seatAt(Table t) {
    if (status != CustomerStatus.WAITING) {
        throw new IllegalMoveException("Only a waiting customer can be seated.");
    }

    table = t;
    status = CustomerStatus.SEATED;
}

public void placeOrder(Order o) {
    if (status != CustomerStatus.SEATED) {
        throw new IllegalMoveException("Only a seated customer can place an order.");
    }

    order = o;
    status = CustomerStatus.ORDERED;
}

public void markServed(int turn) {
    if (status != CustomerStatus.ORDERED) {
        throw new IllegalMoveException("A customer must place an order before being served.");
    }

    servedTurn = turn;
    status = CustomerStatus.SERVED;
}

public void markReadyToPay(int turn) {
    if (status != CustomerStatus.SERVED) {
        throw new IllegalMoveException("A customer must be served before being ready to pay.");
    }

    readyToPayTurn = turn;
    status = CustomerStatus.READY_TO_PAY;
}

public void markPaid() {
    if (status != CustomerStatus.READY_TO_PAY) {
        throw new IllegalMoveException("A customer must be ready to pay before payment.");
    }

    status = CustomerStatus.PAID;
}

public void leave() {
    if (status != CustomerStatus.WAITING
            && status != CustomerStatus.SEATED
            && status != CustomerStatus.ORDERED) {
        throw new IllegalMoveException("A customer can only leave while waiting to be served.");
    }

    status = CustomerStatus.LEFT;
}



You:
Ok now implement the following based on the UML:

loseSatisfactionForWaiting(): Reduce satisfaction by 8 each turn when the customer is WAITING, SEATED or ORDERED. Set the lower limit to be 0
hasGivenUp(): Return true if satisfaction reaches 0 and the customer hasnt been served yet.
Also implement chooseItem(Menu menu) to return a MenuItem chosen from the menu

Keep the existing code unchanged, dont add any other features yet.


ChatGPT:
Add these three methods inside Customer.java. chooseItem() picks uniformly at random and assumes the menu contains at least one item.
public void loseSatisfactionForWaiting() {
    if (status == CustomerStatus.WAITING
            || status == CustomerStatus.SEATED
            || status == CustomerStatus.ORDERED) {
        satisfaction = Math.max(0, satisfaction - 8);
    }
}

public boolean hasGivenUp() {
    return satisfaction == 0 && servedTurn == -1;
}

public MenuItem chooseItem(Menu menu) {
    int index = java.util.concurrent.ThreadLocalRandom.current()
            .nextInt(menu.getItems().size());
    return menu.getItem(index);
}



You:
the leave() method should only allow customers to leave if their satisfaction has reached 0, not just because they havent been served yet.
Update leave() to check hasGivenUp() before changing the status to LEFT. Throw an IllegalMoveException otherwise.
Also update hasGivenUp() to return true only if satisfaction is 0 and the customer is still WAITING, SEATED or ORDERED.
Keep the rest of the code unchanged.


ChatGPT:
Replace leave() and hasGivenUp() in Customer.java with these versions:
public void leave() {
    if (!hasGivenUp()) {
        throw new IllegalMoveException(
                "A customer can only leave if they have given up.");
    }

    status = CustomerStatus.LEFT;
}

public boolean hasGivenUp() {
    return satisfaction == 0
            && (status == CustomerStatus.WAITING
                || status == CustomerStatus.SEATED
                || status == CustomerStatus.ORDERED);
}



You:
ok thanks, now i want to add some validation to the customer methods to prevent invalid actions.

Customer(int id): Make sure the ID is positive, otherwise throw an IllegalArgumentException.
seatAt(Table t): Check that the table isnt null.
placeOrder(Order o): Check that the order isnt null and belongs to this customer.
markServed(int turn): Check that the turn is positive and the order status is SERVED.
markReadyToPay(int turn): Make sure the turn is later than servedTurn.
Use IllegalMoveException for invalid customer actions. Also create a private helper method requireStatus() to avoid repeating the same status checks in every method.
Keep everything else unchanged.



ChatGPT:
In Customer.java, replace the constructor and the five action methods below, then add requireStatus(). placeOrder() checks that the order belongs to this exact Customer instance. The caller must mark the order SERVED before calling customer.markServed(turn).
public Customer(int id) {
    if (id <= 0) {
        throw new IllegalArgumentException("Customer ID must be positive.");
    }

    this.id = id;
    this.satisfaction = 100;
    this.status = CustomerStatus.WAITING;

    // A value of -1 means the turn has not been set yet.
    this.servedTurn = -1;
    this.readyToPayTurn = -1;
}

public void seatAt(Table t) {
    requireStatus(CustomerStatus.WAITING, "Only a waiting customer can be seated.");
    if (t == null) {
        throw new IllegalMoveException("A table is required to seat the customer.");
    }

    table = t;
    status = CustomerStatus.SEATED;
}

public void placeOrder(Order o) {
    requireStatus(CustomerStatus.SEATED, "Only a seated customer can place an order.");
    if (o == null || o.getCustomer() != this) {
        throw new IllegalMoveException("The order must belong to this customer.");
    }

    order = o;
    status = CustomerStatus.ORDERED;
}

public void markServed(int turn) {
    requireStatus(CustomerStatus.ORDERED,
            "A customer must place an order before being served.");
    if (turn <= 0) {
        throw new IllegalMoveException("The served turn must be positive.");
    }
    if (order.getStatus() != OrderStatus.SERVED) {
        throw new IllegalMoveException("The order must be marked as served first.");
    }

    servedTurn = turn;
    status = CustomerStatus.SERVED;
}

public void markReadyToPay(int turn) {
    requireStatus(CustomerStatus.SERVED,
            "A customer must be served before being ready to pay.");
    if (turn <= servedTurn) {
        throw new IllegalMoveException("The ready-to-pay turn must be after the served turn.");
    }

    readyToPayTurn = turn;
    status = CustomerStatus.READY_TO_PAY;
}

public void markPaid() {
    requireStatus(CustomerStatus.READY_TO_PAY,
            "A customer must be ready to pay before payment.");

    status = CustomerStatus.PAID;
}

private void requireStatus(CustomerStatus expectedStatus, String message) {
    if (status != expectedStatus) {
        throw new IllegalMoveException(message);
    }
}



You:
I want to change chooseItem(Menu menu) so that customer choices are predictable when testing the game.
Instead of picking randomly, use the customer ID to select an item from the menu. Use (id - 1) % menu.getItems().size() as the index so the same customer ID always picks the same item.
Also check that the menu isnt null or empty, and throw an IllegalArgumentException if it is.
Keep the rest of Customer.java unchanged.


ChatGPT:
Replace chooseItem() in Customer.java with:
public MenuItem chooseItem(Menu menu) {
    if (menu == null || menu.getItems().isEmpty()) {
        throw new IllegalArgumentException("The menu must contain at least one item.");
    }

    int index = (id - 1) % menu.getItems().size();
    return menu.getItem(index);
}



You:
ok now for stage 2 we need to introduce different customer types using inheritance and polymorphism.
I want RegularCustomer, VIPCustomer and CriticCustomer to extend Customer. 
First, help me refactor Customer into an abstract class.
Each customer type should have its own waiting satisfaction loss, so add an abstract waitingLoss() method and update loseSatisfactionForWaiting() to use it.
Also add applyDiscount(int cents) and rateService(Order order, int currentTurn) with default behaviour in Customer, so that my subclasses can override them later.
Try to keep the existing stage 1 methods and behaviour unchanged where possible. Don't implement any of the subclasses yet.


ChatGPT:
In Customer.java, make Customer abstract:
public abstract class Customer {

Replace loseSatisfactionForWaiting() and add the three methods below. waitingLoss() is protected so each subclass can provide its own value. The default applyDiscount() returns the original price in cents, and rateService() returns the customer’s current satisfaction on the existing 0–100 scale.
protected abstract int waitingLoss();

public void loseSatisfactionForWaiting() {
    if (status == CustomerStatus.WAITING
            || status == CustomerStatus.SEATED
            || status == CustomerStatus.ORDERED) {
        satisfaction = Math.max(0, satisfaction - waitingLoss());
    }
}

public int applyDiscount(int cents) {
    return cents;
}

public int rateService(Order order, int currentTurn) {
    return satisfaction;
}

Once Customer is abstract, any existing new Customer(id) calls will need to instantiate a concrete subclass. RegularCustomer can return 8 from waitingLoss() to preserve the Stage 1 waiting penalty.


You:
wait rateService() should return a reputation level, not the customer's satisfaction. We have GOOD, NEUTRAL and BAD reputation, which affect the waiting penalty by -2, 0 and +2 respectively.
Since waitingLoss() is abstract, each subclass will implement its own base penalty, and the Customer class handles how the reputation will affect the level of satisfaction. This way we can use polymorphism instead of checking which customer type it is.
Can you update the methods accordingly? The final penalty should be at least 1, and satisfaction should not go below 0. Also explain how to represent the 3 reputation levels in Java.


ChatGPT:
Use a Java enum for the three reputation levels. Each level stores its adjustment to the waiting penalty.
Reputation.java
public enum Reputation {
    GOOD(-2),
    NEUTRAL(0),
    BAD(2);

    private final int waitingAdjustment;

    Reputation(int waitingAdjustment) {
        this.waitingAdjustment = waitingAdjustment;
    }

    public int getWaitingAdjustment() {
        return waitingAdjustment;
    }
}

Customer.java
Keep waitingLoss() abstract and update the waiting and rating methods:
protected abstract int waitingLoss();

public void loseSatisfactionForWaiting() {
    loseSatisfactionForWaiting(Reputation.NEUTRAL);
}

public void loseSatisfactionForWaiting(Reputation reputation) {
    if (status == CustomerStatus.WAITING
            || status == CustomerStatus.SEATED
            || status == CustomerStatus.ORDERED) {
        int penalty = Math.max(1, waitingLoss() + reputation.getWaitingAdjustment());
        satisfaction = Math.max(0, satisfaction - penalty);
    }
}

public Reputation rateService(Order order, int currentTurn) {
    return Reputation.NEUTRAL;
}

Each subclass supplies its base penalty through waitingLoss(). Customer applies the reputation adjustment and both minimums. The default rateService() returns NEUTRAL until a subclass defines its own rating rules.
In Stage 2, have Game pass the restaurant’s current reputation to customer.loseSatisfactionForWaiting(reputation). Existing no-argument calls still apply the base penalty.


You:
ok now lets implement RegularCustomer and VIPCustomer as subclasses of Customer.
RegularCustomer should lose 8 satisfaction points per turn, while VIPCustomer only loses 5 and gets a 10% discount on their bill.
Since both inherit from Customer, they should override waitingLoss() to return their respective penalties. VIPCustomer should also override applyDiscount(int cents), while RegularCustomer can just inherit the default implementation.
Keep the constructors simple and reuse the Customer constructor with super(id).


ChatGPT:
RegularCustomer.java
public class RegularCustomer extends Customer {
    public RegularCustomer(int id) {
        super(id);
    }

    @Override
    protected int waitingLoss() {
        return 8;
    }
}

VIPCustomer.java
public class VIPCustomer extends Customer {
    public VIPCustomer(int id) {
        super(id);
    }

    @Override
    protected int waitingLoss() {
        return 5;
    }

    @Override
    public int applyDiscount(int cents) {
        return (int) Math.round(cents * 0.90);
    }
}

The VIP pays 90% of the bill, rounded to the nearest cent. Customer applies the reputation adjustment to both base waiting penalties, and RegularCustomer inherits the full-price billing behaviour.


You:
Now lets implement CriticCustomer.
CriticCustomer should lose 12 satisfaction points per turn, and unlike regular customers, they always choose the most expensive item on the menu.
They should also rate the restaurant based on how long their order takes to be served. If the delay is 1 turn or less, the rating is GOOD, 2 turns is NEUTRAL, and 3 or more turns is BAD.
Use inheritance from Customer and override waitingLoss(), chooseItem() and rateService(). Do not change the original Customer class. Use the order's ready and served turns to calculate the delay.


ChatGPT:
CriticCustomer.java
public class CriticCustomer extends Customer {
    public CriticCustomer(int id) {
        super(id);
    }

    @Override
    protected int waitingLoss() {
        return 12;
    }

    @Override
    public MenuItem chooseItem(Menu menu) {
        if (menu == null || menu.getItems().isEmpty()) {
            throw new IllegalArgumentException("The menu must contain at least one item.");
        }

        MenuItem mostExpensive = menu.getItem(0);
        for (MenuItem item : menu.getItems()) {
            if (item.getPriceCents() > mostExpensive.getPriceCents()) {
                mostExpensive = item;
            }
        }

        return mostExpensive;
    }

    @Override
    public Reputation rateService(Order order, int currentTurn) {
        int readyTurn = order.getReadyTurn();
        int servedTurn = order.getServedTurn();

        if (readyTurn < 0 || servedTurn < readyTurn) {
            return Reputation.NEUTRAL;
        }

        int delay = servedTurn - readyTurn;
        if (delay <= 1) {
            return Reputation.GOOD;
        }
        if (delay == 2) {
            return Reputation.NEUTRAL;
        }
        return Reputation.BAD;
    }
}

Ties go to the first highest-priced item in menu order. The rating assumes Order exposes getReadyTurn() and getServedTurn() and uses -1 for an unset turn. It returns NEUTRAL until both turns form a valid pair. currentTurn stays in the signature so the method overrides Customer.rateService().


You:
Order only stores readyTurn, not servedTurn. The currentTurn passed into rateService() is when the order is served, so we can calculate the delay using currentTurn - order.getReadyTurn(). We don't need another getter in Order.
Can you fix CriticCustomer to use this instead? Also rename Reputation to Rating throughout the code, including the enum and all references to it. Keep the existing behaviour unchanged.


ChatGPT:
CriticCustomer.rateService() now uses currentTurn as the serving turn. The previous version called Order.getServedTurn(), which your Order class doesn’t have.
Rating.java
Rename Reputation.java to Rating.java and use this enum:
public enum Rating {
    GOOD(-2),
    NEUTRAL(0),
    BAD(2);

    private final int waitingAdjustment;

    Rating(int waitingAdjustment) {
        this.waitingAdjustment = waitingAdjustment;
    }

    public int getWaitingAdjustment() {
        return waitingAdjustment;
    }
}

Customer.java
public void loseSatisfactionForWaiting() {
    loseSatisfactionForWaiting(Rating.NEUTRAL);
}

public void loseSatisfactionForWaiting(Rating rating) {
    if (status == CustomerStatus.WAITING
            || status == CustomerStatus.SEATED
            || status == CustomerStatus.ORDERED) {
        int penalty = Math.max(1, waitingLoss() + rating.getWaitingAdjustment());
        satisfaction = Math.max(0, satisfaction - penalty);
    }
}

public Rating rateService(Order order, int currentTurn) {
    return Rating.NEUTRAL;
}

CriticCustomer.java
@Override
public Rating rateService(Order order, int currentTurn) {
    int readyTurn = order.getReadyTurn();

    if (readyTurn < 0 || currentTurn < readyTurn) {
        return Rating.NEUTRAL;
    }

    int delay = currentTurn - readyTurn;
    if (delay <= 1) {
        return Rating.GOOD;
    }
    if (delay == 2) {
        return Rating.NEUTRAL;
    }
    return Rating.BAD;
}

Update any remaining Reputation type references, imports, and enum constants in Game.java and other callers to use Rating.


You:
I think Rating and Reputation should be separate, since they represent different things.
Rating is the feedback given by a customer after being served, while Reputation is the restaurant's overall reputation which affects waiting satisfaction.
Can you redo the code so:

Rating has NONE, GOOD, NEUTRAL and BAD. Customer.rateService() returns Rating.NONE by default, since regular and VIP customers don't give ratings.
Reputation has GOOD (-2), NEUTRAL (0) and BAD (+2), with an adjustLoss(int base) method that applies the adjustment and ensures the penalty is at least 1.
Customer.loseSatisfactionForWaiting(Reputation r) uses r.adjustLoss(waitingLoss()) to calculate the penalty.CriticCustomer.rateService() still returns a Rating based on currentTurn - order.getReadyTurn().

Keep the subclasses and existing customer behaviour unchanged. We should use polymorphism so each subclass provides its own waitingLoss() without checking the type of customer


ChatGPT:
Rating represents feedback from one customer. Reputation represents the restaurant’s overall standing and adjusts how much satisfaction customers lose while waiting.
Rating.java
public enum Rating {
    NONE,
    GOOD,
    NEUTRAL,
    BAD
}

Reputation.java
public enum Reputation {
    GOOD(-2),
    NEUTRAL(0),
    BAD(2);

    private final int waitingAdjustment;

    Reputation(int waitingAdjustment) {
        this.waitingAdjustment = waitingAdjustment;
    }

    public int adjustLoss(int base) {
        return Math.max(1, base + waitingAdjustment);
    }
}

Customer.java
Keep waitingLoss() abstract and replace the waiting and rating methods with these versions.
protected abstract int waitingLoss();

public void loseSatisfactionForWaiting() {
    loseSatisfactionForWaiting(Reputation.NEUTRAL);
}

public void loseSatisfactionForWaiting(Reputation r) {
    if (status == CustomerStatus.WAITING
            || status == CustomerStatus.SEATED
            || status == CustomerStatus.ORDERED) {
        int penalty = r.adjustLoss(waitingLoss());
        satisfaction = Math.max(0, satisfaction - penalty);
    }
}

public Rating rateService(Order order, int currentTurn) {
    return Rating.NONE;
}

CriticCustomer.java
Keep the existing override, including its NEUTRAL fallback for invalid turn data.
@Override
public Rating rateService(Order order, int currentTurn) {
    int readyTurn = order.getReadyTurn();

    if (readyTurn < 0 || currentTurn < readyTurn) {
        return Rating.NEUTRAL;
    }

    int delay = currentTurn - readyTurn;
    if (delay <= 1) {
        return Rating.GOOD;
    }
    if (delay == 2) {
        return Rating.NEUTRAL;
    }
    return Rating.BAD;
}

Java dispatches waitingLoss() to the customer’s subclass, giving base penalties of 8, 5, and 12 for regular, VIP, and critic customers respectively. When wiring this into Game, pass the restaurant’s Reputation to the waiting method and skip Rating.NONE when collecting feedback.


You:
For the 10% discount in VIPCustomer, use integers instead of Math.round()
Change applyDiscount(int cents) to return cents * 90 / 100.


ChatGPT:
Replace applyDiscount() in VIPCustomer.java with:
@Override
public int applyDiscount(int cents) {
    return cents * 90 / 100;
}



You:
🖼️ [Uploaded image: Restaurant_Rush_UML_Stage3_design_preview.png — PNG, 525 KB]
Now we need to implement Stage 3 of RestaurantRush based on the updated UML.
We are adding a Host that can seat customers, ComboMeal which allows two menu items to be ordered together, and HappyHour which gives customers a discount.
We also need to update the existing classes to support these features.
Keep the Stage 1 and Stage 2 functionality the same, including the customer subclasses, VIP discounts, Critic ratings and reputation system. Follow the UML for the class relationships and method signatures.
Let's implement the changes one part at a time, starting with the staff classes.


ChatGPT:
I’ll check how seating, cooking, and payment work in the existing code before applying the Stage 3 staff changes.


ChatGPT:
Move seat(Customer c, Table t) from Waiter into an abstract Seater class. Host and Waiter then inherit the same seating rules. Each staff instance has its own action limit, so the Host can seat a customer and the Waiter can take that customer’s order in the same turn.
Seater.java
public abstract class Seater extends Staff {
    public void seat(Customer c, Table t) {
        requireAvailable();

        if (c == null || t == null) {
            throw new IllegalMoveException("Invalid customer or table");
        }
        if (c.getStatus() != CustomerStatus.WAITING) {
            throw new IllegalMoveException("Customer is not waiting");
        }
        if (!t.isFree()) {
            throw new IllegalMoveException("Table is occupied");
        }

        c.seatAt(t);
        t.seat(c);
        markActed();
    }
}

Host.java
public class Host extends Seater {
}

Waiter.java
Change the declaration to public class Waiter extends Seater and remove the old seat() method. Update takeOrder() to record the turn on which the order was placed.
public Order takeOrder(Customer c, Menu m, int turn) {
    requireAvailable();

    if (c == null || m == null) {
        throw new IllegalMoveException("Invalid customer or menu");
    }
    if (c.getStatus() != CustomerStatus.SEATED) {
        throw new IllegalMoveException("Customer is not seated");
    }
    if (c.getOrder() != null) {
        throw new IllegalMoveException("Customer already has an order");
    }

    Order order = new Order(c.chooseItem(m), c, turn);
    c.placeOrder(order);
    markActed();
    return order;
}

Keep serve(Order o, int turn) in Waiter. It marks the order served before calling customer.markServed(turn). Game calls rateService() after a successful serve and handles any change to restaurant reputation.
Cashier.java
Apply the customer’s discount first so VIP pricing still works through applyDiscount(). Then apply Happy Hour based on when the order was placed.
public class Cashier extends Staff {
    public int collect(Customer c, HappyHour hh) {
        requireAvailable();

        if (c == null) {
            throw new IllegalMoveException("No customer selected");
        }
        if (c.getStatus() != CustomerStatus.READY_TO_PAY) {
            throw new IllegalMoveException("Customer is not ready to pay");
        }

        Order order = c.getOrder();
        if (order == null || order.getStatus() != OrderStatus.SERVED) {
            throw new IllegalMoveException("Customer needs a served order");
        }

        int customerPrice = c.applyDiscount(order.basePriceCents());
        int bill = hh.discountOn(order.getPlacedTurn(), customerPrice);

        Table table = c.getTable();
        order.markPaid();
        c.markPaid();
        if (table != null) {
            table.clear();
        }

        markActed();
        return bill;
    }
}

Staff provides resetTurn(), requireAvailable(), and markActed(). Chef extends Staff directly and uses its existing Stage 2 cook(Order o, int turn) method to call o.advancePrep(turn).
When wiring this into Game, add the Host to the staff reset list, remove customers from the waiting queue after successful seating by either role, and pass turn to takeOrder() and the game’s HappyHour instance to collect(). I checked the signatures and call order against your RestaurantRush archive. javac was unavailable, so compilation is unverified.


You:
For Stage 3, both Host and Waiter need to be able to seat customers. Instead of duplicating the seat() method in both classes, we should introduce a Seater class that extends Staff.
Move the existing seat(Customer c, Table t) method from Waiter into Seater, including all its validation checks.
Then make both Host and Waiter extend Seater. Host should only be responsible for seating customers, while Waiter should retain its takeOrder() and serve() methods.
This way, both classes inherit the same seating behaviour through Seater, and we don't need to duplicate code.
Implement these changes and keep the existing staff action restrictions unchanged.


ChatGPT:
Seater.java
Move Waiter.seat() into this abstract class, preserving its validation and action checks.
public abstract class Seater extends Staff {
    public void seat(Customer c, Table t) {
        requireAvailable();

        if (c == null || t == null) {
            throw new IllegalMoveException("Invalid customer or table");
        }
        if (c.getStatus() != CustomerStatus.WAITING) {
            throw new IllegalMoveException("Customer is not waiting");
        }
        if (!t.isFree()) {
            throw new IllegalMoveException("Table is occupied");
        }

        c.seatAt(t);
        t.seat(c);
        markActed();
    }
}

Host.java
public class Host extends Seater {
}

Waiter.java
Change the declaration to public class Waiter extends Seater and delete its local seat() method. Keep takeOrder() and serve() as they are.
markActed() runs only after successful seating. Seating uses the acting staff member’s action for that turn, so the Host can seat a customer and the Waiter can still take that customer’s order in the same turn.


You:
Next implement ComboMeal.
ComboMeal should extend MenuItem, since we want it to be treated like any other menu item when customers place orders.
It should store two MenuItem objects, first and second. The combo price should be 90% of the sum of their individual prices, calculated using integers since prices are in cents.
The preparation units should be the sum of the preparation units of both items, since the Chef still needs to prepare both.
Use the existing MenuItem methods where possible rather than adding separate logic for combo orders throughout the game.
Implement ComboMeal.java and make the necessary changes to MenuItem if required.


ChatGPT:
I’ll check the existing MenuItem constructor and how Order reads prices and prep units.


ChatGPT:
ComboMeal.java
public class ComboMeal extends MenuItem {
    private static final int COMBO_PERCENT = 90;

    private final MenuItem first;
    private final MenuItem second;

    public ComboMeal(MenuItem first, MenuItem second) {
        super(comboName(first, second),
                comboPrice(first, second),
                Math.addExact(first.getPrepUnits(), second.getPrepUnits()));
        this.first = first;
        this.second = second;
    }

    public MenuItem getFirst() {
        return first;
    }

    public MenuItem getSecond() {
        return second;
    }

    private static String comboName(MenuItem first, MenuItem second) {
        if (first == null || second == null) {
            throw new IllegalArgumentException("A combo needs two menu items");
        }
        return first.getName() + " + " + second.getName() + " combo";
    }

    private static int comboPrice(MenuItem first, MenuItem second) {
        long combinedCents = (long) first.getPriceCents() + second.getPriceCents();
        return Math.toIntExact((combinedCents * COMBO_PERCENT + 50) / 100);
    }
}

The + 50 preserves the rounding rule in your RestaurantRush reference implementation. It rounds to the nearest cent, with half cents rounded up, using integer arithmetic.
The existing MenuItem constructor accepts the calculated name, price, and prep units. Once you add a ComboMeal to the menu, Order reads its price and total prep units through the inherited getPriceCents() and getPrepUnits() methods.


You:
Now implement HappyHour.
HappyHour should store the turn when it was activated using activatedTurn, with -1 indicating that it hasn't been used.
Use activate(int turn) to record the activation turn, and isUsed() to check whether HappyHour has already been activated.
isActive(int turn) should determine whether HappyHour is currently active, while remainingTurns(int turn) returns the number of turns remaining.
We also need wasActiveOn(int turn) to check whether HappyHour was active on a particular turn, since customers may pay after HappyHour has ended.
discountOn(int turn, int cents) should apply the HappyHour discount only if it was active on that turn.
Keep all the HappyHour logic within this class rather than checking the activation turn manually in Game or Cashier. Use integer arithmetic for the discount calculations.


ChatGPT:
HappyHour.java
This uses the existing Stage 3 rules. Happy Hour can be activated once, lasts for the activation turn and the following turn, and gives 20% off eligible orders.
public class HappyHour {
    private static final int DURATION_TURNS = 2;
    private static final int PAY_PERCENT = 80;

    private int activatedTurn = -1;

    public void activate(int turn) {
        if (isUsed()) {
            throw new IllegalMoveException("Happy Hour can only be activated once.");
        }
        if (turn < 1) {
            throw new IllegalMoveException("Turn must be positive.");
        }

        activatedTurn = turn;
    }

    public boolean isUsed() {
        return activatedTurn != -1;
    }

    public boolean isActive(int turn) {
        return isUsed()
                && turn >= activatedTurn
                && turn - activatedTurn < DURATION_TURNS;
    }

    public int remainingTurns(int turn) {
        if (!isActive(turn)) {
            return 0;
        }
        return DURATION_TURNS - (turn - activatedTurn);
    }

    public boolean wasActiveOn(int turn) {
        return isActive(turn);
    }

    public int discountOn(int turn, int cents) {
        if (wasActiveOn(turn)) {
            return (int) ((cents * (long) PAY_PERCENT + 50) / 100);
        }
        return cents;
    }
}

remainingTurns() counts the current turn, so it returns 2 on activation and 1 on the next turn. The discount uses integer arithmetic and rounds to the nearest cent, with half cents rounded up.
At payment, Cashier passes order.getPlacedTurn() and the price after c.applyDiscount(...) to discountOn(). An order placed during Happy Hour still qualifies if the customer pays later.


You:
Now update Order.java.
In Stage 3, Order needs a placedTurn field to record when the customer placed the order. Change the constructor to Order(MenuItem item, Customer customer, int placedTurn).
Keep readyTurn separate from placedTurn. readyTurn is still used by CriticCustomer to determine the service rating, while placedTurn is needed to determine whether the order qualifies for HappyHour.
The order should still go through the same TAKEN, READY, SERVED and PAID states, and the existing preparation and cancellation behaviour should remain unchanged.
Add the relevant getters from the UML and update the constructor calls where necessary.


ChatGPT:
I’ll check where Order is constructed and where the game uses each turn value.


ChatGPT:
In Order.java, add placedTurn beside the existing readyTurn field. The constructor records when the order was placed, and advancePrep(turn) still records readyTurn only when the order first becomes READY.
private final int placedTurn;
private int readyTurn = -1;

public Order(MenuItem item, Customer customer, int placedTurn) {
    if (item == null || customer == null) {
        throw new IllegalArgumentException("An order needs an item and a customer");
    }
    if (placedTurn < 1) {
        throw new IllegalArgumentException("An order must be placed on turn 1 or later");
    }

    this.item = item;
    this.customer = customer;
    this.placedTurn = placedTurn;
    this.status = OrderStatus.TAKEN;
}

public int getPlacedTurn() {
    return placedTurn;
}

public int getReadyTurn() {
    return readyTurn;
}

The other UML getters are already present in the RestaurantRush archive: getItem(), getCustomer(), getStatus(), and getUnitsDone(). Keep the existing preparation, serving, payment, and cancellation methods.
Constructor calls

Waiter.takeOrder() already uses new Order(item, c, turn) in the archive, and Game already passes the current turn to the waiter.
CriticRatingCheck.java still uses new Order(pasta, critic). Change it to new Order(pasta, critic, 3), so the order is placed before the test starts cooking on turn 4. You can then remove the deprecated two-argument constructor.

CriticCustomer uses getReadyTurn() to measure the delay before serving. Cashier uses getPlacedTurn() to check Happy Hour eligibility, even when payment happens later.


You:
Next update the payment logic for Stage 3.
We now have three possible price adjustments:

ComboMeal already has a reduced price of 90% of its two items.
VIPCustomer applies its 10% discount through applyDiscount(int cents).
HappyHour applies its discount based on the order's placedTurn.

The discounts should be applied in that order. ComboMeal determines the base price first, then the customer discount, followed by HappyHour if it was active when the order was placed.
Update Cashier.collect() to accept HappyHour as shown in the UML.
Cashier should use the existing customer polymorphism for the VIP discount instead of checking whether the customer is a VIP. It should also use HappyHour.discountOn() rather than calculating the discount itself.
Make sure the final bill is calculated using integers and the existing payment and table clearing behaviour remains the same.


ChatGPT:
Replace collect() in Cashier.java with the Stage 3 signature and billing logic.
public int collect(Customer c, HappyHour hh) {
    requireAvailable();

    if (c == null) {
        throw new IllegalMoveException("No customer selected");
    }
    if (c.getStatus() != CustomerStatus.READY_TO_PAY) {
        throw new IllegalMoveException("Customer is not ready to pay");
    }

    Order order = c.getOrder();
    if (order == null || order.getStatus() != OrderStatus.SERVED) {
        throw new IllegalMoveException("Customer needs a served order");
    }

    int basePrice = order.basePriceCents();
    int customerPrice = c.applyDiscount(basePrice);
    int bill = hh.discountOn(order.getPlacedTurn(), customerPrice);

    Table table = c.getTable();
    order.markPaid();
    c.markPaid();
    if (table != null) {
        table.clear();
    }

    markActed();
    return bill;
}

basePriceCents() already includes the combo reduction when the order contains a ComboMeal. Java dispatches applyDiscount() to the customer’s implementation, then HappyHour checks the order’s placement turn. Every step uses integer cents.
In Game.java, pass the game’s HappyHour instance when collecting payment:
revenueCents += cashier.collect(payer, happyHour);



You:
Now implement HostChoice and update the existing choice classes according to the UML.
HostChoice should represent either seating a customer or waiting. The Host should only be able to seat a waiting customer at a free table.
The Waiter should still be able to seat customers, take orders and serve food, while the Chef continues preparing orders.
Each staff member should only perform one action per turn. Since Host and Waiter both extend Seater, they should use the same seating validation rather than implementing separate seating rules.
Keep the existing IllegalMoveException handling so invalid actions can be retried without using up the staff member's turn.


ChatGPT:
I’ll check the existing choice classes and how Game retries invalid staff actions.


ChatGPT:
HostChoice, WaiterChoice, and ChefChoice describe the selected action. Game dispatches it, and the staff class validates and performs it.
HostChoice.java
public record HostChoice(Customer customer, Table table) {
    public static HostChoice seat(Customer c, Table t) {
        if (c == null) {
            throw new IllegalMoveException("No customer selected");
        }
        return new HostChoice(c, t);
    }

    public static HostChoice waitTurn() {
        return new HostChoice(null, null);
    }
}

WaiterChoice.java
public record WaiterChoice(Kind kind, Customer customer, Table table) {
    public enum Kind {
        SEAT, TAKE_ORDER, SERVE, WAIT
    }

    public static WaiterChoice seat(Customer c, Table t) {
        return new WaiterChoice(Kind.SEAT, c, t);
    }

    public static WaiterChoice takeOrder(Customer c) {
        return new WaiterChoice(Kind.TAKE_ORDER, c, null);
    }

    public static WaiterChoice serve(Customer c) {
        return new WaiterChoice(Kind.SERVE, c, null);
    }

    public static WaiterChoice waitTurn() {
        return new WaiterChoice(Kind.WAIT, null, null);
    }
}

ChefChoice.java
public record ChefChoice(Order order) {
    public static ChefChoice cook(Order o) {
        if (o == null) {
            throw new IllegalMoveException("No order selected");
        }
        return new ChefChoice(o);
    }

    public static ChefChoice waitTurn() {
        return new ChefChoice(null);
    }
}

The Host and Chef factories reject missing selections because their records use null to represent an intentional wait.
Game.java
Use the existing retry handler around both reading and applying each choice. That way, an IllegalMoveException from either step prompts the same staff member again.
private void applyManagerChoices() {
    untilLegal(() -> applyHostChoice(cli.readHostChoice(this)));
    untilLegal(() -> applyWaiterChoice(cli.readWaiterChoice(this)));
    untilLegal(() -> applyChefChoice(cli.readChefChoice(this)));
}

private void applyHostChoice(HostChoice choice) {
    Customer c = choice.customer();
    if (c != null || choice.table() != null) {
        host.seat(c, choice.table());
        waitingQueue.remove(c);
    }
}

private void applyChefChoice(ChefChoice choice) {
    if (choice.order() != null) {
        chef.cook(choice.order(), turn);
    }
}

In the existing waiter switch, SEAT calls waiter.seat(c, choice.table()) and removes the customer from the queue after seating succeeds. Both seating paths use Seater.seat() to check that the customer is waiting and the table is free. Each staff member calls markActed() only after a successful action, and Game resets all staff, including the Host, at the start of each turn.


You:
Now update Game.java to integrate Stage 3.
Add Host and HappyHour as fields, and include Host in the staff list so its action is reset each turn.
Update the manager choices so Host, Waiter and Chef each get their own action. Host and Waiter can both seat customers, but each should still only act once per turn.
Add offerHappyHour() to handle the activation option, and applyHappyHourRecovery() for the satisfaction recovery mechanic shown in the UML.
Update the order placement logic to pass the current turn into Waiter.takeOrder(), so Order can record placedTurn.
When collecting payment, pass HappyHour to Cashier so it can determine the correct discount.
Keep the existing customer arrivals on turns 1, 4, 7, 10, 13 and 16, with Regular, VIP and Critic customers. The reputation system and waiting satisfaction penalties should also remain unchanged.
Follow the Stage 3 UML method signatures and preserve the existing 18-turn game loop and victory conditions.


ChatGPT:
I’ll check the archived Game.java for the turn order, customer arrivals, and victory conditions before putting the Stage 3 changes together.


ChatGPT:
The archived Game.java already contains the Stage 3 wiring. These are the changes to carry into your working copy. They preserve the 18-turn loop and the existing victory thresholds of 4 paid customers, $45.00 in revenue, and an average paid-customer satisfaction of 60.
Fields and turn order
Add Host and HappyHour, and replace the existing staff list so all four staff members reset each turn.
private final Host host = new Host();
private final HappyHour happyHour = new HappyHour();
private final List<Staff> staff = List.of(host, waiter, chef, cashier);

public Host getHost() {
    return host;
}

public HappyHour getHappyHour() {
    return happyHour;
}

Declare staff after the individual staff fields. In runTurn(), offer Happy Hour after the arrival and start-of-turn display, then apply recovery before staff act.
public void runTurn() {
    if (isOver()) {
        throw new IllegalStateException("Game is over after turn " + maxTurns);
    }

    turn++;
    staff.forEach(Staff::resetTurn);
    processArrival();
    cli.displayTurnStart(this);
    offerHappyHour();
    applyHappyHourRecovery();
    applyManagerChoices();
    processEatingAndPayment();
    processWaitingAndAbandonment();
    cli.displayTurnEnd(this);
}

Happy Hour
Use the existing Stage 3 recovery amount of 15 satisfaction points per active turn. Customer.recoverSatisfaction() caps satisfaction at 100.
private void offerHappyHour() {
    if (!happyHour.isUsed() && cli.askActivateHappyHour(this)) {
        happyHour.activate(turn);
        cli.displayHappyHour(this);
    }
}

private void applyHappyHourRecovery() {
    if (!happyHour.isActive(turn)) {
        return;
    }

    for (Customer c : customers) {
        if (isWaitingForService(c)) {
            c.recoverSatisfaction(15);
            cli.displayRecovery(c, c.getSatisfaction());
        }
    }
}

private boolean isWaitingForService(Customer c) {
    CustomerStatus status = c.getStatus();
    return status == CustomerStatus.WAITING
            || status == CustomerStatus.SEATED
            || status == CustomerStatus.ORDERED;
}

Use the same isWaitingForService() predicate in processWaitingAndAbandonment(). That step still calls c.loseSatisfactionForWaiting(reputation) and checks c.hasGivenUp() at the end of the turn.
Staff actions
Wrap each role’s read-and-apply step in the existing untilLegal() retry handler. A rejected action prompts the same role again. A successful action or a choice to wait moves on to the next role.
private void applyManagerChoices() {
    untilLegal(() -> applyHostChoice(cli.readHostChoice(this)));
    untilLegal(() -> applyWaiterChoice(cli.readWaiterChoice(this)));
    untilLegal(() -> applyChefChoice(cli.readChefChoice(this)));
}

private void applyHostChoice(HostChoice choice) {
    Customer c = choice.customer();
    if (c != null || choice.table() != null) {
        host.seat(c, choice.table());
        waitingQueue.remove(c);
    }
}

private void applyWaiterChoice(WaiterChoice choice) {
    Customer c = choice.customer();

    switch (choice.kind()) {
        case SEAT -> {
            waiter.seat(c, choice.table());
            waitingQueue.remove(c);
        }
        case TAKE_ORDER -> orders.add(waiter.takeOrder(c, menu, turn));
        case SERVE -> {
            Order order = c == null ? null : c.getOrder();
            waiter.serve(order, turn);

            Rating rating = c.rateService(order, turn);
            if (rating != Rating.NONE) {
                reputation = Reputation.valueOf(rating.name());
                cli.displayReview(c, rating, reputation);
            }
        }
        case WAIT -> { }
    }
}

private void applyChefChoice(ChefChoice choice) {
    if (choice.order() != null) {
        chef.cook(choice.order(), turn);
    }
}

Both seating paths use Seater.seat() and remove the customer from the queue only after seating succeeds. Because the Host and Waiter have separate action limits, the Host can seat a customer and the Waiter can take their order on the same turn.
Payment, arrivals, and menu
In the existing payment block, pass the game’s HappyHour instance to the cashier.
Customer payer = nextCustomerToPay();
if (payer != null) {
    revenueCents += cashier.collect(payer, happyHour);
    paidSatisfactions.add(payer.getSatisfaction());
}

Keep the arrival sequence as Regular on turn 1, VIP on turn 4, Critic on turn 7, and Regular on turns 10, 13, and 16. The archived Stage 3 menu puts Salad, Burger, and Pasta first, then appends the Salad–Pasta and Burger–Pasta combos as ordinary MenuItem entries.
I checked the method signatures and call order against the archive. Compilation remains unverified because javac was unavailable.


You:
Now update RestaurantCLI for Stage 3.
Add readHostChoice(Game g) so the player can choose whether the Host seats a customer or waits.
Add askActivateHappyHour(Game g) so the player can decide whether to activate HappyHour when it is available.
The CLI should also display the HappyHour status, including whether it has been used and how many turns are remaining.
Keep the existing customer, table, kitchen, revenue and satisfaction displays. We should also continue displaying the customer types and restaurant reputation from Stage 2.
Use displayReview() when a Critic rates the service, and displayRecovery() when customers regain satisfaction through HappyHour recovery.
Keep the terminal formatting similar to the existing version, and don't move game logic into RestaurantCLI.


ChatGPT:
I’ll check the existing CLI prompts and turn displays so the Stage 3 additions fit the terminal layout.


ChatGPT:
The archived RestaurantCLI.java already has the Stage 3 prompts and displays. If you’re applying the changes to your Stage 2 copy, add the methods below and reuse your existing input and formatting helpers.
Host and Happy Hour prompts
public HostChoice readHostChoice(Game g) {
    while (true) {
        System.out.println();
        System.out.println("  HOST - choose one task");
        System.out.println("    1) Seat a waiting customer");
        System.out.println("    0) Wait");

        Integer pick = readInt("  > ");
        if (pick == null || pick == 0) {
            return HostChoice.waitTurn();
        }
        if (pick != 1) {
            System.out.println("    Please enter 0 or 1.");
            continue;
        }

        System.out.println("    Waiting: " + customerIds(g.getWaitingQueue())
                + "   |   Free tables: " + tableIds(g.getFreeTables()));

        Customer c = askCustomer(g, "    Customer id (blank to go back): ");
        if (c == null) {
            continue;
        }

        Table t = askTable(g, "    Table id (blank to go back): ");
        if (t != null) {
            return HostChoice.seat(c, t);
        }
    }
}

public boolean askActivateHappyHour(Game g) {
    while (true) {
        System.out.print("  Start Happy Hour on turn " + g.getTurn() + "? (y/n) > ");
        if (!in.hasNextLine()) {
            return false;
        }

        String answer = in.nextLine().trim();
        if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
            return true;
        }
        if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
            return false;
        }
        System.out.println("    Please enter y or n.");
    }
}

The Host prompt shows the waiting queue and free tables. Seater.seat() validates the selected customer and table, and Game handles any retry. Game.offerHappyHour() decides when to call the activation prompt and activates Happy Hour if the player agrees.
Status, review, and recovery displays
private static String happyHourStatus(Game g) {
    HappyHour hh = g.getHappyHour();
    int remaining = hh.remainingTurns(g.getTurn());

    if (remaining > 0) {
        return "active, " + remaining + " turn(s) remaining";
    }
    return hh.isUsed() ? "used, 0 turns remaining" : "available (unused)";
}

public void displayHappyHour(Game g) {
    System.out.println("  Happy Hour: " + happyHourStatus(g));
}

public void displayReview(Customer c, Rating rating, Reputation reputation) {
    System.out.println();
    System.out.println("  REVIEW: " + label(c) + " rates the service " + rating + ".");
    System.out.println("    Reputation is now " + reputation + ".");
}

public void displayRecovery(Customer c, int newValue) {
    System.out.println("  Happy Hour: " + label(c)
            + " recovers to satisfaction " + newValue);
}

In displayTurnStart(), extend the existing reputation line beside the revenue and satisfaction summary.
System.out.println("  Reputation " + g.getReputation()
        + "   |   Happy Hour " + happyHourStatus(g));

Keep using label(c) in the waiting, table, and kitchen sections so customer types appear there as well as in reviews and recovery messages. Game calls displayHappyHour() after activation, displayReview() after a rating other than NONE, and displayRecovery() after applying the satisfaction recovery.


You:
Wait, I just realised that most of the Stage 3 work we’ve been writing prompts for was actually assigned to my teammates. My part was only HappyHour.java, the satisfaction recovery method in Customer.java, and the related tests.
Can we ignore all the other Stage 3 prompts you’ve generated so far and focus only on my part from now on? No need to undo anything since we haven’t actually changed any files.


ChatGPT:
Yes. From here on, we’ll focus on HappyHour.java, recoverSatisfaction(int amount) in Customer.java, and the tests for both. We’ll treat the earlier Stage 3 prompts as out of scope. There’s nothing to undo.


You:
Ok now lets work on HappyHour.java for Stage 3.
The HappyHour class should keep track of when it was activated, with activatedTurn initially set to -1. It can only be activated once throughout the game, and should last for 2 turns, the turn it was activated and the next turn.
Add activate(int turn), isUsed(), isActive(int turn), remainingTurns(int turn), wasActiveOn(int turn) and discountOn(int placedTurn, int cents).
For activate(), make sure it throws IllegalMoveException if Happy Hour has already been used or if the turn is less than 1. remainingTurns() should return 2 on the activation turn, 1 on the next turn and 0 otherwise.
For the discount, orders placed during either of the 2 active turns should get 20% off. Use (cents * 80 + 50) / 100 so the final price is rounded to the nearest cent. Orders placed outside these turns should just return the original price.
Keep all the Happy Hour rules within this class, the Game class will handle when to activate it.


ChatGPT:
HappyHour.java
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

If Happy Hour starts on turn 4, remainingTurns() returns 2 on turn 4, 1 on turn 5, and 0 on turn 6. An order placed on turn 5 still gets the discount if the customer pays later. The 80L keeps the calculation in integer arithmetic and prevents overflow during multiplication.


You:
Next update Customer.java to support the satisfaction recovery during Happy Hour.
Add a recoverSatisfaction(int amount) method. It should increase the customer’s satisfaction by the given amount, but cap it at 100 so it cant go above the starting satisfaction.
Also, recovery should only apply to customers who are still waiting for service, so those with WAITING, SEATED or ORDERED status. If they’ve already been served, paid or left, their satisfaction shouldnt change.
Make sure negative amounts throw an IllegalArgumentException.
The rest of Customer.java should stay the same, especially the existing waiting penalties, status transitions and Stage 2 behaviour. We dont need to change any other classes for this part.


ChatGPT:
Add this method to Customer.java:
public void recoverSatisfaction(int amount) {
    if (amount < 0) {
        throw new IllegalArgumentException("Recovery amount cannot be negative.");
    }

    if (status == CustomerStatus.WAITING
            || status == CustomerStatus.SEATED
            || status == CustomerStatus.ORDERED) {
        satisfaction += Math.min(amount, 100 - satisfaction);
    }
}

The method adds at most the points needed to reach 100. It rejects negative amounts in every customer state.


You:
Now lets add the tests for the Stage 3 features we just implemented.
For HappyHour, test that it starts unused, can only be activated once, and is active for exactly 2 turns. Check remainingTurns() as well, including before activation and after it ends. Also test that invalid activation turns throw the correct exception.
For the discount, check that orders placed during either active turn get 20% off, including the rounding, while orders outside Happy Hour dont get any discount.
For Customer recovery, test that satisfaction increases correctly and is capped at 100. Also check that recovery works for WAITING, SEATED and ORDERED customers, but not for customers who have already been served, paid or left. Negative recovery amounts should throw an exception too.
Follow the existing testing style in the project, and keep the tests focused on my Stage 3 changes. No need to modify Game.java or the other Stage 3 classes.


ChatGPT:
Add these cases to test/HappyHourTest.java. This follows the project’s existing test style: a main() method, a check() helper, and AssertionError on failure.
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

        Order order = new Order(new MenuItem("Salad", 900, 1), c, 1);
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
        check(c.getSatisfaction() == before + 3, "Recovery applies in " + expectedStatus);
    }

    private static void testRecoveryAfterServiceOrDeparture() {
        Customer c = new RegularCustomer(1);
        c.loseSatisfactionForWaiting(Reputation.NEUTRAL);
        c.seatAt(new Table(1));

        Order order = new Order(new MenuItem("Salad", 900, 1), c, 1);
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
        check(c.getSatisfaction() == before, "Recovery has no effect in " + expectedStatus);
    }

    private static void testNegativeRecovery() {
        Customer c = new RegularCustomer(1);
        c.loseSatisfactionForWaiting(Reputation.NEUTRAL);
        int before = c.getSatisfaction();

        expectThrows(IllegalArgumentException.class, () -> c.recoverSatisfaction(-1),
                "Reject negative recovery");
        check(c.getSatisfaction() == before, "Rejected recovery leaves satisfaction unchanged");
    }

    private static void expectThrows(
            Class<? extends RuntimeException> expected, Runnable action, String name) {
        try {
            action.run();
        } catch (RuntimeException e) {
            if (!expected.isInstance(e)) {
                throw new AssertionError(name + " threw " + e.getClass().getSimpleName(), e);
            }
            System.out.println(name + " - PASS");
            return;
        }
        throw new AssertionError(name + " did not throw " + expected.getSimpleName());
    }

    private static void check(boolean condition, String name) {
        if (!condition) {
            throw new AssertionError(name + " - FAILED");
        }
        System.out.println(name + " - PASS");
    }
}

Run from the project root with a JDK. The tests are unrun here because javac was unavailable.
javac -d out src/*.java test/HappyHourTest.java
java -cp out HappyHourTest