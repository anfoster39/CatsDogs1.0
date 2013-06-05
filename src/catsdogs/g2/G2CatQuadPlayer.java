package catsdogs.g2;

import java.util.ArrayList;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import catsdogs.sim.Board;
import catsdogs.sim.Cat;
import catsdogs.sim.Move;
import catsdogs.sim.PossibleMove;

public class G2CatQuadPlayer extends catsdogs.sim.Player {
	private Logger logger = Logger.getLogger(this.getClass()); // for logging
private	TreeMap<Integer,String> quadStrings = new TreeMap<Integer,String> ();


	public String getName() {
		return "G2CatPlayer";
	}

	public void startNewGame() {
		logger.info("G2 player starting new game!");

	}
	public Move doMove(int[][] board) {
		ArrayList<PossibleMove> moves = Cat.allLegalMoves(board);
		ArrayList<PossibleMove> moves2 = Cat.allLegalMoves(board);

		Integer Q1 = 0;
		Integer Q2 = 0;
		Integer Q3 = 0;
		Integer Q4 = 0;

		for(int i = 0; i < 7; i++){
			for(int j = 0; j < 7; j++){
				if(board[i][j]==Board.DOG){
					if(i<5 && j< 5 ){
						Q1++;
					}
					if(i<5 && j> 3 ){
						Q2++;
					}
					if(i>3 && j< 5 ){
						Q3++;
					}
					if(i>3 && j>3 ){
						Q4++;
					}
				}
			}
		}

		quadStrings.put(Q1, "Q1");
		quadStrings.put(Q2, "Q2");
		quadStrings.put(Q3, "Q3");
		quadStrings.put(Q4, "Q4");
		ArrayList<Integer> quadrants = new ArrayList<Integer>();
		quadrants.add(Q1);
		quadrants.add(Q2);
		quadrants.add(Q3);
		quadrants.add(Q4);
		PossibleMove cat=null;
		Integer mostPopulated = Q1;
		boolean targetKittyFound  = false;
		while(!targetKittyFound){
			for(Integer quad : quadrants){
				if (quad>mostPopulated){
					mostPopulated = quad;
				}
			}
			 cat = isCatInQuadrant( mostPopulated,  moves);
			
			if(cat!=null){
				if(isThreeInARow(cat,board)){
					moves.remove(cat);
				}else{
					targetKittyFound = true;
				}
			}else{
				if(quadrants.contains(mostPopulated)){
				quadrants.remove(mostPopulated);
				}
				mostPopulated = quadStrings.lowerKey(mostPopulated);
				if(mostPopulated ==null){
					if(moves.size()>0){
						return moves.get(0);
					}else{
						return moves2.get(0);

					}
				}
			}
			logger.info("here");

		}
		
		
		return cat;
	}
	private boolean isThreeInARow(PossibleMove move, int[][]board){

		for(int i = 0; i < 7; i++){
			int catCt = 0;
			if((move.getX()==i+1 && move.getDirection()==Move.LEFT )||(move.getX()==i-1&&move.getDirection()==Move.RIGHT)){
				catCt++;
			}
			for(int j = 0; j < 7; j++){
				if(board[i][j]==Board.CAT ){
					catCt++;
				}
			}
			if(catCt>2){
				return true;
			}
		}
		for(int j = 0; j < 7; j++){
			int catCt = 0;
			//			if((move.getX()==i+1 && move.getDirection()==Move.LEFT )||(move.getX()==i-1&&move.getDirection()==Move.RIGHT)){

			if((move.getY()==j+1 &&move.getDirection()==Move.UP )||(move.getY()==j-1 &&move.getDirection()==Move.DOWN)){
				catCt++;
			}
			for(int i = 0; i < 7; i++){
				if(board[i][j]==Board.CAT){
					catCt++;
				}
			}
			if(catCt>2){
				return true;
			}
		}
		return false;
	}
	private boolean isInQuad(PossibleMove pm, Integer Quad){
		double x = pm.getX();
		double y = pm.getY();
		if(Quad==0){
			return false;
		}
		if(quadStrings.get(Quad).equals("Q1")){
			if(y<5 && x <5){
				return true;
			}
		}
		if(quadStrings.get(Quad).equals("Q2")){
			if(x<5 && y> 3 ){
				return true;
			}
		}
		if(quadStrings.get(Quad).equals("Q3")){
			if(x>3 && y< 5 ){
				return true;
			}
		}
		if(quadStrings.get(Quad).equals("Q4")){
			if(y>3 && x >3){
				return true;
			}
		}
		return false;
	}
	private PossibleMove isCatInQuadrant(int Quad, ArrayList<PossibleMove> moves){
		for(PossibleMove pm : moves){
			if(isInQuad( pm,  Quad)){
				return pm;
			}
		}
		
		
		return null;
		
	}
}
