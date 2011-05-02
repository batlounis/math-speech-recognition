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
                                                                                                              
public class LatexPanelAction extends JPanel implements ActionListener{
	
	//members	
	private JButton getImageButton;
	public static JLabel equationLabel;
	private JLabel title;
	private JLabel jLabel3;
	public static JLabel youSaidLabel;
	public static JTextField latexField;
	
//	private JButton GetCodeButton;
	//private JLabel title;
//	public static JTextField latexField  = new JTextField(20);

	//Constructor
	public LatexPanelAction(){

		//Initialize Components
		getImageButton = new JButton();
		equationLabel = new JLabel();
		title = new JLabel();
		jLabel3 = new JLabel();
		youSaidLabel = new JLabel();
		latexField = new JTextField();
		
				
		//Set Components
		//Equation Label
		equationLabel.setBackground(new java.awt.Color(250, 250, 250));
	        equationLabel.setFont(new java.awt.Font("Cambria Math", 0, 18)); // NOI18N
        	equationLabel.setForeground(new java.awt.Color(0, 3, 33));
	        equationLabel.setHorizontalAlignment(SwingConstants.CENTER);
	        equationLabel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(240, 240, 240), 4, true));
	        equationLabel.setOpaque(true);

		//Title Label
		title.setBackground(new java.awt.Color(250, 250, 250));
        	title.setFont(new java.awt.Font("Cambria Math", 0, 24)); // NOI18N
        	title.setForeground(new java.awt.Color(0, 3, 33));
        	title.setHorizontalAlignment(SwingConstants.CENTER);
        	title.setText("MATH RECONGNIZER");
        	title.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(200, 200, 200), 2, true));
        	title.setOpaque(true);

		//LateX Code title Label
	        jLabel3.setFont(new java.awt.Font("Cambria Math", 1, 15)); // NOI18N
	        jLabel3.setForeground(new java.awt.Color(0, 3, 33));
	        jLabel3.setText("LaTeX Code");

		//getImageButton
		getImageButton = new JButton("Copy As Image");
		getImageButton.setActionCommand("GetImage");
		getImageButton.setSize(10,50);

		//set latexField action command
		latexField.setActionCommand("CompileWrittenCode");		
	        GroupLayout layout = new GroupLayout(this);
	        this.setLayout(layout);
        	layout.setHorizontalGroup(
	            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
	            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	            .addComponent(latexField, GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
                    .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                    .addComponent(getImageButton, GroupLayout.PREFERRED_SIZE, 140, GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(266, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                    .addComponent(jLabel3)
                    .addContainerGap(322, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
                    .addComponent(youSaidLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(equationLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 294, Short.MAX_VALUE))
                    .addContainerGap(112, Short.MAX_VALUE))))
            	    .addComponent(title, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(title, GroupLayout.PREFERRED_SIZE, 52, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 31, Short.MAX_VALUE)
                .addComponent(youSaidLabel)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(equationLabel, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(getImageButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel3)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(latexField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, 
				GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26))
        );


		Caret Lis = new Caret();
		//Add Action Listener
		getImageButton.addActionListener(this);
		latexField.addActionListener(this);
		latexField.addCaretListener(Lis);

	}
	public void actionPerformed(ActionEvent e){
		String com = e.getActionCommand();
		if(com == "GetImage"){
			System.out.println("RECIEVED CODE");			
		}else if (com == "CompileWrittenCode"){
			ThreadsRunner.changeEquation(latexField.getText() , latexField.getText());		
		}
	} // actionPerformed

} // class MyPanel

