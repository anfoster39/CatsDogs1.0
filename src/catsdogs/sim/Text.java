/* 
 * 	$Id: GUI.java,v 1.4 2007/11/14 22:02:59 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package catsdogs.sim;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;






//import colony.Tournament;
//import colony.Tournament.TournamentResult;
/**
 * 
 * @author Chris Murphy
 * 
 */
public final class Text
{
	private final static String VERSION = "1.2";
	
	private GameEngine engine;
	private static final long serialVersionUID = 1L;
	
	private String errorMessage = "Error!";
	
	private volatile boolean fast;
	private GameEngine real_engine;
	public void setEngine(GameEngine engine)
	{
		real_engine = this.engine;
		this.engine = engine;
	}
	public void restoreEngine()
	{
		this.engine = this.real_engine;
	}
	public Text(GameEngine engine, String catPlayerClass, String dogPlayerClass, String runs)
	{
		this.engine = engine;
		

		// configuration
		try {
			engine.getConfig().setCatPlayerClass((Class<CatPlayer>)Class.forName(catPlayerClass));
			engine.getConfig().setDogPlayerClass((Class<DogPlayer>)Class.forName(dogPlayerClass));
			int numRuns = Integer.parseInt(runs);
			
			int catWins = 0, dogWins = 0;

			for (int i = 0; i < numRuns; i++) {
		
				// set up the game
				engine.setUpGame();
		
				while (engine.step()) { // keep doing this until the game ends!
				
					//System.err.println("Step");
			
				}
		
				// game's over... now what?
				if (Cat.wins(engine.getBoard().objects)) {
					System.out.println("Round " + (catWins + dogWins) + ": " + "Cat wins!");
					catWins++;
				}
				else { 
					System.out.println("Round " + (catWins + dogWins) + ": " + "Dog wins!");
					dogWins++;
				}
				
			}
			System.out.println("Cat=" + catWins + " Dog=" + dogWins);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setErrorMessage(String msg) {
		errorMessage = msg;
	}


	public static final void main(String[] args)
	{
		GameEngine engine = new GameEngine(args[0]);
		new Text(engine, args[1], args[2], args[3]);
	}





}
