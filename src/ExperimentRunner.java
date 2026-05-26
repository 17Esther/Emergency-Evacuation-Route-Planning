import java.util.*;

public class ExperimentRunner {

    private static final int TRIALS = 5;

    private static final String LINE =
            "+----------+----------+--------------------+--------------------+--------------+";

    private static double avgMs(Graph g, String s, String t, boolean linear) {
        // warm-up
        if (linear) GraphAlgorithms.dijkstraLinear(g, s, t);
        else        GraphAlgorithms.dijkstra(g, s, t);
        long total = 0;
        for (int i = 0; i < TRIALS; i++) {
            PathResult r = linear ? GraphAlgorithms.dijkstraLinear(g, s, t)
                                  : GraphAlgorithms.dijkstra(g, s, t);
            total += r.runtimeNano;
        }
        return (total / (double) TRIALS) / 1_000_000.0;
    }

    public static void runtimeVsSize() {
        System.out.println("\n=== Experiment 1: Dijkstra runtime vs graph size (E = 3V, avg of "
                + TRIALS + " trials) ===");
        int[] sizes = {100, 500, 1000, 3000, 5000};
        System.out.println(LINE);
        System.out.printf("| %-8s | %-8s | %-18s | %-18s | %-12s |%n",
                "V", "E", "Avg PQ time (ms)", "Avg Linear (ms)", "PQ cost");
        System.out.println(LINE);
        for (int v : sizes) {
            int e = 3 * v;
            Graph g = GraphGenerator.generateRandomGraph(v, e, 10, 5, 5, 123L);
            String s = "V0", t = "V" + (v - 1);
            double pqMs = avgMs(g, s, t, false);
            String linStr = (v <= 3000)
                    ? String.format("%.3f", avgMs(g, s, t, true))
                    : "skipped";
            PathResult r = GraphAlgorithms.dijkstra(g, s, t);
            System.out.printf("| %-8d | %-8d | %-18.3f | %-18s | %-12.3f |%n",
                    v, e, pqMs, linStr, r.totalCost);
        }
        System.out.println(LINE);
    }

    public static void runtimeVsDensity() {
        System.out.println("\n=== Experiment 2: Dijkstra runtime vs density (V = 1000, avg of "
                + TRIALS + " trials) ===");
        int v = 1000;
        int[] edgeCounts = {1000, 3000, 5000, 10000, 20000};
        System.out.println(LINE);
        System.out.printf("| %-8s | %-8s | %-18s | %-18s | %-12s |%n",
                "V", "E", "Avg PQ time (ms)", "Avg Linear (ms)", "PQ cost");
        System.out.println(LINE);
        for (int e : edgeCounts) {
            Graph g = GraphGenerator.generateRandomGraph(v, e, 10, 5, 5, 7L);
            String s = "V0", t = "V" + (v - 1);
            double pqMs  = avgMs(g, s, t, false);
            double linMs = avgMs(g, s, t, true);
            PathResult r = GraphAlgorithms.dijkstra(g, s, t);
            System.out.printf("| %-8d | %-8d | %-18.3f | %-18.3f | %-12.3f |%n",
                    v, e, pqMs, linMs, r.totalCost);
        }
        System.out.println(LINE);
    }

    public static void bfsVsDijkstraOnSample(Graph sample) {
        System.out.println("\n=== Experiment 3: BFS vs Dijkstra on sample building graph ===");
        String s = "RoomA", t = "Exit1";
        PathResult bfs = GraphAlgorithms.bfs(sample, s, t);
        PathResult dij = GraphAlgorithms.dijkstra(sample, s, t);
        System.out.println("From " + s + " to " + t + ":");
        bfs.printResult();
        dij.printResult();
        System.out.println("Note: BFS picks the path with the FEWEST corridors (often the smoky/risky one).");
        System.out.println("      Dijkstra picks the path with the LOWEST evacuation cost (safer).");
    }

    public static void linearVsPQ() {
        System.out.println("\n=== Experiment 4: Linear-scan Dijkstra vs PriorityQueue Dijkstra (avg of "
                + TRIALS + " trials) ===");
        int[] sizes = {200, 500, 1000, 2000};
        System.out.println(LINE);
        System.out.printf("| %-8s | %-8s | %-18s | %-18s | %-12s |%n",
                "V", "E", "Avg PQ time (ms)", "Avg Linear (ms)", "Speedup x");
        System.out.println(LINE);
        for (int v : sizes) {
            int e = 3 * v;
            Graph g = GraphGenerator.generateRandomGraph(v, e, 10, 5, 5, 99L);
            String s = "V0", t = "V" + (v - 1);
            double pqMs  = avgMs(g, s, t, false);
            double linMs = avgMs(g, s, t, true);
            double sp = pqMs == 0 ? 0 : linMs / pqMs;
            System.out.printf("| %-8d | %-8d | %-18.3f | %-18.3f | %-12.2f |%n",
                    v, e, pqMs, linMs, sp);
        }
        System.out.println(LINE);
    }

    public static void blockedCorridorRerouting(Graph sample) {
        System.out.println("\n=== Experiment 5: Blocked corridor rerouting ===");
        String s = "RoomA", t = "Exit1";

        PathResult before = GraphAlgorithms.dijkstra(sample, s, t);
        System.out.println("Before blocking any corridor:");
        before.printResult();

        if (before.hasPath() && before.path.size() >= 2) {
            String a = before.path.get(0);
            String b = before.path.get(1);

            Edge original = sample.findEdge(a, b);
            if (original == null) {
                System.out.println("Could not locate edge " + a + " <-> " + b + " to block.");
                return;
            }
            double origDist = original.getDistance();
            double origRisk = original.getRisk();
            double origCong = original.getCongestion();

            System.out.println("\nBlocking corridor: " + a + " <-> " + b
                    + String.format(" (d=%.2f, r=%.2f, c=%.2f)", origDist, origRisk, origCong));
            sample.removeEdge(a, b);

            PathResult after = GraphAlgorithms.dijkstra(sample, s, t);
            System.out.println("After blocking:");
            after.printResult();

            sample.addEdge(a, b, origDist, origRisk, origCong);
            System.out.println("(corridor restored to original weights)");
        }
    }
}
