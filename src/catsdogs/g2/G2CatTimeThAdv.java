package catsdogs.g2;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import org.apache.log4j.Logger;

import catsdogs.sim.Board;
import catsdogs.sim.Cat;
import catsdogs.sim.Dog;
import catsdogs.sim.Move;
import catsdogs.sim.PossibleMove;

public class G2CatTimeThAdv extends catsdogs.sim.CatPlayer {
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
	private double timeLimit= 5 * 1000000;
	private final double timeBreak = (5 * 1000000) - 100000;
	private int pruningFactor;
	private int gameRound; 

	private double start; 

	public String getName() {
		return "G2CatTimeThAdv";
	}

	public void startNewGame() {
		logger.info("G2 player starting new game!");
		gameRound = 0;

	}
	public Move doMove(int[][] board) {
		
		gameRound++;
		
		/*if (gameRound > 40){
			timeLimit = 25 * 1000000;
		}
		else if (gameRound > 35){
			timeLimit = 10 * 1000000;
		}
		else if (gameRound > 30){
			timeLimit = 15 * 1000000;
		}
		else if (gameRound > 25){
			timeLimit = 30 * 1000000;
		}
		else if (gameRound > 20){
			timeLimit = 45 * 1000000;
		}
		else if (gameRound > 15){
			timeLimit = 65 * 1000000;
		}
		else if (gameRound > 10){
			timeLimit = 75 * 1000000;
		}
		else if (gameRound > 5){
			timeLimit = 150 * 1000000;
		}*/
		
		start = System.nanoTime()/1000;
		pruningFactor = 0;
			/*ThreadDemo myRunnable = new ThreadDemo(board,0);
		new Thread(myRunnable).start();
		try {
			Thread.sleep(4500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(myRunnable.thPm==null){
			return Cat.allLegalMoves(board).get(0);
		}*/
		Move move = getBestMove(board, 0);
	//	double time = (System.nanoTime()/1000 - start) / 1000000;
	//	logger.error("time: " + time +" Round " + gameRound);
		return move;
	}
	
	
	/**
	 * Takes the current board and finds the best cat move based on our scoring
	 * @param currentBoard
	 * @return
	 */
	
	public class ThreadDemo implements Runnable {
		public int[][] board;
		public int thRound;
		public PossibleMove prePm;
		double rtl;
		public PossibleMove thPm;
		int thAlpha;
		int thBeta;
		int score;
		int sz;
		double opz;
		public ThreadDemo(PossibleMove pm, int round, double option, int alpha, int beta, int[][] previousBoard){
			thRound = round;
			board = previousBoard;
			prePm = pm;
			thAlpha=alpha;
			thBeta = beta;
			double opz = option;
			
		}
		   public void run() {
			  score =  miniMax(prePm, thRound, opz, thAlpha, thBeta,board);
		     
		   }	
	}
	public PossibleMove getBestMove(int[][] currentBoard, int round){
		//looks at each option
		
		int bestscore = 10000;
		PossibleMove bestmove = null;
		
		ArrayList<PossibleMove> moves;
		moves = Cat.allLegalMoves(currentBoard);
		int optionCt = 0;
		for(PossibleMove option: moves){
			int score;
			if(Cat.wins(option.getBoard())){
				score = -1000 + round;
				return option;
			}
			else if(Dog.wins(option.getBoard())){
				score = 1000-round;
			}
			else{

				ThreadDemo myRunnable = new ThreadDemo(option, 1, ((timeLimit/moves.size())*(optionCt+1)), -1000, 1000, currentBoard);
				new Thread(myRunnable).start();
				try {
					long sleepTime = 4500/moves.size();
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(myRunnable.score!=0){
					score = myRunnable.score;
				}else{
					score =2000;
				}
				//score = miniMax(option, 1, ((timeLimit/moves.size())*(optionCt+1)), -1000, 1000, currentBoard);
				optionCt++;
			//	logger.error(score);
			}
			if (score < bestscore){
					bestscore = score; 
					bestmove = option;
				}
		}
				
	   return bestmove;
	}
	
	
	private int miniMax(PossibleMove move, int round , double recursiveTimeLimit, int alpha, int beta, int[][] previousBoard){
		if(start + recursiveTimeLimit < System.nanoTime()/1000){ //base case
//			logger.error("Limit " + recursiveTimeLimit + " Time from start: " + ((System.nanoTime()/1000)-start)+ " stopped at recursive level " + round);
			return score(previousBoard, move, round);
		}
		
		ArrayList<PossibleMove> moves;
		
		if(round % 3 > 0){//if Dog turn
			moves = Dog.allLegalMoves(move.getBoard()); // get dog's moves
		//	logger.info(moves.size());
			int optionCt = 0;
			for(PossibleMove option: moves){
				if(System.nanoTime()/1000 - start > timeBreak){
					return alpha;
				}
				int score;
				if(Cat.wins(option.getBoard())){
					score = -1000+round;
				}
				else if(Dog.wins(option.getBoard())){ //if dog wins stop here
					score = 1000-round;
				}
				else{
					score = miniMax(option, (round + 1), (recursiveTimeLimit/moves.size())*(optionCt+1), alpha, beta, move.getBoard());
					optionCt++;
				}
				
				if (alpha < score){
					alpha = score;
				}
	            if (beta <= alpha){
	            	double dRound = round;
	            	pruningFactor += 1;//(1.0/dRound)*20;
	            	break;
	           }
	            
			}
				return alpha;
			}
			else{//Cat turn
				moves = Cat.allLegalMoves(move.getBoard()); // get cat's moves
				int optionCt = 0;
				for(PossibleMove option: moves){
					if(System.nanoTime()/1000 - start > timeBreak){
						return beta;
					}
					int score;
					if(Cat.wins(option.getBoard())){
						score = -1000+round;
					}
					else if(Dog.wins(option.getBoard())){ //if dog wins stop here
						score = 1000-round;
					}
					else{
						score = miniMax(option, (round + 1), (recursiveTimeLimit/moves.size())*(optionCt+1), alpha, beta, move.getBoard());
						optionCt++;
					}
					
					if (beta > score){
						beta = score;
					}
		            if (beta <= alpha){

		            	double dRound = round;
		            	pruningFactor += 1;//(1.0/dRound)*20;
		            	break;
		            }
				  }
					  return beta;
				}

	}
	
	
//	private int findCatDistances(int [][] oldBoard, PossibleMove move){
//		//find the older location
//		//find the new location
//		//see if they are closer or farther from the other cats
//		
//		
//		int oldDistance = 0;
//		int newDistance = 0;
//		
//		//get current cat distances
//		ArrayList<Point2D.Double> cats = findCats(move.getBoard());
//		for (Point2D.Double cat1 : cats){
//			for (Point2D.Double cat2: cats){
//				oldDistance += cat1.distance(cat2);
//			}
//		}
//		
//		//possible null pointer exception
//		//get old cat distances
//		ArrayList<Point2D.Double> oldCats = findCats(oldBoard);
//		for (Point2D.Double cat1 : oldCats){
//			for (Point2D.Double cat2: oldCats){
//				newDistance += cat1.distance(cat2);
//			}
//		}
//		
//		//return differences 
//		if (newDistance < oldDistance){
//			return -1;
//		}
//		if (newDistance > oldDistance){
//			return 1;
//		}
//		return 0;
//		
//	}
	
//	private ArrayList<Point2D.Double> findCats(int[][] board){
//		ArrayList<Point2D.Double> cats = new ArrayList<Point2D.Double>();
//		for (int x = 0; x < board.length; x++){
//			for (int y = 0; y < board[x].length; y++){
//				if(board[x][y] == Board.CAT){
//					cats.add(new Point2D.Double(x,y));
//				}
//			}
//		}
//		return cats;
//	}
//	
//	private int isTwoInARow(int[][] possibleBoard, int[][] previousBoard ){
//		int posCt = howManyInRow(possibleBoard);
//		int prevCt = howManyInRow(previousBoard);
//		if(posCt==prevCt){
//			return 0;
//		}else if(posCt > prevCt){
//			return 1;
//		}else{
//			return -1;
//		}
//	}
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

	private ArrayList<Point2D.Double> findDogs(int[][] board){
		ArrayList<Point2D.Double> dogs = new ArrayList<Point2D.Double>();
		for (int x = 0; x < board.length; x++){
			for (int y = 0; y < board[x].length; y++){
				if(board[x][y] == Board.DOG){
					dogs.add(new Point2D.Double(x,y));
				}
			}
		}
		return dogs;
	}
	private int findDogDistances(int [][] oldBoard, PossibleMove move){
		//find the older location
		//find the new location
		//see if they are closer or farther from the other cats
		
		
		int oldDistance = 0;
		int newDistance = 0;
		
		//get current cat distances
		ArrayList<Point2D.Double> dogs = findDogs(move.getBoard());
		for (Point2D.Double dogs1 : dogs){
			for (Point2D.Double dogs2: dogs){
				oldDistance += dogs1.distance(dogs2);
			}
		}
		
		//possible null pointer exception
		//get old cat distances
		ArrayList<Point2D.Double> oldDogs = findDogs(oldBoard);
		for (Point2D.Double dog1 : oldDogs){
			for (Point2D.Double dog2: oldDogs){
				newDistance += dog1.distance(dog2);
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
	
	
	private int score(int [][] oldBoard, PossibleMove catMove, int round) {
		int score = Cat.allLegalMoves(catMove.getBoard()).size()*10;
		if(Cat.wins(catMove.getBoard())){
			score = -1000+round;
		}
		else if(Dog.wins(catMove.getBoard())){
			score = 1000-round;
		}
		/*if(isTwoInARow(oldBoard, catMove.getBoard())==-1){
			score-= 15;
		}
		if(isTwoInARow(oldBoard, catMove.getBoard())==1){
			score+= 15;
		}
		if(findCatDistances(oldBoard, catMove)==1){
			score+= 5;
		}
		if(findCatDistances(oldBoard, catMove)==-1){
			score-= 5;
		}*/
		/*if(findDogDistances(oldBoard, catMove)==1){
		score-= 5;
		}
		if(findDogDistances(oldBoard, catMove)==-1){
			score+= 5;
		}*/
		return score;
	}
}