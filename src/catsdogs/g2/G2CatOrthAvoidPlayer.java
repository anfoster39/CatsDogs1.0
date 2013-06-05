package catsdogs.g2;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
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

	public String getName() {
		return "G2CatOrthAvoidPlayer";
	}

	public void startNewGame() {
		logger.info("G2 player starting new game!");

	}
	public Move doMove(int[][] board) {
		ArrayList<PossibleMove> moves = Cat.allLegalMoves(board);
		return getBestCatMove(board);
		
	}
	
	/**
	 * Takes the current board and finds the best cat move based on our scoring
	 * @param currentBoard
	 * @return
	 */
	public PossibleMove getBestCatMove(int[][] currentBoard){
		//looks at each option 
		ArrayList<PossibleMove> moves = Cat.allLegalMoves(currentBoard);
		//HashMap<PossibleMove, Integer> scores = new HashMap<PossibleMove, Integer>();
		PossibleMove bestMove = null;
		int bestScore = 10000;
		for (PossibleMove move: moves){
			int score = getFutureScore(move);
			if (score < bestScore){
				bestScore = score;
				bestMove = move;
			}
		}
		logger.info("Cat move score " + bestScore);
		return bestMove;
	}
	
	
	/**
	 * @param move 
	 * @return
	 */
	private Integer getFutureScore(PossibleMove move) {
		PossibleMove bestDogMove = getBestDogSingleMove(Dog.allLegalMoves(move.getBoard()));
		ArrayList<PossibleMove> nextCatMoves = Cat.allLegalMoves(bestDogMove.getBoard());
		int best = 1000;
		for (PossibleMove catMove: nextCatMoves){
			int score = score(catMove);
			if (score < best){
				best = score; 
			}
		}
		return best;
	}

	/**
	 * Scores a single board for the cat
	 * @param catMove
	 * @return the score 
	 */
	private int score(PossibleMove catMove) {
		int score = Cat.allLegalMoves(catMove.getBoard()).size();
		if(Dog.wins(catMove.getBoard())){
			score += 1000;
		}
		if(Cat.wins(catMove.getBoard())){
			score = -100;
		}
		return score;
	}

	/**
	 * Anticipates which move the dog will pick
	 * @param moves
	 * @return
	 */
	public PossibleMove getBestDogSingleMove(ArrayList<PossibleMove> moves){
		  Random r = new Random();

		int size = moves.size();
		int which = r.nextInt(size);
		PossibleMove firstM = moves.get(which);
		ArrayList<PossibleMove> secondOptions = Dog.allLegalMoves(firstM.getBoard());
		size = secondOptions.size();
		which = r.nextInt(size);
		PossibleMove retMove = secondOptions.get(which);
		return retMove;
	}
	
	
//	public ArrayList<PossibleMove> dogBestMoves(ArrayList<PossibleMove> moves){
//		ArrayList<PossibleMove> dogMoves = new ArrayList<PossibleMove>();
//		for(PossibleMove pm : moves){
//			dogMoves.add(getBestDogSingleMove(Dog.allLegalMoves(pm.getBoard())));
//		}
//		
//		return dogMoves;
//		
//	}
//	public PossibleMove returnBestMove(ArrayList<PossibleMove> dogMoves){
//		//pruning dogs moves down to best dog move
//		PossibleMove best;
//		for(PossibleMove pm : dogMoves){
//			
//		}
//		
//		return best;
//	}
	
	public PossibleMove getMoveWithLowestOrth(ArrayList<PossibleMove> moves){
		int minOrth = 100000;
		PossibleMove which = moves.get(0);
		for(int i = 0; i < moves.size(); i++){
			if(Cat.allLegalMoves(moves.get(i).getBoard()).size()<minOrth){
				minOrth = Cat.allLegalMoves(moves.get(i).getBoard()).size();
				which = moves.get(i);
			}
		}
		return which;
		
	}

}