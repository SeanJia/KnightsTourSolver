/**
 * File: KTGui.java
 * @author Zhiwei Jia
 */
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Class Name: KTGui
 * Description: a GUI class for finding Knight's Tour
 */
public class KTGui extends Application {

    // some value relevant to the board
    private int rowSize;
    private int colSize;
    
    // the algorithm object
    private KnightTour kt;
    
    // some components of this GUI
    private Button[][] bArr;
    private StackPane[][] sArr;
    private VBox vBox;
    private Button restart;
    private Pane thePane;
    
    // temp values of coordinates for path transition
    private int tempX;
    private int tempY;
    
    // the Knight's picture
    private ImageView iv = new ImageView("knight's tour.png");
    
    // count of steps to move
    private int stepCount;
    
    // for animation
    private Timeline tl;
    
    // for current position of the board
    private int currRow;
    private int currCol;
    
    // whether it's the first step
    private boolean firstStep = true;
    
    // command line arguments
    private String[] args;
    
    // some boolean indicators
    private BooleanProperty isFinished;
    private BooleanProperty notFound;
    private boolean hasRestarted;
    private boolean canRetouch;
    
    /**
     * process the command line argument
     * @param ar the args
     */
    private void processArgs(String[] ar) {
	
	// default size of the board
	if (ar.length == 0) {
	    rowSize = 8;
	    colSize = 8;
	} else {
	    rowSize = Integer.parseInt(ar[0]);
	    colSize = Integer.parseInt(ar[1]);
	}
	
	// create an algorithm object
	kt = new KnightTour(rowSize, colSize);
    }
    
    /**
     * main method for launching the application
     * @param args
     */
    public static void main(String[] args) {
	Application.launch(args);
    }

    @Override
    /**
     * start the GUI
     */
    public void start(Stage priStage) {
	
	// to obtain the arguments
	args = getParameters().getRaw().toArray(new String[0]);
	processArgs(args);
	 
	// create stackPane array
	sArr = new StackPane[rowSize][colSize];
	for (int i = 0; i < rowSize; i++) {
	    for (int j = 0; j < colSize; j++) 
		sArr[i][j] = new StackPane();
	}
	
	// create a grid pane as the board
	GridPane gP = new GridPane();
	bArr = new Button[rowSize][colSize];
	for (int row = 0; row < rowSize; row++) {
	    for (int col = 0; col < colSize; col++) {
		Rectangle sq = new Rectangle();
		
		// set their color
		if ((row+col)% 2 == 0)
		    sq.setFill(Color.LAVENDERBLUSH);
		else 
		    sq.setFill(Color.LIGHTSKYBLUE);
		sq.setWidth(70);
		sq.setHeight(70);
		
		// create buttons for each cell
		bArr[row][col] = new Button("", sq);
		sArr[row][col].getChildren().add(bArr[row][col]);
		gP.add(sArr[row][col], col, row);
	    }
	}
	
	// set the gap and padding
	gP.setHgap(10);
	gP.setVgap(10);
	gP.setPadding(new Insets(10,10,0,10));
	
	// create panes
	vBox = new VBox();
	vBox.getChildren().add(gP);
	thePane = new Pane();
	thePane.getChildren().add(vBox);
	
	// create a register for step into the algorithm
	TourFindingHandler handler = new TourFindingHandler();
	
	// register the buttons of cells
	for (int i = 0; i < rowSize; i++) {
	    for (int j = 0; j < colSize; j++) 
		bArr[i][j].setOnAction(handler);
	}
	
	// set the notFound to false
	notFound = new SimpleBooleanProperty();
	notFound.set(false);
	
	// create a button for restart
	StackPane sP = new StackPane();
	Rectangle r = new Rectangle();
	r.setHeight(20);
        r.widthProperty().bind(priStage.widthProperty().subtract(20));
	r.setOpacity(0);
	restart = new Button("restart (not available now)");
	restart.setDisable(true);
	isFinished = new SimpleBooleanProperty();
	isFinished.set(true);
	
	// set the canRetouch to true, which indicate whether able to 
	// touch each button of cell to start a new search
        canRetouch = true;
	
        // change the restart button for different situation
	isFinished.addListener(e1 -> {
	    if (!isFinished.get()) {
		restart.setText("restart (not available now)");
		restart.setDisable(true);
	    } else if (!notFound.get()) {
		restart.setText("restart");
		restart.setDisable(false);
	    } else {
		restart.setText("No such tour is found! Restart and try again!");
                restart.setDisable(false);	    
	    }
	});
	notFound.addListener(e2 -> {
	    if (!isFinished.get()) {
		restart.setText("restart (not available now)");
		restart.setDisable(true);
	    } else if (!notFound.get()) {
		restart.setText("restart");
		restart.setDisable(false);
	    } else {
		restart.setText("No such tour is found! Restart and try again!");
                restart.setDisable(false);	    
	    }
	});
	
	// add the restart button to the board
	sP.getChildren().add(r);
	sP.getChildren().add(restart);
	vBox.getChildren().add(sP);
	
	// register the restart button
	restart.setOnAction(e -> {
	    if (isFinished.get())
		restart();
	});
	
	// show the stage
	Scene s = new Scene(thePane);
	priStage.setScene(s);
	priStage.show();
	priStage.setTitle("Finding Knight's Tour (created by Zhiwei Jia)");
    } 
    
    /**
     * Method to show the result of the tour
     */
    private void showResult() {
	
	// set the initial x and y coordinates
	currRow = kt.getTour().peekFront().row;
        currCol = kt.getTour().removeFront().col;
        tempX = currCol+1;
        tempY = currRow+1;
	tempX = 100*tempX-45;
	tempY = 90*tempY-40;
	
	// show the first appearance of the knight
	if (!hasRestarted) {
	    iv.setOpacity(0);
	    thePane.getChildren().add(iv);
	}
	
	// create an animation to show the tour
	tl = new Timeline(new KeyFrame(new Duration(150), e -> { /// TIME ///
	    moveKnight();
	}));
	tl.setCycleCount(kt.getNumCell()+1);
	tl.play();
    }
    
    /**
     * Method to implement the animation for showing the tour
     */
    private void moveKnight() {
        
	// some values relevant to the position
	int row = 0;
	int col = 0;
	int nextX = 0;
	int nextY = 0;
	
	// move the knight to the first position
	if (firstStep) {
	    new PathTransition(new Duration(1), new Line(
		    0,0,tempX,tempY), iv).play();
            firstStep = false;
            iv.setOpacity(1);
	    return;
	}
	
	// do not try to get new values for the last iteration
	if (stepCount < kt.getNumCell()-1) {  
    	    row = kt.getTour().peekFront().row;
            col = kt.getTour().removeFront().col;
	}
	
	// some more important values 
        nextX = col + 1;
        nextY = row + 1;
        nextY = 90*nextY-40;
        nextX = 100*nextX-45;
       
        // do not execute the last translation
        if (stepCount < kt.getNumCell()-1) {
            Line l = new Line(tempX, tempY, nextX, nextY); 
	    new PathTransition(new Duration(130), l, iv).play(); /// TIME ////
        }
	    
	// add the number to the board after the knight's moving
	Text t = new Text(++stepCount + "");
	t.setFont(Font.font("Times New Roman", 20));
        sArr[currRow][currCol].getChildren().add(t);	
	
	// update the x and y coordinates and other values
        tempX = nextX;
        tempY = nextY;
        currRow = row;
        currCol = col;
        
        // check if we've done this finding
        if (stepCount == kt.getNumCell())
            isFinished.set(true);
    }
    
    /**
     * Method to restart the program, where reset some components of this GUI
     */
    private void restart() {
	
	// make the knight disappear
	iv.setOpacity(0);
	
	// update the status of the finding
	notFound.set(false);
	hasRestarted = true;
	firstStep = true;
	stepCount = 0;
	canRetouch = true;
	
	// create a new KnightTour
	processArgs(args);
	
	// create a handler
	TourFindingHandler handler = new TourFindingHandler();
	
	// update the board
	for (int i = 0; i < rowSize; i++) {
	    for (int j = 0; j < colSize; j++) {
		sArr[i][j].getChildren().clear();
		Rectangle sq = new Rectangle();
		if ((i+j)% 2 == 0)
		    sq.setFill(Color.LAVENDERBLUSH);
		else 
		    sq.setFill(Color.LIGHTSKYBLUE);
		sq.setWidth(70);
		sq.setHeight(70);
		bArr[i][j] = new Button("", sq);
		sArr[i][j].getChildren().add(bArr[i][j]);
		
		// register these buttons 
		bArr[i][j].setOnAction(handler);
	    }
	}
    }
    
    /**
     * An inner class for event handling
     */
    class TourFindingHandler implements EventHandler<ActionEvent> {
	    
	@Override
        public void handle(ActionEvent e) {
	    
	    // indicate that we are finding a tour
	    System.out.println("Finding...");
	    
	    // this handler only works when not during a finding
	    if (!isFinished.get() || !canRetouch)
		return;
	    
	    // update the status of this finding
	    isFinished.set(false);
	    canRetouch = false;
	    
	    // find the first position of this tour and call showResult()
    	    Object sr = e.getSource();
	    int i,j;
            for (i = 0; i < rowSize; i++) {
		for (j = 0; j < colSize; j++) {
		    if (sr == bArr[i][j]) {
		        if (kt.tour(i, j)) {
		       	    showResult();
		        } else { 
		            
		            // if no solution exists, print out the information
		            // and set the relevant values
			    System.out.println(
				"No Knight's Tour Found!");
			    isFinished.set(true);
			    notFound.set(true);   
		        }
		        
		        // after finding the right starting position,
		        // end this event handling
			return;
		    }
		}
            }		
	}
    }
}
