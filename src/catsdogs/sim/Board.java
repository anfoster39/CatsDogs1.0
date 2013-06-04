package catsdogs.sim;


import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.log4j.Logger;


public final class Board {
	public static final int pixels_per_meter = 150;
	public static int pixels_per_pixel = 25;
	public static final int X = 7;
	public static final int Y = 7;
	
	public static final int EMPTY = 0;
	public static final int CAT = 1;
	public static final int DOG = 2;

	public boolean impersonated = false;
	public GameEngine engine;
	
	// this represents the objects on the board
	protected int[][] objects;
	
	private final Logger logger = Logger.getLogger(this.getClass());
	
	public Board() {
		init();
	}
	
	public static Point2D toScreenSpace(Point2D point2d) {
		Point2D clone = new Point2D.Double();
		clone.setLocation(toScreenSpace(point2d.getX()),
				toScreenSpace(point2d.getY()));
		return clone;
	}



	public static double fromScreenSpace(double v) {
		return v * pixels_per_pixel / pixels_per_meter;
	}

	public static double toScreenSpace(double v) {
		return v * pixels_per_meter / pixels_per_pixel;
	}

	public static Point2D fromScreenSpace(Point2D p) {
		Point2D r = new Point2D.Double();
		r.setLocation(fromScreenSpace(p.getX()), fromScreenSpace(p.getY()));
		return r;
	}



	private void init() {      
		
		objects = new int[X][Y];
		        
		// place the dogs
		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				objects[x][y] = Board.DOG;
			}
		}
		
		// place the cats
		objects[0][0] = Board.CAT;
		objects[0][Y-1] = Board.CAT;
		objects[X-1][0] = Board.CAT;
		objects[X-1][Y-1] = Board.CAT;
		objects[X/2][Y/2] = Board.CAT;
	    
	}
	
	protected boolean update(Move m) {
		// check for nulls
		if (m == null) {
			logger.error("Move object passed to Board.update is null");
			return false;
		}

		int x = m.getX();
		int y = m.getY();
		
		int obj = objects[x][y];
		
		/*
		if (objects[x][y] != obj) {
			// eh, not sure how this happened, but...
			logger.error("Invalid state in Board.update: GameObject in Move not in right location");
			return false;
		}
		*/
		
		// get the intended destination
		int newx = x;
		int newy = y;
		if (m.getDirection() == Move.UP) newy--;
		else if (m.getDirection() == Move.DOWN) newy++;
		else if (m.getDirection() == Move.RIGHT) newx++;
		else if (m.getDirection() == Move.LEFT) newx--;
		else {
			logger.error("Move object passed to Board.update contains invalid direction: " + m.getDirection());
			return false;
		}
		
		if (newx < 0 || newy < 0 || newx >= X || newy >= Y) {
			logger.error("Move object passed to Board.update tries to move object out of bounds");
			return false;
		}
		
		// so far so good, now let's see whether it's a legal move
		if (obj == Board.CAT) {
			int target = objects[newx][newy];
			if (target == Board.EMPTY) {
				logger.error("Cat trying to move onto empty space");
				return false; // cat can't move onto an empty space
			}
			else if (target == Board.CAT) {
				logger.error("Cat trying to move onto another Cat");
				return false; // cat can't move onto another cat
			}
			else if (target == Board.DOG) {
				// sorry doggy....
				objects[newx][newy] = obj;
				objects[x][y] = Board.EMPTY;
				return true;
			}
			else {
				logger.error("Unknown object found in board");
				return false;
			}
		}
		else if (obj == Board.DOG) {
			if (objects[newx][newy] != Board.EMPTY) {
				logger.error("Dog trying to move to non-empty space");
				return false; // can only move onto empty space
			}
			else {
				objects[newx][newy] = obj;
				objects[x][y] = Board.EMPTY;
				return true;
			}
		}
		else {
			logger.error("Move object passed to Board.update contains neither Cat nor Dog");
			return false;
		}
		
	}


	public static int[][] cloneBoard(int[][] objects) {
		int[][] clone = new int[X][Y];
		for (int x = 0; x < X; x++) {
			for (int y = 0; y < Y; y++) {
				clone[x][y] = objects[x][y];
			}
		}
		return clone;
	}
	
}
