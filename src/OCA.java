import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Pattern;

public class OCA {
    public String languageName;
    public int numStates;
    public int initialState;
    public Set<Integer> finalStates;
    public char[] alphabet;
    public int[][] transitionFunction;

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

    public List<Object> getMembershipAndCounterValue(String word) {
        int currentState = this.initialState;
        int currentCounter = 0;

        for (char c : word.toCharArray()) {
            Pair next = this.Transition(new Triplet(currentState, currentCounter, c));
            if (next == null) {
                return new ArrayList<>(Arrays.asList(false, -1)); // Invalid transition
            }
            currentState = next.state;
            currentCounter = next.counter;
        }

        return new ArrayList<>(Arrays.asList(this.finalStates.contains(currentState), currentCounter));
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

    public void saveOCA(){

        String text = "digraph automaton {\n";
        text += "0 [label=\"\", shape=point];\n";
        text += "0 -> q_0;";
        for(int i=0;i<this.transitionFunction.length;i++){
            for(int j=0;j<this.transitionFunction[i].length;j=j+2){
                int letter_index = i / 2;
                char letter = this.alphabet[letter_index];
                boolean zero_or_positive = (i % 2) != 0;

                int current_state = j / 2;
                int next_state = this.transitionFunction[i][j];
                text += "\n"+ "q_" + current_state + " -> " + "q_" + next_state+"[label=\"" + letter + ", " + (zero_or_positive?"+":"0") + ", " + this.transitionFunction[i][j+1] + "\"];";
            }
        }

        for(int i=0;i<this.numStates;i++){
            text += "\n"+ "q_"+ i+" [shape = "+ (this.finalStates.contains(i)?"doublecircle": "circle") + "];";
        }

        text += "\n}";

        String fileName = this.languageName;
        boolean hasSpecialChars = Pattern.compile("[<>:\"/\\|?*]").matcher(fileName).find();

        if (hasSpecialChars) {
            fileName = fileName.replaceAll("[<>:\"/\\|?*]", "_");
            fileName += "_lang_name_changed";
        }

        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(text);
            System.out.println("Successfully wrote to file: " + fileName);
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

