package catsdogs.g2;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import catsdogs.sim.Cat;
import catsdogs.sim.Move;
import catsdogs.sim.PossibleMove;

public class G2CatPlayer extends catsdogs.sim.Player {
	private Logger logger = Logger.getLogger(this.getClass()); // for logging

	private final Random r = new Random();

	public String getName() {
		return "G2CatPlayer";
	}

	public void startNewGame() {
		logger.info("G2 player starting new game!");

	}
	public Move doMove(int[][] board) {
		
		// find all legal moves
		ArrayList<PossibleMove> moves = Cat.allLegalMoves(board);
		
		int size = moves.size();
		int which = r.nextInt(size);
		
		return moves.get(which);
	}

}
