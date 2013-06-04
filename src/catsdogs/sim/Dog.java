package catsdogs.sim;

import java.util.ArrayList;

public class Dog extends GameObject {

	public Dog(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/*
	 * Returns an ArrayList of all possible legal moves
	 */
	public static ArrayList<PossibleMove> allLegalMoves(int board[][]) {
		
		ArrayList<PossibleMove> moves = new ArrayList<PossibleMove>();
		
		for (int x = 0; x < Board.X; x++) {
			
			for (int y = 0; y < Board.Y; y++) {
				
				if (board[x][y] == Board.DOG) {
					
					// look up
					if (y > 0 && board[x][y-1] == Board.EMPTY) {
						int[][] clone = Board.cloneBoard(board);
						clone[x][y-1] = Board.DOG;
						clone[x][y] = Board.EMPTY;
						moves.add(new PossibleMove(x, y, Move.UP, clone));
					}

					// look down
					if (y < 6 && board[x][y+1] == Board.EMPTY) {
						int[][] clone = Board.cloneBoard(board);
						clone[x][y+1] = Board.DOG;
						clone[x][y] = Board.EMPTY;
						moves.add(new PossibleMove(x, y, Move.DOWN, clone));
					}

					// look left
					if (x > 0 && board[x-1][y] == Board.EMPTY) {
						int[][] clone = Board.cloneBoard(board);
						clone[x-1][y] = Board.DOG;
						clone[x][y] = Board.EMPTY;
						moves.add(new PossibleMove(x, y, Move.LEFT, clone));
					}

					// look right
					if (x < 6 && board[x+1][y] == Board.EMPTY) {
						int[][] clone = Board.cloneBoard(board);
						clone[x+1][y] = Board.DOG;
						clone[x][y] = Board.EMPTY;
						moves.add(new PossibleMove(x, y, Move.RIGHT, clone));
					}

				}
			}
		}
		
		return moves;
	}
	
	public static boolean wins(int[][] board) {

		for (int x = 0; x < Board.X; x++) {
			int count = 0;
			for (int y = 0; y < Board.Y; y++) {
				if (board[x][y] == Board.CAT)
					count++;
				if (count == 3) {
					// DOG WINS!
					return true;
				}
			}
		}
		for (int y = 0; y < Board.Y; y++) {
			int count = 0;
			for (int x = 0; x < Board.X; x++) {
				if (board[x][y] == Board.CAT)
					count++;
				if (count == 3) {
					// DOG WINS!
					return true;
				}
			}
		}
		
		return false;

	}
	
}
