# Design Notes

## Ownership

| Owner | Classes |
|-------|---------|
| Winston | Game, Main |
| Darren | Staff, Waiter, Chef, Cashier |
| Myat | Order, Menu, MenuItem, Table, OrderStatus, IllegalMoveException, RestaurantCLI, WaiterChoice, ChefChoice |
| Sadhana | Customer, CustomerStatus |

## Additions not on the Stage 1 UML

| Class (owner) | Addition | Why |
|---------------|----------|-----|
| Game (Winston) | `private static final int VICTORY_MIN_PAID = 4`<br>`VICTORY_MIN_REVENUE_CENTS = 4500`<br>`VICTORY_MIN_AVG_SATISFACTION = 60` | Victory targets from brief §2.1, named instead of magic numbers in `isVictory()`. |
| Game (Winston) | `private void retryUntilValid(Runnable move)` | Single error route: catches `IllegalMoveException`, calls `cli.displayError(reason)` and asks again without using up the staff task (brief §3.4). |
| WaiterChoice (Myat) | `record WaiterChoice(Kind kind, Customer customer, Table table)`, `Kind { SEAT, TAKE_ORDER, SERVE, WAIT }` | Return type of `RestaurantCLI.readWaiterChoice`. The CLI resolves ids to objects. |
| ChefChoice (Myat) | `record ChefChoice(Kind kind, Order order)`, `Kind { COOK, WAIT }` | Return type of `RestaurantCLI.readChefChoice`. |

## Contracts Game relies on

Game passes the manager's choice straight to the staff. Each class guards its own transitions. A staff member that coordinates several objects also checks every condition by itself. Illegal moves throw `IllegalMoveException` (never a `NullPointerException`) before that class changes its own state.

Staff check availability, null arguments, and the relevant status conditions before changing any objects. For actions involving multiple objects, validate all required conditions before the first update so a rejected action leaves the state unchanged. Individual classes also enforce their own status rules.

| Class (owner) | Method | Throws `IllegalMoveException` if |
|---------------|--------|-----------------------------------|
| IllegalMoveException (Myat) | constructor | Never; it must extend `RuntimeException` so it can be thrown without changing the UML signatures. |
| Staff (Darren) | `requireAvailable()` | The staff member already acted this turn. |
| Waiter (Darren) | `seat(c, t)` | Already acted, `c` or `t` is null, `t` is occupied, or `c` is not WAITING. All checked before calling `Table.seat(c)` and `Customer.seatAt(t)`. |
| Waiter (Darren) | `takeOrder(c, m)` | Already acted, `c` or `m` is null, or `c` is not SEATED. All checked before creating the order and calling `Customer.placeOrder(o)`. |
| Waiter (Darren) | `serve(o, turn)` | Already acted, `o` is null (customer has not ordered), or `o` is not READY. All checked before calling `Order.markServed()` and `Customer.markServed(turn)`. |
| Chef (Darren) | `cook(o)` | Already acted, `o` is null, or `o` is not TAKEN. Checked before calling `Order.advancePrep()`. |
| Cashier (Darren) | `collect(c)` | Already acted, `c` is null, or `c` is not READY_TO_PAY. All checked before calling `Customer.markPaid()`, marking the order PAID and freeing the table; returns the bill in cents. |
| Table (Myat) | `seat(c)` | The table is occupied. |
| Customer (Sadhana) | `seatAt(t)` | The customer is not WAITING. |
| Customer (Sadhana) | `placeOrder(o)` | The customer is not SEATED. |
| Customer (Sadhana) | `markPaid()` | The customer is not READY_TO_PAY (`Cashier.collect(c)` already rejects a customer who is not READY_TO_PAY before calling this). |
| Order (Myat) | `markServed()` | The order is not READY. |
| Order (Myat) | `advancePrep()` | The order is not TAKEN. |
| Order (Myat) | `cancel()` | Never from TAKEN or READY (called by `Game.abandon()`). |
| Customer (Sadhana) | `leave()` | Never from WAITING, SEATED or ORDERED (called by `Game.abandon()`). |
