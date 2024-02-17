public class Message {
    private int id;
    private double theta;
    private int colour;
    private int power;
    public Message(int id, double theta, int colour, int power){
        setID(id);
        setTheta(theta);
        setColour(colour);
        setPower(power);
    }
    private void setID(int i){
        id =i;
    }
    private void setTheta(double th){
        theta = th;
    }
    private void setColour(int c){
        colour = c;
    }
    private void setPower(int powr){
        power = powr;
    }
    public int getPower(){
        return power;
    }
    public int getID() {
        return id;
    }
    public double getTheta() {
        return theta;
    }
    public int getColour() {
        return colour;
    }
}
