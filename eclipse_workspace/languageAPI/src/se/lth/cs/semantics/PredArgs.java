/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.lth.cs.semantics;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;

import se.lth.cs.semparser.corpus.Predicate;
import se.lth.cs.semparser.corpus.Word;

/**
 *
 * @author pierre
 */
public class PredArgs implements ProgramStatement{

    Predicate predicate;
    Word a1 = null;
    Word a2 = null;
    String a1Out = "";
    
    PredArgs(Predicate predicate, Word a1) {
        this.predicate = predicate;
        this.a1 = a1;
    }

    
    PredArgs(Predicate predicate, Word a1, Word a2) {
        this.predicate = predicate;
        this.a1 = a1;
        this.a2 = a2;
    }

    public String getPredicate() {
        return predicate.getLemma();
    }
    
    public String getPredicateWithSense() {
        return predicate.getPred();
    }
    
    
    public String getNaturalPredicate(){
    	int index = predicate.getPred().indexOf('.');
    	String str = predicate.getPred().substring(0,index);
    	return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    	
    }
    
    
    public String toString(){

    	String tmp = a2 != null? getAllA2(): "";
    	String tmp1 = a1 != null? getAllA1(): "";
    	String out = getPredicate()  +" " +   predicate.getLemma() + "\t" + tmp1 + " "+ tmp;
    	return out;
    	
    }
    
    public  static int mapToInteger(String s){
    	switch(s){
    	case "zero": return 0;
    	
    	case "one": return 1;
    	
    	case "two": return 2;
    	case "three": return 3;
    	case "four": return 4;
    	case "five": return 5;
    	case "six": return 6;
    	case "seven": return 7;
    	case "eight": return 8;
    	case "nine": return 9;
    	case "ten": return 10;
    	case "eleven": return 11;
    	case "twelve": return 12;
    	
    	
    	}
    	return -1;
    	
    }
    
    
    public String[] outPutA1Xml(){
    	return outputWordXML(a1);
    
    }
    
    public String[] outPutA2Xml(){
    	return a2!= null? outputWordXML(a2) : new  String[]{"", "", ""};
    }
/*
    public String[] outputWordXML(Word w){
    	String[] xml = outputWordXML(w);
    	xml[0]  = getAll(predicate, w);
    	return xml;
    }
*/
    public String[] outputWordXML(Word w){

    	//System.out.println("Word2 " + w);
        	String str = "";
        	String[] xmls = {"", "", ""} ;
        	if(w == null) return xmls;
        	//System.out.println(w);
        	/*switch(w.getPOS()){
        		case "NN":
        			xmls[0] = w.getLemma();
        			break;
        		case "NNS": str += " PLURAL ";
        			xmls[0] = w.getLemma();
        			break;

        		case "NNP": str += " PLURAL ";
        			xmls[0] = w.getLemma();
        			break;
        		case "JJ":
        			xmls[0] = w.getLemma();
        			break;
        		case "CD": 
        			//System.out.println("Quant " + w.getForm());
        			int i = mapToInteger(w.getForm()) ;
    			if(i >= 0){	
    				String pl = "";
    				if(i > 1) pl += "PLURAL ";
    				xmls[2] = "" + i;
    				str += pl + i + " ";}
    			else{ 
    				xmls[2] = w.getForm() ;
    				str += w.getForm() + " ";}
    			Word obj = SemanticSubmitter.searchAmongChildren(w);
    			if(obj!= null) w = obj;
        			break;
        		case "DT": 
        			//Word obj2 = searchAmongChildren(w);
        			//if(obj2!= null) w = obj;
        			break;
        		case "PDT": 
        			xmls[2] = w.getForm();
        			break;
        		case "IN": 
        			Word obj3 = SemanticSubmitter.searchAmongChildren(w);
        			if(obj3!= null) w = obj3;
        			break;
        	}
        	*/
        	String w_str = "";
        	ArrayList<Word> q = new ArrayList<Word>();
        	q.add(w);
        	for(Word child: w.getChildren()){
        		if(child.getHead().equals(w))q.add(child);
        	}
        	
        	while(!q.isEmpty()){
        		Word c = q.remove(0);

				//if(!w.equals(c) && !w.getLemma().toLowerCase().equals("and") && ! w.getLemma().toLowerCase().equals("or"))q.addAll(c.getChildren());
        		System.err.println("Args to:" + c);
        		switch(c.getPOS()){
        		case "NN":        			
        				//if(xmls[0].length() == 0){ //xmls[0] += "_AND_";
        				xmls[0] += c.getLemma() + " ";	
        			if(!c.equals(w))	q.addAll(c.getChildren());
        				//}
        			
        			break;
        		case "NNS": str += " PLURAL ";

    				if(xmls[0].length() == 0){// xmls[0] += "_AND_";
        			xmls[0]+= c.getLemma();}
        			if(xmls[2] == null  || xmls[2].length() == 0 )xmls[2] = "all";
        		
        			break;
        		case "PRP": str += " PLURAL ";

    				xmls[0]+= c.getLemma();
        		//	if(xmls[2] == null  || xmls[2].length() == 0 )xmls[2] = "";
        		
        			break;

        		case "NNP": str += " PLURAL ";

        	
    				xmls[0] += c.getLemma();
    				
        		
        		
        			break;
        		case "CD": 
        			int i = mapToInteger(c.getForm()) ;
    				if(i >= 0){
    					xmls[2] = "" +mapToInteger(c.getForm());
    				}
    				
    				else{ xmls[2] += c.getForm();}
        			break;
        		case "DT": 
        			xmls[1] = c.getForm() + " ";
        			xmls[1]=  c.getForm() ;
        			if(xmls[2].equals("")){
        				if(xmls[1].equals("a") || xmls[1].equals("an")) {
        					xmls[2] = ""+1;
        					xmls[1] = "any";
        				}
        			}
        			break;
        		case "PDT": 

        		//	System.out.println("pdt " + c.getForm() );
        			xmls[1]=  c.getForm() ;
        			if(xmls[2].equals("")){
        				if(xmls[1].equals("a") || xmls[1].equals("an")) {
        					xmls[2] = ""+1;
        				}else{
        				xmls[2] = xmls[1];}
        			}
        			//xmls[1] += c.getForm() + " ";
        			break;

        	case "CC": 
        		if(xmls[0].length() > 0) xmls[0] += "_ ";
        		if(!c.equals(w))	q.addAll(c.getChildren());
        		//System.out.println("FOUND CCCCC");
        		
        			break;

        	case "IN": 
        		if(xmls[0].length() > 0) xmls[0] += "_ ";
        		if(!c.equals(w))	q.addAll(c.getChildren());
        		//System.out.println("FOUND CCCCC");
        		
        			break;

        	case "TO": 
        		if(xmls[0].length() > 0)xmls[0] += "_ ";
        		if(!c.equals(w))q.addAll(c.getChildren());
        		//System.out.println("FOUND CCCCC");
        		
        			break;
        	case "JJ": xmls[0]= c.getForm()+"_" + xmls[0];
        	break;
         	}
        	}
        	str += w_str + w.getForm() + " ";
        	//System.out.println(w);
        	//str += w.getLemma();
        	//xmls[0] = w.getLemma();
        	return xmls;
        	
        }


    public String getType(){
		return "NORMAL";
		
	}
    

    public String getA1() {
        if (a1 == null) {
            return null;
        }
        return a1.getForm();
    }
  

    public String getA2() {
        if (a2 == null) {
            return null;
        }
        // Quick hack to get the noun.
        if (a2.getPOS().equals("IN")) {
            List<Word> listAux = new ArrayList<Word>(a2.getChildren());
            return listAux.get(0).getForm();
        }
        return a2.getForm();
    }
    
    
    
    
    private boolean ignoreWords(Word c){
    	switch(c.getForm().toLowerCase()){
    	case "the": return true;
    	case "to": return true;
    	case "from": return true;
    	case "a": return true;
    	case "an": return true;
    	}
    	return false;
    }
    
    public String getAllA2(){
    	String str = "";
    	for(Word c: a2.getChildren()){
    		if(!ignoreWords(c)){
    		str += c.getForm() + "_";}
    	}
    	if(!a2.getForm().equals("in")){
    	str += a2.getForm();
    	}
    	return str;
    	
    }
    
    public String getAllA1(){
    	String str = "";
    	for(Word c: a1.getChildren()){
    		if(!ignoreWords(c)){
    		str += c.getForm() + "_";}
    	}
    	str += a1.getForm();
    	return str;
    	
    }
    int sentenceNbr;
    

	@Override
    public void setSentenceNbr(int i) {
		sentenceNbr = i;
		//System.out.println("PPPPPPPPPPPPPPP + "  + i );
		
	}
    public int getSentenceNbr() {
		// TODO Auto-generated method stub
		return sentenceNbr;
	}
}
