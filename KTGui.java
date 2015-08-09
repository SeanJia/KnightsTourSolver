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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Class Name: KTGui
 * Description: a GUI class for finding Knight's Tour, a version for
 *              Java desktop applications
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
    private ImageView knightPic;
    
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
    private String[] args = new String[2];
    
    // some boolean indicators
    private BooleanProperty isFinished;
    private BooleanProperty notFound;
    private boolean canRetouch;
    
    // main scene and home scene
    Scene homeScene;
    Scene mainScene;
    
    // whether the tool has been reset
    boolean afterReset;
    
    // whether stop the process after pressing "home" button
    boolean stop;
    
    // whether it's standard 8x8 board
    boolean isStd;
    
    /**
     * previously for process the command line argument, in this version
     * for passing arguments from the menu scene
     * @param ar the args
     */
    private void processArgs(String[] ar) {
	
    	// default size of the board
    	rowSize = 8;
    	colSize = 8;
    	
    	try {
    		rowSize = Integer.parseInt(ar[0]);
    		colSize = Integer.parseInt(ar[1]);
    	} catch (Exception ex) {
    		System.out.print("");
    	}  // no exception expected
	
    	// create an algorithm object
    	kt = new KnightTour(rowSize, colSize);
    	if (rowSize == 8)
    		isStd = true;
    	else
    		isStd = false;
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
	
    	// to obtain the arguments, no longer needed
    	// args = getParameters().getRaw().toArray(new String[0]);
    	// processArgs(args);
    	
    	// lock the size of the primary stage
    	priStage.setResizable(false);

        // setting the home scene
        BorderPane homeP = new BorderPane();
        // homeP.setPadding(new Insets(20,10,10,10));
        homeScene = new Scene(homeP, 680, 645);
        
        // the top part
        StackPane topPane = new StackPane();
        Image knightHome = new Image("knightHome.png");
        ImageView img = new ImageView(knightHome);
        img.setFitHeight(475);
        img.setFitWidth(475);
        topPane.getChildren().add(img);
        topPane.setPadding(new Insets(30,30,0,0));
        
        // the center part
        StackPane centerPane = new StackPane();
        centerPane.setPadding(new Insets(5,30,5,30));
        VBox intro = new VBox();
        HBox hbox = new HBox();
        Text text1 = new Text("Created by Zhiwei Jia, this is an interesting" +
              " tool for solving the ancient Knight's Tour puzzle."); 
        Text text2 = new Text("     For more information, welcome to visit my personal website: ");
        Text website = new Text("http://zhiweijia.net");
        website.setUnderline(true);
        website.setFill(Color.BLUE);
        text1.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
        website.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
        text2.setFont(Font.font("Helvetica", FontWeight.NORMAL, 14));
        website.setOnMouseClicked(e -> getHostServices().showDocument("http://zhiweijia.net"));
        hbox.getChildren().addAll(text2, website);
        intro.getChildren().addAll(text1, hbox);
        VBox.setMargin(hbox, new Insets(10,0,0,35));
        VBox.setMargin(text1, new Insets(0,0,0,30));
        centerPane.getChildren().add(intro);
        
        // the bottom part
        HBox boxForChoice = new HBox();
        RadioButton eight = new RadioButton("8x8 board");
        RadioButton six = new RadioButton("6x6 board");
        RadioButton five = new RadioButton("5x5 board");
        ToggleGroup group = new ToggleGroup();
        eight.setToggleGroup(group);
        six.setToggleGroup(group);
        five.setToggleGroup(group);
        boxForChoice.setPadding(new Insets(30, 5, 5, 5));
        boxForChoice.getChildren().addAll(eight, six, five);
        Button start = new Button("Start");
        start.setOnAction(e -> {
        	if (eight.isSelected())
        		startProgram(8, priStage);
        	else if (six.isSelected())
        		startProgram(6, priStage);
        	else if (five.isSelected())
        		startProgram(5, priStage);
        });
        boxForChoice.getChildren().add(start);
        HBox.setMargin(start, new Insets(-4, 0, 0, 30));
        HBox.setMargin(eight, new Insets(0, 0, 40, 165));
        boxForChoice.setSpacing(10);
        homeP.setTop(topPane);
        homeP.setCenter(centerPane);
        homeP.setBottom(boxForChoice);
            
        // show the stage
        priStage.setScene(homeScene);
        priStage.show();
        priStage.setTitle("Finding Knight's Tour");
    } 
    
    /**
     * Method to change the scene and start the program
     * @param boardSize the size of the board 
     * @param stage passing the primary stage
     */
    private void startProgram(int boardSize, Stage stage) {
    	args[0] = "" + boardSize;
    	args[1] = "" + boardSize;
    	processArgs(args);
    	changeScene(stage);    	
    	if (afterReset) 
    		restart();
    }
    
    /**
     * Method to show the board and change the scene
     * @param stage the primary stage
     */
    private void changeScene(Stage stage) { 
    	
    	// create stackPane array
    	sArr = new StackPane[rowSize][colSize];
    	for (int i = 0; i < rowSize; i++) 
    		for (int j = 0; j < colSize; j++) 
    			sArr[i][j] = new StackPane();
	
    	// create a grid pane as the board
    	GridPane gP = new GridPane();
    	bArr = new Button[rowSize][colSize];
    	for (int row = 0; row < rowSize; row++) {
    		for (int col = 0; col < colSize; col++) {
    			Rectangle sq = new Rectangle();
		
    			// set their color
    			if ((row+col) % 2 == 0)
    				sq.setFill(Color.LAVENDERBLUSH);
    			else 
    				sq.setFill(Color.LIGHTSKYBLUE);
    			
    			// different size for standard and non-standard board
    			if (isStd) {
    				sq.setWidth(56);
    				sq.setHeight(56);
    			} else {
    				sq.setWidth(70);
    				sq.setHeight(70);
    			}
    			
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
    	for (int i = 0; i < rowSize; i++) 
    		for (int j = 0; j < colSize; j++) 
    			bArr[i][j].setOnAction(handler);
	
    	// set the notFound to false
    	notFound = new SimpleBooleanProperty();
    	notFound.set(false);
	
    	// create a button for restart
    	StackPane sP = new StackPane();
    	HBox hb = new HBox();
    	Rectangle r = new Rectangle();
    	r.setHeight(20);
        r.widthProperty().bind(stage.widthProperty().subtract(20));
        r.setVisible(false);
        restart = new Button("Restart (unavailable)");
        restart.setDisable(true);
        isFinished = new SimpleBooleanProperty();
        isFinished.set(true);
        
	
        // set the canRetouch to true, which indicate whether able to 
        // touch each button of cell to start a new search
        canRetouch = true;
	
        // change the restart button for different situation
        isFinished.addListener(e1 -> {
        	if (!isFinished.get()) {
        		restart.setText("Restart (unavailable)");
        		restart.setDisable(true);
        	} else if (!notFound.get()) {
        		restart.setText("Restart");
        		restart.setDisable(false);
        	} else {
        		restart.setText("No such tour. Restart and try again!");
                restart.setDisable(false);	    
        	}
        });	
        notFound.addListener(e2 -> {
        	if (!isFinished.get()) {
        		restart.setText("Restart (unavailable)");
        		restart.setDisable(true);
        	} else if (!notFound.get()) {
        		restart.setText("Restart");
        		restart.setDisable(false);
        	} else {
        		restart.setText("No such tour. Restart and try again!");
                restart.setDisable(false);	    
        	}
        });
	
        // the home button
        Button home = new Button("Home");
        home.setOnAction(e -> backToHome(stage));
        
        // add the restart button and the home button to the board
        sP.getChildren().add(r);
        hb.getChildren().addAll(restart, home);
        sP.getChildren().add(hb);
        vBox.getChildren().add(sP);
        HBox.setMargin(restart, new Insets(5, 150, 0, 0));
        hb.setAlignment(Pos.BASELINE_RIGHT);
        HBox.setMargin(home, new Insets(5, 200, 0, 0));
	
        // register the restart button
        restart.setOnAction(e -> {
	    if (isFinished.get())
	    	restart();
        });	
        
        // change the scene
        mainScene = new Scene(thePane, 680, 645); 
        stage.setScene(mainScene);
    }
    
    /**
     * Method to back to home
     * @param stage passing the primary stage
     */
    private void backToHome(Stage stage) {
        start(stage); 
        stop = true;
    	afterReset = true;
    	
    	// to clear the ongoing process
    	restart();
    }
    
    /**
     * Method to show the result of the tour
     */
    private void showResult() {
	
    	// restart the showing process
    	stop = false;
    	
    	// set the knight picture
    	knightPic = new ImageView("knight's tour.png");
    	
    	// set the initial x and y coordinates
    	currRow = kt.getTour().peekFront().row;
        currCol = kt.getTour().removeFront().col;
        tempX = currCol+1;
        tempY = currRow+1;
        
        // different size for standard and non-standard board
        if (isStd) {
        	tempX = 86*tempX-37;
        	tempY = 76*tempY-30;
        } else {
        	tempX = 100*tempX-45;
        	tempY = 90*tempY-40;
        }

        // different size for standard and non-standard board
        if (isStd) {
        	knightPic.setFitHeight(56);
        	knightPic.setFitHeight(56);
        } else {
        	knightPic.setFitHeight(70);
        	knightPic.setFitHeight(70);
        }
        
        // the first appearance of the knight
        knightPic.setVisible(false);
        thePane.getChildren().add(knightPic);
        knightPic.setX(tempX-30);
        knightPic.setY(tempY-30);
	
        // create an animation to show the tour
        try {
        	tl = new Timeline(new KeyFrame(new Duration(150), e -> {
        		if (!stop) 
        			moveKnight();
        		else {
        			tl.stop();
        			tl.setCycleCount(0);
        		}	
        	}));
        	tl.setCycleCount(kt.getNumCell()+1);
            tl.play();
        } catch (NullPointerException ex) { /* don't worry */ }
    }
    
    /**
     * Method to implement the animation for showing the tour
     * @throws NullPointerException
     */
    private void moveKnight() throws NullPointerException {
        
    	// some values relevant to the position
    	int row = 0;
    	int col = 0;
    	int nextX = 0;
    	int nextY = 0;
	
    	// move the knight to the first position
    	if (firstStep) {
            firstStep = false;
        	// display the knight picture
            knightPic.setVisible(true);
            return;
    	}
    		
    	// do not try to get new values for the last iteration
    	if (stepCount < kt.getNumCell()-1) {
    		try {
    			row = kt.getTour().peekFront().row;
    			col = kt.getTour().removeFront().col;
    		} catch (NullPointerException ex) {}
    	}
	
    	// some more important values 
        nextX = col + 1;
        nextY = row + 1;
        
        // different size for standard and non-standard board
        if (isStd) {
            nextX = 86*nextX-37;
            nextY = 76*nextY-30;
        } else {
        	nextX = 100*nextX-45;
        	nextY = 90*nextY-40;
        }
       
        // do not execute the last translation
        if (stepCount < kt.getNumCell()-1) {
        	knightPic.setVisible(true);
            Line l = new Line(tempX, tempY, nextX, nextY); 
            new PathTransition(new Duration(130), l, knightPic).play(); 
        }
	    
        // add the number to the board after the knight's moving
        // not adding any number for the last move
        Text t = new Text(++stepCount + "");
        t.setFont(Font.font("Times New Roman", 20));
        if (stepCount < rowSize*colSize)        	
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
	
    	// delete the old knight
        thePane.getChildren().remove(knightPic);
        knightPic = null;
        System.gc();
	
    	// update the status of the finding
    	notFound.set(false);
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
    	        
    			// different size for standard and non-standard board
    			if (isStd) {
    				sq.setWidth(56);
    				sq.setHeight(56);
    			} else {
    				sq.setWidth(70);
    				sq.setHeight(70);
    			}
    	        
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
