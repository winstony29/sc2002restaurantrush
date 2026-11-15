# Restaurant Rush

A turn-based restaurant management game for the command line, written in Java for SC2002 Object Oriented Design & Programming.

**Repository:** https://github.com/winstony29/sc2002restaurantrush

## Build and run

Requires Java 17 or later (tested with Java 26). From the repository root:

```bash
javac -d out src/*.java
java -cp out Main
```

## How to play

You are the manager. The game lasts 18 turns with 2 tables. Six customers arrive, on turns 1, 4, 7, 10, 13 and 16.

Each turn you give one task to each staff member by typing the number of an option on the screen:

- **Host** seats a waiting customer at a free table, or waits.
- **Waiter** seats a customer, takes an order, serves a ready order, or waits.
- **Chef** cooks one unit of an order, or waits.
- **Cashier** collects payment from one customer who has finished eating. This happens automatically.

Customers who are still waiting for a table or for food lose satisfaction every turn. A customer whose satisfaction reaches 0 leaves.

| Feature | Rule |
|---|---|
| Menu | Salad $9.00 (1 unit), Burger $15.00 (1 unit), Pasta $18.00 (2 units) |
| Combo meals (Stage 3) | Salad + Pasta and Burger + Pasta, at 10% off the two prices added together |
| Regular customer | Loses 8 satisfaction per waiting turn |
| VIP customer (Stage 2) | Loses 5 per waiting turn and gets 10% off the bill |
| Critic customer (Stage 2) | Loses 12 per waiting turn, orders the most expensive item, and rates the service when served |
| Reputation (Stage 2) | Set by the Critic's rating. GOOD lowers every customer's waiting loss by 2, BAD raises it by 2 |
| Happy Hour (Stage 3) | Can be started once, at the start of a turn, and lasts 2 turns. Orders placed during it get 20% off, and waiting customers recover 15 satisfaction at the start of each active turn |

**Victory** needs all three at the end of turn 18: at least 4 customers paid, revenue of at least $45.00, and an average satisfaction of at least 60 among customers who paid.

## Reproducible runs

Each script in `test/` is a list of manager answers, one per line. A script only matches the stage it was written for, so check out that stage's tag first.

| Stage | Commands | Result |
|---|---|---|
| 3 (also `main`) | `java -cp out Main < test/stage3-victory.txt` | VICTORY: revenue $71.64, 4 paid, 0 left, average satisfaction 87.0 |
| 3 (also `main`) | `java -cp out Main < test/stage3-defeat.txt` | DEFEAT: revenue $22.50, 2 paid, 1 left, average satisfaction 46.5 |
| 1 | `git checkout stage-1`, rebuild, then `java -cp out Main < test/stage1-victory.txt` | VICTORY: revenue $66.00, 5 paid, average satisfaction 79.2 |
| 1 | `git checkout stage-1`, rebuild, then `java -cp out Main < test/stage1-defeat.txt` | DEFEAT: revenue $24.00, 2 paid, average satisfaction 36.0 |

More detail is in [TEST_NOTES.md](TEST_NOTES.md). Unit checks: `test/ReputationCheck.java` (Stage 2) and `test/HappyHourTest.java` (Stage 3).

## Stage comparisons

The three stages are tagged `stage-1`, `stage-2` and `stage-3` on runnable commits.

### Stage 1 → Stage 2

Compare: https://github.com/winstony29/sc2002restaurantrush/compare/stage-1...stage-2

**New files**

| File | Purpose |
|---|---|
| `src/RegularCustomer.java` | The Stage 1 customer behaviour as a subclass (waiting loss 8) |
| `src/VIPCustomer.java` | Waiting loss 5 and a 10% discount |
| `src/CriticCustomer.java` | Waiting loss 12, orders the most expensive item, rates the service |
| `src/Rating.java` | The Critic's rating of one visit: NONE, GOOD, NEUTRAL, BAD |
| `src/Reputation.java` | The restaurant's reputation: GOOD, NEUTRAL, BAD, and how it adjusts waiting loss |
| `test/ReputationCheck.java` | Unit check for `Reputation.adjustLoss` |

**Existing files changed**

| File | Change | Reason |
|---|---|---|
| `src/Customer.java` | Became abstract. Added `waitingLoss()`, `applyDiscount(cents)`, `rateService(order, currentTurn)` and `loseSatisfactionForWaiting(Reputation)` | Gives each customer type a place to override its own waiting loss, discount and rating, so no other class checks the customer's type |
| `src/Game.java` | Arrival turns array replaced by a map from turn to customer type. Added the `reputation` field and `getReputation()`. Asks the served customer for a rating and updates the reputation | Creates the three customer types and applies the reputation rule to every waiting customer |
| `src/Order.java` | Added `readyTurn` and `getReadyTurn()`. `advancePrep()` became `advancePrep(turn)`. `billCents()` renamed `basePriceCents()` | The Critic's rating depends on how long a ready order waited. The price is now the base price before the customer's discount |
| `src/Chef.java` | `cook(o)` became `cook(o, turn)` | Passes the turn to the order so it can record when it became ready |
| `src/Cashier.java` | The bill is now `c.applyDiscount(o.basePriceCents())` | Lets the customer apply its own discount |
| `src/RestaurantCLI.java` | Turn screens show the reputation, how long an order has been ready, and each customer's type | Displays the new Stage 2 state |

**Files deleted:** none.

### Stage 2 → Stage 3

Compare: https://github.com/winstony29/sc2002restaurantrush/compare/stage-2...stage-3

**New files**

| File | Purpose |
|---|---|
| `src/Seater.java` | Abstract staff class holding the seating rule shared by Host and Waiter |
| `src/Host.java` | The new staff member, who only seats customers |
| `src/HostChoice.java` | The manager's choice for the Host each turn |
| `src/ComboMeal.java` | A menu item made of two items at 10% off |
| `src/HappyHour.java` | Tracks when Happy Hour was started and decides the 20% discount |
| `test/HappyHourTest.java` | Unit checks for Happy Hour and satisfaction recovery |
| `test/stage3-victory.txt`, `test/stage3-defeat.txt` | Reproducible Stage 3 runs |

**Existing files changed**

| File | Change | Reason |
|---|---|---|
| `src/Waiter.java` | Now extends `Seater`; `seat` moved into `Seater`. `takeOrder(c, m)` became `takeOrder(c, m, turn)` | Host and Waiter share one seating rule. The order records the turn it was placed |
| `src/Order.java` | Added `placedTurn` and `getPlacedTurn()`. The two-argument constructor was replaced by `Order(item, customer, placedTurn)` | The Happy Hour discount depends on when the order was placed, not when it is paid |
| `src/Customer.java` | Added `recoverSatisfaction(amount)`. Removed the unused no-argument `loseSatisfactionForWaiting()` | Happy Hour recovery, capped at 100 |
| `src/Cashier.java` | `collect(c)` became `collect(c, happyHour)` | Applies the customer's discount first, then the Happy Hour discount |
| `src/Game.java` | Added the Host, the `HappyHour` object, the two combo meals and the Happy Hour offer and recovery steps at the start of a turn | Runs the Stage 3 turn order: arrival, turn screen, Happy Hour, then Host, Waiter and Chef choices, payment, waiting |
| `src/RestaurantCLI.java` | Added `readHostChoice`, `askActivateHappyHour`, `displayHappyHour`, `displayReview` and `displayRecovery` | Input and display for the Host, Happy Hour and the Critic's review |
| `DESIGN_NOTES.md` | Updated for Stage 3 | Records the Stage 3 design decisions |

**Files deleted:** none.

## Video timestamps

| # | Segment | Time |
|---|---------|------|
| 1 | Game launching from the submitted project | 01:55 |
| 2 | Manager choices, including a constrained-staff decision | 03:55 |
| 3 | A dish going READY → SERVED → payment | 01:55 |
| 4 | VIP or Critic behaviour | 00:55 and 03:28 |
| 5 | Stage 3 feature | 03:28 – 04:40 |
| 6 | One victory and one defeat | Victory 04:41, defeat 05:31 |
| 7 | Stage tag comparisons and the diagrams | 07:10; sequence diagram at 10:31 |
| – | Project distribution (who built what) | 13:08 |
| – | Reflections | 13:37 |

## Team

| Name | Main classes |
|------|------|
| Winston Yang | `Game`, `Main` |
| Darren | `Staff`, `Waiter`, `Chef`, `Cashier`, `Seater`, `Host` |
| Htut Myat Min | `Order`, `Menu`, `MenuItem`, `Table`, `RestaurantCLI`, the choice records, `Rating`, `Reputation`, `ComboMeal` |
| Sadhana Kumar | `Customer` and its three subclasses, `HappyHour` |

## Other evidence

- [DESIGN_NOTES.md](DESIGN_NOTES.md): design decisions.
- [TEST_NOTES.md](TEST_NOTES.md): reproducible runs.
- [ai-history/](ai-history/): AI chat histories for each member.
