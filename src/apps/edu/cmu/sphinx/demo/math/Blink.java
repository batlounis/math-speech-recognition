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

public class Blink extends Thread{
	//String eq;
	//String ceq;
	//public static void setEquation(String Equation){eq = Equation;}
	//public static void setCursorEquation(String CursorEquation){ceq = CursorEquation;}
    @Override
    public void run(){
	
	try{	while(true){
			Thread.sleep(500);			
			ThreadsRunner.changeEquation(DialogManager.CursorFreeEquation,DialogManager.CursorEquation);}
        }catch(Exception e){
			System.err.println(e);
		}
        System.exit(0);
    }

    @Override
    protected void finalize() throws Throwable {

    }
}
