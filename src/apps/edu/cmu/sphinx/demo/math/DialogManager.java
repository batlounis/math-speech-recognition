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

//NOTE: POWER AND DIVISION!!!!!!!!


public class DialogManager implements Configurable {
	
	// if this is false, utterances below will be used
	boolean use_microphone=false;
	
	// utterances we might want to use instead of using the microphone
	static final String[] utterances = {
	"plus f of f of x",
	"over", "a plus b to the power c","skip","to the power", "d","skip",
	"plus root" ,"x","skip", "over y","over y","plus sum", "from", "a plus b", "to","c plus d","of", "over","x",
	"minus","open", "to a plus b","three over b",
	"plus three minus", "open","over","a times b","close everything","open","four",
	"divided by",
	"open","x plus y","close everything","skip","close","undo","undo","undo",
	};
	
	/* Variable declaration */
	public static ArrayList<String> utteranceList = new ArrayList<String>();	
	public static String Equation = "";
	public static String CursorEquation = "";	
	public static String CursorFreeEquation = "";
	public boolean addUtterance = false;
	public void setAddUtterance(boolean b){addUtterance = b;}	
	public static int Cursor = -1;
	public static String Comment; //to display tips and warning messages to user;
	//public boolean IsBounds
	/* Function Definitions */
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
	public void addBrackets(){
		if(Cursor == Equation.length()-1){
				Equation = Equation + "()";
			}else{
				Equation = Equation.substring(0,Cursor + 1) + "()" + Equation.substring(Cursor + 1);
			}	
		Cursor = Cursor + 1;	
	}
	public void closeEverything(){//closes all remaining brackets
		//count remaining unclosed open brackets
		int open = 0;
		int close = 0;
		//keep track of bracelets too: if we're inside a function or brackets don't close stuff outside it
		int f = Equation.indexOf("}",Cursor+1);
		int f2 = Equation.indexOf(")",Cursor+1);
				
		if(f>0 & f<f2 & f2>0| f>0 & f2<0){//this means we are inside a function so find the matching closing bracelet
			f = findMatchingB(f, '{','}');
		}else if (f2>0 & f2<f & f>0| f2>0 & f<0){
			f = findMatchingB(f, '(',')');
		}else{f=0;}
		for(int i = Cursor; i>f;i--){
			if(Equation.charAt(i) == '('){ open++;
			}else if(Equation.charAt(i) == ')'){close++;}
			
		}
		//Add brackets or display warning message
		int loop = open - close; //loop is the number of closing brackets that need to be added;
		if(loop>0){String Brackets = "";
			for(int i = loop; i>0;i--){
				Brackets = Brackets + ")";	
			}
			if(Cursor == Equation.length()-1){
				Equation = Equation + Brackets;
			}else{
				Equation = Equation.substring(0,Cursor + 1) + Brackets + Equation.substring(Cursor + 1);
			}
			Cursor = Cursor + loop; 
		}else if (loop == 0){ Comment = "There's nothing to be closed";
		}else{ Comment = "You have too many closing brackets!!";}
	}
	//finds the length of the term which will be the numerator in the upcomming fraction (if it's not a bracket or function)
	public int findLastTermLength(int i){
		char c = Equation.charAt(i);
		int termLength = 0;		
		while(c != '+' & c != '-'& c != '*'& c != '{'& c != '^'& c != '(' & c!= ')'& c!= '{'& c!= '}'){			
			termLength++;
			i--;
			c = Equation.charAt(i);			
		}
		return termLength;	
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
		return i+1;
	}
	//This function finds the matching bracket and if there's a corresponding function
	public int findMatchingBandF(int i, char o,char c){
		int k = findMatchingB(i,o,c);
		return (k - findLastTermLength(k-1));
	}
	//Checks if it's a fraction since frac has two parameters
	public int findTerm(int CursorT){
		boolean frac = false;
		int i = findMatchingB(CursorT,'{','}');
		int b = checkIfBounds();		
		//Check if frac to continue search
		if(Equation.substring(i-1,i).equals(" ") & Equation.substring(i-2,i-1).equals("}")){
			frac = true; CursorT = i - 2;
		}else if(Equation.substring(i-1,i).equals("}")){frac = true; CursorT = i - 1;
		}else if(b>-1){//not fraction - Bounds
			return b;		
		}else{//normal funtions
			String[] S = {"\\sqrt"};			
			int i2 = checkIfFunctionInt(i,1,S);		
			if(i2>-1){i = i2; return i;}
		}
		if(frac){
			i = findMatchingB(CursorT,'{','}');
			//check if preceded by the frac function			
			String[] S = {"\\frac"};			
			int i2 = checkIfFunctionInt(i,1,S);		
			if(i2>-1){i = i2;}
		}
		
		return i;

	}

	public void skip(){//gets out of current curly braces or bracket
		int i = Equation.indexOf("{",Cursor + 1);
		if(i<0){Cursor = Equation.length() - 1;}
		else{Cursor = i;}
			
	}
	public String checkIfFunction(int i,int size, String[] functions){
		for(int j = 0; j<size; j++){
			System.out.println("i: " + i);

			int check = Equation.lastIndexOf(functions[j],i);
			System.out.println("check: " + check);
			if((check == i - functions[j].length()) || (check == i - functions[j].length()-1)){
				return functions[j];			
			}		
		}
		return "NF";	
	}
	public int checkIfFunctionInt(int i,int size, String[] functions){
		for(int j = 0; j<size; j++){
			int check = Equation.lastIndexOf(functions[j],i);
			if((check == i - functions[j].length()) || (check == i - functions[j].length()-1)){
				return check;			
			}		
		}
		return -1;	
	}
	public int checkIfBounds(){//used for integral and summation
		String[] S = {"\\int","\\displaystyle\\sum\\limits"};		
		int i2 = Equation.indexOf("}",Cursor);
		if(i2>-1){i2 = findMatchingB(i2,'{','}');}
		else {return -1;}
		if(Equation.charAt(i2 - 1) == '^' || Equation.charAt(i2 - 2) == '^'){
			i2 = Equation.lastIndexOf('^',i2);			
			if((Equation.charAt(i2-1) == '}')||Equation.charAt(i2-2) == '}'&& Equation.charAt(i2-1) == ' '){
				i2 = Equation.lastIndexOf('}',i2);				
				i2 = findMatchingB(i2,'{','}');
				if(Equation.charAt(i2-1) == '_' || Equation.charAt(i2-2) == '_' && Equation.charAt(i2-1) == ' '){
					i2 = Equation.lastIndexOf('_',i2);
										
					i2 = checkIfFunctionInt(i2,2,S);					
					if(i2 != -1){					
						return i2;}
				}
			}else{
				i2 = checkIfFunctionInt(i2,2,S);
				if(i2 != -1){
					return i2;
				}else {return -2;}//it's a power, see what to do about that later
			}
	
		}else if(Equation.charAt(i2 - 1) == '_' || Equation.charAt(i2 - 2) == '_' && Equation.charAt(i2-1) == ' '){
			i2 = Equation.lastIndexOf('_',i2);
			i2 = checkIfFunctionInt(i2,2,S);					
			if(i2 != -1){					
				return i2;}
		}
		return -1;
	}
	public void ofSkip(){//gets the user out of bounds curly braces-- the difference from skip is that it only works for bounds
		if(checkIfBounds()!=-1){
			skip();		
		}
	}
	public void from(String S){
		//Check if we're specifying bounds: for now it's just for integration
		String[] S2 = {"\\int","\\displaystyle\\sum\\limits"};		
		int i = checkIfFunctionInt(Cursor,2,S2);//check if it's following something that requires bounds
		
		if(i >-1){
			if(Cursor == Equation.length()-1){//if cursor at end of string
				Equation = Equation + "_{" + S + "}";
				Cursor = Cursor + 2;
			}else{//if not at end of string
				Equation = Equation.substring(0,Cursor + 1) + "_{" + S + "}" + Equation.substring(Cursor + 1);
				Cursor = Cursor + 2;
			}
			if(S.length()>0){Cursor = Cursor + S.length() + 1;}
			
		}
		
	}
	public void to(String S){

		String[] S2 = {"\\int","\\displaystyle\\sum\\limits"};
		int i = checkIfFunctionInt(Cursor,2,S2);//check if it's following something that requires bounds
		if(i >-1){
			if(Cursor == Equation.length()-1){//if cursor at end of string
				Equation = Equation + "^{" + S + "}";
				Cursor = Cursor + 2;
			}else{//if not at end of string
				Equation = Equation.substring(0,Cursor + 1) + "^{" + S + "}" + Equation.substring(Cursor + 1);
				Cursor = Cursor + 2;
			}
			if(S.length()>0){Cursor = Cursor + S.length() + 1;}
			
		}else{
			int i2 = Equation.indexOf("}",Cursor);
			if(i2>-1){i2 = findMatchingB(i2,'{','}');}
			else {return;}
			if(Equation.charAt(i2 - 1) == '_' || Equation.charAt(i2 - 2) == '_' && Equation.charAt(i2-1) == ' '){
				i2 = Equation.lastIndexOf('_',i2);
				i2 = checkIfFunctionInt(i2,2,S2);					
				if(i2 != -1){
					skip();					
					if(Cursor == Equation.length()-1){//if cursor at end of string
						Equation = Equation + "^{" + S + "}";
						Cursor = Cursor + 2;
					}else{//if not at end of string
						Equation = Equation.substring(0,Cursor + 1) + "^{" + S + "}" + Equation.substring(Cursor + 1);
						Cursor = Cursor + 2;
					}
					if(S.length()>0){Cursor = Cursor + S.length() + 1;}
				}
			}
		}
	}	
	public void boundedIntegral(String S){//also used for sum
		S = setBackslash(S);
		if(Cursor + 1 < Equation.length()){//if cursor not at end of string
			Equation = Equation.substring(0,Cursor+1) + S + Equation.substring(Cursor+1);
		}else{//if at end of string
			Equation = Equation + S;	
		}
		Cursor = Cursor + S.length() - 4;
	}
	public void addRoot(String S){//also used for functions as in f(x)
		S = setBackslash(S);
		if(Cursor + 1 < Equation.length()){//if cursor not at end of string
			Equation = Equation.substring(0,Cursor+1) + S + Equation.substring(Cursor+1);
		}else{//if at end of string
			Equation = Equation + S;	
		}
		Cursor = Cursor + S.length() - 1;

	}
	//handles bracketing issues with power operation
	public void handlePower(String p){
		//find matching bracket/bracelet
		int openingBracelet = -1;
		int CursorT = Cursor;
		if(p.length() == 0){p = "{}";CursorT--;}//so that cursor remains inside curly braces after the term has been said		
		if(Equation.charAt(Cursor)=='}'){;
			openingBracelet = findTerm(Cursor);
		}
		if(openingBracelet>-1){CursorT = CursorT + 3 + p.length();
			if(Cursor < Equation.length()-1){//if not at end of string
				Equation = Equation.substring(0,openingBracelet) + '(' + Equation.substring(openingBracelet,Cursor+1)
					+ ")^" + p + Equation.substring(Cursor+1);
			}else{//if at end of string
				Equation = Equation.substring(0,openingBracelet) + '(' + Equation.substring(openingBracelet)
					+ ")^" + p;}

		}else{CursorT = CursorT + 1 + p.length();
			if(Cursor < Equation.length()-1){//if not at end of string
				Equation = Equation.substring(0,Cursor + 1) + '^' + p + Equation.substring(Cursor+1);
			}else{//if at end of string
				Equation = Equation + '^' + p;}
		}
		Cursor = CursorT;
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
			System.out.println("invalid");Comment = "you can't divide over nothing"; return;
		}
		//Division Cases: Not at end of string
		if(Equation.length()>Cursor+1){//If cursor not at end of string
			if(Equation.substring(Cursor,Cursor + 1).equals(")")){//If closing bracket
				int p = findMatchingBandF(Cursor,'(',')');				
				Equation = Equation.substring(0,p) + "\\frac{" + Equation.substring(p,Cursor+1) + "}{" + den + "}" +
				Equation.substring(Cursor+1);
			}
			else if(Equation.substring(Cursor,Cursor + 1).equals("}")){//If closing bracket
				int p = findTerm(Cursor); //p = findFt(p);				
				Equation = Equation.substring(0,p) + "\\frac{" + Equation.substring(p,Cursor+1) + "}{" + den + "}" +
				Equation.substring(Cursor+1);
			}
			else{
				int lastCharLength = findLastTermLength(Cursor);
				Equation = Equation.substring(0,Cursor + 1 - lastCharLength) + "\\frac{" + Equation.substring(Cursor + 1 -
				lastCharLength,Cursor+1) + "}{" + den + "}" + Equation.substring(Cursor+1);//just take the last term
			}
		}
		else{//if cursor at end of string
			if(Equation.substring(Cursor).equals(")")){//If closing bracket
				System.out.println("I'm should be HERE");				
				int p = findMatchingBandF(Cursor,'(',')'); 
				Equation = Equation.substring(0,p) + "\\frac{" + Equation.substring(p) + "}{" + den + "}";
			}
			else if(Equation.substring(Cursor).equals("}")){//If closing bracket
				int p = findTerm(Cursor); //p = findFt(p);
				Equation = Equation.substring(0,p) + "\\frac{" + Equation.substring(p) + "}{" + den + "}";
			}
			else{
				int lastCharLength = findLastTermLength(Cursor);
				Equation = Equation.substring(0,Cursor + 1 - lastCharLength) + "\\frac{" + Equation.substring(Cursor + 1 - 
				lastCharLength) + "}{" + den + "}";//take the last term
			}
		}
		Cursor = CursorT;
			
	}
	//function to replace % with \\ cuz stupid gram won't write backslash!!
	public String setBackslash(String S){
		String S2 = S;		
		for (int i = 0; i < S.length(); i++) { // Test and Loop
			if (S.charAt(i) == '%'){
				if (i == 0){
					S = "\\" + S.substring(i+1);
				}else if(i<S.length()-1){
					S = S.substring(0,i) + "\\" + S.substring(i+1);
				}else{
					S = S.substring(0,i) + "\\";}
			}
		}
		return S;
	
	}
	//function to update equation with simple terms at cursor position
	public void addAtCursor(String S){
		S = setBackslash(S);
		if(Equation.length()>Cursor+1){//If cursor is not located at the end of the string
			Equation = Equation.substring(0,Cursor+1) + S + Equation.substring(Cursor+1);
		}else{
			Equation = Equation + S; //If at end then just append
		}
		Cursor = Cursor + S.length();//move cursor
	}
	public void recompileUtterances(int size){
		for(int i = 0; i<size;i++){
			//System.out.println("List Content: " + utteranceList);
			try

			{String tagString =  getTagString(utteranceList.get(i));}catch(Exception e){}
			/*System.out.println("List Content: " + utteranceList);
			System.out.println("Iteration: " + i);			
			System.out.println("Utterance: " + utteranceList.get(i));
			System.out.println("Cursor: " + Cursor);
			System.out.println("Equation: " + Equation + '\n');*/
			

		}
		CursorEquation = Equation.substring(0,Cursor+1) + "\\textsf{I }" + Equation.substring(Cursor+1);
		CursorFreeEquation = Equation.substring(0,Cursor+1) + "\\textsf{  }" + Equation.substring(Cursor+1);
		ThreadsRunner.changeEquation(CursorEquation , CursorFreeEquation);
		ThreadsRunner.change_text(Equation,utteranceList.get(size-1));
		System.out.println("List Content: " + utteranceList);
		addUtterance = false;		
	}
	public void appendUtteranceList(String S){
		if(addUtterance){
			utteranceList.add(S);}
	}
	public void undo(int i){
		//re-initialize cursor
		Equation = "";
		Cursor = -1;
		int size = utteranceList.size() - 1;
		//remove last i statements
		for(int j = 0; j<i; j++){
			utteranceList.remove(size - j);		
		}
		System.out.println("List Size: " + utteranceList.size());
		System.out.println("List Content: " + utteranceList);	
		recompileUtterances(utteranceList.size());
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
    // ------------------------------------    [javac] 			System.out.println("lastCharLength: " + lastCharLength);

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
				appendUtteranceList(youSaid);				
				System.out.println("Utterance: " + youSaid);
				System.out.println("Utterance List Size: " + utteranceList.size());
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
				appendUtteranceList(utterance);				
				System.out.println("Utterance: " + utterance);
				System.out.println("Utterance List Size: " + utteranceList.size());
				System.out.println("Utterance boolean: " + addUtterance);				
				System.out.println("Utterance List: " + utteranceList);
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
