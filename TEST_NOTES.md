# Test Notes — Stage 1

Two reproducible runs of the base setting, driven by manager-answer scripts in `test/`. Each line of a script is one answer to a CLI prompt, in order; the first line is the Enter for the welcome screen.

Build once before either run:

```bash
javac -d out src/*.java
```

## Victory run

Reproduce:

```bash
java -cp out Main < test/stage1-victory.txt
```

| Turn | Waiter | Chef |
|-----:|--------|------|
| 1 | seat c1 at t1 | wait |
| 2 | take c1's order | cook c1 |
| 3 | serve c1 | wait |
| 4 | seat c2 at t2 | wait |
| 5 | take c2's order | cook c2 |
| 6 | serve c2 | wait |
| 7 | seat c3 at t1 | wait |
| 8 | take c3's order | cook c3 |
| 9 | wait | cook c3 |
| 10 | serve c3 | wait |
| 11 | seat c4 at t2 | wait |
| 12 | take c4's order | cook c4 |
| 13 | serve c4 | wait |
| 14 | seat c5 at t1 | wait |
| 15 | take c5's order | cook c5 |
| 16 | serve c5 | wait |
| 17 | seat c6 at t2 | wait |
| 18 | take c6's order | cook c6 |

Totals: 5 paid, revenue $66.00, average satisfaction 79.2.

```
  RESULT: VICTORY!
```

## Defeat run

Reproduce:

```bash
java -cp out Main < test/stage1-defeat.txt
```

| Turn | Waiter | Chef |
|-----:|--------|------|
| 1 | seat c1 at t1 | wait |
| 2 | take c1's order | wait |
| 3 | wait | wait |
| 4 | seat c2 at t2 | wait |
| 5 | take c2's order | wait |
| 6 | wait | wait |
| 7 | wait | wait |
| 8 | wait | wait |
| 9 | wait | cook c1 |
| 10 | serve c1 | cook c2 |
| 11 | serve c2 | wait |
| 12 | wait | wait |
| 13 | seat c3 at t1 | wait |
| 14 | seat c4 at t2 | wait |
| 15 | take c3's order | cook c3 |
| 16 | take c4's order | cook c3 |
| 17 | serve c3 | cook c4 |
| 18 | serve c4 | wait |

Totals: 2 paid, revenue $24.00, average satisfaction 36.0.

```
  RESULT: DEFEAT
```
