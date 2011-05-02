package edu.cmu.sphinx.demo.math;


import javax.swing.JApplet;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;

 
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.TimerPool;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.demo.math.*;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import java.io.*;
import java.net.URL;
import java.util.*;




public class HelloWorld  extends JApplet  {
    //Called when this applet is loaded into the browser.
    public void init() {
        //Execute a job on the event-dispatching thread; creating this applet's GUI.
        try {

            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
					JLabel lbl;
		            lbl = new JLabel("\nWelcome to the Math Recognition Demo ");
                    add(lbl);
                }
			}
            );
        } catch (Exception e) {
            System.err.println("createGUI didn't complete successfully");
        }
    }





}
