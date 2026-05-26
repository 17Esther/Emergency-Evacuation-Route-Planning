import java.util.*;

public class GraphAlgorithms {

    private static class NodeDistance {
        final String vertex;
        final double distance;
        NodeDistance(String vertex, double distance) {
            this.vertex = vertex;
            this.distance = distance;
        }
    }

    private static double computePathCost(Graph g, List<String> path) {
        if (path == null || path.size() < 2) return 0.0;
        double total = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            double c = g.edgeCost(path.get(i), path.get(i + 1));
            if (Double.isInfinite(c)) return Double.POSITIVE_INFINITY;
            total += c;
        }
        return total;
    }

    private static List<String> reconstruct(Map<String, String> parent, String start, String target) {
        if (!parent.containsKey(target) && !start.equals(target)) return Collections.emptyList();
        LinkedList<String> path = new LinkedList<>();
        String cur = target;
        while (cur != null) {
            path.addFirst(cur);
            if (cur.equals(start)) return path;
            cur = parent.get(cur);
        }
        return Collections.emptyList();
    }

    public static PathResult bfs(Graph g, String start, String target) {
        long t0 = System.nanoTime();
        if (!g.containsVertex(start) || !g.containsVertex(target)) {
            return new PathResult("BFS (fewest edges)", Collections.emptyList(),
                    Double.POSITIVE_INFINITY, 0, System.nanoTime() - t0);
        }
        Map<String, String> parent = new HashMap<>();
        Set<String> visited = new HashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        boolean found = start.equals(target);
        while (!queue.isEmpty() && !found) {
            String u = queue.poll();
            for (Edge e : g.getNeighbours(u)) {
                String dest = e.getDestination();
                if (!visited.contains(dest)) {
                    visited.add(dest);
                    parent.put(dest, u);
                    if (dest.equals(target)) { found = true; break; }
                    queue.add(dest);
                }
            }
        }
        long elapsed = System.nanoTime() - t0;
        if (!found) {
            return new PathResult("BFS (fewest edges)", Collections.emptyList(),
                    Double.POSITIVE_INFINITY, visited.size(), elapsed);
        }
        List<String> path = reconstruct(parent, start, target);
        double cost = computePathCost(g, path);
        return new PathResult("BFS (fewest edges)", path, cost, visited.size(), elapsed);
    }

    public static boolean dfsReachable(Graph g, String start, String target) {
        if (!g.containsVertex(start) || !g.containsVertex(target)) return false;
        Set<String> visited = new HashSet<>();
        Deque<String> stack = new ArrayDeque<>();
        stack.push(start);
        while (!stack.isEmpty()) {
            String u = stack.pop();
            if (!visited.add(u)) continue;
            if (u.equals(target)) return true;
            for (Edge e : g.getNeighbours(u)) {
                if (!visited.contains(e.getDestination())) stack.push(e.getDestination());
            }
        }
        return false;
    }

    public static PathResult dijkstra(Graph g, String start, String target) {
        long t0 = System.nanoTime();
        if (!g.containsVertex(start) || !g.containsVertex(target)) {
            return new PathResult("Dijkstra (PQ)", Collections.emptyList(),
                    Double.POSITIVE_INFINITY, 0, System.nanoTime() - t0);
        }
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> settled = new HashSet<>();
        for (String v : g.getVertices()) dist.put(v, Double.POSITIVE_INFINITY);
        dist.put(start, 0.0);

        PriorityQueue<NodeDistance> pq =
                new PriorityQueue<>(Comparator.comparingDouble(n -> n.distance));
        pq.add(new NodeDistance(start, 0.0));

        while (!pq.isEmpty()) {
            NodeDistance top = pq.poll();
            String u = top.vertex;
            if (!settled.add(u)) continue;
            if (u.equals(target)) break;
            double du = dist.get(u);
            for (Edge e : g.getNeighbours(u)) {
                String dest = e.getDestination();
                if (settled.contains(dest)) continue;
                double nd = du + e.getCost();
                if (nd < dist.get(dest)) {
                    dist.put(dest, nd);
                    parent.put(dest, u);
                    pq.add(new NodeDistance(dest, nd));
                }
            }
        }

        long elapsed = System.nanoTime() - t0;
        double d = dist.get(target);
        if (Double.isInfinite(d)) {
            return new PathResult("Dijkstra (PQ)", Collections.emptyList(),
                    Double.POSITIVE_INFINITY, settled.size(), elapsed);
        }
        return new PathResult("Dijkstra (PQ)", reconstruct(parent, start, target), d, settled.size(), elapsed);
    }

    public static PathResult dijkstraLinear(Graph g, String start, String target) {
        long t0 = System.nanoTime();
        if (!g.containsVertex(start) || !g.containsVertex(target)) {
            return new PathResult("Dijkstra (linear)", Collections.emptyList(),
                    Double.POSITIVE_INFINITY, 0, System.nanoTime() - t0);
        }
        Map<String, Double> dist = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        Set<String> settled = new HashSet<>();
        for (String v : g.getVertices()) dist.put(v, Double.POSITIVE_INFINITY);
        dist.put(start, 0.0);

        int n = g.getVertexCount();
        for (int k = 0; k < n; k++) {
            String u = null;
            double best = Double.POSITIVE_INFINITY;
            for (String v : g.getVertices()) {
                if (settled.contains(v)) continue;
                double dv = dist.get(v);
                if (dv < best) { best = dv; u = v; }
            }
            if (u == null || Double.isInfinite(best)) break;
            settled.add(u);
            if (u.equals(target)) break;
            for (Edge e : g.getNeighbours(u)) {
                String dest = e.getDestination();
                if (settled.contains(dest)) continue;
                double nd = best + e.getCost();
                if (nd < dist.get(dest)) {
                    dist.put(dest, nd);
                    parent.put(dest, u);
                }
            }
        }
        long elapsed = System.nanoTime() - t0;
        double d = dist.get(target);
        if (Double.isInfinite(d)) {
            return new PathResult("Dijkstra (linear)", Collections.emptyList(),
                    Double.POSITIVE_INFINITY, settled.size(), elapsed);
        }
        return new PathResult("Dijkstra (linear)", reconstruct(parent, start, target), d, settled.size(), elapsed);
    }
}
