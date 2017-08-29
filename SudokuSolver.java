import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class SudokuSolver {

    public static int board[][];
	public static int numRow;
	public static int numCol;
	public static int numSub;
    private int start[][];
    public static CyclicBarrier startBarrier = new CyclicBarrier(4);
    public static CyclicBarrier endBarrier = new CyclicBarrier(4); 

    /**
     * Creates a new instance of SudokuSolver
     */

    public SudokuSolver(){
        setStart(new int[9][9]);
        board = new int[9][9];
    }

    public String toString(){
        String puzzleString = "Row/Col\n   1  2  3  4  5  6  7  8  9\n";
        puzzleString = puzzleString + "  --------------------------\n";
        for(int i=0; i<9; i++){
            puzzleString = puzzleString + (i+1) + " |";
            for(int j=0; j<9; j++){
                if(board[i][j] == 0)
                    puzzleString = puzzleString + ".  ";
                else
                    puzzleString = puzzleString + board[i][j] + "  ";
            }
            puzzleString = puzzleString + "\n";
        }
        return puzzleString;
    }

    public void addInitial(int row, int col, int value){
        if(row>=0 && row<=9 && col>=0 && col<=9 && value>=1 && value<=9){
            getStart()[row][col] = value;
            board[row][col] = value;
        }
    }

    public void addGuess(int row, int col, int value){
        // only set the value if the start is 0
        if(row>=0 && row<=9 && col>=0 && col<=9 && value>=1 && value<=9 && getStart()[row][col] == 0){
            board[row][col] = value;
        }
    }

    public int getValueIn(int row, int col){
        return board[row][col];
    }

    public void reset(){
        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                board[i][j] = getStart()[i][j];
    }

    public boolean isFull(){
        boolean allFilled = true;
        for(int i=0; i<9; i++)
            for(int j=0; j<9; j++)
                allFilled = allFilled && board[i][j]>0;

        return allFilled;
    }

    public boolean[] getAllowedValues(int row, int col){
        // Save the value at the location, then try all 9 values
        int savedValue = board[row][col];
        boolean result[] = new boolean[9];

        for(int value = 1; value <=9; value++){
            board[row][col] = value;
            result[value-1] = checkPuzzle();
        }
        board[row][col] = savedValue;
        return result;
    }

    public boolean checkPuzzle(){
        boolean looksGood = true;
        // See if the values in the squares are legal;
        for(int i=0; i<9; i++){
            looksGood = looksGood && rowChecker.checkRow(i);
             looksGood = looksGood && colChecker.checkCol(i);
             looksGood = looksGood && subChecker.checkSub(i);
        }
        return looksGood;
    }

    public boolean checkRow(int row){
        // Make sure a number only appears once in the row
        int count[] = new int[10];
        for(int col=0; col<9; col++){
            count[board[row][col]]++;
        }

        boolean countIsOk = true;
        for(int i=1; i<=9; i++)
            countIsOk = countIsOk && (count[i]<=1);

        return countIsOk;
    }

    public boolean checkCol(int col){
        // Make sure a number only appears once in the col
        int count[] = new int[10];
        for(int row=0; row<9; row++){
            count[board[row][col]]++;
        }

        boolean countIsOk = true;
        for(int i=1; i<=9; i++)
            countIsOk = countIsOk && (count[i]<=1);

        return countIsOk;
    }

    public boolean checkSub(int sub){
        // Make sure a number only appears once in a 3by3 subarray

        int count[] = new int[10];
        int rowBase = (sub/3) *3;  // This will give 0, 3, or 6 because of integer division
        int colBase = (sub%3) *3;

        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                count[board[rowBase+i][colBase+j]]++;
            }
        }

        boolean countIsOk = true;
        for(int i=1; i<=9; i++)
            countIsOk = countIsOk && (count[i]<=1);

        return countIsOk;
    }


    // Initialize the puzzle with puzzle.txt
    public static void initializePuzzle(SudokuSolver p){
	String fileName="puzzle.txt";
	Scanner inputStream=null;
	try{
	    inputStream=new Scanner(new File(fileName));
	}catch(FileNotFoundException e){
	    System.out.println("Error cannot open file "+fileName);
	    System.exit(0);
	}
	

	int row=0;
	int col=0;
	while(inputStream.hasNextInt()){
	    if(col==9){
		row++;
		col=0;
	    }
	    p.addInitial(row,col,inputStream.nextInt());
	    col++;
	}
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    	
      Scanner reader = new Scanner(System.in);

        System.out.println("Sudoku Game: ");

        SudokuSolver puzzle = new SudokuSolver();
	//Read puzzle initial input
        initializePuzzle(puzzle);
        System.out.print("The puzzle is: \n" + puzzle);
        
        CyclicBarrier start = SudokuSolver.startBarrier;
        CyclicBarrier end = SudokuSolver.endBarrier;
        
        //created 3 threads to check each
        rowChecker rowC = new rowChecker(numRow, board, start, end);
        colChecker colC = new colChecker(numCol, board, start, end);
        subChecker subC = new subChecker(numSub, board, start, end);
        rowC.start();
        colC.start();
        subC.start();

        boolean done = false;
        while(!done){
	            System.out.println("What would you like to do? \n" +
	                    "Clear Puzzle(C) Set a square (S) Get possible values (G) Quit (Q) ");
	            String response = reader.next();
	            response = response.toLowerCase();	
	            
	            boolean rightAnswer = false;	//added for loop so if incorrect response is entered then try again
	        while (rightAnswer == false){
	            if(response.equals("q")){
	                System.out.println("Thanks for playing.");
	                done = true;
	                rightAnswer = true;
	            } else if(response.equals("s")){
	                System.out.println("Which row (1-9) and colume (1-9) do you want to change?");
	                int row = reader.nextInt()-1;
	                int col = reader.nextInt()-1;
	                System.out.println("What should the value (1-9) be?");
	                int value = reader.nextInt();
	                try {
						start.await();
					} catch (InterruptedException | BrokenBarrierException e) {
					
						e.printStackTrace();
					}
	                puzzle.addGuess(row, col, value);
	                rightAnswer = true;
	            } else if(response.equals("g")){
	                System.out.println("Which row (1-9) and colume (1-9) do you want to get values for?");
	                int row = reader.nextInt()-1;
	                int col = reader.nextInt()-1;
	
	                boolean valid[] = puzzle.getAllowedValues(row, col);
	
	                System.out.print("Allowed values are: ");
	                for(int i=0; i<9; i++){
	                    if(valid[i])
	                        System.out.print((i+1)+ "  ");
	                }
	                System.out.println();
	                rightAnswer = true;
	            } else if(response.equals("c")){
	                puzzle.reset();
	            } else {
	            	System.out.println("What would you like to do? \n" +
		                    "Clear Puzzle(C) Set a square (S) Get possible values (G) Quit (Q) ");
		            response = reader.next();
		            response = response.toLowerCase();
	            }
	        }    
            try {
    			end.await();
    		} catch (InterruptedException | BrokenBarrierException e) {
    			e.printStackTrace();
    		}
            System.out.print("The puzzle is now: \n" + puzzle);
            if(!puzzle.checkPuzzle())
                System.out.println("You have made an error in the puzzle.");
            else if(puzzle.isFull())
                System.out.println("Congratulations, you have completed the puzzle.");
        }
        
    }

	public int[][] getStart() {
		return start;
	}

	public void setStart(int start[][]) {
		this.start = start;
	}

}
