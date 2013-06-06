package catsdogs.g0;


import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import catsdogs.sim.Cat;
import catsdogs.sim.Move;
import catsdogs.sim.PossibleMove;




public class RandomCatPlayer extends catsdogs.sim.CatPlayer {

	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	
	private final Random r = new Random();
	
	public String getName() {
		return "Random Cat Player";
	}

	public void startNewGame() {
		logger.info("Cat player starting new game!");

	}

	public Move doMove(int[][] board) {
		
		// find all legal moves
		ArrayList<PossibleMove> moves = Cat.allLegalMoves(board);
		
		int size = moves.size();
		int which = r.nextInt(size);
		
		return moves.get(which);
	}


}
