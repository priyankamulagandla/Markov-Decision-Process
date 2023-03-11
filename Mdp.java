import java.util.List;
import java.util.Map;

public class Mdp {
    Graph g;
    double[][] transitionMatrix;
    double[] rewards;
    double discountFactor;
    int iterationCount;
    double tolerance;
    boolean minimizeCost;
    double[] values;
    List<String> decisionNodeNames;
    Map<String, String> policy;

    public Mdp(Graph g, double[][] transitionMatrix, double[] rewards, double discountFactor, int iterationCount, double tolerance, boolean minimizeCost) {
        this.g = g;
        this.transitionMatrix = transitionMatrix;
        this.rewards = rewards;
        this.discountFactor = discountFactor;
        this.iterationCount = iterationCount;
        this.tolerance = tolerance;
        this.minimizeCost = minimizeCost;
        this.values = Solver.getRewards(g);
        this.decisionNodeNames = Solver.getDecisionNodeNames(g);
        this.policy = Solver.getIntialPolicy();
    }
}
