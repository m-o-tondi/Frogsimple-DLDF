import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Node{
    int id;
    double theta=0;
    int colour;
    Node[] neighbours;
    int bestColour;
    ArrayList<Message> messageList = new ArrayList<>();
    int power=0;

    public void setID(int i){
        id=i;
    }

    public int countNeighbours(){
        return neighbours.length;
    }

    public void setPower(int pwr){
        power=pwr;
    }

    public void revertToBest(){
        setColour(bestColour);
    }

    public void setTheta(double t){
        theta=t;
    }

    public void setBestColour(){
        bestColour=colour;
    }

    public void firstTheta(){
        Random r = new Random();
        theta = r.nextDouble()*(1-Double.MIN_VALUE);
    }

    public int getId() {
        return id;
    }

    public double getTheta(){
        return theta;
    }

    public void setColour(int c){
        colour=c;
    }

    public int getColour(){
        return colour;
    }

    private void clearMessageList(){
        messageList.clear();
    }

    private void sendMessages(){
        Message mess = new Message(id, theta, colour, power);
        for(Node node : neighbours){
            node.messageDelivery(mess);
        }
    }

    public void setNeighbours(ArrayList<Node> n){
        neighbours=new Node[n.size()];
        for(int i=0; i<neighbours.length; i++){
            neighbours[i]=n.get(i);
        }
    }

    private void messageDelivery(Message message){
        messageList.add(message);
    }

    public double sensorEvent(double aFac){
        processMessageList();
        double thetaDisp = recalculateTheta(aFac);
        setColour(minColourNotUsed());
        //System.out.println("Node "+id+": "+theta+", "+colour);
        sendMessages();
        clearMessageList();
        return thetaDisp;
    }

    public void sensorEvent2(int biggestColour){
        processMessageList();
        if(higherPowerPresent()){
            setColour(minColourHigherPower(biggestColour));
            powerFromStrongest();
        }
        sendMessages();
        clearMessageList();
    }

    private void processMessageList(){
        //go through messagelist one by one starting from the most recently added..
        for(int i = messageList.size()-1; i>-1; i--) {
            //for each message you find, select it as the 'test data'..
            Message mess = messageList.get(i);
            //go through messagelist starting from the second most recently added..
            if(i>0){
                for (int n = i - 1; n > -1; n--) {
                    //if there is an older message that matches the test data sender, delete it
                    if (mess.getID() == messageList.get(n).getID()) {
                        messageList.remove(n);
                        //decrement so you're not going over an entry twice
                        n = n - 1;
                    }
                }
            }
            //decrement i so you're not selecting the same message twice
            i = i-1;
        }
    }

    private double inc(double messTheta, double nodeTheta){
        double inc = messTheta-nodeTheta;
        if(inc>=0){
            inc = inc-0.5;
        }
        else{
            inc = inc+0.5;
        }
        return inc;
    }

    private double recalculateTheta(double aFac){
        double thetaOld = theta;
        double total = 0;
        for(Message mes : messageList){
            total+=inc(mes.getTheta(), thetaOld);
        }
        double thetaNew = (thetaOld+(aFac*total));
        while(thetaNew<0){
            thetaNew+=1;
        }
        while(thetaNew>=1){
            thetaNew=thetaNew-1;
        }
        setTheta(thetaNew);
        return Math.min(Math.abs(thetaNew-thetaOld), (1-Math.abs(thetaNew-thetaOld)));
    }

    private double recalculateTheta2(double aFac){
        double thetaOld = theta;
        double total = 0;
        for(Message mess : messageList){
            total = total + (Math.sin(2*Math.PI*(mess.getTheta()-theta)))/(2*Math.PI);
        }
        double thetaNew = thetaOld - aFac*(total);
        while(thetaNew<0){
            thetaNew+=1;
        }
        while(thetaNew>=1){
            thetaNew=thetaNew-1;
        }
        setTheta(thetaNew);
        return Math.min(Math.abs(thetaNew-thetaOld), (1-Math.abs(thetaNew-thetaOld)));
    }

    public void largestDegreeColouring(){
        processMessageList();
        setColour(minColourNotUsed());
        sendMessages();
        clearMessageList();
    }

    private int minColourNotUsed(){
        ArrayList<Integer> neighColours = new ArrayList<>();
        //get colours of all neighbours and add to a list, then sort list
        for(Message mess : messageList) {
            boolean inList = false;
            for(int color : neighColours){
                if(color==mess.getColour()){
                    inList = true;
                }
            }
            if(!inList){
                neighColours.add(mess.getColour());
            }
        }
        Collections.sort(neighColours);
        int num = 0;
        //cycle through colours in ascending order..
        for(int n = 0; n<neighColours.size(); n++){
            //if there is a gap in the entries (and therefore there is a minimum number not selected)..
            if(n+1!=neighColours.get(n)){
                //set colour to be the number that should be in that gap
                return n+1;
            }
            //if no gap, set highest number to be most recently viewed colour
            else if(neighColours.get(n)>num){
                num = neighColours.get(n);
            }
        }
        //if you reach here without a return, the list must be gapless, therefore choose number 1 greater than largest colour among neighbours
        return (num+1);
    }

    private int minColourHigherPower(int bestColour){
        ArrayList<Integer> badColours = new ArrayList<>();
        for(Message mess : messageList){
            if(mess.getPower()>=power){
                badColours.add(mess.getColour());
            }
        }
        Collections.sort(badColours);
        for(int i=1; i<bestColour+1; i++){
            if(!badColours.contains(i)){
                return i;
            }
        }
        return colour;
    }

    private void powerFromStrongest(){
        int pwr = power;
        for(Message mess : messageList){
            if(mess.getPower()>pwr){
                pwr = mess.getPower();
            }
        }
        power = pwr;
    }

    private boolean higherPowerPresent(){
        for(Message mess : messageList){
            if(mess.getPower()>=power && mess.getColour()==colour && mess.getPower()!=0){
                return true;
            }
        }
        return false;
    }

    public void minConflicts(int limit){
        int[] coloursArray = new int[limit];
        for(int i =0; i<limit; i++){
            coloursArray[i]=0;
        }
        for(Node neigh : neighbours){
            if(!(neigh.getColour()>limit)){
                coloursArray[neigh.getColour()-1]++;
            }
        }
        int minimumConflict = coloursArray[0];
        int newColour = 1;
        for(int i=0; i<limit; i++){
            if(coloursArray[i]<minimumConflict){
                minimumConflict=coloursArray[i];
                newColour = i+1;
            }
        }
        setColour(newColour);
    }

    public void forbidColour(int forbiddenColour, int limit){
        if(colour==forbiddenColour){
            colour = limit+1;
        }
        else if(colour>forbiddenColour){
            colour=colour-1;
        }
    }

    public boolean properColouring(){
        for(Node node : neighbours){
            if(node.getColour()==colour){
                return false;
            }
        }
        return true;
    }
}