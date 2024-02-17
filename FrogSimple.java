import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class FrogSimple {
    int round = 0;
    private int bestColour;
    private ArrayList<Node> nodesFilled = new ArrayList<>();
    private double degree;
    private double minDegree;
    //set thetaDisplacement>threshold for the first run
    private double thetaDisplacement = 0.5;
    private int maxColour;
    private double threshold = 0.001;
    private double aFac = 0.02;
    private int roundsRequired = 0;
    private int roundsRequired2 = 0;

    public int getBestColour() {
        return bestColour;
    }

    public int getRoundsRequired() {
        return roundsRequired;
    }

    public int getRoundsRequired2() {
        return roundsRequired2;
    }

    //create population and store details
    public FrogSimple(ArrayList<Node> nodes, int maxDegree, int minDegree) {
        setNodes(nodes);
        //each colouring will only ever be, as a maximum, the number of nodes in the graph, and should be much lower than that after our first run
        setBestColour(nodes.size());
        setDegree(maxDegree, minDegree);

    }

    private void setNodes(ArrayList<Node> nodes) {
        nodesFilled = nodes;
    }

    private void setBestColour(int nodes) {
        bestColour = nodes;
    }

    private void setDegree(int maxDegree, int minDeg){
        degree=maxDegree;
        minDegree=minDeg;
    }

    //=========================================================================================================

    //FrogSim part 1
    public void phaseOne(){

        int rounds = 0;
        //if theta relative change since last round is below a threshold, and therefore not subject to any more change, move to phase 2
        while (thetaDisplacement > threshold && rounds<20) {
            rounds++;
            thetaDisplacement = 0;
            maxColour = 0;
            //order the nodes by current value of theta so they can fire in that order
            nodesFilled.sort(Comparator.comparingDouble(Node::getTheta));
            //sensorEvent returns the amount of difference between the theta of the last round and the current round
            for (Node n : nodesFilled) {
                thetaDisplacement +=n.sensorEvent(aFac);
                //finds current highest colour in the graph
                if (n.getColour() > maxColour) {
                    maxColour = n.getColour();
                }
            }
            //checks to see if current colouring is better than the previous best colouring
            if (maxColour < bestColour) {
                bestColour = maxColour;
                for(Node n : nodesFilled){
                    n.setBestColour();
                }
                roundsRequired=rounds;
            }
            //checks if theta convergence is taking place
            thetaDisplacement = thetaDisplacement/nodesFilled.size();

        }
        roundsRequired2 = rounds;
        //Once phase 1 is finished, revert to best colouring of the graph
        for (Node n : nodesFilled){
            n.revertToBest();
        }
        int coloursUsed = 0;
        for(Node n : nodesFilled){
            if(n.getColour()>coloursUsed){
                coloursUsed=n.getColour();
            }
            n.setBestColour();
        }
        bestColour=coloursUsed;
        //frogsimPartTwo();
        phaseTwo();
    }

    //Distributed Largest Degree First - DONE
    public void largestDegreeFirst(){
        //max represents the latest firing time a node can have ie. [0,1)
        double max = 0.99999999;
        double interval;
        //if nodes have different degrees
        if(minDegree!=degree){
            //formula for calculating the interval between firing times of each node
            interval = Math.pow(max,2)*(1/(degree-minDegree));
            for(Node node : nodesFilled){
                //function to shift the firing time of each node slightly by less that half of the interval width each way
                Random rand = new Random();
                int neighbours = node.countNeighbours();
                double intervalShift = (rand.nextDouble()*(interval))-(interval/2);
                if(neighbours == 1){
                    //cant have firing time greater than max firing time
                    intervalShift = -Math.abs(intervalShift);
                }
                else if(neighbours == degree){
                    //cant have firing time less than 0
                    intervalShift = Math.abs(intervalShift);
                }
                //shift the firing time of each node by the interval shift assigned to it
                double firingTime = (max-((neighbours-minDegree)/(degree-minDegree))*max)+intervalShift;
                node.setTheta(firingTime);
            }
        }
        //if all nodes have the same degree - not really any point doing LDF but here goes
        else{
            for(Node node : nodesFilled){
                Random rand = new Random();
                //assign random firing time in [0,1)
                double firingTime = rand.nextDouble()*max;
                node.setTheta(firingTime);
            }
        }
        //order nodes according to firing time, and therefore degree
        nodesFilled.sort(Comparator.comparingDouble(Node::getTheta));
        for (Node n : nodesFilled) {
            n.largestDegreeColouring();
        }
        for(Node n : nodesFilled){
            n.setBestColour();
        }
        bestColour=maxColour();
        roundsRequired+=1;
        phaseTwo();
    }

    //Simple Decentralised Graph Colouring
    public void phaseTwo(){
        //testColouring();
        boolean properColouring = isProperColouring();
        int version = 2;
        int rounds = 0;
        int roundsReq = 0;
        //how many rounds to run sdg part
        for(int i = 0; i<100; i++){
            rounds++;
            Collections.shuffle(nodesFilled);
            //if proper colouring, then decrease the number of colours allowed
            if(properColouring){
                switch(version){
                    case 1: //largest number removed
                        bestColour--;
                        properColouring = false;
                        break;
                    case 2: //random colour removed
                        Random random = new Random();
                        int forbiddenColour = random.nextInt(bestColour)+1;
                        bestColour--;
                        properColouring = false;
                        for(Node node : nodesFilled){
                            node.forbidColour(forbiddenColour, bestColour);
                        }
                        break;
                }
            }
            for(Node node : nodesFilled){
                node.minConflicts(bestColour);
            }
            if(isProperColouring()){
                //if proper colouring, then set the current colour as the best colouring
                for(Node n : nodesFilled){
                    n.setBestColour();
                }
                properColouring = true;
                roundsReq=rounds;
            }
        }
        roundsRequired+=roundsReq;
        for(Node node : nodesFilled) {
            node.revertToBest();
        }
        bestColour=maxColour();
    }

    //FrogSim part 2 (comparison only)
    private void frogsimPartTwo(){
        Random random = new Random();
        for(Node n : nodesFilled){
            if(n.getColour()==1){
                n.setPower(random.nextInt(100)+1);
            }
        }
        //ok
        int rounds = 0;
        for(int i = 0; i<20; i++){
            for(Node n : nodesFilled){
                n.sensorEvent2(bestColour);
            }
            if(isProperColouring()){
                if(maxColour()<bestColour){
                    rounds=i+1;
                    bestColour=maxColour();
                    for(Node n: nodesFilled){
                        n.setBestColour();
                    }
                }
            }
        }
        roundsRequired=roundsRequired+rounds;
        for(Node n :nodesFilled){
            n.revertToBest();
        }
    }

    //FrogSim-, FrogSim+, SDGC, Frogsimple, DLDF+

    //========================================================================================================

    public boolean isProperColouring(){
        int colr = 0;
        //for each node, check that its colour does not create conflicts with the colour of any neighbouring node
        for(Node node : nodesFilled){
            if(!node.properColouring()){
                return false;
            }
            else if(node.getColour()>colr){
                colr = node.getColour();
            }
        }
        bestColour=colr;
        return true;
    }

    public int maxColour(){
        int col = nodesFilled.get(0).getColour();
        for(Node n : nodesFilled){
            if(n.getColour()>col){
                col = n.getColour();
            }
        }
        return col;
    }

    private void printColouring(){
        for(Node n : nodesFilled){
            System.out.println("Node "+n.getId()+": "+n.getColour());
        }
        System.out.println();
    }

    private void testColouring(){
        for(Node n : nodesFilled){
            Random random = new Random();
            int colr = random.nextInt(1000)+1;
            n.setColour(colr);
            bestColour=maxColour();
        }
    }
}
