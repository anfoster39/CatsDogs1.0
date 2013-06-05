package catsdogs.g2;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import catsdogs.sim.Board;
import catsdogs.sim.Cat;
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
		ArrayList<PossibleMove> moves2 = Cat.allLegalMoves(board);
		boolean ok = false;
		int size = moves.size();
		int which = r.nextInt(size);
		while(!ok){
			if(!isThreeInARow(moves.get(which), board)){
				ok=true;
			}else{
				moves.remove(which);

				 size = moves.size();
				 which = r.nextInt(size);
			}
		}
		return moves.get(which);
		
	}
	private boolean isThreeInARow(PossibleMove move, int[][]board){

		for(int i = 0; i < 7; i++){
			int catCt = 0;
			if((move.getX()==i+1 && move.getDirection()==Move.LEFT )||(move.getX()==i-1&&move.getDirection()==Move.RIGHT)){
				catCt++;
			}
			for(int j = 0; j < 7; j++){
				if(board[i][j]==Board.CAT ){
					catCt++;
				}
			}
			if(catCt>2){
				return true;
			}
		}
		for(int j = 0; j < 7; j++){
			int catCt = 0;
			if((move.getY()==j+1 &&move.getDirection()==Move.UP )||(move.getY()==j-1 &&move.getDirection()==Move.DOWN)){
				catCt++;
			}
			for(int i = 0; i < 7; i++){
				if(board[i][j]==Board.CAT){
					catCt++;
				}
			}
			if(catCt>2){
				return true;
			}
		}
		return false;
	}
	
}
