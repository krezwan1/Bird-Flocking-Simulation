import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.canvas.GraphicsContext;

/**
 * A class that models the basic functions of an agent in this simulation.
 * Class was taken for the purposes of running a simulation.
 * 
 * Name: Kazi Rezwan
 * Author: Dr. Lee Stemkoski
 * Date: April 26, 2017
 * Course: CSC 302-001
 * Assignment Number: 5
 */
public class Agent
{
    private static ArrayList<Agent> agentList;
    private static int created = 0;
    private int ID;

    public Color color;
    public Color borderColor;

    public double row;
    public double col;

    private HashMap<String, Double> parameters;

    
    public Agent()
    {
        color = Color.GRAY;
        borderColor = Color.BLACK;

        row = -1;
        col = -1;
        
        parameters = new HashMap<String, Double>();

        agentList.add(this);

        ID = created;
        created++;
    }

    public static void initializeAgents()
    {
        agentList = new ArrayList<Agent>();
    }
    
    /**
     *  Creates a copy of the list of Agents.
     */
    public static ArrayList<Agent> getList()
    {
        ArrayList<Agent> copy = new ArrayList<Agent>();
        for (Agent a : agentList)
            copy.add(a);
        return copy;
    }
    
    /**
     * A method that returns the value that the bird's angle should be incrememented
     * towards its neighboring bird.
     * @param currentAngle The current angle of the bird.
     * @param targetAngle The angle of the neighboring bird.
     * @param maxIncrement The amount the turn should not exceed.
     * @return The amount to turn the bird.
     */
    public static double incrementAngleTowards(double currentAngle, double targetAngle, double maxIncrement)
    {
        double difference = targetAngle - currentAngle;
        
        //Adjust difference if targetAngle and currentAngle differ by more than
        //180 degrees.
        if (difference > 180)
            difference -= 360;
        else if (difference < -180)
            difference += 360;
        
        //Return the difference between the currentAngle and the targetAngle.
        //Clamp the value if it is greater than the maxIncrement.
        return clamp(difference, maxIncrement);
    }

    /**
     * A method that returns the value that the bird's angle should be incrememented
     * away from its neighboring bird.
     * @param currentAngle The current angle of the bird.
     * @param targetAngle The angle of the neighboring bird.
     * @param maxIncrement The amount the turn should not exceed.
     * @return The amount to turn the bird.
     */
    public static double incrementAngleAway(double currentAngle, double targetAngle, double maxIncrement)
    {
        return incrementAngleTowards(currentAngle, targetAngle, maxIncrement) * -1;
    }

    /**
     * A method that clamps a particular value within a certain range.
     * @param value The value to clamp.
     * @param maxIncrement The value to not exceed.
     * @return A value within [-maxIncrement, maxIncrement].
     */
	public static double clamp(double value, double maxIncrement)
	{
        if (value >= maxIncrement)
            return maxIncrement;
        else if (value <= 0 - maxIncrement)
            return 0 - maxIncrement;
        else
            return value;
	}
	
	/**
	 * A method that calculates the average of any given amount of doubles.
	 * @param angles Any number of doubles. Each double represents an angle in degrees.
	 * @return The average angle of the given angles.
	 */
	public static double averageAngleDegrees(double... angles)
	{
	    double totalX = 0, totalY = 0;
	    
	    for (double a : angles)
	    {
	        totalX += Math.cos(Math.toRadians(a));
	        totalY += Math.sin(Math.toRadians(a));
	    }
	    
	    double avgX = totalX / angles.length;
	    double avgY = totalY / angles.length;
	    double avgAngle = Math.toDegrees(Math.atan2(avgY, avgX));
	    
	    return avgAngle;
	}
    
    /**
     *  Removes this agent from list of agents;
     *  effect will not be apparent until the next time getList() is called.
     */
    public void destroy()
    {
        agentList.remove(this);
    }

    public void setLocation(double r, double c)
    {
        row = r;
        col = c;
    }

    public void setLocation(Patch p)
    {
        setLocation( p.row, p.col );
    }

    public Patch getPatch()
    {
        int gw = Patch.gridWidth;
        return Patch.getPatchAt(((int)Math.round(row)+gw)%gw, ((int)Math.round(col)+gw)%gw);
    }

    public void moveBy(double dx, double dy)
    {
        col += dx; // = (int)Math.round(col + dx);
        row += dy; // = (int)Math.round(row + dy);

        int gw = Patch.gridWidth;

        col = (col+gw)%gw;
        row = (row+gw)%gw;
    }

    public ArrayList<Patch> getNeighborPatches()
    {
        return getPatch().getNeighborPatches();
    }

    public ArrayList<Agent> getNeighborAgents()
    {
        return getPatch().getNeighborAgents();
    }

    // parameter methods

    public void setParameter(String name, double value)
    {
        parameters.put(name, value);
    }

    public double getParameter(String name)
    {
        return parameters.get(name);
    }

    public void addToParameter(String name, double increase)
    {
        double value = this.parameters.get(name);
        this.parameters.put( name, value + increase );
    }

    // rendering
    
    public String toString()
    {
        return "[Agent #" + ID + " @ " + row + "," + col + "]";
    }

    public void render(GraphicsContext gc)
    {
        gc.setFill( this.color );
        gc.setStroke( this.borderColor );

        int pw = Patch.patchWidth;
        gc.fillOval(col * pw + 2, row * pw + 2, pw-4, pw-4);
        gc.strokeOval(col * pw + 2, row * pw + 2, pw-4, pw-4);
    }
}