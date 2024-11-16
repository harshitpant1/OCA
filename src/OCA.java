import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;


public class OCA {
//    private String languageName;
    public String languageName;
    private int numStates;
    private int initialState;
    private Set<Integer> finalStates;
    private char[] alphabet;
    private int[][] transitionFunction;

    public OCA(String languageName, int numStates, int initialState, Set<Integer> finalStates, char[] alphabet, int[][] transitionFunction) {
        this.languageName = languageName;
        this.numStates = numStates;
        this.initialState = initialState;
        this.finalStates = finalStates;
        this.alphabet = alphabet;
        this.transitionFunction = transitionFunction;
    }

    public Pair Transition(Triplet input) {
        int currentState = input.state;
        int currentCounter = input.counter;
        char letter = input.letter;

        int index = Arrays.binarySearch(alphabet, letter);
        if (index < 0) {
            return null; // invalid letter
        }

        int row = index * 2 + (currentCounter > 0 ? 1 : 0);
        int col = currentState;

        int nextState = transitionFunction[row][col * 2];
        int counterChange = transitionFunction[row][col * 2 + 1];

        return new Pair(nextState, currentCounter + counterChange);
    }

    public boolean testMembership(OCA oca, String word) {
        int currentState = oca.initialState;
        int currentCounter = 0;

        for (char c : word.toCharArray()) {
            Pair next = oca.Transition(new Triplet(currentState, currentCounter, c));
            if (next == null) {
                return false; // Invalid transition
            }
            currentState = next.state;
            currentCounter = next.counter;
        }

        return oca.finalStates.contains(currentState);
    }

    public int counterValue(OCA oca, String word) {
        int currentState = oca.initialState;
        int currentCounter = 0;

        for (char c : word.toCharArray()) {
            Pair next = oca.Transition(new Triplet(currentState, currentCounter, c));
            if (next == null) {
                return -1; // invalid transition
            }
            currentState = next.state;
            currentCounter = next.counter;
        }

        return currentCounter;
    }

    public static OCA inputOCA(String filename) {
        try (Scanner scanner = new Scanner(new File(filename))) {
            String languageName = scanner.nextLine();
            int numStates = scanner.nextInt();
            int initialState = scanner.nextInt();

            scanner.nextLine(); 
            String tempStringOfFinalStatesInInput = scanner.nextLine();

            String[] tempArrayOfFinalStatesAsStrings = tempStringOfFinalStatesInInput.split(" ");

            int[] tempArrayOfFinalStatesAsInts = new int[tempArrayOfFinalStatesAsStrings.length];
            for (int i = 0; i < tempArrayOfFinalStatesAsStrings.length; i++) {
                tempArrayOfFinalStatesAsInts[i] = Integer.parseInt(tempArrayOfFinalStatesAsStrings[i]);
            }

            Set<Integer> finalStates = new HashSet<>();
            for (int number : tempArrayOfFinalStatesAsInts) {
                finalStates.add(number);
            }

            String alphabetStr = scanner.nextLine();
            alphabetStr = alphabetStr.replaceAll("\\s+", "");
            char[] alphabet = alphabetStr.toCharArray();

            int[][] transitionFunction = new int[alphabet.length * 2][numStates * 2];
            for (int i = 0; i < alphabet.length * 2; i++) {
                for (int j = 0; j < numStates * 2; j++) {
                    transitionFunction[i][j] = scanner.nextInt();
                }
            }

            return new OCA(languageName, numStates, initialState, finalStates, alphabet, transitionFunction);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return null;
        }
    }

    public static void saveOCA(OCA oca){

        String text = "digraph automaton {\n";
        text += "0 [label=\"\", shape=point];\n";
        text += "0 -> q_0;";
        for(int i=0;i<oca.transitionFunction.length;i++){
            for(int j=0;j<oca.transitionFunction[i].length;j=j+2){
                int letter_index = i / 2;
                char letter = oca.alphabet[letter_index];
                boolean zero_or_positive = (i % 2) != 0;

                int current_state = j / 2;
                int next_state = oca.transitionFunction[i][j];
                text += "\n"+ "q_" + current_state + " -> " + "q_" + next_state+"[label=\"" + letter + ", " + (zero_or_positive?"+":"0") + ", " + oca.transitionFunction[i][j+1] + "\"];";
            }
        }

        for(int i=0;i<oca.numStates;i++){
            text += "\n"+ "q_"+ i+" [shape = "+ ( oca.finalStates.contains(i)?"doublecircle": "circle") + "];";
        }

        text += "\n}";

        try (FileWriter writer = new FileWriter(oca.languageName)) {
            writer.write(text);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    // Helper classes
    static class Triplet {
        int state;
        int counter;
        char letter;

        public Triplet(int state, int counter, char letter) {
            this.state = state;
            this.counter = counter;
            this.letter = letter;
        }
    }

    static class Pair {
        int state;
        int counter;

        public Pair(int state, int counter) {
            this.state = state;
            this.counter = counter;
        }
    }
}

