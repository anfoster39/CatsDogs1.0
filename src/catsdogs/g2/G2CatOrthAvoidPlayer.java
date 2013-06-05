package catsdogs.g2;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import catsdogs.sim.Board;
import catsdogs.sim.Cat;
import catsdogs.sim.Dog;
import catsdogs.sim.Move;
import catsdogs.sim.PossibleMove;

public class G2CatOrthAvoidPlayer extends catsdogs.sim.Player {
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	private final Random r = new Random();


	public String getName() {
		return "G2CatOrthAvoidPlayer";
	}

	public void startNewGame() {
		logger.info("G2 player starting new game!");

	}
	public Move doMove(int[][] board) {
		ArrayList<PossibleMove> moves = Cat.allLegalMoves(board);
		boolean ok = false;
		int size = moves.size();
		int which = r.nextInt(size);
		while(!ok){
			if(!Dog.wins(moves.get(which).getBoard())){
				ok=true;
			}else{
				moves.remove(which);

				 size = moves.size();
				 which = r.nextInt(size);
			}
		}
		int test = getOrthCount(board);
		logger.info(test);
		return moves.get(which);
		
	}
	public int getOrthCount(int[][] board){
		int orthCt = 0;

		for(int i = 0; i < 7; i++){
			for(int j = 0; j < 7; j++){
				if(board[i][j]==Board.CAT){
					Point2D.Double ptHere = new Point2D.Double(i,j);
					orthCt+=getAdjacentCount(board, ptHere);
				}
			}
		}
		
		return orthCt;
	}
	public int getAdjacentCount(int[][] board, Point2D.Double pt) {
		int adjCt = 0;
		try{
			if(board[(int) pt.x][(int) (pt.y+1)]==Board.DOG){
				adjCt++;
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			
		}
		try{

			if(board[(int) pt.x][(int) (pt.y-1)]==Board.DOG){
				adjCt++;
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			
		}try{
			if(board[(int) pt.x+1][(int) (pt.y)]==Board.DOG){
				adjCt++;
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			
		}try{
			if(board[(int) pt.x-1][(int) (pt.y)]==Board.DOG){
				adjCt++;
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			
		}
		return adjCt;
	}

}