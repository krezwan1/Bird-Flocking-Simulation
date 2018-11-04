import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*; 
import javafx.animation.*;
import javafx.geometry.*;
import java.io.*;
import java.util.*;
import javafx.scene.chart.*;
import javafx.collections.*;

/**
 * An abstract class the runs particular simulations.
 * Class was taken for the purposes of running a simulation.
 * 
 * Name: Kazi Rezwan
 * Author: Dr. Lee Stemkoski
 * Date: April 26, 2017
 * Course: CSC 302-001
 * Assignment Number: 5
 */
public abstract class Simulation extends Application 
{
    
    protected Thread mainLoop;
    
    protected ObservableList<XYChart.Series> dataSeriesList;
    
    protected int tickNumber;
    
    protected double tickDelay;
    
    public abstract void initialize();
    
    public abstract void tick(int tickNumber);
    
    public void start(Stage mainStage) 
    {
        mainStage.setTitle("Agent Based Modeling");

        HBox root = new HBox();

        Scene mainScene = new Scene(root);
        mainStage.setScene(mainScene);

        // custom code below --------------------------------------------

        dataSeriesList = FXCollections.observableArrayList();
        
        initialize();
        
        // set up canvas 

        int canvasWidth = Patch.patchWidth * Patch.gridWidth;
        Canvas canvas = new Canvas(canvasWidth, canvasWidth);
        GraphicsContext context = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        // set up chart

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");

        LineChart chart = new LineChart(xAxis,yAxis);

        chart.setData( dataSeriesList );
        chart.setCreateSymbols(false); // true = circles at data points
        chart.setLegendVisible(true);
        chart.setLegendSide(Side.BOTTOM);
        root.getChildren().add( chart );

        // set up separate thread to run simulation
        tickDelay = 100;
        
        mainLoop = new Thread()
        {
            public void run()
            {
                for (tickNumber = 0; tickNumber < 10000; tickNumber++)
                {
                    try { Thread.sleep( (int)tickDelay ); }
                    catch (Exception e) 
                    {
                        e.printStackTrace();
                    }

                    
                    tick(tickNumber);

                    // anything that affects the GUI should be placed in a block like this
                    // to avoid ConcurrentModificationExceptions
                    Platform.runLater( () ->
                        {
                            // redraw patches
                            for ( Patch p : Patch.getList() )
                                p.render( context );
                            
                            // redraw patches
                            for ( Agent a : Agent.getList() )
                                a.render( context );

                        });
                }
            }
        };


        // set up interactive thread controls 
        // activated by key press - [P]ause, [R]esume, [S]top, speed-[UP], speed-[DOWN]
        mainScene.setOnKeyPressed( 
            new EventHandler<KeyEvent>()
            {
                public void handle(KeyEvent event) 
                {                     
                    String key = event.getCode().toString();

                    if (key.equals("P"))
                        mainLoop.suspend();

                    if (key.equals("R"))
                        mainLoop.resume();

                    if (key.equals("S"))
                        mainLoop.stop();
                        
                    // speed up: less delay
                    if (key.equals("UP"))
                        tickDelay *= 0.80;
                        
                    // speed down: more delay
                    if (key.equals("DOWN"))
                        tickDelay *= 1.25;
                        
                    // tick delay bounds
                    if (tickDelay < 64)
                        tickDelay = 64;
                        
                    if (tickDelay > 2000)
                        tickDelay = 2000;
                }
            }
        );
        // custom code above --------------------------------------------

        mainStage.show();
        mainLoop.start();
    }

    
}