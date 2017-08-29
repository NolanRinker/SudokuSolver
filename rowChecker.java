import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class rowChecker extends Thread {
	
	private static int row = SudokuSolver.numRow;
	private static int board[][] = SudokuSolver.board;
	static CyclicBarrier start; 
	static CyclicBarrier end;
	
	public rowChecker(int row, int board[][], CyclicBarrier start, CyclicBarrier end){
		rowChecker.row = row;
		rowChecker.board = board;
		rowChecker.start = start;
		rowChecker.end = end;
		
	}
	
	public void run(){
		while (true) {
			try {
				start.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
			
			checkRow(rowChecker.row);
			
			try {
				end.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}
	public static boolean checkRow(int row){
        int count[] = new int[10];
        for(int col=0; col<9; col++){
            count[board[row][col]]++;
        }

        boolean countIsOk = true;
        for(int i=1; i<=9; i++)
            countIsOk = (countIsOk && (count[i]<=1));
        return countIsOk;
    }
}