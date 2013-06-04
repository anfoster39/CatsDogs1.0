/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package catsdogs.sim;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;

import org.apache.log4j.Logger;

public class GameConfig implements Cloneable{

	static int gameDelay = 100;
	int number_of_rounds;
	int current_round;
	public static int density = 50;
	public static int turnProbability = 50; // TODO: make this configurable
	int max_rounds = max_rounds_max;
	private ArrayList<Class<Player>> availableCatPlayers;
	private ArrayList<Class<Player>> availableDogPlayers;
	private Class<Player> catPlayerClass;
	private Class<Player> dogPlayerClass;
	public static Random random;
	private Properties props;
	private String confFileName;
	private Logger log = Logger.getLogger(this.getClass());

	public Class<Player> getCatPlayerClass() {
		return catPlayerClass;
	}

	public void setCatPlayerClass(Class<Player> playerClass) {
		this.catPlayerClass = playerClass;
	}

	public Class<Player> getDogPlayerClass() {
		return dogPlayerClass;
	}

	public void setDogPlayerClass(Class<Player> playerClass) {
		this.dogPlayerClass = playerClass;
	}


	public static final int max_rounds_max = 5000;
	public static final long TIME_LIMIT = 5000;




	public void setMaxRounds(int v) {
		this.max_rounds = v;
	}

	public int getMaxRounds() {
		return max_rounds;
	}

	public GameConfig(String filename) {
		confFileName = filename;
		props = new Properties();
		availableCatPlayers = new ArrayList<Class<Player>>();
		availableDogPlayers = new ArrayList<Class<Player>>();
		load();
	}

	/**
	 * Read in configuration file.
	 * 
	 * @param file
	 */
	public void load() {
		try {
			FileInputStream in = new FileInputStream(confFileName);
			props.loadFromXML(in);
		} catch (IOException e) {
			System.err.println("Error reading configuration file:"
					+ e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
		extractProperties();
	}

	/**
	 * Get the game configuration parameters out of the Property object.
	 * 
	 */
	private void extractProperties() {
		String s;

		// READ IN CLASSES
		s = props.getProperty("catsdogs.cat");
		if (s != null) {
			String[] names = s.split(" ");
			for (int i = 0; i < names.length; i++) {
				try {
					availableCatPlayers.add((Class<Player>) Class
							.forName(names[i]));
				} catch (ClassNotFoundException e) {
					log.error("[Configuration] Class not found: " + names[i]);
				}
			}
		}
		
		if (availableCatPlayers.size() == 0)
			log.fatal("No Cat player classes loaded!!!");

		s = props.getProperty("catsdogs.dog");
		if (s != null) {
			String[] names = s.split(" ");
			for (int i = 0; i < names.length; i++) {
				try {
					availableDogPlayers.add((Class<Player>) Class
							.forName(names[i]));
				} catch (ClassNotFoundException e) {
					log.error("[Configuration] Class not found: " + names[i]);
				}
			}
		}
		
		if (availableDogPlayers.size() == 0)
			log.fatal("No Dog player classes loaded!!!");
		
		random = new Random();
	}



	public ComboBoxModel getCatPlayerListModel() {
		DefaultComboBoxModel m = new DefaultComboBoxModel();
		for (Class c : availableCatPlayers) {
			m.addElement(c);
		}
		return m;
	}

	public ComboBoxModel getDogPlayerListModel() {
		DefaultComboBoxModel m = new DefaultComboBoxModel();
		for (Class c : availableDogPlayers) {
			m.addElement(c);
		}
		return m;
	}


}
