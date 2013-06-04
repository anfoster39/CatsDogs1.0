
package catsdogs.sim;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Chris Murphy
 */
public abstract class Player {

    /**
     * Returns the name for this player
     */
    public abstract String getName();
    

	/**
	 * Called on the player when a new game starts
	 */
	public abstract void startNewGame();

	/**
	 * Allows you to move the planes 
	 */
	public abstract Move doMove(int[][] board);
	
}

