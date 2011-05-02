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
import org.scilab.forge.jlatexmath.*;//TeXConstants;
//import org.scilab.forge.jlatexmath.TeXFormula;
//import org.scilab.forge.jlatexmath.TeXIcon;

public class ThreadsRunner extends Thread{
	/*variable declaration*/
	//public static JLabel label = new JLabel("Start Speaking");
	public static TeXFormula formula = new TeXFormula("start");
	//public static JLabel LateXlabel = new JLabel("");
	
	public static void changeEquation (String S, String S2){
	try{	
		LatexPanelAction.equationLabel.setText("");
		formula.setLaTeX(S);
		TeXIcon ti = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
		BufferedImage b = new BufferedImage(ti.getIconWidth(), ti.getIconHeight(),
								BufferedImage.TYPE_4BYTE_ABGR);	
		ImageIcon Icon = new ImageIcon(b);
		ti.paintIcon(LatexPanelAction.equationLabel, b.getGraphics(), 0, 0);
		LatexPanelAction.equationLabel.setIcon(Icon);
		}
	catch (Exception e){System.err.println(e);}
	}
	public static void change_text(String text, String Utterance){
		LatexPanelAction.youSaidLabel.setText("You said: " + Utterance);
		LatexPanelAction.latexField.setText(text);
		//the following two lines are just for test purposes

	}
	
	public static void main(String[] args){
		System.out.println("HERE");
		SphinxThread sphinx_thread = new SphinxThread();
		sphinx_thread.start();		
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            		public void run() {
                		createAndShowGUI();
            		}
		});
		
	}
	
	private static void createAndShowGUI() {
	try{	   

		//Create and set up the window.
	        JFrame frame = new JFrame("Math Equations");
		frame.setSize(700,400);
		frame.setLocation(20,200);     
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		long t1 = System.currentTimeMillis();			
		Container Pane = frame.getContentPane();		
		JComponent LPanel = new LatexPanelAction();
		Pane.add(LPanel); 
	        frame.setVisible(true);
		long t2 = System.currentTimeMillis();
		System.out.println(t2 - t1);
	}
	catch(Exception e){
			System.err.println(e);
		}
			
    }
	
	
	
}

