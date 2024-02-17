import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

public class Runner {
    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        if(args.length<2 || args.length>2){
            System.err.println("\nERROR: Program takes exactly 2 arguments: graph filename, and alorithm index (1 for FrogSimple and 2 for dLDF)\n");
            System.exit(1);
        }
        int nodesCreated = 0;
        String filename = args[0];
        int program = Integer.parseInt(args[1]);
        if(program!=1 && program!=2){
        	System.err.println("\nERROR: algorithm index " +program+" is invalid. Please choose 1 for FrogSimple, or 2 for dLDF.\n");
            System.exit(1);
        }
        Node[] nodes = new Node[0]; //move this inside when finalising
        BufferedReader reader;
        int maxDegree = 0;
        int minDegree = Integer.MAX_VALUE;
        //count number of lines and therefore number of nodes
        try{
            int lineCount = 0;
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            while (line != null) {
                lineCount++;
                line = reader.readLine();
            }
            nodes = new Node[lineCount];
        } catch (IOException e) {
            System.err.println("\nERROR: "+filename + " is not a valid location\n");
            System.exit(1);
        }
        //Create the nodes if they have neighbours, otherwise they don't matter to our graph colouring
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            int x = 0;
            //until end of file..
            while (line != null) {
                //if node (line) has neighbours (values)..
                if (!line.equals("")) {
                    //create a new node..
                    Node node = new Node();
                    nodesCreated++;
                    node.setID(x + 1);
                    node.firstTheta();
                    //and add to array of nodes
                    nodes[x] = node;
                }
                line = reader.readLine();
                //otherwise increment and leave cell empty in array
                x++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.print("Verifying graph input");
        TimeUnit.SECONDS.sleep(1);
        System.out.print(".");
        TimeUnit.SECONDS.sleep(1);
        System.out.print(".");
        TimeUnit.SECONDS.sleep(1);
        System.out.print(".");
        TimeUnit.SECONDS.sleep(1);

        //Assign neighbours to nodes in the array
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line = reader.readLine();
            int i = 0;
            //until end of file..
            while (line != null) {
                int degree = 0;
                //if node (line) has neighbours (values)..
                if (!line.equals("")) {
                    //create new array-list..
                    ArrayList<Node> neighbours = new ArrayList<>();
                    String[] inputNumber = line.split(",");
                    //add neighbours to array-list as ints..
                    for (String s : inputNumber) {
                        int x = Integer.parseInt(s);
                        if(x>nodes.length){
                            System.err.println("\nERROR: Node " + nodes[i].getId() + " has an edge pointing to inexistent node "+ x);
                            System.exit(1);
                        }
                        else if (nodes[x - 1] != null) {
                            neighbours.add(nodes[x - 1]);
                            degree++;
                        }
                        else if(x<=nodesCreated){
                            System.err.println("\nERROR: Node " + nodes[i].getId() + " has neighbour node "+x+" that does not reciprocate");
                            System.exit(1);
                        }
                    }
                    //give neighbours to node
                    nodes[i].setNeighbours(neighbours);
                    //System.out.println();
                    //nodes[i].printNeighbours();
                }
                line = reader.readLine();
                i++;
                //set max and min degree
                if (degree > maxDegree) {
                    maxDegree = degree;
                } else if (degree < minDegree) {
                    minDegree = degree;
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //create array-list without empty cells, which we stored instead of creating neighbour-less nodes, as they wouldn't affect algorithm
        ArrayList<Node> nodesFilled = new ArrayList<>();
        for (Node node : nodes) {
            if (node != null) {
                nodesFilled.add(node);
            }
        }

        //check that the neighbour relationships are all two-way
        for(Node n : nodesFilled){
            for(Node neighbour : n.neighbours){
                boolean reciprocates = false;
                for(Node neigbourOfNeighbour : neighbour.neighbours){
                    if(neigbourOfNeighbour==n){
                        reciprocates = true;
                        break;
                    }
                }
                if(!reciprocates){
                    System.err.println("\nERROR: Node " + n.getId() + " has neighbour node "+neighbour.getId()+" that does not reciprocate");
                    System.exit(1);
                }
            }
        }

        System.out.println(" Graph input verified.");
        TimeUnit.SECONDS.sleep(1);

        //=============================================================================================================================
        FrogSimple frogSimple = new FrogSimple(nodesFilled, maxDegree, minDegree);
        //frogSimple.phaseOne();
        //frogSimple.phaseTwo();
        System.out.println("\n/--------------------------------------------------------------------------------------------------\\");
        int best = 10000;
        double mean = 0;
        double roundsRequired = 0;
        if(program==1){
			for(int i = 0; i<100; i++){
	            System.out.print("▊");
	            frogSimple = new FrogSimple(nodesFilled, maxDegree, minDegree);
	            frogSimple.phaseOne();
	            if(frogSimple.getBestColour()<best){
	                best = frogSimple.getBestColour();
	            }
	            mean += frogSimple.getBestColour();
	            roundsRequired += frogSimple.getRoundsRequired();
	        }
        }
        else if(program==2){
        	for(int i = 0; i<100; i++){
        		System.out.print("▊");
	            frogSimple = new FrogSimple(nodesFilled, maxDegree, minDegree);
	            frogSimple.largestDegreeFirst();
	            if(frogSimple.getBestColour()<best){
	                best = frogSimple.getBestColour();
	            }
	            mean += frogSimple.getBestColour();
	            roundsRequired += frogSimple.getRoundsRequired();
	        }
        }
        System.out.println("\nCOMPLETE.\n");
        //System.out.println("\n"+best +"\t"+ mean/100 +"\t"+ roundsRequired/100);
        
        //=============================================================================================================================

        TimeUnit.SECONDS.sleep(1);
        System.out.print("Validating algorithm output");
        TimeUnit.SECONDS.sleep(1);
        System.out.print(".");
        TimeUnit.SECONDS.sleep(1);
        System.out.print(".");
        TimeUnit.SECONDS.sleep(1);
        System.out.print(".");
        TimeUnit.SECONDS.sleep(1);

        //check if the algorithm has generated a proper colouring, and print the colours if it is
        if(!frogSimple.isProperColouring()){
            System.err.println("ERROR: Graph colouring is not a proper colouring");
            System.exit(1);
        }
        else{
            System.out.print(" Algorithm output validated.\n");
            nodesFilled.sort(Comparator.comparingInt(Node::getId));
            int colour = 0;
            String[] fileLocation = filename.split("/");

            PrintStream fileStream = new PrintStream(new File("Colourings/"+ fileLocation[1]));
            PrintStream stdout = System.out;
            System.setOut(fileStream);
            for (int i = 0; i<nodes.length; i++){
                if(nodes[i]==null){
                    System.out.print("1");
                }
                else{
                    if(nodes[i].getColour()>colour){
                        colour = nodes[i].getColour();
                    }
                    System.out.print(nodes[i].colour);
                }
                if(i!=(nodes.length-1)){
                    System.out.print(",");
                }
            }
            System.setOut(stdout);
            System.out.println("Colours used: "+colour+"\n");
        }
    }
}