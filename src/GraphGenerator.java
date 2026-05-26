import java.util.*;

public class GraphGenerator {

    public static Graph createSampleBuildingGraph() {
        Graph g = new Graph();
        String[] nodes = {
                "RoomA", "RoomB", "RoomC", "RoomD",
                "Corr1", "Corr2", "Corr3", "Stair1",
                "Lobby", "Exit1", "Exit2"
        };
        for (String n : nodes) g.addVertex(n);

        // Short but RISKY route: RoomA -> Corr1 -> Stair1 -> Exit1
        g.addEdge("RoomA", "Corr1", 5,  8, 3);   // smoky corridor
        g.addEdge("Corr1", "Stair1", 4, 9, 4);   // hot stairwell
        g.addEdge("Stair1", "Exit1", 3, 7, 5);   // crowded exit

        // Longer but SAFER route: RoomA -> RoomB -> Corr2 -> Lobby -> Exit1
        g.addEdge("RoomA", "RoomB", 6, 1, 1);
        g.addEdge("RoomB", "Corr2", 7, 1, 2);
        g.addEdge("Corr2", "Lobby", 8, 0, 1);
        g.addEdge("Lobby", "Exit1", 6, 1, 2);

        // Side network
        g.addEdge("RoomB", "RoomC", 4, 2, 1);
        g.addEdge("RoomC", "Corr3", 5, 2, 2);
        g.addEdge("Corr3", "Lobby", 4, 1, 1);
        g.addEdge("RoomC", "RoomD", 3, 3, 2);
        g.addEdge("RoomD", "Exit2", 9, 2, 2);
        g.addEdge("Corr2", "Corr3", 3, 1, 1);

        return g;
    }

    public static Graph generateRandomGraph(int vertices, int edges,
                                            double maxDistance, double maxRisk,
                                            double maxCongestion) {
        return generateRandomGraph(vertices, edges, maxDistance, maxRisk, maxCongestion, 42L);
    }

    public static Graph generateRandomGraph(int vertices, int edges,
                                            double maxDistance, double maxRisk,
                                            double maxCongestion, long seed) {
        if (vertices <= 0) throw new IllegalArgumentException("vertices > 0");
        long maxPossible = (long) vertices * (vertices - 1) / 2;
        if (edges > maxPossible) edges = (int) maxPossible;
        if (edges < vertices - 1) edges = vertices - 1;

        Random rnd = new Random(seed);
        Graph g = new Graph();
        List<String> names = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            String name = "V" + i;
            g.addVertex(name);
            names.add(name);
        }

        Set<Long> used = new HashSet<>();

        // Spanning tree to guarantee connectivity
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < vertices; i++) order.add(i);
        Collections.shuffle(order, rnd);
        for (int i = 1; i < vertices; i++) {
            int a = order.get(i);
            int b = order.get(rnd.nextInt(i));
            int u = Math.min(a, b), v = Math.max(a, b);
            used.add(((long) u << 32) | v);
            g.addEdge(names.get(u), names.get(v),
                    1 + rnd.nextDouble() * maxDistance,
                    rnd.nextDouble() * maxRisk,
                    rnd.nextDouble() * maxCongestion);
        }

        int remaining = edges - (vertices - 1);
        int attempts = 0, maxAttempts = remaining * 20 + 100;
        while (remaining > 0 && attempts++ < maxAttempts) {
            int a = rnd.nextInt(vertices);
            int b = rnd.nextInt(vertices);
            if (a == b) continue;
            int u = Math.min(a, b), v = Math.max(a, b);
            long key = ((long) u << 32) | v;
            if (!used.add(key)) continue;
            g.addEdge(names.get(u), names.get(v),
                    1 + rnd.nextDouble() * maxDistance,
                    rnd.nextDouble() * maxRisk,
                    rnd.nextDouble() * maxCongestion);
            remaining--;
        }
        return g;
    }
}
