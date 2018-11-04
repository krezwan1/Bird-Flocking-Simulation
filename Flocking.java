import javafx.application.*;
import javafx.scene.chart.*;
import javafx.collections.*;
import javafx.scene.paint.*;
import java.util.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * A class that simulates how birds flock together when flying.
 * Birds flocking together follow three basic rules for this simulation:
 * 
 *  1) If another bird is too close to the current bird, this bird flies away.
 *  2) If another bird is too far from the current bird, this bird flies
 *     towards the other bird
 *  3) A bird will adjust its course to match the average angle of the bird around it.
 * 
 * The average angle and standard deviation of the angle of the bird's flight
 * pattern is also plotted.
 * 
 * Author: Kazi Rezwan
 * Instructor: Dr. Lee Stemkoski
 * Date: April 26, 2017
 * Course: CSC 302-001
 * Assignment Number: 5
 */
public class Flocking extends Simulation
{
    //Fields
    public ObservableList<XYChart.Data> avgAngleList = FXCollections.observableArrayList();
    public ObservableList<XYChart.Data> stdDevAngleList = FXCollections.observableArrayList();
    
    /**
     *   Run the simulation.
     */
    public static void main(String[] args) 
    {
        // Automatic VM reset, thanks to Joseph Rachmuth.
        try
        {
            launch(args);
            System.exit(0);
        }
        catch (Exception error)
        {
            error.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * A method that initializes the board for the Simulation.
     * All patches are set to the color black, and all Agents
     * are initialized with angle and speed parameters.
     */
    public void initialize()
    {
        //Initialize Patches on the board, and the Agents.
        int patchWidth = 12, gridWidth = 60;
        Patch.initializePatches(patchWidth, gridWidth);
        Agent.initializeAgents();

        //Loop through the board and spawn birds randomly.
        //Initialize birds on that Patch, and set their color to BLUE.
        double probabilityAgent = 0.05;
        for (Patch p : Patch.getList())
        {
            //Set all Patches to black.
            p.color = Color.BLACK;

            if (Math.random() < probabilityAgent)
            {
                //Initialize each bird agent.
                Agent bird = new Agent();
                bird.setLocation(p);
                bird.color = Color.BLUE;

                //Set parameters of each bird agent.
                bird.setParameter("angle", 360 * Math.random());
                bird.setParameter("speed", (Math.random() * 0.10) + 1);
            }
        }
        
        dataSeriesList.add( new XYChart.Series("Average Angle", avgAngleList));
        dataSeriesList.add( new XYChart.Series("Standard Deviation of Angle", stdDevAngleList));
    }

    /**
     * A method that processes the bird agents on the board at every "tick"
     * of the simulation clock.
     * The angles of each bird are adjusted based on where they are with
     * respect to other birds. Each bird agent is then moved forward in its
     * adjusted angle.
     */
    public void tick(int tickNumber)
    {
        //Variables for line chart.
        final int tick = tickNumber;
        DescriptiveStatistics dsa = new DescriptiveStatistics();
        
        //Process every bird agent on the board.
        for (Agent bird : Agent.getList())
        {
            ArrayList<Agent> neighbors = bird.getNeighborAgents();

            if (neighbors.size() == 0)
            {
                //Process all birds within a 5-unit radius, if you have no immediate neighbors.
                neighbors = Utils.filter( Agent.getList(),
                    p -> distance(bird.col, bird.row, p.col, p.row) <= 5 );

                //Find the average angle of all birds within a 5 unit radius.
                double[] angles = new double[neighbors.size()];
                for (int i = 0 ; i < neighbors.size() ; i++)
                {
                    angles[i] = neighbors.get(i).getParameter("angle");
                }
                double averageAngle = Agent.averageAngleDegrees(angles);
                
                //Add average angle to data set for line chart.
                dsa.addValue( averageAngle );

                //Increment the angle of this bird agent towards the average angle.
                bird.setParameter("angle", bird.getParameter("angle") +
                    Agent.incrementAngleTowards(bird.getParameter("angle"), averageAngle, 5));
            }
            else
            {
                //Loop through neighbors to find closest.
                Agent closest = neighbors.get(0);
                for (Agent neighbor : neighbors)
                {
                    double closestDistance = distance(bird.col, bird.row, closest.col, closest.row);
                    double neighborDistance = distance(bird.col, bird.row, neighbor.col, neighbor.row);

                    if (neighborDistance < closestDistance)
                    {
                        closest = neighbor;
                    }
                }

                //Determine angle and distance between this bird and closest neighbor.
                double collisionAngle = angle(bird.col, bird.row, closest.col, closest.row);
                double closestDistance = distance(bird.col, bird.row, closest.col, closest.row);

                double currentAngle = bird.getParameter("angle");
                if (closestDistance < 1)
                {
                    bird.setParameter("angle", currentAngle +
                        Agent.incrementAngleAway(currentAngle, collisionAngle, 1));
                }
                else if (closestDistance > 1)
                {
                    bird.setParameter("angle", currentAngle +
                        Agent.incrementAngleTowards(currentAngle, collisionAngle, 3));
                }
            }
            
            //Move the bird forward.
            double angle = bird.getParameter("angle");
            double speed = bird.getParameter("speed");
            bird.moveBy(speed * Math.cos(Math.toRadians(angle)), speed * Math.sin(Math.toRadians(angle)));

            //Change the color of the bird so that it matches its angle.
            bird.color = Color.hsb(angle, 1, 1);
        }
        
        //Add data to line chart variables for data visualization.
        double avg = dsa.getMean();
        double stdDev = dsa.getStandardDeviation();
        
        Platform.runLater( () ->
                        {
                            if (!Double.isNaN(stdDev))
                                stdDevAngleList.add( new XYChart.Data(tick, stdDev) );
                        }); 
        
        Platform.runLater( () ->
                        {
                            if (!Double.isNaN(avg))
                                avgAngleList.add( new XYChart.Data(tick, avg) );
                        }); 
    }

    /**
     * A helper method that calculates the distance between two points.
     * The points are of the convention (x1, y1) and (x2, y2).
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The distance between the two.
     */
    public double distance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    }

    /**
     * A helper method that calculates the angle between two points.
     * The points are of the convention (x1, y1) and (x2, y2).
     * @param x1 The x coordinate of the first point.
     * @param y1 The y coordinate of the first point.
     * @param x2 The x coordinate of the second point.
     * @param y2 The y coordinate of the second point.
     * @return The angle of the vector connecting the two points in degrees.
     */
    public double angle(double x1, double y1, double x2, double y2)
    {
        return Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
    }

}