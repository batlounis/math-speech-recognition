/*
 * Copyright 1999-2004 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 *
 */

package edu.cmu.sphinx.demo.math;

import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.Configurable;
import edu.cmu.sphinx.util.props.PropertyException;
import edu.cmu.sphinx.util.props.PropertySheet;
import edu.cmu.sphinx.util.props.S4Component;

import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.speech.EngineException;
import edu.cmu.sphinx.tools.tags.ObjectTagsParser;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.util.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;




public class DialogManager implements Configurable {
	
	// if this is false, utterances below will be used
	boolean use_microphone=false;
	
	// utterances we might want to use instead of using the microphone
	static final String[] utterances = {
	"one over one",
	"over two",
	"over three",
	"go back",
	"go back",
	"over", "a plus b","skip",
	"minus three over b",
	"plus three minus", "a times b",
	"divided by",
	"x plus y","skip"
	};

	/* Variable declaration */
	public static String Equation = "";
	public static String CursorEquation = "";	
	public static String CursorFreeEquation = "";
	String Case = "";
	String CasePrev = "";
	public void setCase(String S){if(updateCase){CasePrev = Case; Case = S;}updateCase = true;}
	public static int Cursor = -1;
	int lastCharLengthPrev = 1;
	int lastCharLength = 1;
	int lastCharLengthTemp = 1;
	int FtLengthPrev;	
	int FtLength;//last function length(example :\frac{}{} is 9
	public void setFtLength(int i){FtLengthPrev = FtLength; FtLength = i;}	
	boolean AllowLCL = true;
	public void setAllowLCL(boolean b){AllowLCL = b;}//allow or disallow last char length for div purposes (may be unused)
	public void setLCL(String S){
		if(AllowLCL){
			lastCharLengthPrev = lastCharLength;
			lastCharLength = S.length();}
		else{lastCharLengthTemp = S.length();}
				System.out.println("LCL: " + lastCharLength);
		}
	public void updateLCL(){lastCharLengthPrev = lastCharLength; lastCharLength = lastCharLengthTemp;}
	//UC: Undo Cursors: remove from UC1 to UC2
	int UC1 = 0;
	int UC2 = 0;
	//BC: Bracket Cursors: keeping track of brackets for division and other purposes
	//int OBC = 0; //matching Opening Bracket Cursor
	//int CBC = 0; //last Closing Bracket Cursor
	//public void setOBC(String S){OBC = Cursor + S.length();if(OBC>CBC){CBC = -1;}}
	//Control for automatic bracket assumed by system
	boolean AB = false;//works only one step back
	public void setAB(boolean b, String S){AB = b;}// OBC = Cursor + S.length();}
	boolean allowed = true;//only allowed to go back once
	int SkipVal = 0;//for going back in skip case
	boolean updateCase = true;//used in the function which keeps track of what was previously said

	public static void setCursorAndRecompile(int C,boolean forward){//cursor location is changed based on latex code cursor
		try{
			Cursor = C - 1;
			//Check if in a valid position
			CursorEquation = Equation.substring(0,Cursor+1) + "\\textsf{I }" + Equation.substring(Cursor+1);
			CursorFreeEquation = Equation.substring(0,Cursor+1) + "\\textsf{  }" + Equation.substring(Cursor+1);
			try {ThreadsRunner.changeEquation(CursorEquation , CursorFreeEquation);}
			catch(Exception e){
				if(forward){Cursor = Equation.indexOf("{",Cursor + 1);
					LatexPanelAction.latexField.setCaretPosition(Cursor);}
				else{Cursor = Equation.lastIndexOf("}",Cursor + 1);
					LatexPanelAction.latexField.setCaretPosition(Cursor);}
			}
		
		}catch(Exception e){}
	}
	//This function finds the matching bracelet
	public int findMatchingB(int i, char o,char c){
		int close = 0;
		int open = 0;		
		if (Equation.charAt(i) == o){open++;}
		else if (Equation.charAt(i) == c){close++;}
		i--;
		while(close != open){
			if (Equation.charAt(i) == o){open++;}
			else if (Equation.charAt(i) == c){close++;}
			i--;

		}		
		return i;
	}
	//Checks if it's a fraction since frac has two parameters
	public int findTerm(int CursorT){
		boolean frac = false;
		int i = findMatchingB(CursorT,'{','}');
		System.out.println("i-1: " + Equation.substring(i-1,i));
		System.out.println("i-2: " + Equation.substring(i-2,i-1));
		//Check if frac to continue search
		if(Equation.substring(i-1,i).equals(" ") & Equation.substring(i-2,i-1).equals("}")){
			frac = true; CursorT = i - 2;
		}
		else if(Equation.substring(i-1,i).equals("}")){frac = true; CursorT = i - 1;}
		if(frac){
			i = findMatchingB(CursorT,'{','}');}
		System.out.println("frac: " + frac);
		return i;
	}
	//find function before a term with curly braces
	public int findFt(int CursorT){
		//for now it's just frac
		return Equation.lastIndexOf("\\frac",CursorT);}

	public void skip(){//gets out of current curly braces or bracket
		int i = Equation.indexOf("{",Cursor + 1);
		if(i<0){SkipVal = Cursor - Equation.length() + 1; Cursor = Equation.length() - 1;}
		else{SkipVal = i - Cursor; Cursor = i;}
			
	}
	//function to handle bracketing issues with division when we say divided by after a closing bracket...
	public void handleDivision(String den){
		int CursorT;		
		if(den.length() == 0){
			System.out.println("ZERO LENGTH");			
			den = " ";
			CursorT = Cursor + 9;} 
		else{
			CursorT = Cursor + 9 + den.length();}
		if(Equation.charAt(Cursor)=='{'|Equation.charAt(Cursor)=='('|((Equation.charAt(Cursor-1)=='('|Equation.charAt(Cursor-1)=='{')
		&Equation.charAt(Cursor)==' ')){
			System.out.println("invalid");updateCase = false; return;
		}
		if(Equation.length()>Cursor+1){//If cursor not at end of string
			if(Equation.substring(Cursor,Cursor + 1).equals(")")){//If closing bracket
				int p = findMatchingB(Cursor,'(',')');				
				Equation = Equation.substring(0,p) + "\\frac{" + Equation.substring(p,Cursor+1) + "}{" + den + "}" +
				Equation.substring(Cursor+1);
				UC1 = p + 6;//OBC + 6;
				UC2 = Cursor + 6;//CBC + 6;			
			}
			else if(Equation.substring(Cursor,Cursor + 1).equals("}")){//If closing bracket
				int p = findTerm(Cursor); p = findFt(p);				
				Equation = Equation.substring(0,p) + "\\frac{" + Equation.substring(p,Cursor+1) + "}{" + den + "}" +
				Equation.substring(Cursor+1);
				UC1 = p + 6;//OBC + 6;
				UC2 = Cursor + 6;//CBC + 6;	
			}
			else{
				Equation = Equation.substring(0,Cursor + 1 - lastCharLength) + "\\frac{" + Equation.substring(Cursor + 1 -
				lastCharLength,Cursor+1) + "}{" + den + "}" + Equation.substring(Cursor+1);//just take the last term
				UC1 = Cursor - lastCharLength + 7; //UCs now point to the numerator term so that it can be preserved in undo
				UC2 = Cursor + 6;}
		}
		else{//if cursor at end of string
			if(Equation.substring(Cursor).equals(")")){//If closing bracket
				System.out.println("I'm should be HERE");				
				int p = findMatchingB(Cursor,'(',')'); 
				Equation = Equation.substring(0,p) + "\\frac{" + Equation.substring(p) + "}{" + den + "}";
				UC1 = p + 6;//OBC + 6;
				UC2 = Cursor + 6;//CBC + 6;			
			}
			else if(Equation.substring(Cursor).equals("}")){//If closing bracket
				int p = findTerm(Cursor); p = findFt(p);
				Equation = Equation.substring(0,p) + "\\frac{" + Equation.substring(p) + "}{" + den + "}";
				UC1 = p + 6;//OBC + 6;
				UC2 = Cursor + 6;//CBC + 6;
			}
			else{
				Equation = Equation.substring(0,Cursor + 1 - lastCharLength) + "\\frac{" + Equation.substring(Cursor + 1 - 
				lastCharLength) + "}{" + den + "}";//take the last term
				UC1 = Cursor - lastCharLength + 7; //UCs now point to the numerator term so that it can be preserved in undo
				UC2 = Cursor + 6;		
			}
		}
		Cursor = CursorT;
			
	}
	//function to replace % with \\ cuz stupid gram won't write backslash!!
	public String setBackslash(String S){
		String S2 = S;		
		for (int i = 0; i < S.length(); i = i) { // Test and Loop
			i = S.indexOf("%",i);
			if (i<0){
			return S2;}
			else{ 
				if(i!=0){
					S2 = S.substring(0,i) + "\\" + S.substring(i+1);}
				else{
					S2 = "\\"+ S.substring(1);}
			}		
			i = i + 1;
		}
		return S2;
	
	}
	//function to update equation with simple terms at cursor position
	public void addAtCursor(String S){
		S = setBackslash(S);
		UC1 = Cursor + 1;
		if(AB & !(S.substring(S.length()-1).equals("("))){
			S = S + ")";
			//CBC = Cursor + S.length();
			//lastCharLength = S.length() + 1;
			AB=false;}
		if(Equation.length()>Cursor+1){//If cursor is not located at the end of the string
			Equation = Equation.substring(0,Cursor+1) + S + Equation.substring(Cursor+1);
		}else{
			Equation = Equation + S; //If at end then just append
		}
		Cursor = Cursor + S.length();//move cursor
		UC2 = Cursor;
		allowed = true;
	}
	public void goBack(){//undo last step according to UC cursors
		if(Case == "term" | Case == "operation")
		{
			
			if(Equation.length()>Cursor+1){//If cursor is not located at the end of the string
				if(AB & (Equation.substring(Cursor).equals("("))){//if deleting something that sets AB => unset AB
					AB = false;}				
				Equation = Equation.substring(0,UC1) + Equation.substring(UC2 + 1);//delete
			}else{
				if(AB & (Equation.substring(Equation.length()-1).equals("("))){//if deleting something that sets AB => unset AB
					AB = false;}				
				Equation = Equation.substring(0,UC1);
				
			}
			FtLengthPrev = FtLength;
			lastCharLength = lastCharLengthPrev;
			//recheck those when adding the bracelet option			
			//OBC = Equation.lastIndexOf('(');
			//CBC = Equation.lastIndexOf(')');
			//if(CBC<OBC){CBC = -1;}
			Cursor = UC1 - 1;
			UC2 = UC1 - 1;
		
		}
		else if(Case == "dividePrev"){
		//REMOVE Den
			if(Equation.length()>Cursor+1){//If cursor is not located at the end of the string
				int i = Equation.indexOf("}",UC2+2) + 1;
				Cursor = UC2 - 6;
				Equation = Equation.substring(0,UC1-6) + Equation.substring(UC1,UC2+1) + Equation.substring(i);
			}else{
				System.out.println("UC2: " + UC2);				
				Equation = Equation.substring(0,UC1-6) + Equation.substring(UC1,UC2 + 1);
				Cursor = Equation.length()-1;				
			}
			lastCharLength = lastCharLengthPrev;		
			//AllowLCL = false;			
			System.out.println("lastCharLength: " + lastCharLength);
		}
		else if(Case == "skip"){
			Cursor = Cursor - SkipVal;
		}
		else if(Case == "return"){
			System.out.println("Not allowed");updateCase = false;
		}
	}

/**************** END OF ADDED EQUATIONS **********************/
    /**
     * The property that defines the name of the grammar component 
     * to be used by this dialog manager
     */
    @S4Component(type = JSGFGrammar.class)
    public final static String PROP_JSGF_GRAMMAR = "jsgfGrammar";

    /**
     * The property that defines the name of the microphone to be used 
     * by this dialog manager
     */
    @S4Component(type = Microphone.class)
    public final static String PROP_MICROPHONE = "microphone";

    /**
     * The property that defines the name of the recognizer to be used by
     * this dialog manager
     */
    @S4Component(type = Recognizer.class)
    public final static String PROP_RECOGNIZER = "recognizer";

    // ------------------------------------
    // Configuration data
    // ------------------------------------
    private JSGFGrammar grammar;
    private Logger logger;
    private Recognizer recognizer;
    private Microphone microphone;
    ObjectTagsParser parser;
	BaseRecognizer jsapiRecognizer;

    /*
    * (non-Javadoc)
    *
    * @see edu.cmu.sphinx.util.props.Configurable#n	
ewProperties(edu.cmu.sphinx.util.props.PropertySheet)
    */
    public void newProperties(PropertySheet ps) throws PropertyException {
        logger = ps.getLogger();
        grammar = 
            (JSGFGrammar) ps.getComponent(PROP_JSGF_GRAMMAR);
        microphone = 
            (Microphone) ps.getComponent(PROP_MICROPHONE);
        recognizer = 
            (Recognizer) ps.getComponent(PROP_RECOGNIZER);

    }

    private ObjectTagsParser getTagsParser() {
        if (parser == null) {
            parser = new ObjectTagsParser();
            parser.put("appObj", this);
        }
        return parser;
    }


	// This method is called from grammar file to print on screen
	public static void print(String s){
		System.out.println(s);
	}

	String getTagString(String result) throws GrammarException {
	      RuleParse ruleParse = getRuleParse(result);

	     if (ruleParse == null)
	          return null;

		  getTagsParser().parseTags(ruleParse);
	      String[] tags = ruleParse.getTags();
	      if (tags == null)
	          return "";
	      StringBuilder sb = new StringBuilder();
	      for (String tag : tags)
	          sb.append(tag).append(' ');
	      return sb.toString().trim();
	  }


    /**
     * Retrieves the rule parse for the given TEXT.
     *
     * @param the recognition result
     * @return the rule parse for the result
     * @throws GrammarException if there is an error while parsing the
     * result
     */
    RuleParse getRuleParse(String resultText) throws GrammarException {
        jsapiRecognizer = new BaseRecognizer(grammar.getGrammarManager());
		try{
			jsapiRecognizer.allocate();
		}
		catch(EngineException e){e.printStackTrace();}
        RuleGrammar ruleGrammar = new BaseRuleGrammar (jsapiRecognizer, grammar.getRuleGrammar());
        RuleParse ruleParse = ruleGrammar.parse(resultText, null);
        return ruleParse;
    }


    /**
     * Gets the recognizer and the dialog nodes ready to run
     *
     * @throws IOException if an error occurs while allocating the
     * recognizer.
     */
    public void allocate() throws IOException {
        recognizer.allocate();
    }

    /**
     * Releases all resources allocated by the dialog manager
     */
    public void deallocate() {
        recognizer.deallocate();
    }

    /**
     * Invokes the dialog manager. The dialog manager begin to process
     * the dialog states starting at the initial node. This method
     * will not return until the dialog manager is finished processing
     * states
     * @throws JSGFGrammarException 
     * @throws JSGFGrammarParseException 
     */
    public void go() throws JSGFGrammarParseException, JSGFGrammarException {
        try {
		
	    if(use_microphone){
			if (microphone.startRecording()) {
	                while (true) {
						Result result = recognizer.recognize();
						String youSaid = result.getBestFinalResultNoFiller();
						String tagString =  getTagString(result.getBestFinalResultNoFiller());
						//updateEquation(isReturn, isTerm, intTagString,intTagString);
				System.out.println("Utterance: " + youSaid);
				System.out.println("Cursor: " + Cursor);
				System.out.println("Equation: " + Equation + '\n');
				CursorEquation = Equation.substring(0,Cursor+1) + "\\textsf{I }" + Equation.substring(Cursor+1);
				CursorFreeEquation = Equation.substring(0,Cursor+1) + "\\textsf{  }" + Equation.substring(Cursor+1);
				ThreadsRunner.changeEquation(CursorFreeEquation , CursorEquation);
				ThreadsRunner.change_text(Equation,youSaid);

				ThreadsRunner.changeEquation(CursorEquation , CursorFreeEquation);
	                }
           } else {
              error("Can't start the microphone");
           }
		}else{
			for(String utterance : utterances) {
				String tagString =  getTagString(utterance);

				System.out.println("Utterance: " + utterance);
				System.out.println("Cursor: " + Cursor);
				System.out.println("Equation: " + Equation + '\n');
				CursorEquation = Equation.substring(0,Cursor+1) + "\\textsf{I }" + Equation.substring(Cursor+1);
				CursorFreeEquation = Equation.substring(0,Cursor+1) + "\\textsf{  }" + Equation.substring(Cursor+1);
				ThreadsRunner.changeEquation(CursorEquation , CursorFreeEquation);
				ThreadsRunner.change_text(Equation,utterance);
				try{System.in.read();} catch(Exception e){};
			}
		//wait
		while(true){}
		     }
        } catch (GrammarException ge) {
            error("grammar problem -- "+ ge);
        }
    }




    /**
     * Issues a warning message
     *
     * @param s the message
     */
    private void warn(String s) {
        System.out.println("Warning: " + s);
    }

    /**
     * Issues an error message
     *
     * @param s the message
     */
    private void error(String s) {
        System.out.println("Error: " + s);
    }

    /**
     * Issues a tracing message
     *
     * @parma s the message
     */
    private void trace(String s) {
        logger.info(s);
    }


    public Recognizer getRecognizer() {
        return recognizer;
    }

    /**
     * Sets the recognizer
     *
     * @param recognizer the recognizer
     */
    public void setRecognizer(Recognizer recognizer) {
        this.recognizer = recognizer;
    }


}
