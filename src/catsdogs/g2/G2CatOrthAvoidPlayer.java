package catsdogs.g2;

import java.awt.geom.Point2D;
import java.util.ArrayList;
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
		PossibleMove which = getMoveWithLowestOrth(moves);
		while(true){
			if(!Dog.wins(which.getBoard())){
				return which;
			}else{
				moves.remove(which);
				 which = getMoveWithLowestOrth(moves);
			}
		}
		
	}

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