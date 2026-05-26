import java.util.*;

public class Graph {
    private final Map<String, List<Edge>> adj = new LinkedHashMap<>();

    public void addVertex(String v) {
        adj.putIfAbsent(v, new ArrayList<>());
    }

    public void addEdge(String u, String v, double distance, double risk, double congestion) {
        if (u.equals(v)) return;
        addVertex(u);
        addVertex(v);
        for (Edge e : adj.get(u)) {
            if (e.getDestination().equals(v)) return;
        }
        adj.get(u).add(new Edge(v, distance, risk, congestion));
        adj.get(v).add(new Edge(u, distance, risk, congestion));
    }

    public boolean removeEdge(String u, String v) {
        boolean removed = false;
        if (adj.containsKey(u)) {
            removed |= adj.get(u).removeIf(e -> e.getDestination().equals(v));
        }
        if (adj.containsKey(v)) {
            removed |= adj.get(v).removeIf(e -> e.getDestination().equals(u));
        }
        return removed;
    }

    public List<Edge> getNeighbours(String v) {
        return adj.getOrDefault(v, Collections.emptyList());
    }

    public Set<String> getVertices() {
        return adj.keySet();
    }

    public int getVertexCount() {
        return adj.size();
    }

    public int getEdgeCount() {
        int total = 0;
        for (List<Edge> list : adj.values()) total += list.size();
        return total / 2;
    }

    public boolean containsVertex(String v) {
        return adj.containsKey(v);
    }

    public Edge findEdge(String u, String v) {
        for (Edge e : getNeighbours(u)) {
            if (e.getDestination().equals(v)) return e;
        }
        return null;
    }

    public double edgeCost(String u, String v) {
        Edge e = findEdge(u, v);
        return e == null ? Double.POSITIVE_INFINITY : e.getCost();
    }

    public void printGraph() {
        System.out.println("Graph: " + getVertexCount() + " vertices, " + getEdgeCount() + " edges");
        for (Map.Entry<String, List<Edge>> entry : adj.entrySet()) {
            System.out.println("  " + entry.getKey());
            for (Edge e : entry.getValue()) {
                System.out.println("      " + e);
            }
        }
    }
}
