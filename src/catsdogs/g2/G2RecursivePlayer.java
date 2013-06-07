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

public class G2RecursivePlayer extends catsdogs.sim.CatPlayer {
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	private final int recursiveLimit = 3;

	public String getName() {
		return "G2CatRecursivePlayer";
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
		
		int bestscore;
		if(round % 3 > 0){//in this case this is a dog move (maximize score)
			bestscore = -10000;
		}else{ // is cat move and minimizes score
			bestscore = 1000;
		}
		
		PossibleMove bestmove = null;
		
		ArrayList<PossibleMove> moves;
		if(round % 3 > 0){//in this case this is a dog move
			moves = Dog.allLegalMoves(currentBoard);
		}else{//in this case this is a cat move
			moves = Cat.allLegalMoves(currentBoard);
		}
		
		for(PossibleMove option: moves){
			int score = miniMax(option,++round);
			if(round % 3 > 0){//in this case this is a dog move (maximize score)
				if (score > bestscore){
					bestscore = score; 
					bestmove = option;
				}
			}
			else{ // is cat move and minimizes score
				if (score < bestscore){
					bestscore = score; 
					bestmove = option;
				}
			}
		}
				
	   return bestmove;
	}
	
	
	private int miniMax(PossibleMove move, int round){
		if(round >= recursiveLimit){
			return score(move);
		}

		int bestscore;
		if(round % 3 > 0){//in this case this is a dog move (maximize score)
			bestscore = -10000;
		}else{ // is cat move and minimizes score
			bestscore = 1000;
		}
	  
	   ArrayList<PossibleMove> moves;
		if(round % 3 > 0){//in this case this is a dog move
			moves = Dog.allLegalMoves(move.getBoard());
		}else{//in this case this is a cat move
			moves = Cat.allLegalMoves(move.getBoard());
		}
		
		for(PossibleMove option: moves){
			int score;
			if(Cat.wins(option.getBoard())){
				return -100;
			}
			if(Dog.wins(option.getBoard())){
				return 100;
			}
			 score = miniMax(option, ++round);
			if(round % 3 > 0){//in this case this is a dog move (maximize score)
				if (score > bestscore){
					bestscore = score; 
				}
			}
			else{ // is cat move and minimizes score
				if (score < bestscore){
					bestscore = score; 
				}
			}
		}
				
	   return bestscore;
	}
	
	
	/**
	 * @param move
	 * @return
	 */
	private int score(PossibleMove move) {
		int score;
		if(move == null){
			score = -88; //TODO find a value that works
		}
		score =Cat.allLegalMoves(move.getBoard()).size();
		if (Cat.wins(move.getBoard()) ){
			score -= 100;
		}
		else if(Dog.wins(move.getBoard())){
			score += 100;
		}
		return score;
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