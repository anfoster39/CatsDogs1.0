package catsdogs.g2;

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
		ArrayList<PossibleMove> moves2 = Cat.allLegalMoves(board);
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
		return moves.get(which);
		
	}
}