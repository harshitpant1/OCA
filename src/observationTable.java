import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

class ObservationTable {

    static class Tuple {

        String row;
        String column;

        boolean membership;
        int counterValue;

        public Tuple(String row, String column, boolean membership, int counterValue)
        {
            this.row = row;
            this.column = column;
            this.membership = membership;
            this.counterValue = counterValue;

        }

        @Override
        public boolean equals(Object o) {

            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            Tuple tuple = (Tuple) o;

            if (membership != tuple.membership) return false;

//            if (counterValue != tuple.counterValue) return false;
//            if (column != null ? !column.equals(tuple.column) : tuple.column != null) return false;

            if (!column.equals(tuple.column)) return false;

            return true;
        }

        @Override
        public String toString() {
            return "(" + row + ", " + column + ", " + membership + ", " + counterValue + ")";
        }
    }

    public final Set<Tuple> table;

    public ObservationTable() {
        this.table = new HashSet<>();
    }

    public void add(String row, String column, boolean membership, int counterValue)
    {
        table.add(new Tuple(row, column, membership, counterValue));
    }

    public Set<Tuple> getTuplesByCounterValue(int counterValue)
    {
        return table.stream()
                .filter(tuple -> tuple.counterValue == counterValue)
                .collect(Collectors.toSet());
    }

    public void printTable()
    {
        for (Tuple t : table)
        {
            System.out.println(t);
        }
    }

    public static Set<Set<Tuple>> groupTuplesByRow(Set<Tuple> tuples)
    {
        Map<String, Set<Tuple>> groupedByRow = new HashMap<>();

        for (Tuple tuple : tuples)
        {
            groupedByRow.computeIfAbsent(tuple.row, k -> new HashSet<>()).add(tuple);
        }

        return new HashSet<>(groupedByRow.values());
    }

    public static Set<Tuple> projectToColumnsAndMembership(Set<Tuple> tuples) {

        return tuples.stream()
//                .map(tuple -> new Tuple(null, tuple.column, tuple.membership, tuple.counterValue))
                .map(tuple -> new Tuple(null, tuple.column, tuple.membership, 0))
                .collect(Collectors.toSet());
    }

}