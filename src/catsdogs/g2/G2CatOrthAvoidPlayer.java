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
		int roundIncremented = round+1;
		PossibleMove myBestMove = null;
		//looks at each option
		if(round % 3 > 0){//in this case this is a dog move
			ArrayList<PossibleMove> dogMoves = Dog.allLegalMoves(currentBoard);
			ArrayList<PossibleMove> dogMovesFiltered = new ArrayList<PossibleMove>();
			int size = dogMoves.size();
			if (size == 0){
				return null;
			}
			for(int i = 0 ; i< size; i++){
				if(!Cat.wins(dogMoves.get(i).getBoard())){
					dogMovesFiltered.add(dogMoves.get(i));
				}
				if(Dog.wins(dogMoves.get(i).getBoard())){//if there is a winning move in the bunch, keep that move
					return dogMoves.get(i);
				}
			}
			if(dogMovesFiltered.size() == 0){
				return dogMoves.get(0);
			}
			int myBestDogScore = -10000;
			for (PossibleMove dogMove: dogMovesFiltered){
				int bestScoreFromThisMovePath = getFutureScore(dogMove, roundIncremented);
				if (bestScoreFromThisMovePath > myBestDogScore ){
					myBestDogScore = bestScoreFromThisMovePath;
					myBestMove = dogMove;
				}
			}
		}else{//in this case this is a cat move
			ArrayList<PossibleMove> catMoves = Cat.allLegalMoves(currentBoard);
			ArrayList<PossibleMove> catMoves2keep = new ArrayList<PossibleMove>();

			int size = catMoves.size();
			if (size == 0){
				return null;
			}
			//throws away any suicide moves
			for(int i = 0 ; i< size; i++){
				if(!Dog.wins(catMoves.get(i).getBoard())){
					catMoves2keep.add(catMoves.get(i));
				}
				if(Cat.wins(catMoves.get(i).getBoard())){//if there is a winning move in the bunch, keep that move
					return catMoves.get(i);
				}
			}
			if(catMoves2keep.size() == 0){
				return catMoves.get(0);
			}
			int myBestCatScore = 10000;
			for (PossibleMove keptMoveCatRound: catMoves2keep){
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
				ArrayList<PossibleMove> nextDogMovesFiltered  = new ArrayList<PossibleMove>();

				for(int i = 0 ; i< nextDogMoves.size(); i++){
					if(!Cat.wins(nextDogMoves.get(i).getBoard())){
						nextDogMovesFiltered.add(nextDogMoves.get(i));
					}
					if(Dog.wins(nextDogMoves.get(i).getBoard())){//if there is a winning move in the bunch, keep that move
						return 100;
					}
				}
				if (nextDogMovesFiltered.size() == 0){
					return -1000;
				}
				int endRecursionBestDogScore = -10000;
				for (PossibleMove eachNextDogMove: nextDogMovesFiltered){
					int thisMovebottomLevelScore = score(move.getBoard(), eachNextDogMove);
					if (thisMovebottomLevelScore > endRecursionBestDogScore ){
						endRecursionBestDogScore = thisMovebottomLevelScore;
					}
				}
				return endRecursionBestDogScore;
			}
			else{//in this case this is a cat move
				ArrayList<PossibleMove> nextCatMoves  = Cat.allLegalMoves(move.getBoard());
				ArrayList<PossibleMove> nextCatMovesFiltered  = new ArrayList<PossibleMove>();

				for(int i = 0 ; i< nextCatMoves.size(); i++){
					if(!Dog.wins(nextCatMoves.get(i).getBoard())){
						nextCatMovesFiltered.add(nextCatMoves.get(i));
					}
					if(Cat.wins(nextCatMoves.get(i).getBoard())){//if there is a winning move in the bunch, keep that move
						return -100;
					}
				}
				if (nextCatMovesFiltered.size() == 0){
					return 1000;
				}
				int endRecursionBestCatScore = 10000;
				for (PossibleMove eachNextCatMove: nextCatMovesFiltered){
					int thisMovebottomLevelScore = score(move.getBoard(), eachNextCatMove);
					if (thisMovebottomLevelScore < endRecursionBestCatScore ){
						endRecursionBestCatScore = thisMovebottomLevelScore;
					}
				}
				return endRecursionBestCatScore;
			}
		}
		///////If this is reached, recursion is continuing
		int roundIncremented = round+1;
		int continuingRecursionBestScore;
		if(round % 3 > 0){//in this case this is a dog move
			ArrayList<PossibleMove> nextDogMoves = Dog.allLegalMoves(move.getBoard());
			ArrayList<PossibleMove> nextDogMovesFiltered  = new ArrayList<PossibleMove>();
			for(int i = 0 ; i< nextDogMoves.size(); i++){
				if(!Cat.wins(nextDogMoves.get(i).getBoard())){
					nextDogMovesFiltered.add(nextDogMoves.get(i));
				}
				if(Dog.wins(nextDogMoves.get(i).getBoard())){//if there is a winning move in the bunch, keep that move
					return 100;
				}
			}
			if (nextDogMovesFiltered.size() == 0){
				return -1000;
			}
			continuingRecursionBestScore = -10000;
			for (PossibleMove eachDogMove: nextDogMovesFiltered){
				//if getBestCatMoves returns null, which means that the game has ended.
				PossibleMove pm = getBestCatMove(eachDogMove.getBoard(), roundIncremented);
				int eachDogMoveScore=0;
				if(pm ==null){
					if (Cat.wins(eachDogMove.getBoard()) ){
						eachDogMoveScore = -1000;
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
			ArrayList<PossibleMove> nextCatMoves = Cat.allLegalMoves(move.getBoard());
			ArrayList<PossibleMove> nextCatMovesFiltered  = new ArrayList<PossibleMove>();

			for(int i = 0 ; i< nextCatMoves.size(); i++){
				if(!Dog.wins(nextCatMoves.get(i).getBoard())){
					nextCatMovesFiltered.add(nextCatMoves.get(i));
				}
				if(Cat.wins(nextCatMoves.get(i).getBoard())){//if there is a winning move in the bunch, keep that move
					return -100;
				}
			}
			if (nextCatMovesFiltered.size() == 0){
				return 1000;
			}
			continuingRecursionBestScore = 10000;
			for (PossibleMove catMove: nextCatMoves){
				//if getBestCatMoves returns null, which means that the game has ended.
				PossibleMove pm = getBestCatMove(catMove.getBoard(), roundIncremented);
				int score=0;
				if(pm ==null){
					if (Cat.wins(catMove.getBoard()) ){
						score = -100;
					}
					else{
						score = 1000;
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

		if(isTwoInARow(oldBoard, catMove.getBoard())==-1){
			score-= 5;
		}
		if(isTwoInARow(oldBoard, catMove.getBoard())==1){
			score+= 5;
		}
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