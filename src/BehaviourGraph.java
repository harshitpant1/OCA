import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class BehaviourGraph {

    public static class Vertex {
        int name;
        int counterValue;
        Set<Integer> labelSet = new HashSet<>();
    }

    List<Character> alphabet;
    List<Vertex> listOfVertices;
    Vertex initialState;
    List<Vertex> finalStates;
    List<List<Vertex>> transitionFunction;

    public BehaviourGraph(List<Character> alphabet, List<Vertex> listOfVertices, Vertex initialState, List<Vertex> finalStates, List<List<Vertex>> transitionFunction) {
        this.alphabet = alphabet;
        this.listOfVertices = listOfVertices;
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.transitionFunction = transitionFunction;
    }

    public List<Object> getMembershipAndCounterValue(String word) {
        Vertex currentState = this.initialState;
        Vertex nextState = null;


        char[] wordArr = word.toCharArray();
        for (char symbol : wordArr) {
            int i = 0;

            for(int j=0;j<this.alphabet.size();j++){
                if(this.alphabet.get(j) == symbol) i=j;
            }

            nextState = this.transitionFunction.get(i).get(currentState.name);
            currentState = nextState;
            if (currentState == null) {
                return null;
            }
        }

        return new ArrayList<>(Arrays.asList(this.finalStates.contains(currentState), currentState.counterValue));
    }

    public void printBG() {
        System.out.println("Initial State: " + initialState.name);

        System.out.print("Final State(s): ");
        for (Vertex finalState : finalStates) {
            System.out.print(finalState.name + " ");
        }
        System.out.println();

        System.out.println("Vertices:");
        for (Vertex vertex : listOfVertices) {
            System.out.println("  Vertex " + vertex.name +
//                    " [CounterValue: " + vertex.counterValue);
                    " [CounterValue: " + vertex.counterValue +
                    ", LabelSet: " + vertex.labelSet + "]");
        }

        System.out.println("Transition Function:");
        for (int i = 0; i < transitionFunction.size(); i++) {
            List<Vertex> transitions = transitionFunction.get(i);
            System.out.print("  On input '" + (i == 0 ? "a" : "b") + "': ");
            for (Vertex transition : transitions) {
                if (transition == null) {
                    System.out.print("NA ");
                    continue;
                }
                System.out.print(transition.name + " ");
            }
            System.out.println();
        }
    }


    public String findLexmin(Vertex v) {
        Queue<Pair<Vertex, String>> queue = new LinkedList<>();
        Set<Vertex> visited = new HashSet<>();

        queue.add(new Pair<>(this.initialState, ""));
        visited.add(this.initialState);

        while (!queue.isEmpty()) {
            Pair<Vertex, String> current = queue.poll();
            Vertex currentState = current.getKey();
            String currentString = current.getValue();

            if (currentState == v) {
                return currentString;
            }


            for (char symbol : this.alphabet) {
                int index = 0;
                for(int j=0;j<this.alphabet.size();j++){
                    if(this.alphabet.get(j) == symbol) index=j;
                }
                Vertex nextState = this.transitionFunction.get(index).get(currentState.name);
                if (nextState != null && !visited.contains(nextState)) {
                    queue.add(new Pair<>(nextState, currentString + symbol));
                    visited.add(nextState);
                }
            }
        }

        return null;
    }

   static class LexminFactor{
        String x;
        String y;
        String z;
        int i;

        public LexminFactor(String x, String y, String z, int i){
            this.x=x;
            this.y=y;
            this.z=z;
            this.i=i;
        }

    }

    public Set<LexminFactor> getLexminFactors(Vertex v) {
        int cv = v.counterValue;
        String lexmin = findLexmin(v);
        Set<LexminFactor> setOfLexminFactors = new HashSet<>();

        int n = lexmin.length();

        int i=2;

        for (int yLength = 1; yLength <= n / i; yLength++) { // y cannot be longer than n/i
            for (int xLength = 0; xLength <= n - i * yLength; xLength++) {
                int zLength = n - xLength - i * yLength;

                String x = lexmin.substring(0, xLength);
                String y = lexmin.substring(xLength, xLength + yLength);

                boolean yRepeated = true;
                for (int j = 1; j < i; j++) {
                    if (!y.equals(lexmin.substring(xLength + j * yLength, xLength + (j + 1) * yLength))) {
                        yRepeated = false;
                        break;
                    }
                }

                if (yRepeated) {
                    String z = lexmin.substring(xLength + i * yLength, n);
                    LexminFactor temp = new LexminFactor(x, y, z, i);
                    setOfLexminFactors.add(temp);
                }
            }
        }



        setOfLexminFactors.removeIf( factor ->
                     !( (int) this.getMembershipAndCounterValue(factor.x + factor.y).get(1) - (int) this.getMembershipAndCounterValue(factor.x).get(1) <= cv*cv
                     && (int) this.getMembershipAndCounterValue(factor.x + factor.y).get(1) - (int) this.getMembershipAndCounterValue(factor.x).get(1) >= 1 )
        );

        setOfLexminFactors.removeIf( factor -> {
            int temp = (int) this.getMembershipAndCounterValue(factor.x+factor.y).get(1) - (int) this.getMembershipAndCounterValue(factor.x).get(1);
          for(int u=1;u<= factor.i;u++){
             if(!(temp == (int) this.getMembershipAndCounterValue(factor.x + factor.y.repeat(u)).get(1) - (int) this.getMembershipAndCounterValue(factor.x +factor.y.repeat(u-1)).get(1))){
                 return true;
             }
        }
          return false;
                }
        );


        for(LexminFactor lf:setOfLexminFactors){


        }

        return setOfLexminFactors;
    }

    public Vertex transition(Vertex v, String word){

        Vertex currentState = v;
        Vertex nextState = null;

        char[] wordArr = word.toCharArray();
        for (char symbol : wordArr) {
            int i = 0;
            for(int j=0;j<this.alphabet.size();j++){
                if(this.alphabet.get(j) == symbol) i=j;
            }
            nextState = this.transitionFunction.get(i).get(currentState.name);
            currentState = nextState;
            if (currentState == null) {
                return null;
            }
        }
        return currentState;
    }

    public boolean startLabel(Vertex v, int m) {
        Set<LexminFactor> candidateSet = getLexminFactors(v);



        List<Vertex> candidateSequence = new ArrayList<>();


        for (LexminFactor lf : candidateSet) {
            boolean cvDifferenceIs_d = true;
            int temp1 = v.counterValue;
            int temp2 = this.transition(v, lf.y).counterValue;
            int d = temp2 - temp1;
            candidateSequence.add(v);
            for (int i = 1; i < lf.i; i++) {
                Vertex temp = this.transition(v, lf.y.repeat(i));
                temp2 = temp.counterValue;
                if (temp.counterValue <= m) {
                    if (temp2 - temp1 != d) {
                        cvDifferenceIs_d = false;
                        break;
                    }

//                    if(!candidateSequence.contains(temp)) // This line is not needed i guess.
                    candidateSequence.add(temp);
                }
                temp1 = temp.counterValue;
            }

            if (!cvDifferenceIs_d) {
                continue;
            }

            boolean labelFunctionResult = label(candidateSequence, v.counterValue, m, d);

            if (labelFunctionResult) return true;
            else {
                for (int i = 0; i < listOfVertices.size(); i++) {
                    listOfVertices.get(i).labelSet.clear();
                }
            }

        }
        return false; // TODO: find out where to return false from. This will get clear once u write the label function.
    }

    public boolean label(List<Vertex> candidateSequence, int n, int m, int d){

        int k=0;
        Stack<List<Vertex>> stack1 = new Stack<>();
        stack1.push(candidateSequence);
        while(!stack1.isEmpty() && k<=n*d){
            List<Vertex> seq1 = stack1.pop();
            for(Vertex v:seq1){
                v.labelSet.add(k);
            }
            for(char character : this.alphabet){
                List<Vertex> seq2 = new ArrayList<>();
                for(Vertex v:seq1){
                    Vertex temp = this.transition(v, Character.toString(character));
                    seq2.add(temp);
                }
                boolean continue_loop = false;
                for(int i=0;i<seq2.size()-1;i++){
                    if(seq2.get(i+1).counterValue-seq2.get(i).counterValue !=d){
                        continue_loop=true;
                        break;
                    }
                    if(this.finalStates.contains(seq2.get(i+1))!=this.finalStates.contains(seq2.get(i))){
                        continue_loop=true;
                        break;
                    }
                }
                if(continue_loop) continue;
                stack1.push(seq2);
                k=k+1;
            }
        }

        if(k>n*d) return false;
        return true;

    }


    public void saveBG(String filename) {
        StringBuilder text = new StringBuilder("digraph BehaviourGraph {\n");

        text.append("rankdir=TB;\n");  // Top to Bottom layout
        text.append("nodesep=1.0;\n");// Adjust node separation (default is 0.25)
        text.append("ranksep=1.5;\n");// Adjust rank separation (default is 0.5)



        // initial state
        text.append("0 [label=\"\", shape=point];\n");
        text.append("0 -> v_" + initialState.name + ";\n");

        // transitions
        for (int i = 0; i < transitionFunction.size(); i++) {
            char input = (i == 0) ? 'a' : 'b';
            List<Vertex> transitions = transitionFunction.get(i);
            for (int j = 0; j < transitions.size(); j++) {
                Vertex source = listOfVertices.get(j);
                Vertex target = transitions.get(j);
                if (target != null) {
                    text.append("v_" + source.name + " -> v_" + target.name +
                            " [label=\"" + input + "\"];\n");
                }
            }
        }

        // grouping vertices by counter value and adding them at the same level
        Map<Integer, List<Vertex>> levelMap = new HashMap<>();
        for (Vertex vertex : listOfVertices) {
            levelMap.computeIfAbsent(vertex.counterValue, k -> new ArrayList<>()).add(vertex);
        }

        for (Map.Entry<Integer, List<Vertex>> entry : levelMap.entrySet()) {
            text.append("{rank=same; ");
            for (Vertex vertex : entry.getValue()) {
                text.append("v_" + vertex.name + "; ");
            }
            text.append("}\n");
        }

        // Adding vertices
        for (Vertex vertex : listOfVertices) {
            text.append("v_" + vertex.name + " [shape=" +
                    (finalStates.contains(vertex) ? "doublecircle" : "circle") +
                    ", label=\"" + vertex.name + ", " + vertex.counterValue + "\"];\n");
        }

        text.append("}\n");

        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(text.toString());
            System.out.println("Successfully wrote BehaviourGraph to file: " + filename);
        } catch (IOException e) {
            System.err.println("Error writing BehaviourGraph to file: " + e.getMessage());
        }
    }



    // Helper class to represent a pair of values. Used in findLexmin function.
    public static class Pair<K, V> {
        private final K key;
        private final V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }

}



