
public class Main {
    public static void main(String[] args) {

        OCA oca = OCA.inputOCA("input1.txt");

//        if (oca != null) {
//            String word1 = "aaabbbaba";
//
//            System.out.println("\nThe language is: " + oca.languageName);
//            System.out.println("\nIs " + word1 + " accepted? " + oca.testMembership(oca, word1));
//            System.out.println("\nFinal counter value for " + word1 + ": " + oca.counterValue(oca, word1));
//        }

        //OCA.saveOCA(oca);
        ConfigGraph.convert_OCA_to_CongigGraph(oca, 4);



    }
}