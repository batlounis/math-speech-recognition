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
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

// Public Class for Panel to get equation in latex form
                                                                                                              
public class Caret extends JPanel implements CaretListener{

public static int prevCaretPos = LatexPanelAction.latexField.getCaretPosition();
int caretPosition = LatexPanelAction.latexField.getCaretPosition();
	public void caretUpdate(CaretEvent e){
		boolean forward = true;
		if(caretPosition>prevCaretPos){forward = true;}else{forward = false;}

		caretPosition = LatexPanelAction.latexField.getCaretPosition();
		DialogManager.setCursorAndRecompile(caretPosition,forward);
		prevCaretPos = caretPosition;
		System.out.println("Cursor Position: " + caretPosition);
			
	}
}
