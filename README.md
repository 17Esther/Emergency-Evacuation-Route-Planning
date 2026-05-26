# COMP47500 - Assignment 5: Graphs

## Topic
**Emergency Evacuation Route Planning Using Weighted Graphs**

## Theoretical contribution
A building is modelled as an **undirected weighted graph** `G = (V, E)` where:

- `V` = rooms, corridors, stairwells and exits
- `E` = walkable links between them
- The cost of an edge combines three real-world evacuation factors:

    `cost(e) = distance + 2 * risk + congestion`

`risk` is doubled because in an emergency a smoky / damaged corridor matters
more than a slightly longer but safer one. Finding the safest fastest route
to an exit becomes a **single-source shortest path** problem on this graph.

We compare three algorithmic perspectives on the same instance:

| Algorithm                | What it optimises             | Use case during evacuation |
|--------------------------|-------------------------------|----------------------------|
| BFS                      | Fewest hops (ignores weight)  | Quick, naive guidance      |
| Dijkstra (PriorityQueue) | Minimum total weighted cost   | Smart router               |
| Dijkstra (linear scan)   | Same, O(V^2) baseline         | Teaching baseline          |
| DFS                      | Reachability only             | Sanity / connectivity      |

## Build / run
```
javac src/*.java
java -cp src Main
```

Java 17+ is required.

## Files
| File                      | Purpose                                              |
|---------------------------|------------------------------------------------------|
| `src/Edge.java`           | Weighted edge with `distance`, `risk`, `congestion` |
| `src/Graph.java`          | Adjacency-list undirected graph                      |
| `src/PathResult.java`     | Path + cost + visited count + runtime               |
| `src/GraphAlgorithms.java`| BFS, DFS, Dijkstra (PQ), Dijkstra (linear)          |
| `src/GraphGenerator.java` | Sample building graph + random connected graphs     |
| `src/ExperimentRunner.java`| 5 timing experiments, table output                  |
| `src/Main.java`           | Driver: prints title, demos, runs experiments        |

## Experiments
1. **Dijkstra runtime vs size**: V = 100, 500, 1000, 3000, 5000 (E = 3V).
2. **Dijkstra runtime vs density**: V = 1000, E ∈ {1k,3k,5k,10k,20k}.
3. **BFS vs Dijkstra on the sample**: shows BFS picks the *short risky* path
   (RoomA -> Corr1 -> Stair1 -> Exit1, cost 72) while Dijkstra picks the
   *longer safer* one (cost 39).
4. **Linear vs PriorityQueue Dijkstra**: empirical `O(V^2)` vs `O((V+E) log V)`.
5. **Blocked corridor rerouting**: removes an edge on the optimal path and
   re-runs Dijkstra to demonstrate dynamic reaction to a blocked route.

## Tuning practical efficiency
- **Adjacency list** (not matrix): saves memory on sparse building graphs.
- **`PriorityQueue<double[]>`** keyed by tentative distance: avoids autoboxing
  every comparison and gives `O((V+E) log V)`.
- **Lazy deletion**: outdated PQ entries are skipped via a `settled` set rather
  than `decreaseKey` (which `PriorityQueue` does not support efficiently).
- **Early exit**: Dijkstra stops as soon as the target is settled.
- **Fixed seed** in `GraphGenerator` ⇒ reproducible experiments.
- **Warm-up call** before timing to neutralise JIT effects.

## Sample output (excerpt)
```
BFS:      RoomA -> Corr1 -> Stair1 -> Exit1   cost = 72.0
Dijkstra: RoomA -> RoomB -> Corr2 -> Lobby -> Exit1   cost = 39.0
```
