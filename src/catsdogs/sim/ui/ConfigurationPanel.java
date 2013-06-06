/*
 * 	$Id: ConfigurationPanel.java,v 1.3 2007/11/14 22:00:22 johnc Exp $
 * 
 * 	Programming and Problem Solving
 *  Copyright (c) 2007 The Trustees of Columbia University
 */
package catsdogs.sim.ui;


import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import catsdogs.sim.GameConfig;
import catsdogs.sim.GameEngine;
import catsdogs.sim.CatPlayer;
import catsdogs.sim.DogPlayer;






public final class ConfigurationPanel extends JPanel implements ChangeListener, ItemListener, ListSelectionListener
{
	private static final long serialVersionUID = 1L;
	private GameConfig config;

	static Font config_font = new Font("Arial", Font.PLAIN, 14);

	private JSpinner roundSpinner;
	
	private JLabel catPlayerLabel;
	private JComboBox catPlayerBox;
	private JLabel dogPlayerLabel;
	private JComboBox dogPlayerBox;

	private Class catPlayerClass;
	private Class dogPlayerClass;

	private JSlider delaySlider;
	

	
	public ConfigurationPanel(final GameConfig config,final GameEngine engine)
	{
		this.config = config;
		this.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Configuration"));
		this.setPreferredSize(new Dimension(350, 1200));
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		this.setLayout(layout);

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		JPanel panel;
		
		panel = new JPanel(new FlowLayout());
		panel.setMinimumSize(new Dimension(100, 200));
		catPlayerLabel = new JLabel("Cat Player:");
		panel.add(catPlayerLabel);
		// make player combo box
		catPlayerBox = new JComboBox(config.getCatPlayerListModel());
		catPlayerBox.addItemListener(this);
		catPlayerBox.setRenderer(new ClassRenderer());
		panel.add(catPlayerBox);
		layout.setConstraints(panel, c);
		this.add(panel);
		config.setCatPlayerClass((Class<CatPlayer>) catPlayerBox.getSelectedItem());

		
		panel = new JPanel(new FlowLayout());
		panel.setMinimumSize(new Dimension(100, 200));
		dogPlayerLabel = new JLabel("Dog Player:");
		panel.add(dogPlayerLabel);
		// make player combo box
		dogPlayerBox = new JComboBox(config.getDogPlayerListModel());
		dogPlayerBox.addItemListener(this);
		dogPlayerBox.setRenderer(new ClassRenderer());
		panel.add(dogPlayerBox);
		layout.setConstraints(panel, c);
		this.add(panel);
		config.setDogPlayerClass((Class<DogPlayer>) dogPlayerBox.getSelectedItem());

		
		
		delaySlider = new JSlider(0, 1000);
		delaySlider.setValue(30);
		panel = new JPanel(new FlowLayout());
		panel.add(new JLabel("Delay (0 - 1000ms):"));
		panel.add(delaySlider);
		layout.setConstraints(panel, c);
		this.add(panel);
		
		mouseCoords = new JLabel("Mouse: N/A");
		add(mouseCoords);
		
		
	}
	public void setMouseCoords(Point2D p)
	{
		if(p == null)
			mouseCoords.setText("Mouse: N/A");
		float v1 = (float) Math.round(p.getX()*100)/100;
		float v2 = (float) Math.round(p.getY()*100)/100;
		mouseCoords.setText(String.format("Mouse: %4.2f, %4.2f", v1,v2));
	}
	protected JLabel mouseCoords;
	
	public void setEnabled(boolean enabled)
	{
		super.setEnabled(enabled);
		catPlayerBox.setEnabled(enabled);
		dogPlayerBox.setEnabled(enabled);
	}

	public void stateChanged(ChangeEvent arg0)
	{
		if (arg0.getSource().equals(roundSpinner))
			config.setMaxRounds(((Integer) ((JSpinner) arg0.getSource()).getValue()).intValue());
		else
			throw new RuntimeException("Unknown State Changed Event!!");
	}

	public void itemStateChanged(ItemEvent arg0)
	{
		if (arg0.getSource().equals(catPlayerBox) && arg0.getStateChange() == ItemEvent.SELECTED)
		{
			// config.setActivePlayer((Class)arg0.getItem());
			catPlayerClass = (Class) arg0.getItem();
			config.setCatPlayerClass((Class<CatPlayer>) catPlayerBox.getSelectedItem());
		}
		else if (arg0.getSource().equals(dogPlayerBox) && arg0.getStateChange() == ItemEvent.SELECTED)
		{
			// config.setActivePlayer((Class)arg0.getItem());
			dogPlayerClass = (Class) arg0.getItem();
			config.setDogPlayerClass((Class<DogPlayer>) dogPlayerBox.getSelectedItem());
		}
	}

	public JSlider getSpeedSlider()
	{
		return delaySlider;
	}

	public void valueChanged(ListSelectionEvent e)
	{
	}
	
	public void updateScore(int caught)
	{
		//score.setText("Caught: " + caught + " ("+Math.round(10000d*(double) (caught)/(double)1000)/100d + "%)");
	}
}
