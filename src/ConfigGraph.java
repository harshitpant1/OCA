import java.util.ArrayList;
import java.util.List;

public class ConfigGraph {

    public String languageName;
    public int numStates;
    public int initialState;
    public List<Integer> finalStates;
    public List<Character> alphabet;
    public List<List<Integer>> transitionFunction;

    public ConfigGraph(String name, int numStates, int initState, List<Integer> finStates, List<Character> inputAlpha, List<List<Integer>> transitions) {
        this.languageName = name;
        this.numStates = numStates;
        this.initialState = initState;
        this.finalStates = finStates;
        this.alphabet = inputAlpha;
        this.transitionFunction = transitions;
    }

    public int getNumStates() {
        return numStates;
    }

    public int getInitialState() {
        return initialState;
    }

    public boolean isFinalState(int state) {
        return finalStates.contains(state);
    }

    public List<Character> getInputAlphabet() {
        return alphabet;
    }

    public int applyTransitions(int inputSymbolIndex, int presentState) {
        return transitionFunction.get(inputSymbolIndex).get(presentState);
    }

    public static ConfigGraph convert_OCA_to_CongigGraph(OCA oca, int counter_value) {
        String languageName = oca.languageName;
        int numStates = oca.numStates;
        int initialState = oca.initialState;
        List<Integer> finalStates = new ArrayList<>(oca.finalStates);

        List<Character> alphabet = new ArrayList<>();
        for (char c : oca.alphabet) {
            alphabet.add(c);
        }


        List<List<Integer>> transitionFunction = new ArrayList<>();
        int numRows = oca.alphabet.length;
        int numCols = (counter_value + 1) * numStates;

        for (int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < numCols; j++) {
                int current_state_CG = j % numStates;
                int current_counter_val_CG = j / numStates;
                int ip_index = i;

                int next_state_OCA = oca.transitionFunction[2 * ip_index + (current_counter_val_CG == 0 ? 0 : 1)][current_state_CG * 2];
                int next_counter_val_OCA = oca.transitionFunction[2 * ip_index + (current_counter_val_CG == 0 ? 0 : 1)][current_state_CG * 2 + 1];

                row.add(numStates * next_counter_val_OCA + next_state_OCA);
            }
            transitionFunction.add(row);
        }

        return new ConfigGraph(languageName, numStates, initialState, finalStates, alphabet, transitionFunction);
    }
}
