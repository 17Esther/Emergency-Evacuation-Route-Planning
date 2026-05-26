import java.util.*;

public class PathResult {
    public final List<String> path;
    public final double totalCost;
    public final int visitedNodes;
    public final long runtimeNano;
    public final String algorithm;

    public PathResult(String algorithm, List<String> path, double totalCost,
                      int visitedNodes, long runtimeNano) {
        this.algorithm = algorithm;
        this.path = path == null ? Collections.emptyList() : path;
        this.totalCost = totalCost;
        this.visitedNodes = visitedNodes;
        this.runtimeNano = runtimeNano;
    }

    public boolean hasPath() {
        return path != null && !path.isEmpty();
    }

    public void printResult() {
        System.out.println("---- " + algorithm + " ----");
        if (!hasPath()) {
            System.out.println("  No path found.");
        } else {
            System.out.println("  Path        : " + String.join(" -> ", path));
            System.out.println("  Hops        : " + (path.size() - 1));
            System.out.printf ("  Total cost  : %.3f%n", totalCost);
        }
        System.out.println("  Visited     : " + visitedNodes);
        System.out.printf ("  Runtime     : %.3f ms (%d ns)%n",
                runtimeNano / 1_000_000.0, runtimeNano);
    }
}
