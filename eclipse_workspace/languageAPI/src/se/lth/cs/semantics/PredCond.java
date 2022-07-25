package se.lth.cs.semantics;

import se.lth.cs.semparser.corpus.Word;

import java.util.*;

import javax.sql.rowset.Predicate;

public class PredCond implements ProgramStatement {

	public List<ProgramStatement> predargs = new ArrayList<ProgramStatement>();
	public String cc = "";
	private Word w;
	int sentenceNbr;
	
	
	public PredCond(){
		
	}
	
	public void add(Word p){
		w = p;
	}
	

	public boolean hasValue(){
		return w!=null;
		
	}
	
	public void add(ProgramStatement p){
		if(p!= null){
		predargs.add(p);
		}
	}
	
	public PredCond(List<ProgramStatement> a){
		predargs.addAll(a);
	}
	
	public String valueString(){
		//System.out.println("In value as string  ..." + toString());
		if(w == null) return "";
		
		String[] xmls = outputWordXML(w);
		
		if(xmls[2].length() > 0){
		return xmls[2] + "_" + xmls[0];
		}else{
			return xmls[0];}
		
	}
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
	        		//System.err.println("Wording " + w + " child " + c);
	        		switch(c.getPOS()){
	        		case "NN":        			
	        				//if(xmls[0].length() == 0){ //xmls[0] += "_AND_";
	        				xmls[0] += c.getLemma();	
	        				//}
	        			
	        			break;
	        		case "NNS": str += " PLURAL ";

	    				if(xmls[0].length() == 0){// xmls[0] += "_AND_";
	        			xmls[0]+= c.getLemma();}
	        			if(xmls[2] == null  || xmls[2].length() == 0 )xmls[2] = "all";
	        		
	        			break;
	        		case "PRP": str += " PLURAL ";

	    				if(xmls[0].length() == 0){// xmls[0] += "_AND_";
	        			xmls[0]+= c.getLemma();}
	        		//	if(xmls[2] == null  || xmls[2].length() == 0 )xmls[2] = "";
	        		
	        			break;

	        		case "NNP": str += " PLURAL ";

	        	
	    				if(xmls[0].length() == 0){// xmls[0] += "_AND_";
	        			xmls[0] += c.getLemma();
	    				}
	        		
	        		
	        			break;
	        		case "CD": 
	        			int i = PredArgs.mapToInteger(c.getForm()) ;
	    				if(i >= 0){
	    					xmls[2] = "" +PredArgs.mapToInteger(c.getForm());
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
	        		//q.addAll(c.getChildren());
	        			break;
	        	case "IN": 
	    			Word obj3 = SemanticSubmitter.searchAmongChildren(w);
	    			if(obj3!= null) w = obj3;
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

	
	public String toString(){
		String str = "";//cc + " ";
		if(w!= null && w.getLemma()!= null)str += w.getLemma();
		for(ProgramStatement p: predargs){
			if(p!= null )str+= p.toString() + " ";
		}
		return str;
		
	}
	/*
	public ArrayList predicates(){
		ArrayList<Predicate> preds = new ArrayList<Predicate>();
		for(ProgramStatement a: predargs){
			preds.add((Predicate) a.predicate);
		}
		return preds;
	}
	*/
	public String getType(){
		return "COND";
		
	}

	@Override
	public void setSentenceNbr(int i) {
		sentenceNbr = i;
		
	}
	public int getSentenceNbr() {
		// TODO Auto-generated method stub
		return sentenceNbr;
	}
}
