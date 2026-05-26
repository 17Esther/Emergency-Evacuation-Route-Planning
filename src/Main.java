public class Main {
    public static void main(String[] args) {
        System.out.println("==============================================================");
        System.out.println(" COMP47500 - Assignment 5: Graphs");
        System.out.println(" Emergency Evacuation Route Planning Using Weighted Graphs");
        System.out.println("==============================================================");

        Graph sample = GraphGenerator.createSampleBuildingGraph();
        System.out.println("\n--- Sample Building Graph ---");
        sample.printGraph();

        System.out.println("\n--- Reachability check (DFS) ---");
        System.out.println("RoomA -> Exit1 reachable? " +
                GraphAlgorithms.dfsReachable(sample, "RoomA", "Exit1"));
        System.out.println("RoomA -> Exit2 reachable? " +
                GraphAlgorithms.dfsReachable(sample, "RoomA", "Exit2"));
        System.out.println("RoomA -> Ghost reachable? " +
                GraphAlgorithms.dfsReachable(sample, "RoomA", "Ghost"));

        System.out.println("\n--- Path queries on sample graph ---");
        GraphAlgorithms.bfs(sample, "RoomA", "Exit1").printResult();
        GraphAlgorithms.dijkstra(sample, "RoomA", "Exit1").printResult();
        GraphAlgorithms.dijkstraLinear(sample, "RoomA", "Exit1").printResult();

        System.out.println("\n--- Invalid / no-path handling ---");
        GraphAlgorithms.dijkstra(sample, "RoomA", "DoesNotExist").printResult();

        ExperimentRunner.runtimeVsSize();           // Experiment 1
        ExperimentRunner.runtimeVsDensity();        // Experiment 2
        ExperimentRunner.bfsVsDijkstraOnSample(sample); // Experiment 3
        ExperimentRunner.linearVsPQ();              // Experiment 4
        ExperimentRunner.blockedCorridorRerouting(sample); // Experiment 5

        System.out.println("\n=== Done ===");
    }
}
