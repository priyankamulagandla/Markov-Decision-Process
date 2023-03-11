import java.util.List;
import java.util.Map;

public class Graph {
    Map<String, List<String>> edgeMap;
    Map<String, Node> nodeMap;

    public Graph(Map<String, List<String>> edgeMap, Map<String, Node> nodeMap) {
        this.edgeMap = edgeMap;
        this.nodeMap = nodeMap;
    }
}
