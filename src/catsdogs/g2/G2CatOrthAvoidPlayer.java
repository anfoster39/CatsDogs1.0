package catsdogs.g2;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import catsdogs.sim.Cat;
import catsdogs.sim.Dog;
import catsdogs.sim.Move;
import catsdogs.sim.PossibleMove;

public class G2CatOrthAvoidPlayer extends catsdogs.sim.Player {
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	private final int recursiveLimit = 2;

	public String getName() {
		return "G2CatOrthAvoidPlayer";
	}

	public void startNewGame() {
		logger.info("G2 player starting new game!");

	}
	public Move doMove(int[][] board) {
		return getBestCatMove(board, 0);
		
	}
	
	/**
	 * Takes the current board and finds the best cat move based on our scoring
	 * @param currentBoard
	 * @return
	 */
	public PossibleMove getBestCatMove(int[][] currentBoard, int round){
		//looks at each option 
		ArrayList<PossibleMove> moves = Cat.allLegalMoves(currentBoard);
		if (moves.size() == 0){
			return null;
		}
		//HashMap<PossibleMove, Integer> scores = new HashMap<PossibleMove, Integer>();
		PossibleMove bestMove = null;
		int bestScore = 10000;
		int size = moves.size();
		ArrayList<PossibleMove> moves2keep = new ArrayList<PossibleMove>();
		for(int i = 0 ; i< size; i++){
			if(!Dog.wins(moves.get(i).getBoard())){
				moves2keep.add(moves.get(i));
			}
		}
		if(moves2keep.size() == 0){
			logger.info("Round " + round + "Moves " + moves.toString());
			return moves.get(0);
		}
		for (PossibleMove move: moves2keep){
			int score = getFutureScore(move, round);
			if (score < bestScore ){
				bestScore = score;
				bestMove = move;
			}
		}
		return bestMove;
	}
	
	
	/**
	 * @param move 
	 * @return
	 */
	private Integer getFutureScore(PossibleMove move, int round) {
		round += 1;
		if (round >= recursiveLimit){
			PossibleMove bestDogMove = getBestDogSingleMove(Dog.allLegalMoves(move.getBoard()));
			ArrayList<PossibleMove> nextCatMoves = Cat.allLegalMoves(bestDogMove.getBoard());
			if (nextCatMoves.size() == 0){
				return 200;
			}
			int best = 1000;
			for (PossibleMove catMove: nextCatMoves){
				int score = score(catMove);
				if (score < best){
					best = score; 
				}
			}	
			return best;
		}
		PossibleMove bestDogMove = getBestDogSingleMove(Dog.allLegalMoves(move.getBoard()));
		ArrayList<PossibleMove> nextCatMoves = Cat.allLegalMoves(bestDogMove.getBoard());
		if (nextCatMoves.size() == 0){
			return 200;
		}
		int best = 1000;
		for (PossibleMove catMove: nextCatMoves){
			PossibleMove pm = getBestCatMove(catMove.getBoard(), round);
			int score=0;
			if(pm ==null){
				if (Cat.wins(catMove.getBoard()) ){
				score = -100;
				}
				else{
				score = 100;
				}
			}
			else{
				score = score(pm);
			}
			
			if (score < best){
				best = score; 
			}
		}
		return best;
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
		

}