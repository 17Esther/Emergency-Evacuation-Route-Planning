public class Edge {
    private final String destination;
    private final double distance;
    private final double risk;
    private final double congestion;
    private final double cost;

    public Edge(String destination, double distance, double risk, double congestion) {
        this.destination = destination;
        this.distance = distance;
        this.risk = risk;
        this.congestion = congestion;
        this.cost = distance + 2.0 * risk + congestion;
    }

    public String getDestination() { return destination; }
    public double getDistance()    { return distance; }
    public double getRisk()        { return risk; }
    public double getCongestion()  { return congestion; }
    public double getCost()        { return cost; }

    @Override
    public String toString() {
        return String.format("-> %s [d=%.2f, r=%.2f, c=%.2f, cost=%.2f]",
                destination, distance, risk, congestion, cost);
    }
}
