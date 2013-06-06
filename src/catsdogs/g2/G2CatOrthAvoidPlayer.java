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

public class G2CatOrthAvoidPlayer extends catsdogs.sim.Player {
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
		//	logger.info("Round " + round + "Moves " + moves.toString());
			return moves.get(0);
		}
		int round1 = round+1;
		for (PossibleMove move: moves2keep){
			int score = getFutureScore(move, round1);
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
		if (round >= recursiveLimit){
			
			ArrayList<PossibleMove> nextMoves;
			if(round % 3 >0){//in this case this is a dog move
			 nextMoves = Dog.allLegalMoves(move.getBoard());
			}else{//in this case this is a cat move
				nextMoves = Cat.allLegalMoves(move.getBoard());
			}
			if (nextMoves.size() == 0){
				return 200;
			}
			int best = 1000;
			for (PossibleMove catMove: nextMoves){
				int score = score(move.getBoard(), catMove);
				if (score < best){
					best = score; 
				}
			}	
			return best;
		}
		
		ArrayList<PossibleMove> nextMoves;
		if(round % 3 >0){//in this case this is a dog move
		 nextMoves = Dog.allLegalMoves(move.getBoard());
		}else{//in this case this is a cat move
			nextMoves = Cat.allLegalMoves(move.getBoard());
		}		if (nextMoves.size() == 0){
			return 200;
		}
		int best = 1000;
		for (PossibleMove catMove: nextMoves){
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
				score = score(catMove.getBoard(), pm);
			}
			
			if (score < best){
				best = score; 
			}
		}
		round += 1;

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
	private int score(int [][] oldBoard, PossibleMove catMove) {
		int score =0;//Cat.allLegalMoves(catMove.getBoard()).size();
		/*
		if(isTwoInARow(oldBoard, catMove.getBoard())==-1){
			score-= 5;
		}
		if(isTwoInARow(oldBoard, catMove.getBoard())==1){
			score+= 5;
		}
		if(findCatDistances(oldBoard, catMove)==1){
			score+= 5;
		}
		if(findCatDistances(oldBoard, catMove)==-1){
			score-= 5;
		}*/
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
	
//	private ArrayList<Point2D.Double> getOldCatLocations(PossibleMove move){
//		PossibleMove oldBoard = null;
//		switch (move.getDirection()){
//			//up
//			case 0:
//				oldBoard = new PossibleMove(move.getX(), move.getY(), 2, move.getBoard());
//				break;
//			//right
//			case 1:
//				oldBoard = new PossibleMove(move.getX(), move.getY(), 3, move.getBoard());
//				break;
//			//down
//			case 2:
//				oldBoard = new PossibleMove(move.getX(), move.getY(), 0, move.getBoard());
//				break;
//			//left
//			case 3:
//				oldBoard = new PossibleMove(move.getX(), move.getY(), 1, move.getBoard());
//				break;
//		}
//		return findCats(oldBoard.getBoard());
//		
//	}

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