/* 
 * 	$Id: GameEngine.java,v 1.6 2007/11/28 16:30:47 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package catsdogs.sim;


import java.awt.Color;
import java.util.List;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;


import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import catsdogs.sim.GameListener.GameUpdateType;
import catsdogs.sim.ui.GUI;





public final class GameEngine 
{
	private GameConfig config;
	private Board board;
	private int round = 0;
	public GUI gui;
	private ArrayList<GameListener> gameListeners;
	private Logger log;
	boolean initDone = false;
	private CatPlayer catPlayer;
	private DogPlayer dogPlayer;
	private static final Random random = new Random();
	private final Logger logger = Logger.getLogger(this.getClass());
	
	private int turn = 0; // indicates whose turn it is; 0 = cat, 1 and 2 = dog

	static {
		PropertyConfigurator.configure("logger.properties");
	}


	public GameEngine(String configFile)
	{
		config = new GameConfig(configFile);
		gameListeners = new ArrayList<GameListener>();
		//board = new Board();
		//board.engine = this;
	    log = Logger.getLogger(GameController.class);
	}

	public void addGameListener(GameListener l)
	{
		gameListeners.add(l);
	}

	public int getCurrentRound()
	{
		return round;
	}
	
	public GameConfig getConfig()
	{
		return config;
	}
	public Board getBoard()
	{
		return board;
	}

	
	public boolean step() {
		boolean result = true;
		
		/*
		// let's see where the Cats are
		for (int x = 0; x < board.X; x++) {
			for (int y = 0; y < board.Y; y++) {
				if (board.objects[x][y] != null && board.objects[x][y] instanceof Cat)
					System.err.println("Found Cat at " + x + ", " +y);
			}
		}
		*/
		
		try {
			if (turn == 0) {
				long start = System.currentTimeMillis();
				result = moveCat();
				long end = System.currentTimeMillis();
				if (end - start > GameConfig.TIME_LIMIT) {
					logger.info("Cat took too long to move (" + (end-start) + "ms)!");
					gui.setErrorMessage("Cat took too long to move!");
					notifyListeners(GameUpdateType.ERROR); 
					return false;
				}
				turn++;
			}
			else if (turn == 1) {
				long start = System.currentTimeMillis();
				result = moveDog(1);
				long end = System.currentTimeMillis();
				if (end - start > GameConfig.TIME_LIMIT) {
					logger.info("Dog took too long to move (" + (end-start) + "ms)!");
					gui.setErrorMessage("Dog took too long to move!");
					notifyListeners(GameUpdateType.ERROR); 
					return false;
				}
				turn++;
			}
			else if (turn == 2) {
				long start = System.currentTimeMillis();
				result = moveDog(2);
				long end = System.currentTimeMillis();
				if (end - start > GameConfig.TIME_LIMIT) {
					logger.info("Dog took too long to move (" + (end-start) + "ms)!");
					gui.setErrorMessage("Dog took too long to move!");
					notifyListeners(GameUpdateType.ERROR); 
					return false;
				}
				turn = 0;
			}
			else result = false;
			
			if (!result) {
				gui.setErrorMessage("Player tried to make an illegal move!");
				notifyListeners(GameUpdateType.ERROR); 
				return false;
			}
			

			notifyListeners(GameUpdateType.MOVEPROCESSED);

			// check to see if the game is over
			
			if (Dog.wins(board.objects)) {
				logger.info("DOG WINS!");
				notifyListeners(GameUpdateType.GAMEOVER_DOGWINS);
				return false;
			}
			
			if (Cat.wins(board.objects)) {
				logger.info("CAT WINS!");
				notifyListeners(GameUpdateType.GAMEOVER_CATWINS);
				return false;
			}
			
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return result;
	}
	
	private boolean moveCat() {
		Move m = catPlayer.doMove(Board.cloneBoard(board.objects));
		logger.info("Cat at " + m.getX() + ", " + m.getY() + " trying to move " + m.getDirection());
		// check for illegal move, e.g. if object is a Dog
		int x = m.getX();
		int y = m.getY();
		if (board.objects[x][y] != Board.CAT) {
			// ERROR!!
			logger.error("Cat Player tried to move something that's not a Cat");
			return false;
		}
		if (board.update(m) == false) {
			// ERROR!!
			logger.error("Cat Player made an illegal move");
			return false;
		}
	
		return true;

	}
	
	
	private boolean moveDog(int turn) {
		
		Move m = null;
		if (turn == 1) m = dogPlayer.doMove1(Board.cloneBoard(board.objects));
		else if (turn == 2) m = dogPlayer.doMove2(Board.cloneBoard(board.objects));
		else {
			// ERROR!!
			logger.error("Game is in illegal state trying to move dog: turn="+turn);
			return false;
		}

		logger.info("Dog at " + m.getX() + ", " + m.getY() + " trying to move " + m.getDirection());
		// check for illegal move, e.g. if object is a Cat
		int x = m.getX();
		int y = m.getY();
		if (board.objects[x][y] != Board.DOG) {
			// ERROR!!
			logger.error("Dog Player tried to move something that's not a Dog");
			return false;
		}
		if (board.update(m) == false) {
			// ERROR!!
			logger.error("Dog Player made an illegal move");
			return false;
		}
		
		return true;
	}
	


	
	private final static void printUsage()
	{
		System.err.println("Usage: GameEngine <config file>");
	}

	public void removeGameListener(GameListener l)
	{
		gameListeners.remove(l);
	}
	public void notifyRepaint()
	{
		Iterator<GameListener> it = gameListeners.iterator();
		while (it.hasNext())
		{
			it.next().gameUpdated(GameUpdateType.REPAINT);
		}
	}
	private void notifyListeners(GameUpdateType type)
	{
		Iterator<GameListener> it = gameListeners.iterator();
		while (it.hasNext())
		{
			it.next().gameUpdated(type);
		}
	}

	public static final void main(String[] args)
	{
		System.setOut(
    		    new PrintStream(new OutputStream() {
					@Override
					public void write(int b) throws IOException {						
					}
				}));
		if (args.length < 1)
		{
			printUsage();
			System.exit(-1);
		}
		GameEngine engine = new GameEngine(args[0]);
		new GUI(engine);
	}


	
	public boolean setUpGame()
	{
		try
		{
			log.info("New game!");
			
			board = new Board();
			board.engine=this;

			round = 0;

			initDone = false;

			catPlayer = config.getCatPlayerClass().newInstance();
			catPlayer.startNewGame();
			
			dogPlayer = config.getDogPlayerClass().newInstance();
			dogPlayer.startNewGame();
			
			turn = 0;
			
			initDone = true;
		} catch (Exception e)
		{
			log.error("Exception: " + e);
			e.printStackTrace();
			return false;
		}
		notifyListeners(GameUpdateType.STARTING);
		return true;
	}
	

	
}
