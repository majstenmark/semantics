/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.lth.cs.main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


import se.lth.cs.semantics.PredArgs;
import se.lth.cs.semantics.ProgramStatement;
import se.lth.cs.semantics.SemanticSubmitter;
import se.lth.cs.semparser.corpus.Predicate;
import se.lth.cs.semparser.corpus.Sentence;
import se.lth.cs.semparser.corpus.Word;

public class Main{

    private static final long serialVersionUID = 1L;
    private static String semanticServer = "localhost:8072"; // Do	a	search	along	the	z-axis	until	you	measure	a	force	of	5	Newton. 
    

    public static List<String> sentenceDetector(String narrative) {
        String[] sentences = narrative.split("\\.");
        for(int i = 0; i < sentences.length; i ++){
        	sentences[i] = sentences[i] + ".";
        	
        }
        return Arrays.asList(sentences);
    }

    
    public static void main(String[] args) throws Exception{
    	//String narrative = "Insert the red button into the yellow box. Pick up the yellow box. Screw the nut to the button. Fly to the Jupiter.";
    	String narrative = "Take any two boxes from the input tray.";
        
    	String output = process(narrative);
      System.out.println(output);
      
  	//"Take any two boxes from the input tray."
  	// "Take all boxes from the input tray."
  	// "Take three boxes from the input tray."
  	//	"Take a PCB from the input tray."
  	//	"Hold 5 N in y-direction."
  	//	"Move to the PCB"//,
  	//	"Use myskill to assemble the shiledcan to the PCB."
  	//	"Assemble the shieldcan to the PCB using myskill."
  	//	"Repeat the program 10 times."
  	//	"Retract from the fixture"
  	//	"Retract from the PCB"
  	//	"Move in the z-dir until contact."
  	//	"Search in the z-direction until contact while holding 5 N in the y-direction."
  	//	"Assemble the shieldcan to the PCB using ShieldCanInsertion."
  	//	"While holding 4 N in z-direction and 4 N in the y-direction, search in the x-direction until contact."
  	//	"Search in the z-direction until you measure 5 N."
  	//"Pick three boxes and put them on the table."
    }
    


    private boolean assemblyOperation(String pred){
    	if(pred.equals("Assemble")) {return true;}
    	return false;
    }
    
    private static String process(String narrative) throws UnsupportedEncodingException{
   
       // 
        narrative = URLDecoder.decode(narrative, "UTF-8");
        narrative = narrative.replaceAll("!", ".");
        
        List<String> sentences = sentenceDetector(narrative);
        System.out.println(sentences);
        String output = SemanticSubmitter.processEverything(sentences);
        return output;
        /*
        List<Sentence> parsedSentences = new ArrayList<Sentence>();
        SemanticSubmitter semSubmitter = new SemanticSubmitter();

        for (String sentence : sentences) {
            String parsedOutput = semSubmitter.processSentence(sentence, semanticServer);
            parsedSentences.add(new Sentence(parsedOutput));
        }
        List<ProgramStatement> teachMe = new ArrayList<ProgramStatement>();
        List<ProgramStatement> actions = new ArrayList<ProgramStatement>();
        int sentenceNbr  = 0;
        for (Sentence parsedSentence : parsedSentences) {
        	sentenceNbr++;
            actions.addAll(semSubmitter.mapPredicates(parsedSentence, teachMe));
            //System.out.println(parsedSentence.toString());
            
            for (ProgramStatement action : actions) {

            	String out = action.toString();
            	//System.out.println("Test " + out);
            	action.setSentenceNbr(sentenceNbr);
            	
            //	 System.out.println("Output " + out);
            }
          ////      System.out.println("TEACH ME " + action.toString());
            //}
        }
        String testPrint = PrintToXml.printToXml(actions, teachMe);
        System.out.println("Testprintedresuult " + testPrint);
        return testPrint;
        
        */
        
    	
    }
    
    private static class Mapping{

    	String predicate, action;
    	public Mapping(String p, String a){
    		predicate = p;
    		action = a;
    	}
    } 
    
    private static List<Mapping> readVerbs(String text){
    	String[] pairs = text.split(";");
    	List<Mapping> mapping = new ArrayList<Mapping>();
    	for(String p : pairs){
    		String[] pa = p.split(" ");
    		Mapping m = new Mapping(pa[0], pa[1]);
    		mapping.add(m);
    	}
    	return mapping;
    }
    
  
    
    public static void setup(String cmd, String text) {
 
        PrintWriter fo;
		try {
			fo = new PrintWriter("understood_predicates.txt");
		
        // The semantic server processes only one parameter: text
       // System.out.println("sfkhfksl");
       // out.println("fdsfksdfhksdfhkjshfjkshdfkjshd");
        
        
        if(cmd.equals("verbs")) {
        	String verbs = text;
        	System.out.println("Got input text " + verbs);

            SemanticSubmitter.mapping = new HashMap<String, String>();
            SemanticSubmitter.handledPredicates = new HashSet<String>();
        	List<Mapping> mapping = readVerbs(URLDecoder.decode(verbs, "UTF-8"));
        	for(Mapping m: mapping){
            fo.println(m.predicate + " " + m.action);
            SemanticSubmitter.addPredicate(m.predicate, m.action);
            }
        	

            SemanticSubmitter.saveToFile();
        } else {
        }
        


        
        fo.close();
        }
    catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
    
}