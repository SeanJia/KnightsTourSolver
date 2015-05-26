/**
 * File: KnightTour.java
 * @author Zhiwei Jia
 */

import java.util.ArrayList;
/**
 * Class Name: KnightTour
 * Description: a class to use backtracking algorithm with multithreading
 *              to find a knight's tour from anywhere in board of any size
 */
public class KnightTour {
    
    // the number of total cells in the board
    private int numCell;
    
    // some Deques to store tours
    private MyDeque<Cell> tour;
    private MyDeque<Cell> tour1;    
    private MyDeque<Cell> tour2;
    private MyDeque<Cell> tour3;
    private MyDeque<Cell> tour4;
    
    // some boards for multi-threading
    private Cell[][] board;
    private Cell[][] board1;
    private Cell[][] board2;
    private Cell[][] board3;
    private Cell[][] board4;
    
    // whether the board is a square matrix
    private boolean square;
    
    // the initial position of the tour
    private int iniRow;
    private int iniCol;
    
    // multiple threads
    private Thread[] threadArr = new Thread[4];
    
    // a value to tell whether the tour is found
    private volatile boolean hasFoundTour;
    
    /**
     * Constructor with specified initial position
     * @param rowSize initial row position
     * @param colSize initial col position
     */
    public KnightTour(int rowSize, int colSize) {
	
	// set values of the board
	numCell = rowSize*colSize;
	if (rowSize == colSize)
	    square = true;
	
	// initialize objects 
	tour1 = new MyDeque<>(numCell);
	tour2 = new MyDeque<>(numCell);
	tour3 = new MyDeque<>(numCell);
	tour4 = new MyDeque<>(numCell);
	board = new Cell[rowSize][colSize];
        board1 = new Cell[rowSize][colSize];
        board2 = new Cell[rowSize][colSize];
        board3 = new Cell[rowSize][colSize];
        board4 = new Cell[rowSize][colSize];
        
        // initialize boards
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) 
        	board1[i][j] = new Cell(i, j);
        }
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) 
        	board2[i][j] = new Cell(i, j);
        }
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) 
        	board3[i][j] = new Cell(i, j);
        }
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) 
        	board4[i][j] = new Cell(i, j);
        }
    }
    
    /**
     * Method to return a list of Cells that can be reached from
     * the given position; support the multi-threading;
     * @param row given row position
     * @param col given col position
     * @param thread a number to stand for certain thread 
     * @return an ArrayList for the Cells
     */
    public ArrayList<Cell> getReachable(int row, int col, int thread) {
	ArrayList<Cell> result = new ArrayList<>(); 
	
	// nested loop to search through all the possible positions
	for (int i = -2; i <= 2; i++) {
	    for (int j = -2; j <= 2; j++) {
		
		// if within scope of the board, add it to the list
		if (Math.abs(i)+Math.abs(j) == 3 && row+i < board.length 
	            && row+i >= 0 && col+j >= 0 && col+j < board[0].length) {
		    
		    // support the multi-threading
		    switch (thread) {
			case 1: result.add(board1[row+i][col+j]); break;
			case 2: result.add(board2[row+i][col+j]); break;
			case 3: result.add(board3[row+i][col+j]); break;
			case 4: result.add(board4[row+i][col+j]); break;
			
		    }
		}
	    }
	}
	
	// return the result
	return result;
    }
    
    /**
     * Method to find the tour; use backtracking algorithm to implement
     * @param iniRow initial row position
     * @param iniCol initial col position
     * @return whether successfully found a tour 
     */
    public boolean tour(int iniRow, int iniCol) {
	
	// set the relevant values
	this.iniRow = iniRow;
	this.iniCol = iniCol;
	
	// initialize boards
	Cell ini1 = board1[iniRow][iniCol];
	tour1.addBack(ini1);
	ini1.visited = true;
	Cell ini2 = board2[board.length-1-iniRow][board[0].length-1-iniCol];
	tour2.addBack(ini2);
	ini2.visited = true;
	
	// the square case
	if (square) {
	    Cell ini3 = board3[iniCol][iniRow];
	    tour3.addBack(ini3);
	    ini3.visited = true;
	    Cell ini4 = board4[board.length-1-iniCol][board[0].length-1-iniRow];
	    tour4.addBack(ini4);
	    ini4.visited = true;
	}
	
	// create threads to find such a tour
	// the square case
	if (square) {
	    for (int i = 0; i < 4; i++) 
		threadArr[i] = new Thread(new FindingTask(i));
	    for (int i = 0; i < 4; i++) {
		threadArr[i].start();
	    }
	} else {
	    threadArr[0] = new Thread(new FindingTask(0));
	    threadArr[1] = new Thread(new FindingTask(1));
	    threadArr[0].start();
	    threadArr[1].start();
	}

	// put the main thread to sleep until has found a tour
	try {
	    while(!hasFoundTour)
		Thread.sleep(200);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	
	// return whether it has found a tour
	return hasFoundTour;
    }
    
    /**
     * Helper method to implement the backtracking algorithm, where create
     * threads to search from different direction for the tour
     * @param row initial row position
     * @param col initial col position
     * @param stepToGo whether reach a dead end
     * @param thread number indicating which thread
     * @return whether succeed for the current step
     */
    private boolean tour(int row, int col, int stepToGo, int thread) {
	
	// stop the finding of other threads if one has found a tour
	if (hasFoundTour)
	    return true;
	
	// for the last step, success!
	if (stepToGo == 0)
	    return true;
	
	// backtracking algorithm
	ArrayList<Cell> currArr = getReachable(row, col, thread); 
	int i = 0;
	while (i < currArr.size()) {
	    Cell curr = currArr.get(i);
	    if (!curr.visited) {
		
		// add the current step; support multi-threading
		switch (thread) {
		    case 1: tour1.addBack(curr); break;
		    case 2: tour2.addBack(curr); break;
		    case 3: tour3.addBack(curr); break;
		    case 4: tour4.addBack(curr); break;
		}
		curr.visited = true;
	    } else {
		i++;
		continue;
	    }
	    
	    // recursive call
	    if (!tour(curr.row, curr.col, --stepToGo, thread)) {
		stepToGo++;
		curr.visited = false;
		i++;
		
		// if not succeed, remove the current step
		// support multi-threading
		switch (thread) {
		    case 1: tour1.removeBack(); break;
		    case 2: tour2.removeBack(); break;
		    case 3: tour3.removeBack(); break;
		    case 4: tour4.removeBack(); break;
		}
		
		// and then try another direction for the current step
		continue;
	    } else
		return true;    // succeed!
	}
	
	// as i == currArr.size(), we run out of our choice, 
	// and so no solution exists, we get back for another try
	return false;	
    }
    
    /**
     * An inner class for the Cell
     */
    class Cell {
	int row;
	int col;
	boolean visited;
	
	/**
	 * Construtor for a cell
	 * @param row
	 * @param col
	 */
	public Cell(int row, int col) {
	    this.row = row;
	    this.col = col;
	}
	
	@Override
	/**
	 * the toString method
	 */
	public String toString() {
	    return row + " " + col;
	}
    }
    
    /**
     * An inner class as a task for finding tour
     */
    class FindingTask implements Runnable {
	
	// a value indicating which task it is
	int taskID;
	
	/**
	 * Constructor for a task object
	 * @param taskID its ID
	 */
	FindingTask(int taskID) {
	    this.taskID = taskID;
	}
	
	@Override
        /**
         * to run the task, where we search via four options of direction
         * support multi-threading
         */
	public void run() {
	    
	    // the first direction
	    if (taskID == 0) {
		if (tour(iniRow, iniCol, numCell - 1, 1)) 
		    setTour(tour1); // set the tour this thread found 
	    }
	    
	    // the second one
	    if (taskID == 1) {
		if (tour(board.length-1-iniRow,board[0].length-1-iniCol,
			numCell-1,2)) {
		    
		    // rotate and set the tour this thread found
		    rotate(tour2);
		    setTour(tour2);
		}
	    }
	    
	    // the third one; work only if square board
	    if (taskID == 2 && square) {
		if (tour(iniCol, iniRow, numCell - 1, 3)) {
		    
		    // reflect and set the tour this thread found
		    reflect(tour3);
		    setTour(tour3);
		}
	    }
	    
	    // the fourth one; only work if square board
	    if (taskID == 3 && square) {
		if (tour(board.length-1-iniCol,board[0].length-1-iniRow,
			numCell-1,4)) {
		    
		    // rotate and reflect and then set the tour
		    // that this thread found
		    rotate(tour4);
		    reflect(tour4);
		    setTour(tour4);
		}
	    }
	}
    }
    
    /**
     * Method to return the tour Deque
     * @return tour
     */
    public MyDeque<Cell> getTour() {
	return tour;
    }
    
    /**
     * Method to return the total number of cells in the board
     * @return number of cells
     */
    public int getNumCell() {
	return numCell;
    }
    
    /**
     * Helper method to rotate a tour's step list 
     * @param candidate the list to be rotated
     */
    private void rotate(MyDeque<Cell> candidate) {
        for (int i = 0; i < numCell; i++) {
            Cell curr = candidate.removeBack();
            curr.row = board.length-1-curr.row;
            curr.col = board[0].length-1-curr.col;
            candidate.addFront(curr);
        }
    }
    
    /**
     * Helper method to reflect a tour's step list
     * @param candidate the list to be reflected
     */
    private void reflect(MyDeque<Cell> candidate) {
	for (int i = 0; i < numCell; i++) {
	    Cell curr = candidate.removeBack();
	    int temp = curr.row;
	    curr.row = curr.col;
	    curr.col = temp;
	    candidate.addFront(curr);
	}
    }
    
    /**
     * Helper method to set the tour that a thread found to the 
     * output tour's step list; synchronized and guaranteed to 
     * only set once
     * @param tour the found tour
     */
    private synchronized void setTour(MyDeque<Cell> tour) {
	if (!hasFoundTour) 
	    this.tour = tour;
	
	// indicate that this program has found a tour
	hasFoundTour = true;
    }
}
