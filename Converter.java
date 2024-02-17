import java.io.*;
import java.util.ArrayList;

public class Converter {
    private static int nodes;

    public static void main(String[] args) throws FileNotFoundException {
        BufferedReader reader;
        boolean foundNumber = false;
        String filename = "fpsol2.i.1.col";
        try {
            reader = new BufferedReader(new FileReader("DimacsGraphSet/"+ filename));
            String line = reader.readLine();
            while(!foundNumber){
                System.out.println(line);
                String[] inputNumber = line.split(" ");
                if(inputNumber[0].equals("p")){
                    nodes = Integer.parseInt(inputNumber[2]);
                    foundNumber=true;
                }
                else{
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("number of nodes is "+ nodes);
        ArrayList<ArrayList<Integer>> graph = new ArrayList<>(nodes);
        for(int i = 0; i< nodes; i++){
            ArrayList<Integer> neighbours = new ArrayList<>();
            graph.add(neighbours);
        }
        try {
            reader = new BufferedReader(new FileReader("DimacsGraphSet/"+ filename));
            String line = reader.readLine();
            while (line != null) {
                String[] inputNumber = line.split(" ");
                if(inputNumber[0].equals("e")){
                    int x = Integer.parseInt(inputNumber[1]);
                    //System.out.print("x = " + x);
                    int y = Integer.parseInt(inputNumber[2]);
                    //System.out.print(" y = " + y + "\n");
                    graph.get(x-1).add(y);
                    graph.get(y-1).add(x);
                }
                // read next line
                line = reader.readLine();
            }
            //System.out.println("gets here");
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintStream fileStream = new PrintStream(new File("Graphs/"+ filename));
        System.setOut(fileStream);
        for(int i = 0; i< graph.size(); i++){
            for(int n = 0; n<(graph.get(i)).size(); n++){
                System.out.print((graph.get(i)).get(n));
                if(n<(graph.get(i)).size()-1) {
                    System.out.print(",");
                }
            }
            if(i!= graph.size()-1){
                System.out.println();
            }
        }
    }
}
