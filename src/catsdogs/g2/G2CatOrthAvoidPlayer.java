package catsdogs.g2;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

import catsdogs.sim.Board;
import catsdogs.sim.Cat;
import catsdogs.sim.Dog;
import catsdogs.sim.Move;
import catsdogs.sim.PossibleMove;

public class G2CatOrthAvoidPlayer extends catsdogs.sim.CatPlayer {
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	private final int recursiveLimit = 3;

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
		ArrayList<PossibleMove> moves;
		if(round % 3 > 0){//in this case this is a dog move
			moves = Dog.allLegalMoves(currentBoard);
		}else{//in this case this is a cat move
			moves = Cat.allLegalMoves(currentBoard);
		}
		int size = moves.size();
		//if at the end of a game
		if (size == 0){
			return null;
		}
		PossibleMove myBestMove = null;
		//throws away any suicide moves
		ArrayList<PossibleMove> moves2keep = new ArrayList<PossibleMove>();
		if(round % 3 < 1){
			for(int i = 0 ; i< size; i++){
				if(!Dog.wins(moves.get(i).getBoard())){
					moves2keep.add(moves.get(i));
				}
			}
		}else{
			moves2keep = moves;
		}
		//if there are no ok moves, return a random move
		if(moves2keep.size() == 0){
		//	logger.info("Round " + round + "Moves " + moves.toString());
			return moves.get(0);
		}
		
		int roundIncremented = round+1;
		if(round % 3 > 0){//in this case this is a dog move
			int myBestDogScore = -10000;
			for (PossibleMove keptMoveDogRound: moves2keep){
				int bestScoreFromThisMovePath = getFutureScore(keptMoveDogRound, roundIncremented);
				if (bestScoreFromThisMovePath > myBestDogScore ){
					myBestDogScore = bestScoreFromThisMovePath;
					myBestMove = keptMoveDogRound;
				}
			}
		}
			else{//in this case this is a cat move
				int myBestCatScore = 10000;
				for (PossibleMove keptMoveCatRound: moves2keep){
					int bestScoreFromThisMovePath = getFutureScore(keptMoveCatRound, roundIncremented);
					if (bestScoreFromThisMovePath < myBestCatScore ){
						myBestCatScore = bestScoreFromThisMovePath;
						myBestMove = keptMoveCatRound;
					}
				}
		
		}
		return myBestMove;
	}
	
	
	/**
	 * @param move 
	 * @return
	 */
	private Integer getFutureScore(PossibleMove move, int round) {
		if (round >= recursiveLimit){//if this is the last iteration, and recursion has ended
			if(round % 3 > 0){//in this case this is a dog move
				ArrayList<PossibleMove> nextDogMoves = Dog.allLegalMoves(move.getBoard());
				if (nextDogMoves.size() == 0){
					return 200;
				}
				int endRecursionBestDogScore = -10000;
				for (PossibleMove eachNextDogMove: nextDogMoves){
					int thisMovebottomLevelScore = score(move.getBoard(), eachNextDogMove);
					if (thisMovebottomLevelScore > endRecursionBestDogScore ){
						endRecursionBestDogScore = thisMovebottomLevelScore;
					}
				}
				return endRecursionBestDogScore;
			}
			else{//in this case this is a cat move
				ArrayList<PossibleMove> nextCatMoves  = Cat.allLegalMoves(move.getBoard());
				if (nextCatMoves.size() == 0){
					return 200;
				}
				int endRecursionBestCatScore = 10000;
				for (PossibleMove eachNextCatMove: nextCatMoves){
					int thisMovebottomLevelScore = score(move.getBoard(), eachNextCatMove);
					if (thisMovebottomLevelScore < endRecursionBestCatScore ){
						endRecursionBestCatScore = thisMovebottomLevelScore;
					}
				}
				return endRecursionBestCatScore;
			}
		}
		///////If this is reached, recursion is continuing
		ArrayList<PossibleMove> nextMoves;
		if(round % 3 > 0){//in this case this is a dog move
			nextMoves = Dog.allLegalMoves(move.getBoard());
		}else{//in this case this is a cat move
			nextMoves = Cat.allLegalMoves(move.getBoard());
		}
		int roundIncremented = round+1;
		if (nextMoves.size() == 0){
			return 200;
		}
		int continuingRecursionBestScore;
		if(round % 3 > 0){//in this case this is a dog move
			continuingRecursionBestScore = -10000;
			for (PossibleMove eachDogMove: nextMoves){
				//if getBestCatMoves returns null, which means that the game has ended.
				PossibleMove pm = getBestCatMove(eachDogMove.getBoard(), roundIncremented);
				int eachDogMoveScore=0;
				if(pm ==null){
					if (Cat.wins(eachDogMove.getBoard()) ){
						eachDogMoveScore = -100;
					}
					else{
						eachDogMoveScore = 100;
					}
				}
				else{
					eachDogMoveScore = score(eachDogMove.getBoard(), pm);
				}
				if (eachDogMoveScore > continuingRecursionBestScore){
					continuingRecursionBestScore = eachDogMoveScore; 
				}
			}
		}else{//in this case this is a cat move
			continuingRecursionBestScore = 10000;
			for (PossibleMove catMove: nextMoves){
				//if getBestCatMoves returns null, which means that the game has ended.
				PossibleMove pm = getBestCatMove(catMove.getBoard(), roundIncremented);
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
					score = score(catMove.getBoard(), pm);
				}	
				if (score < continuingRecursionBestScore){
					continuingRecursionBestScore = score; 
				}
			}
		}		
		return continuingRecursionBestScore;
	}
	
	/**
	 * Scores a single board for the cat
	 * @param catMove
	 * @return the score 
	 */
	private int score(int [][] oldBoard, PossibleMove catMove) {
		int score =Cat.allLegalMoves(catMove.getBoard()).size();

		/*if(isTwoInARow(oldBoard, catMove.getBoard())==-1){
			score-= 5;
		}
		if(isTwoInARow(oldBoard, catMove.getBoard())==1){
			score+= 5;
		}*/
//		if(findCatDistances(oldBoard, catMove)==1){
//			score+= 5;
//		}
//		if(findCatDistances(oldBoard, catMove)==-1){
//			score-= 5;
//		}
		return score;
	}
	
	private int findCatDistances(int [][] oldBoard, PossibleMove move){
		//find the older location
		//find the new location
		//see if they are closer or farther from the other cats
		
		
		int oldDistance = 0;
		int newDistance = 0;
		
		//get current cat distances
		ArrayList<Point2D.Double> cats = findCats(move.getBoard());
		for (Point2D.Double cat1 : cats){
			for (Point2D.Double cat2: cats){
				oldDistance += cat1.distance(cat2);
			}
		}
		
		//possible null pointer exception
		//get old cat distances
		ArrayList<Point2D.Double> oldCats = findCats(oldBoard);
		for (Point2D.Double cat1 : oldCats){
			for (Point2D.Double cat2: oldCats){
				newDistance += cat1.distance(cat2);
			}
		}
		
		//return differences 
		if (newDistance < oldDistance){
			return -1;
		}
		if (newDistance > oldDistance){
			return 1;
		}
		return 0;
		
	}
	
	private ArrayList<Point2D.Double> findCats(int[][] board){
		ArrayList<Point2D.Double> cats = new ArrayList<Point2D.Double>();
		for (int x = 0; x < board.length; x++){
			for (int y = 0; y < board[x].length; y++){
				if(board[x][y] == Board.CAT){
					cats.add(new Point2D.Double(x,y));
				}
			}
		}
		return cats;
	}
	
	private int isTwoInARow(int[][] possibleBoard, int[][] previousBoard ){
		int posCt = howManyInRow(possibleBoard);
		int prevCt = howManyInRow(previousBoard);
		if(posCt==prevCt){
			return 0;
		}else if(posCt > prevCt){
			return 1;
		}else{
			return -1;
		}
	}
	public int howManyInRow (int[][] board){
		int doubleRowCt = 0;
		for(int i = 0; i < 7; i++){
			int catCt = 0;
			
			for(int j = 0; j < 7; j++){
				if(board[i][j]==Board.CAT ){
					catCt++;
				}
			}
			if(catCt>1){
				doubleRowCt++;
			}
		}
		for(int j = 0; j < 7; j++){
			int catCt = 0;			
			for(int i = 0; i < 7; i++){
				if(board[i][j]==Board.CAT){
					catCt++;
				}
			}
			if(catCt>1){
				doubleRowCt++;
			}
		}
		return doubleRowCt;
	}

}