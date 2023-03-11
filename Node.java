import java.util.List;

public class Node {
    String name;
    String value;
    List<String> probs;
    NodeType type = NodeType.UNKNOWN;

    public Node(String name, String value, List<String> probs) {
        this.name = name;
        this.value = value;
        this.probs = probs;
    }

    public Node(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public Node(String name) {
        this.name = name;
    }
}
