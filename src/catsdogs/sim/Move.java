package catsdogs.sim;

public class Move {
	
	public static final int UP = 0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	
	private int direction;
	private int x;
	private int y;
	
	public Move(int x, int y, int dir) {
		this.x = x;
		this.y = y;
		direction = dir;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	
	public int getDirection() { return direction; }

}
