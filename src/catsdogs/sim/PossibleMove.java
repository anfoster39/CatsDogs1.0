package catsdogs.sim;

public class PossibleMove extends Move {

	private int[][] board;
	
	public PossibleMove(int x, int y, int dir, int board[][]) {
		super(x, y, dir);
		this.board = board;
	}
	
	public int[][] getBoard() { return board; }

}
