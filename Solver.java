import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Solver{

    public static Map<String,String> encodingMap;
    public static Map<String,String> decodingMap;
    public static double[][] transitMatrix;
    public static List<String> decisionNodes;
    public static Map<String, String> initialPolicy;
    public static boolean v;

    public static void solver(String inputFile, boolean verbose, double discountFactor, int iterationCount, double tolerance, boolean minimizeCost){
        v = verbose;
        List<String> lines = null;
        try {
            lines = Files.readAllLines(Paths.get(inputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<String> fmt_lines =  Parser.formatLines(lines);
        List<List<LexToken>> tokenList = Parser.createTokens(fmt_lines);
        Graph g = createGraph(tokenList);
        double[][] transitionMatrix = getTransitionMatrix(g);
        double[] rewards = getRewards(g);
        Mdp mdp = new Mdp(g,transitionMatrix,rewards,discountFactor,iterationCount,tolerance,minimizeCost);
        run(g, mdp);
    }

    public static Graph createGraph(List<List<LexToken>> tokenList){
        Map<String, List<String>> edgeMap = new HashMap<>();
        Map<String, Node> NodeMap = new HashMap<>();
        Graph g = new Graph(edgeMap,NodeMap);
        g = Util.assignValues(g, tokenList);
        g = Util.assignProbabilities(g, tokenList);
        g = Util.assignEdges(g, tokenList);
        g = Util.validateNodes(g);
        g = Util.assignNodeTypes(g);
        return g;
    }

    public static double[][] getTransitionMatrix(Graph g){
        double[][] transitionMatrix = new double[g.nodeMap.size()][g.nodeMap.size()];
        Map<String, String> encodeMap = getEncodedNodeNames(g);
        for(String key : g.nodeMap.keySet()){
            List<String> transitionProbs = g.nodeMap.get(key).probs;
            List<String> neighbours = g.edgeMap.get(key);
            if(null == transitionProbs || transitionProbs.isEmpty()){
                transitionProbs = new ArrayList<>();
            }
            else{
                if(transitionProbs.size() == 1){
                    double alpha = (1 - Double.parseDouble(transitionProbs.get(0)));
                    List<String> expandedTransitionProbs = new ArrayList<>();
                    expandedTransitionProbs.add(transitionProbs.get(0));
                    double failureProbSplits = neighbours.size()-1 > 0 ? alpha/(neighbours.size()-1) : 0;
                    for(int i =1;i<neighbours.size();i++){
                        expandedTransitionProbs.add(String.valueOf(failureProbSplits));
                    }
                    transitionProbs = expandedTransitionProbs;
                }
                double sum =0;
                for(String s : transitionProbs){
                    sum = sum + Double.parseDouble(s);
                }
                if(sum!=1) {
                    System.out.println("Sum of transition probabilities for the node "+key+" is not equal to 1");
                    System.exit(0);
                }
            }
            int index =0;
            for(String neighbour : neighbours){
                int row= Integer.parseInt(encodeMap.get(key));
                int column = Integer.parseInt(encodeMap.get(neighbour));
                double prob;
                if(null == transitionProbs || transitionProbs.isEmpty()){
                    prob = 0;
                } else{
                    prob = Double.parseDouble(transitionProbs.get(index));
                }
                transitionMatrix[row][column] = prob;
                index++;
            }
        }
        transitMatrix = transitionMatrix;
        return transitionMatrix;
    }

    public static double[][] updateTransitionMatrix(Graph g, Map<String,String> policy){
        Map<String,String> encodeMap = getEncodedNodeNames(g);
        List<String> decisionNodes = getDecisionNodeNames(g);
        for(String nodeName : decisionNodes){
            List<String> neighbours = g.edgeMap.get(nodeName);
            List<String> transitionProbs = g.nodeMap.get(nodeName).probs;
            double alpha = (1 - Double.parseDouble(transitionProbs.get(0)));
            String choiceNodeName = policy.get(nodeName);
            double failureProbSplits = neighbours.size()-1 > 0 ? alpha/(neighbours.size()-1) : 0;
            List<String> expandedTransitionProbs = new ArrayList<>();
            for(int i =0;i<neighbours.size();i++){
                expandedTransitionProbs.add(String.valueOf(failureProbSplits));
            }
            expandedTransitionProbs.set(neighbours.indexOf(choiceNodeName), transitionProbs.get(0));
            transitionProbs = expandedTransitionProbs;
            double sum =0;
            for(String s : transitionProbs){
                sum = sum + Double.parseDouble(s);
            }
            if(sum!=1) {
                System.out.println("Sum of transition probabilities for the node "+nodeName+" is not equal to 1");
                System.exit(0);
            }
            int index = 0;
            for(String neighbour : neighbours){
                int row= Integer.parseInt(encodeMap.get(nodeName));
                int column = Integer.parseInt(encodeMap.get(neighbour));
                double prob;
                if(null == transitionProbs || transitionProbs.isEmpty()){
                    prob = 0;
                } else{
                    prob = Double.parseDouble(transitionProbs.get(index));
                }
                transitMatrix[row][column] = prob;
                index++;
            }
        }
        return transitMatrix;
    }

    public static Map<String,String> getEncodedNodeNames(Graph g){
        encodingMap = new HashMap<>();
        decodingMap = new HashMap<>();
        Map<String, Node> nodeTreeMap = new TreeMap<>(g.nodeMap);
        int idx=0;
        for(String key : nodeTreeMap.keySet()){
            encodingMap.put(key,String.valueOf(idx));
            decodingMap.put(String.valueOf(idx), key);
            idx++;
        }
        return encodingMap;
    }

    public static double[] getRewards(Graph g){
        double[] rewards = new double[g.nodeMap.size()];
        Map<String, String> encodeMap = getEncodedNodeNames(g);
        for(String key : g.nodeMap.keySet()){
            rewards[Integer.parseInt(encodeMap.get(key))] = Double.parseDouble(g.nodeMap.get(key).value);
        }
        return rewards;
    }

    public static List<String> getDecisionNodeNames(Graph g){
        List<String> decisionNodeNames = new ArrayList<>();
        for(String key : g.nodeMap.keySet()){
            if(NodeType.DECISION.equals(g.nodeMap.get(key).type)){
                decisionNodeNames.add(key);
            }
        }
        decisionNodes = decisionNodeNames;
        return decisionNodeNames;
    }

    public static Map<String, String> getIntialPolicy(){
        Map<String,String> policy = new HashMap<>();
        Map<String,String> decodeMap = decodingMap;
        int index = 0;
        double[][] transitionMatrix = transitMatrix;
        for(double[] row : transitionMatrix){
            String state = decodeMap.get(String.valueOf(index));
            if(!decisionNodes.contains(state)){
                index++;
                continue;
            }
            double max=-1000;
            int maxIndex=0;
            for(int i=0;i<row.length;i++){
                if(row[i]>max){
                    maxIndex = i;
                    max=row[i];
                }
            }
            String choice = decodeMap.get(String.valueOf(maxIndex));
            policy.put(state, choice);
            index++;
        }
        initialPolicy = policy;
        return policy;
    }

    public static double[] valueIteration(Mdp mdp){
        if(v) {
            System.out.println("Value Iteration (" + transitMatrix.length + "," + transitMatrix[0].length + ")");
        }
        double[] values = mdp.values;
        for(int i=0;i<mdp.iterationCount;i++){
            if(v) {
                System.out.println("Value Iteration : " + (i + 1));
            }
            double[][] values2D = new double[values.length][1];
            for(int j=0;j<values.length;j++){
                values2D[j][0] = values[j];
            }
            double[][] matMul = new double[transitMatrix.length][values2D[0].length];
            try {
                matMul = matrixMultiplication(transitMatrix, transitMatrix.length, transitMatrix[0].length, values2D, values2D.length, values2D[0].length);
            }catch (Exception e){
                e.printStackTrace();
            }
            double[][] matMulH = new double[1][values.length];
            for(int p=0;p<values.length;p++) {
                matMulH[0][p] = matMul[p][0];
            }

            for (int idx = 0; idx < matMulH.length; idx++) {
                for (int j = 0; j < matMulH[0].length; j++) {
                    matMulH[idx][j] = mdp.discountFactor * matMulH[idx][j];
                }
            }

            for(int idx =0; idx < values.length;idx++){
                matMulH[0][idx] = matMulH[0][idx] + mdp.rewards[idx];
            }
            if(allClose(matMulH,values,mdp.tolerance)){
                for(int idx=0;idx<values.length;idx++){
                    values[idx] = matMulH[0][idx];
                }
                if(v) {
                    for (int idx = 0; idx < values.length; idx++)
                        System.out.print(values[idx] + " ");
                    System.out.println();
                    System.out.println();
                }
                break;
            }
            for(int idx=0;idx<values.length;idx++){
                values[idx] = matMulH[0][idx];
            }
        }
        mdp.values = values;
        return values;
    }

    public static double[][] matrixMultiplication(
            double[][] matrix1, int rows1, int cols1,
            double[][] matrix2, int rows2, int cols2)
            throws Exception {
        if (cols1 != rows2) {
            throw new Exception("Invalid matrix given.");
        }
        double resultMatrix[][] = new double[rows1][cols2];
        for (int i = 0; i < resultMatrix.length; i++) {
            for (int j = 0; j < resultMatrix[i].length; j++) {
                for (int k = 0; k < cols1; k++) {
                    resultMatrix[i][j] += matrix1[i][k] * matrix2[k][j];
                }
            }
        }
        return resultMatrix;
    }

    public static boolean allClose(double[][] matmulH, double[] values, double tolerance){
        double[] diff = new double[values.length];
        boolean[] trueCheck = new boolean[values.length];
        for(int i=0;i<values.length;i++){
            diff[i] = Math.abs(matmulH[0][i]-values[i]);
        }
        for(int i=0;i<values.length;i++){
            if(diff[i] <= tolerance)
                trueCheck[i] = true;
        }
        for(int i=0;i<trueCheck.length;i++){
            if(!trueCheck[i]){
                return false;
            }
        }
        return true;
    }

    public static Map<String,String> policyIteration(Graph g, Mdp mdp){
        if(v) {
            System.out.println("Policy Iteration");
            System.out.println();
        }
        Map<String, String> encodeMap = getEncodedNodeNames(g);
        Map<String,String> newPolicy = new HashMap<>();
        List<String> decisionNodes = getDecisionNodeNames(g);
        for(String nodeName : decisionNodes){
            List<String> neighbours = g.edgeMap.get(nodeName);
            String neighbourIndex = encodeMap.get(neighbours.get(0));
            double idealNeighbourValue = mdp.values[Integer.parseInt(neighbourIndex)];
            newPolicy.put(nodeName, neighbours.get(0));
            for(int i=1;i<neighbours.size();i++){
                neighbourIndex = encodeMap.get(neighbours.get(i));
                double neighbourValue = mdp.values[Integer.parseInt(neighbourIndex)];
                if(mdp.minimizeCost){
                    if(neighbourValue < idealNeighbourValue){
                        idealNeighbourValue = neighbourValue;
                        newPolicy.put(nodeName, neighbours.get(i));
                    }
                } else{
                    if(neighbourValue > idealNeighbourValue){
                        idealNeighbourValue = neighbourValue;
                        newPolicy.put(nodeName, neighbours.get(i));
                    }
                }
            }
        }
        updateTransitionMatrix(g, newPolicy);
        return newPolicy;
    }

    public static void run(Graph g, Mdp mdp){
        Map<String, String> curentPolicy = initialPolicy;
        while(true){
            double[] values_new = valueIteration(mdp);
            Map<String, String> newPolicy = policyIteration(g, mdp);
            if(curentPolicy.equals(newPolicy)){
                printPolicy(newPolicy);
                printValues(values_new);
                System.exit(0);
            }
            curentPolicy = newPolicy;
        }
    }

    public static void printPolicy(Map<String,String> policy){
        System.out.println("----------POLICY----------");
        for(String key : policy.keySet()){
            System.out.println(key +" -> "+ policy.get(key));
        }
    }

    public static void printValues(double[] values){
        System.out.println("----------VALUES----------");
        Map<String,String> decodeMap = decodingMap;
        int index =0;
        for(double value : values){
            System.out.println(decodeMap.get(String.valueOf(index))+ " = "+ Math.round(value * 1000.0) / 1000.0);
            index++;
        }
    }
}