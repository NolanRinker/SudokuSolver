import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class subChecker extends Thread {
	
	private static int sub = SudokuSolver.numSub;
	private static int board[][] = SudokuSolver.board;;
	static CyclicBarrier start;
	static CyclicBarrier end; 
	
	public void run(){
		while (true) {
			try {
				start.await();
			} catch (InterruptedException | BrokenBarrierException e) {

				e.printStackTrace();
			}
			checkSub(subChecker.sub);
			try {
				end.await();
			} catch (InterruptedException | BrokenBarrierException e) {

				e.printStackTrace();
			}
		}
	}
	
	public subChecker(int sub, int board[][], CyclicBarrier start, CyclicBarrier end){
		subChecker.sub = sub;
		subChecker.board = board;
		subChecker.start = start;
		subChecker.end = end;
	}
	
	public static boolean checkSub(int sub){
        int count[] = new int[10];
        int rowBase = (sub/3) *3;  
        int colBase = (sub%3) *3;

        for(int i=0; i<3; i++){
            for(int j=0; j<3; j++){
                count[board[rowBase+i][colBase+j]]++;
            }
        }

        boolean countIsOk = true;
        for(int i=1; i<=9; i++)
            countIsOk = (countIsOk && (count[i]<=1));
        return countIsOk;
    }
}