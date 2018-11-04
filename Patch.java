import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.canvas.GraphicsContext;

/**
 * A class that models patches on a display board for this simulation.
 * Class was taken for the purposes of running a simulation.
 * 
 * Name: Kazi Rezwan
 * Author: Dr. Lee Stemkoski
 * Date: April 26, 2017
 * Course: CSC 302-001
 * Assignment Number: 5
 */
public class Patch
{
    // primary means of accessing the patches
    private static ArrayList<Patch> patchList;

    public static Integer patchWidth;
    public static Integer gridWidth;
    // the grid is stored to simplify access by coordinates
    private static Patch[][] patchGrid;

    public Color color;
    public Color borderColor;

    public int row;
    public int col;

    public ArrayList<Patch> neighborPatches;

    private HashMap<String, Double> parameters;

    public Patch(int r, int c)
    {
        color = Color.WHITE;
        borderColor = Color.BLACK;

        row = r;
        col = c;

        neighborPatches = new ArrayList<Patch>();

        parameters = new HashMap<String, Double>();
    }

    public static void initializePatches(int pWidth, int gWidth)
    {
        patchWidth = pWidth;
        gridWidth = gWidth;

        patchList = new ArrayList<Patch>();
        patchGrid = new Patch[gridWidth][gridWidth];

        for (int r = 0; r < gridWidth; r++)
        {
            for (int c = 0; c < gridWidth; c++)
            {
                Patch p = new Patch(r,c);
                patchList.add(p);
                patchGrid[r][c] = p;
            }
        }

        boolean includeDiagonals = true;

        int w = gridWidth;
        // set up neighbors
        for (int r = 0; r < gridWidth; r++)
        {
            for (int c = 0; c < gridWidth; c++)
            {
                Patch p = patchGrid[r][c];

                p.neighborPatches.add( patchGrid[(r-1+w)%w][(c+0+w)%w] );
                p.neighborPatches.add( patchGrid[(r+1+w)%w][(c+0+w)%w] );
                p.neighborPatches.add( patchGrid[(r+0+w)%w][(c-1+w)%w] );
                p.neighborPatches.add( patchGrid[(r+0+w)%w][(c+1+w)%w] );

                if (includeDiagonals)
                {
                    p.neighborPatches.add( patchGrid[(r-1+w)%w][(c-1+w)%w] );
                    p.neighborPatches.add( patchGrid[(r-1+w)%w][(c+1+w)%w] );
                    p.neighborPatches.add( patchGrid[(r+1+w)%w][(c-1+w)%w] );
                    p.neighborPatches.add( patchGrid[(r+1+w)%w][(c+1+w)%w] );
                }

            }
        }
    }

    public static Patch getPatchAt(int r, int c)
    {
        return patchGrid[r][c];
    }

    public static ArrayList<Patch> getList()
    {
        return patchList;
    }

    public ArrayList<Agent> getAgents()
    {
        ArrayList<Agent> agentsHere = new ArrayList<Agent>();
        for (Agent a : Agent.getList())
        {
            if (a.row == this.row && a.col == this.col)
                agentsHere.add(a);
        }
        return agentsHere;
    }

    public void destroyAgentsHere()
    {
        for (Agent a : getAgents())
            a.destroy();
    }

    public ArrayList<Patch> getNeighborPatches()
    {
        return neighborPatches;
    }

    public ArrayList<Agent> getNeighborAgents()
    {
        ArrayList<Agent> totalNeighborAgents = new ArrayList<Agent>();
        
        //System.out.println("getting nbr agents");
        for (Patch neighborPatch : neighborPatches)
        {
            ArrayList<Agent> neighborAgents = neighborPatch.getAgents();
            //System.out.println( "size - " + getAgents().size() );
            for (Agent a : neighborAgents)
                totalNeighborAgents.add( a );
        }
        return totalNeighborAgents;
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
        return "[Patch @ " + row + "," + col + "]";
    }

    public void render(GraphicsContext gc)
    {
        gc.setFill( this.color );
        gc.setStroke( this.borderColor );

        gc.fillRect(col * patchWidth, row * patchWidth, patchWidth, patchWidth);
        gc.strokeRect(col * patchWidth, row * patchWidth, patchWidth, patchWidth);
    }
}