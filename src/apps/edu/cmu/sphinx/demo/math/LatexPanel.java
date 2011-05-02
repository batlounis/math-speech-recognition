package edu.cmu.sphinx.demo.math;

import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.util.*;
import org.scilab.forge.jlatexmath.*;

// Public Class for Panel to get equation in latex form
                                                                                                              
public class LatexPanel extends JPanel {
	//members
	private JButton GetCodeButton;
	private JLabel title;

	//Constructor
	public LatexPanel(JLabel LateXlabel){
		//Create Button & Label
		GetCodeButton = new JButton("Copy Latex Code");
		GetCodeButton.setActionCommand("GetCode");
		GetCodeButton.setSize(10,50);
		title = new JLabel("LaTeX Code");
		title.setFont(new Font("Serif", Font.PLAIN,20));

		//Set Layout
		setLayout(new GridLayout(2,1,2,4));
		add(title);
		add(LateXlabel);

		//Add Action Listener
		//Listener LatexLis = new LatexPanelAction();
		//GetCodeButton.addAddActionListener(LatexLis);
//.addActionListener(this);
		
	}//end constructor
			
} // class MyPanel

