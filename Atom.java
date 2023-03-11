public class Atom {
    public String value;
    NodeType type;

    public Atom(String value) {
        this.value = value;
        this.type = NodeType.ATOM;
    }
}
