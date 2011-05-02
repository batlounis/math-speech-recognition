package edu.cmu.sphinx.demo.math;

import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.TimerPool;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.util.props.PropertyException;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

import edu.cmu.sphinx.demo.math.*;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.Rule;
import javax.speech.recognition.RuleGrammar;
import java.io.*;
import java.net.URL;
import java.util.*;


public class SphinxThread extends Thread {

    @Override
    public void run(){
		try{
            URL url;
            url = SphinxThread.class.getResource("math.config.xml");

            ConfigurationManager cm = new ConfigurationManager(url);
            DialogManager dialogManager = (DialogManager)cm.lookup("dialogManager");

            System.out.println("\nWelcome to the Math Recognition Demo ");
			dialogManager.allocate();
            System.out.println("Running  ...");
            dialogManager.go();
            System.out.println("Cleaning up  ...");
            dialogManager.deallocate();

        } catch (IOException e) {
            System.err.println("Problem when loading Dialog: " + e);
        } catch (PropertyException e) {
            System.err.println("Problem configuring Dialog: " + e);
        } catch(Exception e){
			System.err.println(e);
		}
        System.exit(0);
    }

    @Override
    protected void finalize() throws Throwable {

    }
}
