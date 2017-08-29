import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class colChecker extends Thread {
	
	private static int col = SudokuSolver.numCol;
	private static int board[][] = SudokuSolver.board;
	static CyclicBarrier start;
	static CyclicBarrier end; 
	
	public void run(){
		while (true) {
			try {
				start.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
			checkCol(colChecker.col);
			try {
				end.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}
	
	public colChecker(int col, int board[][], CyclicBarrier start, CyclicBarrier end){
		colChecker.col = col;
		colChecker.board = board;
		colChecker.start = start;
		colChecker.end = end;
	}
	
	public static boolean checkCol(int col){
		int count[] = new int[10];
		for(int row=0; row<9; row++){
			count[board[row][col]]++;
		}
			boolean countIsOk = true;
		for(int i=1; i<=9; i++)
			countIsOk = (countIsOk && (count[i]<=1));
			return countIsOk;
	}	
	
}
