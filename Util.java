import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Util {
    public static void addNode(Graph g, String name, String value, List<String> probs){
        if(g.nodeMap.containsKey(name)){
            System.out.println("A node with name "+name+" has already been added to the graph");
            System.exit(0);
        }
        Node node = new Node(name,value,probs);
        List<String> edges = new ArrayList<>();
        g.edgeMap.put(name, edges);
        g.nodeMap.put(name,node);
        g.nodeMap.get(name).probs = probs;
    }

    public static void addNode(Graph g, String name, String value){
        if(g.nodeMap.containsKey(name)){
            System.out.println("A node with name "+name+" has already been added to the graph");
            System.exit(0);
        }
        Node node = new Node(name,value);
        List<String> edges = new ArrayList<>();
        g.edgeMap.put(name, edges);
        g.nodeMap.put(name,node);
        List<String> probs = new ArrayList<>();
        g.nodeMap.get(name).probs = probs;
    }

    public static void addNode(Graph g, String name){
        if(g.nodeMap.containsKey(name)){
            System.out.println("A node with name "+name+" has already been added to the graph");
            System.exit(0);
        }
        Node node = new Node(name);
        List<String> edges = new ArrayList<>();
        g.edgeMap.put(name, edges);
        g.nodeMap.put(name,node);
        List<String> probs = new ArrayList<>();
        g.nodeMap.get(name).probs = probs;
    }

    public static void addEdge(Graph g, String name1, String name2){
        if(!g.nodeMap.containsKey(name1)){
            System.out.println("Node "+name1+" is not present in the graph to add an edge");
            System.exit(0);
        }
        if (!g.nodeMap.containsKey(name2)){
            System.out.println("Node "+name2+" is not present in the graph to add an edge");
            System.exit(0);
        }
        g.edgeMap.get(name1).add(name2);
    }

    public static Graph assignNodeTypes(Graph g){
        Set<String> keySet = g.nodeMap.keySet();
        for(String key : keySet){
            Node node = g.nodeMap.get(key);
            if(null == node.probs || node.probs.isEmpty()){
                node.type = NodeType.TERMINAL;
            }
            else if(node.probs.size() == 1){
                if(null != g.edgeMap.get(key) && g.edgeMap.get(key).size()==1){
                    node.type = NodeType.CHANCE;
                }
                else{
                    node.type =NodeType.DECISION;
                }
            }
            else if(node.probs.size() > 1){
                node.type = NodeType.CHANCE;
            }
        }
        return g;
    }

    public static Graph assignEdges(Graph g, List<List<LexToken>> tokenList){
        for(List<LexToken> lexTokens : tokenList){
            if(NodeType.COLON.equals(lexTokens.get(1).type)){
                String parent_node_name = lexTokens.get(0).value;
                if(!g.nodeMap.containsKey(parent_node_name)){
                    addNode(g,parent_node_name);
                }
                for (int i=2;i<lexTokens.size();i++){
                    if(NodeType.ATOM.equals(lexTokens.get(i).type)){
                        String node_name = lexTokens.get(i).value;
                        if(!g.nodeMap.containsKey(node_name)){
                            addNode(g,node_name);
                        }
                        addEdge(g,parent_node_name,node_name);
                    }
                }
            }
        }
        return g;
    }

    public static Graph validateNodes(Graph g){
        Set<String> keySet = g.nodeMap.keySet();
        for(String key : keySet){
            Node node = g.nodeMap.get(key);
            if(g.edgeMap.get(key).isEmpty() && null != node.probs && !node.probs.isEmpty()){
                System.out.println("Node "+key+" has no edges but has a probability entry");
                System.exit(0);
            }
            if(!g.edgeMap.get(key).isEmpty() && (null == node.probs || node.probs.isEmpty())){
                node.probs.add("1");
            }
            if(!g.edgeMap.get(key).isEmpty() && null == node.value){
                node.value = "0";
            }
        }
        return g;
    }

    public static Graph assignValues(Graph g, List<List<LexToken>> tokenList){
        for(List<LexToken> lexTokens : tokenList){
            if(NodeType.EQUALS.equals(lexTokens.get(1).type)){
                addNode(g, lexTokens.get(0).value, lexTokens.get(2).value);
            }
        }
        return g;
    }

    public static Graph assignProbabilities(Graph g, List<List<LexToken>> tokenList){
        for(List<LexToken> lexTokens : tokenList){
            if(NodeType.MOD.equals(lexTokens.get(1).type)){
                String node_name = lexTokens.get(0).value;
                List<String> probs = new ArrayList<>();
                for(int i=2;i<lexTokens.size();i++){
                    probs.add(lexTokens.get(i).value);
                }
                if(!g.nodeMap.containsKey(node_name)){
                    addNode(g, node_name);
                }
                g.nodeMap.get(node_name).probs = probs;
            }
        }
        return g;
    }
}
