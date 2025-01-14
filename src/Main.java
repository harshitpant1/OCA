import java.util.*;
import java.util.stream.Collectors;


public class Main {

    public static ObservationTable fillTable(OCA teacher, Set<String> rows, Set<String> columns) {
        Set<String> temp = new HashSet<>(rows);
        ObservationTable observationTable = new ObservationTable();


        for (String r : rows) {
            for (char character : teacher.alphabet) {
                temp.add(r + character);
            }
        }


        for (String x : temp) {
            int counterValue = (int) teacher.getMembershipAndCounterValue(x).get(1);
            for (String y : columns) {
                List<Object> result = teacher.getMembershipAndCounterValue(x + y);
                boolean membership = (boolean) result.get(0);

                observationTable.add(x, y, membership, counterValue);
            }
        }

        return observationTable;
    }


    public static ObservationTable fillTableWithoutExtensions(OCA teacher, Set<String> rows, Set<String> columns) {
        Set<String> temp = new HashSet<>(rows);
        ObservationTable observationTable = new ObservationTable();


        for (String x : temp) {
            int counterValue = (int) teacher.getMembershipAndCounterValue(x).get(1);

            for (String y : columns) {
                List<Object> result = teacher.getMembershipAndCounterValue(x + y);
                boolean membership = (boolean) result.get(0);

                observationTable.add(x, y, membership, counterValue);
            }
        }

        return observationTable;
    }


    public static boolean is_d_closed(int d, OCA teacher, Set<String> rows, Set<String> columns) {

        ObservationTable ot1 = fillTableWithoutExtensions(teacher, rows, columns);

        for (String row : rows) {
            for (char character : teacher.alphabet) {
                String extendedRow = row + character;
                List<Object> membershipAndCounterValueER = teacher.getMembershipAndCounterValue(extendedRow);
                int counterValueER = (int) membershipAndCounterValueER.get(1);

                List<Object> membershipAndCounterValueR = teacher.getMembershipAndCounterValue(row);
                int counterValueR = (int) membershipAndCounterValueR.get(1);

                if (counterValueR == counterValueER && counterValueER <= d) {

                    Set<ObservationTable.Tuple> setER = columns.stream()
                            .map(column -> new ObservationTable.Tuple(
                                    extendedRow,
                                    column,
                                    (boolean) teacher.getMembershipAndCounterValue(extendedRow + column).get(0),
                                    (int) teacher.getMembershipAndCounterValue(extendedRow).get(1)))
                            .collect(Collectors.toSet());


                    Set<ObservationTable.Tuple> setER_bar = ObservationTable.projectToColumnsAndMembership(setER);


                    Set<ObservationTable.Tuple> setR = ot1.table;
                    Set<Set<ObservationTable.Tuple>> setOfSetsR = ObservationTable.groupTuplesByRow(setR);


                    boolean sameRowExists = false;
                    for (Set<ObservationTable.Tuple> group : setOfSetsR) {
                        Set<ObservationTable.Tuple> group_bar = ObservationTable.projectToColumnsAndMembership(group);


                        if (setER_bar.size() != group_bar.size()) {
                            sameRowExists = false;
                            continue;
                        }


                        boolean allTuplesOfsetER_barFoundInGroup_bar = true;
                        for (ObservationTable.Tuple element1 : setER_bar) {
                            boolean sameTupleFound = false;
                            for (ObservationTable.Tuple element2 : group_bar) {
                                if (element1.equals(element2)) {
                                    sameTupleFound = true;
                                    break;
                                }
                            }
                            if (!sameTupleFound) {
                                allTuplesOfsetER_barFoundInGroup_bar = false;
                                break; // i'm not sure if we need this break statement
                            }
                        }

                        if (allTuplesOfsetER_barFoundInGroup_bar) {
                            sameRowExists = true;
                            break;
                        }

                    }


                    if (!sameRowExists) {
                        return false;
                    }

                }
            }
        }
        return true;
    }


    public static void make_d_closed(int d, OCA teacher, Set<String> rows, Set<String> columns) {
        ObservationTable ot2 = fillTableWithoutExtensions(teacher, rows, columns);

        while (!is_d_closed(d, teacher, rows, columns)) {

            Set<String> addTheseToRows = new HashSet<>();

            for (String row : rows) {
                for (char character : teacher.alphabet) {
                    ot2 = fillTableWithoutExtensions(teacher, rows, columns);
                    String extendedRow = row + character;
                    List<Object> membershipAndCounterValueER = teacher.getMembershipAndCounterValue(extendedRow);
                    int counterValueER = (int) membershipAndCounterValueER.get(1);

                    List<Object> membershipAndCounterValueR = teacher.getMembershipAndCounterValue(row);
                    int counterValueR = (int) membershipAndCounterValueR.get(1);


                    if (counterValueR == counterValueER && counterValueER <= d) {

                        Set<ObservationTable.Tuple> setER = columns.stream()
                                .map(column -> new ObservationTable.Tuple(
                                        extendedRow,
                                        column,
                                        (boolean) teacher.getMembershipAndCounterValue(extendedRow + column).get(0),
                                        (int) teacher.getMembershipAndCounterValue(extendedRow).get(1)))
                                .collect(Collectors.toSet());

                        Set<ObservationTable.Tuple> setR = ot2.table;

                        Set<ObservationTable.Tuple> setER_bar = ObservationTable.projectToColumnsAndMembership(setER);

                        Set<Set<ObservationTable.Tuple>> setOfSetsR = ObservationTable.groupTuplesByRow(setR);


                        boolean sameRowExists = false;
                        for (Set<ObservationTable.Tuple> group : setOfSetsR) {
                            Set<ObservationTable.Tuple> group_bar = ObservationTable.projectToColumnsAndMembership(group);

                            if (setER_bar.size() != group_bar.size()) {
                                sameRowExists = false;
                                continue;
                            }


                            boolean allTuplesOfsetER_barFoundInGroup_bar = true;
                            for (ObservationTable.Tuple element1 : setER_bar) {
                                boolean sameTupleFound = false;
                                for (ObservationTable.Tuple element2 : group_bar) {
                                    if (element1.equals(element2)) {
                                        sameTupleFound = true;
                                        break;
                                    }
                                }
                                if (!sameTupleFound) {
                                    allTuplesOfsetER_barFoundInGroup_bar = false;
                                    break;
                                }
                            }

                            if (allTuplesOfsetER_barFoundInGroup_bar) {
                                sameRowExists = true;
                                break;
                            }

                        }

                        if (!sameRowExists) {
                            addTheseToRows.add(extendedRow);

                        }


                    }
                }
            }

            if (!addTheseToRows.isEmpty()) {
                rows.addAll(addTheseToRows);
            }
        }
    }

    // isSimilar tests whether for two row(s): do they have the same membership for all row+column pairs. Does not check for counter value.
    public static boolean isSimilar(Set<String> rows, Set<String> columns, String row1, String row2, OCA teacher) {

        Set<ObservationTable.Tuple> setRow1 = columns.stream()
                .map(column -> new ObservationTable.Tuple(
                        null,
                        column,
                        (boolean) teacher.getMembershipAndCounterValue(row1 + column).get(0),
                        (int) teacher.getMembershipAndCounterValue(row1).get(1)))
                .collect(Collectors.toSet());

        Set<ObservationTable.Tuple> setRow2 = columns.stream()
                .map(column -> new ObservationTable.Tuple(
                        null,
                        column,
                        (boolean) teacher.getMembershipAndCounterValue(row2 + column).get(0),
                        (int) teacher.getMembershipAndCounterValue(row2).get(1)))
                .collect(Collectors.toSet());

        boolean rowsAreEqual = true;
        if (setRow1.size() != setRow2.size()) {
            rowsAreEqual = false;
        }

        for (ObservationTable.Tuple element1 : setRow1) {
            boolean sameTupleFound = false;
            for (ObservationTable.Tuple element2 : setRow2) {
                if (element1.equals(element2)) {
                    sameTupleFound = true;
                    break;
                }
            }
            if (!sameTupleFound) {
                rowsAreEqual = false;
                break;
            }
        }

        return rowsAreEqual;
    }


    // isEqual tests whether two row(s) have the same counter value and also do they have the same membership for all row+column pairs
    public static boolean isEqual(Set<String> rows, Set<String> columns, String row1, String row2, OCA teacher) {
        List<Object> membershipAndCounterValue1 = teacher.getMembershipAndCounterValue(row1);
        int counterValue1 = (int) membershipAndCounterValue1.get(1);

        List<Object> membershipAndCounterValue2 = teacher.getMembershipAndCounterValue(row2);
        int counterValue2 = (int) membershipAndCounterValue2.get(1);

        if (counterValue1 != counterValue2) return false;

        return isSimilar(rows, columns, row1, row2, teacher);
    }

    public static boolean is_d_consistent(int d, OCA teacher, Set<String> rows, Set<String> columns) {
        for (String row1 : rows) {

            List<Object> membershipAndCounterValueRow1 = teacher.getMembershipAndCounterValue(row1);
            int counterValueRow1 = (int) membershipAndCounterValueRow1.get(1);

            if (counterValueRow1 <= d) {

                for (String row2 : rows) {


                    if (row1.equals(row2)) continue;


                    if (isEqual(rows, columns, row1, row2, teacher)) {

                        for (char character : teacher.alphabet) {
                            String extendedRow1 = row1 + character;
                            String extendedRow2 = row2 + character;

                            if (!isSimilar(rows, columns, extendedRow1, extendedRow2, teacher)) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static void make_d_consistent(int d, OCA teacher, Set<String> rows, Set<String> columns) {

        while (!is_d_consistent(d, teacher, rows, columns)) {
            for (String row1 : rows) {

                List<Object> membershipAndCounterValueRow1 = teacher.getMembershipAndCounterValue(row1);
                int counterValueRow1 = (int) membershipAndCounterValueRow1.get(1);

                if (counterValueRow1 <= d) {

                    for (String row2 : rows) {


                        if (row1.equals(row2)) continue;


                        if (isEqual(rows, columns, row1, row2, teacher)) {

                            for (char character : teacher.alphabet) {
                                String extendedRow1 = row1 + character;
                                String extendedRow2 = row2 + character;

                                if (!isSimilar(rows, columns, extendedRow1, extendedRow2, teacher)) {

                                    Set<String> addTheseAndAllTheirSuffixesToColumns = new HashSet<>();

                                    for (String col : columns) {
                                        boolean memb1 = (boolean) teacher.getMembershipAndCounterValue(row1 + character + col).get(0);
                                        boolean memb2 = (boolean) teacher.getMembershipAndCounterValue(row2 + character + col).get(0);

                                        if (memb1 != memb2) {
                                            addTheseAndAllTheirSuffixesToColumns.add(character + col);
                                        }
                                    }
                                    for (String str : addTheseAndAllTheirSuffixesToColumns) {
                                        for (int i = 0; i < str.length(); i++) {
                                            columns.add(str.substring(i));
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static BehaviourGraph convert_OT_to_BG(ObservationTable ot, Set<String> rows, Set<String> columns, OCA teacher) {
        List<BehaviourGraph.Vertex> listOfVertices = new ArrayList<>();
        BehaviourGraph.Vertex initialState = null;
        List<BehaviourGraph.Vertex> finalStates = new ArrayList<>();

        List<Character> alphabet = new ArrayList<>();

        for(char symbol:teacher.alphabet){
            alphabet.add(symbol);
        }


        List<List<BehaviourGraph.Vertex>> transitionFunction = new ArrayList<>();

        for (int i = 0; i < teacher.alphabet.length; i++) {
            List<BehaviourGraph.Vertex> outer = new ArrayList<>(rows.size());
            for (int j = 0; j < rows.size(); j++) {
                outer.add(null);
            }
            transitionFunction.add(outer);
        }


        List<String> rowsAtIndicesBeingNames = new ArrayList<>();

        rowsAtIndicesBeingNames.add(0, "");

        for (String r : rows) {
            if (!r.equals("")) {
                rowsAtIndicesBeingNames.add(r);
            }
        }


        for (int i = 0; i < rowsAtIndicesBeingNames.size(); i++) {

            String row = rowsAtIndicesBeingNames.get(i);

            int cv=0;
            boolean membership = false;
            for (ObservationTable.Tuple t : ot.table) {
                if (row.equals(t.row) && t.column.equals("")) {
                    cv = t.counterValue;
                    membership = t.membership;
                    break;
                }
            }

            BehaviourGraph.Vertex v = new BehaviourGraph.Vertex();
            v.name = i;
            v.counterValue = cv;
            listOfVertices.add(v);

            if (membership) {
                finalStates.add(v);
            }

            if (i == 0) {
                initialState = v;
            }

        }

        for (int k = 0; k < teacher.alphabet.length; k++) {
            for (BehaviourGraph.Vertex v : listOfVertices) {

                String row = rowsAtIndicesBeingNames.get(v.name);
                ObservationTable ot1 = fillTableWithoutExtensions(teacher, rows, columns);
                String extendedRow = row + teacher.alphabet[k];


                Set<ObservationTable.Tuple> setER = columns.stream()
                        .map(column -> new ObservationTable.Tuple(
                                extendedRow,
                                column,
                                (boolean) teacher.getMembershipAndCounterValue(extendedRow + column).get(0),
                                (int) teacher.getMembershipAndCounterValue(extendedRow).get(1)))
                        .collect(Collectors.toSet());


                Set<ObservationTable.Tuple> setER_bar = ObservationTable.projectToColumnsAndMembership(setER);


                Set<ObservationTable.Tuple> setR = ot1.table;
                Set<Set<ObservationTable.Tuple>> setOfSetsR = ObservationTable.groupTuplesByRow(setR);


                boolean sameRowExists = false;
                String sameRowWord = "";
                for (Set<ObservationTable.Tuple> group : setOfSetsR) {
                    Set<ObservationTable.Tuple> group_bar = ObservationTable.projectToColumnsAndMembership(group);


                    if (setER_bar.size() != group_bar.size()) {
                        sameRowExists = false;
                        continue;

                    }

                    List<Object> membershipAndCounterValueER = teacher.getMembershipAndCounterValue(extendedRow);
                    int counterValueER = (int) membershipAndCounterValueER.get(1);

                    String thisGroupRow = "";
                    for (ObservationTable.Tuple t : group) {
                        thisGroupRow = t.row;
                        break;
                    }

                    List<Object> membershipAndCounterValueThisGroupRow = teacher.getMembershipAndCounterValue(thisGroupRow);
                    int counterValueThisGroupRow = (int) membershipAndCounterValueThisGroupRow.get(1);

                    boolean counterValueIsSame = (counterValueThisGroupRow == counterValueER);

                    if (counterValueIsSame) {
                        boolean allTuplesOfsetER_barFoundInGroup_bar = true;
                        for (ObservationTable.Tuple element1 : setER_bar) {
                            boolean sameTupleFound = false;
                            for (ObservationTable.Tuple element2 : group_bar) {
                                if (element1.equals(element2)) {
                                    sameTupleFound = true;
                                    break;
                                }
                            }
                            if (!sameTupleFound) {
                                allTuplesOfsetER_barFoundInGroup_bar = false;
                                break;
                            }
                        }

                        if (allTuplesOfsetER_barFoundInGroup_bar) {
                            sameRowExists = true;
                            for (ObservationTable.Tuple t : group) {
                                sameRowWord = t.row;
                                break;
                            }
                            break;
                        }
                    }

                }


                if (!sameRowExists) {
                    transitionFunction.get(k).set(v.name, null);
                    continue;
                }

                for (int i = 0; i < rowsAtIndicesBeingNames.size(); i++) {
                    if (sameRowWord.equals(rowsAtIndicesBeingNames.get(i))) {
                        for (BehaviourGraph.Vertex v2 : listOfVertices) {
                            if (v2.name == i) {
                                transitionFunction.get(k).set(v.name, v2);
                            }
                        }
                    }
                }


            }


        }
        return new BehaviourGraph(alphabet, listOfVertices, initialState, finalStates, transitionFunction);
    }


    public static String equivalenceQuery(BehaviourGraph bg, OCA teacher) {

        long startTime = System.currentTimeMillis();

        Queue<String> wordsQueue = new LinkedList<>();
        wordsQueue.add("");

        while (!wordsQueue.isEmpty()) {
            int currentSize = wordsQueue.size();
            for (int i = 0; i < currentSize; i++) {
                String word = wordsQueue.poll();
                for (char symbol : teacher.alphabet) {
                    String newWord = word + symbol;

                    List<Object> teacherMembAndCV = teacher.getMembershipAndCounterValue(newWord);
                    List<Object> learntMembAndCV = bg.getMembershipAndCounterValue(newWord);

                    if (learntMembAndCV == null) {
                        return newWord;
                    }

                    if (!teacherMembAndCV.get(0).equals(learntMembAndCV.get(0)) ||
                            !teacherMembAndCV.get(1).equals(learntMembAndCV.get(1))) {
                        return newWord;
                    }

                    wordsQueue.add(newWord);
                }
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime > 300_000) { // 5 minute timeout
                System.out.println("Breaking the loop after 5 minutess");
                break;
            }
        }

        return "";
    }

    public static BehaviourGraph learnBG(int d, OCA oca) {
        Set<String> rows = new HashSet<>(Set.of("", "a", "b"));
        Set<String> columns = new HashSet<>(Set.of(""));

        BehaviourGraph bg = null;


        boolean counterExampleExists = true;

        while (counterExampleExists) {
            while (!(is_d_closed(d, oca, rows, columns) && is_d_consistent(d, oca, rows, columns))) {


                if (!is_d_closed(d, oca, rows, columns)) {

                    make_d_closed(d, oca, rows, columns);
                }
                if (!is_d_consistent(d, oca, rows, columns)) {
                    make_d_consistent(d, oca, rows, columns);
                }
            }

            ObservationTable ot = fillTableWithoutExtensions(oca, rows, columns);

            bg = convert_OT_to_BG(ot, rows, columns, oca);

            String counterEx = equivalenceQuery(bg, oca);

            if (counterEx.equals("") || counterEx.length() > d) {
                counterExampleExists = false;
                break;
            } else {
                if (counterEx.length() <= d) {
                    for (int i = 0; i < counterEx.length(); i++) {
                        rows.add(counterEx.substring(0, i + 1));

                    }
                }
            }

        }
        return bg;
    }


//    public static void main(String[] args) {
//        OCA oca = OCA.inputOCA("input1.txt");
//
////        Set<String> rows = new HashSet<>(Set.of("", "a", "ab"));
////        Set<String> columns = new HashSet<>(Set.of("", "a", "aabba"));
//
////        Set<String> rows = new HashSet<>(Set.of("", "ab"));
////        Set<String> columns = new HashSet<>(Set.of("", "ba"));
//
////        Set<String> rows = new HashSet<>(Set.of("", "ab", "a", "b", "ba", "bb"));
////        Set<String> columns = new HashSet<>(Set.of("", "ba"));
//
//
//        Set<String> rows = new HashSet<>(Set.of("", "b", "ba", "a", "bb", "ab"));
//        Set<String> columns = new HashSet<>(Set.of("", "a", "b", "ba"));
//
//
//        ObservationTable table = fillTable(oca, rows, columns);
//
//        System.out.println("Observation Table:");
//        table.printTable();
//
//        int d = 3;
//
//
//        //////////////////////D closed///////////////////////////////
//        boolean result = is_d_closed(d, oca, rows, columns);
//
//
//        System.out.println("////////////checking before: Is D Closed: " + result);
//
//        if (!result) {
//            make_d_closed(d, oca, rows, columns);
//        }
//
////        if(true){
////            make_d_closed(d, oca, rows, columns);
////        }
////
//        result = is_d_closed(d, oca, rows, columns);
//
//
//        System.out.println("//////////////checking again: Is D Closed: " + result);
//
//        //////////////////////D consistent///////////////////////////////
//
//        boolean consistentBefore = is_d_consistent(d, oca, rows, columns);
//        System.out.println("Is d-consistent before make_d_consistent: " + consistentBefore);
//
//
//        make_d_consistent(d, oca, rows, columns);
////
//        boolean consistentAfter = is_d_consistent(d, oca, rows, columns);
//        System.out.println("Is d-consistent: " + consistentAfter);
//
//        BehaviourGraph bg = convert_OT_to_BG(table, rows, columns, oca);
//
//        // use equivalence query to get counterExample
//
//        String counterExample = equivalenceQuery(bg, oca);
//
//    }
//}
public static void main(String[] args) {
        OCA oca = OCA.inputOCA("input.txt");

        BehaviourGraph bg = learnBG(15, oca);

//       boolean t= bg.startLabel(bg.transition(bg.initialState, "aababa"), 6);
        bg.printBG();
        bg.saveBG("myBG.dot");

    }
}



