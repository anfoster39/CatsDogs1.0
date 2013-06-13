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




/**
 * 
 * @author Chris Murphy
 * 
 */
public final class Simulator
{
	
	private GameEngine engine;
	private static final long serialVersionUID = 1L;
	
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
	
	
	private void simulate(Class<CatPlayer> catPlayer, Class<DogPlayer> dogPlayer, int numRuns) {

		engine.getConfig().setCatPlayerClass(catPlayer);
		engine.getConfig().setDogPlayerClass(dogPlayer);

		
		int catWins = 0, dogWins = 0;
		
		for (int i = 0; i < numRuns; i++) {
		
			// set up the game
			engine.setUpGame();
				
			while (engine.step()) { // keep doing this until the game ends!
				
				//System.err.println("Step");
			
			}
		
			
			if (Cat.wins(engine.getBoard().objects)) {
				//System.out.println("Cat wins!");
				catWins++;
			}
			else { 
				//System.out.println("Dog wins!");
				dogWins++;
			}
				
		}
		System.out.println(catPlayer.getName() + "=" + catWins + " " + dogPlayer.getName() + "=" + dogWins);

		
	}
	
	public Simulator(GameEngine engine, String myCatPlayerClass, String myDogPlayerClass, String runs)
	{
		this.engine = engine;
		
		ArrayList<Class<CatPlayer>> catPlayers = engine.getConfig().availableCatPlayers;
		ArrayList<Class<DogPlayer>> dogPlayers = engine.getConfig().availableDogPlayers;
		int numRuns = Integer.parseInt(runs);
		
		try {
			Class<CatPlayer> myCatPlayer = (Class<CatPlayer>)Class.forName(myCatPlayerClass);
			Class<DogPlayer> myDogPlayer = (Class<DogPlayer>)Class.forName(myDogPlayerClass);
			
			// first play my cat against all dogs 							
			for (Class<DogPlayer> dogPlayer : dogPlayers) {
										
				simulate(myCatPlayer, dogPlayer, numRuns);
				
			}

			// now play my dog against all cats 							
			for (Class<CatPlayer> catPlayer : catPlayers) {
										
				simulate(catPlayer, myDogPlayer, numRuns);
				
			}

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	public static final void main(String[] args)
	{
		GameEngine engine = new GameEngine(args[0]);
		new Simulator(engine, args[1], args[2], args[3]);
	}





}
