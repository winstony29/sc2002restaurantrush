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

Game passes the manager's choice straight to the staff. Each rule is checked by exactly one class. Illegal moves throw `IllegalMoveException` (never a `NullPointerException`) before that class changes its own state.

Staff only check that they have not acted this turn and that their arguments are not null. Status rules live in the class that owns the state and are reached by delegation.

| Class (owner) | Method | Throws `IllegalMoveException` if |
|---------------|--------|-----------------------------------|
| IllegalMoveException (Myat) | constructor | Never; it must extend `RuntimeException` so it can be thrown without changing the UML signatures. |
| Staff (Darren) | `requireAvailable()` | The staff member already acted this turn. |
| Waiter (Darren) | `seat(c, t)` | `c` or `t` is null. Delegates to `Table.seat(c)` and `Customer.seatAt(t)`. |
| Waiter (Darren) | `takeOrder(c, m)` | `c` or `m` is null. Delegates to `Customer.placeOrder(o)`. |
| Waiter (Darren) | `serve(o, turn)` | `o` is null (customer has not ordered). Delegates to `Order.markServed()`. |
| Chef (Darren) | `cook(o)` | `o` is null. Delegates to `Order.advancePrep()`. |
| Cashier (Darren) | `collect(c)` | `c` is null. Delegates to `Customer.markPaid()`; marks the order PAID, frees the table and returns the bill in cents. |
| Table (Myat) | `seat(c)` | The table is occupied. |
| Customer (Sadhana) | `seatAt(t)` | The customer is not WAITING. |
| Customer (Sadhana) | `placeOrder(o)` | The customer is not SEATED. |
| Customer (Sadhana) | `markPaid()` | The customer is not READY_TO_PAY (so `Cashier.collect(c)` throws through this call). |
| Order (Myat) | `markServed()` | The order is not READY. |
| Order (Myat) | `advancePrep()` | The order is not TAKEN. |
| Order (Myat) | `cancel()` | Never from TAKEN or READY (called by `Game.abandon()`). |
| Customer (Sadhana) | `leave()` | Never from WAITING, SEATED or ORDERED (called by `Game.abandon()`). |
