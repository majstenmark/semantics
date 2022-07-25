/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package se.lth.cs.semantics;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

import se.lth.cs.main.PrintToXml;
import se.lth.cs.semparser.corpus.Predicate;
import se.lth.cs.semparser.corpus.Sentence;
import se.lth.cs.semparser.corpus.Word;

/**
 *
 * @author pierre
 */
public class SemanticSubmitter {

    /**
     * @param args the command line arguments
     */
    private static String semanticServer = "localhost:8072";
 
    
    public SemanticSubmitter() {
    	readFiles();
    }

    public SemanticSubmitter(String semanticServer) {
    	readFiles();
        this.semanticServer = semanticServer;
    }

    
    public static String processEverything( List<String> narrative){
    	handledPredicates = new HashSet<String>();
        List<Sentence> parsedSentences = new ArrayList<Sentence>();
        SemanticSubmitter semSubmitter = new SemanticSubmitter();
        
        /*
         * The sentences are parsed one by one. Note that the entity mapping of it to the previous object is done in the robotstudio client.
         */
        
        for (String sentence : narrative) {
            String parsedOutput = semSubmitter.processSentence(sentence);
            Sentence s = new Sentence(parsedOutput);
            
            parsedSentences.add(s);
        }
        
        
        /*
         * This parts maps the parsed output to robot action, the actions in the file understood predicates (the skills that the robot has in the system). 
         * If the skill is not in the system it will be stored in the teachMe list.
         */
        List<ProgramStatement> teachMe = new ArrayList<ProgramStatement>();
        List<ProgramStatement> actions = new ArrayList<ProgramStatement>();
        int sentenceNbr  = 0;
        for (Sentence parsedSentence : parsedSentences) {
        	sentenceNbr++;
            List<ProgramStatement> actionsInSentence = semSubmitter.mapPredicates(parsedSentence, teachMe);
            
            for (ProgramStatement action : actionsInSentence) {
            		action.setSentenceNbr(sentenceNbr);
            	
            }
            
            actions.addAll(actionsInSentence);
            /*for (ProgramStatement action : teachMe) {
                System.out.println("TEACH ME " + action.toString());
            }*/
        }
        
        String seq = PrintToXml.printToXml(actions);
        String learn = PrintToXml.printToXml(teachMe);
      //  System.out.println(testPrint);
        return "Seguence: \n" + seq + "\nTo learn:" + learn;
    	
    }

   

    public String processSentence(String description) {
        String serverURL = "http://" + semanticServer + (semanticServer.endsWith("/") ? "" : "/") + "parse";
        
        String parseOutput;
        try {
            parseOutput = makeHTTPRequest(serverURL, description);
        } catch (IOException e) {
            System.err.println("Failed to connect to SemanticHTTPServer");
            e.printStackTrace();
            return null;
        }
        return parseOutput;

    }

    private ProgramStatement handlePredicate(Predicate predicate, HashSet<Predicate> handledPreds, List<ProgramStatement> teachMe){
    	//System.out.println("Is handling " + predicate);
    	if(predicate.isNeg() || handledPreds.contains(predicate) || ignoredPredicates.contains(predicate.getPred())){
        	return null;
        		
        	}
            if (!handledPredicates.contains(predicate.getPred()) && !handledPreds.contains(predicate)) {
            	ProgramStatement stm =  createArgs(predicate, handledPreds);
            	teachMe.add(stm);
            	handledPreds.add(predicate);
            	
            	  return null;
            } else if(!handledPreds.contains(predicate)){
            	ProgramStatement stm = createArgs(predicate, handledPreds);
            	handledPreds.add(predicate);
            	return stm;
            
                  }

    	
    	return null;
    	
    }
    
    public static void savePredicatesToFile(){
    	File file = new File("predicates.txt");
    	ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));

			out.writeObject(handledPredicates);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }
    
    public static void openPredicates(){
    	File file = new File("predicates.txt");
		try {

			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			handledPredicates = (HashSet<String>) in.readObject();
			in.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public static void saveToFile(){
    	savePredicatesToFile();
    	saveMappingsToFile();
    }
    
    public static void readFiles(){
    	openPredicates();
    	openMappings();
    }
    
    public static void saveMappingsToFile(){
    	File file = new File("mapping.txt");
    	ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));

			out.writeObject(mapping);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }
    
    public static void openMappings(){
    	File file = new File("mapping.txt");
		try {

			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			mapping = (HashMap<String, String>) in.readObject();
			in.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    public List<ProgramStatement> mapPredicates(Sentence sentence, List<ProgramStatement> teachMe) {
//        for (Predicate predicate : sentence.getPredicates()) {
//            if (predicate.getPOS().startsWith("VB")) { 
//                System.out.println(predicate.toString());
//            }
//        }
        List<ProgramStatement> actions = new ArrayList<ProgramStatement>();
        HashSet<Predicate> handledPreds = new HashSet<Predicate>();
        
        Predicate root = null;
        for(Predicate p: sentence.getPredicates()){
        	if(p.getDeprel().equals("ROOT")){
        		root = p;
        		break;
        	}
        }
        if(root!= null) {
        	ProgramStatement statement = handlePredicate(root, handledPreds, teachMe);
        	ProgramStatement wildCard = getWildCard(root);

			//System.out.println("Wildcard received " + wildCard);
        	if(statement != null){actions.add(statement);
        	if(wildCard!= null)
			{
			actions.add(wildCard);
			}
        	}
        }
        
        for (Predicate predicate : sentence.getPredicates()) {
        	ProgramStatement stm = handlePredicate(predicate, handledPreds, teachMe);
        	ProgramStatement wildCard = getWildCard(predicate);

			if(stm != null){
        		actions.add(stm);
        		if(wildCard!= null)
        			{
        			actions.add(wildCard);
        			}
        	}
        }
        return actions;
        
       
    }
    
    private ProgramStatement getWildCard(Predicate p){
    	//String[] use = {"using", 	"_", 	"use"	,"_",	"VBG",	"_", "_", 	"_", 	"1",	"_",	"ADV",	"Y",	"use.01"};
    	Word use = new Word("using", "use", "VBG", null);
    	//System.out.println("Testing wildcardsa");
    	for(Word w: p.getArgMap().keySet()){
  //  		System.out.println("Testing wildcards a " + w);
    		if(p.getArgMap().get(w).equals("AM-MNR")){

//        		System.out.println("Manners a " + w);
    		List<Word> children = new LinkedList<Word>();
    		children.addAll(w.getChildren());
    		
    		while(!children.isEmpty()){
    				Word u = children.remove(0);
    				//System.out.println("Manners a " + u);
    				if(u.getPOS().equals("NN") || u.getPOS().equals("NNS") || u.getPOS().equals("NNP") ){
    					Predicate pred = new Predicate(use, true);
    					ProgramStatement pp = new PredArgs(pred, u);
    					//System.out.println("Manners AAA " + pp);
    					return pp;
    				}
    				children.addAll(u.getChildren());
    		}
    	}
    	}
    	
    	return null;
    	
    }
  
    private static Word findLocs(Predicate p){
    	
    	for(Word w: p.getArgMap().keySet()){
    		
    		
    		// if (p.getArgMap().get(w).equals("AM-TMP")) {
    		//	 return w;
            //  }
    		 if (p.getArgMap().get(w).equals("TMP")) {
    			 
                 return w;
               }
    		 if (p.getArgMap().get(w).equals("IN")) {
    			   
                 return w;
               }
    		 if (p.getArgMap().get(w).equals("AM-LOC")) {
    			  
                 return w;
               }
    		 if (p.getArgMap().get(w).equals("A4")) {
   			  
                 return w;
               }
    		 if (p.getArgMap().get(w).equals("AM-DIR")) {
   			  
                 return w;
               }
    		 
    	}
    	
    	return null;
    	
    }

    
    private static boolean isParalell(Predicate p){
    	for(Word w: p.getChildren()){
		//	System.out.println("Searching words " + w);
			if(w.getPOS().equals("IN") && (w.getDeprel().equals("TMP") || w.getDeprel().equals("ADV"))){
				if(w.getForm().toLowerCase().equals("while")){
			
		        	 
					return true;}
				
			}
			}
    	return false;
    }
    
    private static boolean hasCondition(Predicate p){
    	for(Word w: p.getChildren()){
		//	System.out.println("Searching words " + w);
			if(w.getPOS().equals("IN") && w.getDeprel().equals("TMP")){
				if(w.getForm().toLowerCase().equals("until")){
					 
					return true;}
				
			}
			}
    	return false;
    }
    
    private static boolean hasIf(Predicate p){
    	List<Word> children = new LinkedList<Word>();
		children.addAll(p.getChildren());
		
		while(!children.isEmpty()){
			Word w = children.remove(0);
    	
			if(w.getPOS().equals("IN") && w.getDeprel().equals("ADV")){
				if(w.getForm().toLowerCase().equals("if")){
					
					return true;
				}
			}
			if(w.getPOS().equals("WRB") && w.getDeprel().equals("TMP")){
				if(w.getForm().toLowerCase().equals("when")){
					
					return true;
				}
			}
			children.addAll(w.getChildren());
			}
    	return false;
    }
    
    private static ProgramStatement createArgs(Predicate predicate, HashSet<Predicate> ignore){
    	
    	 String targetArgLabel = "A1";
    	 Word argValA1 = null, argValA2  = null;
         
         if (!predicate.getArgMap().values().contains(targetArgLabel)) {
        	 Word locs = findLocs(predicate);
        	 argValA1 = locs;
        	 
        	if(locs == null){
        		for(Word w : predicate.getChildren()){
        			if(w.getDeprel().equals("LOC")){
        				argValA1 = w;
        				
        			}
        		}
        		
        	}
        	 
         }else{
         
        
        int count= 0; 
         for (Word w : predicate.getArgMap().keySet()) {
         	
             if (predicate.getArgMap().get(w).equals("A1")) {
            	 if(count == 0){
            		 
              	argValA1 = w;
            	 }else{
            		 argValA2 = w;
            		 
            	 }
                count ++; 
             	
             }
         }
         }
         // We extract A2
         if (!predicate.getArgMap().values().contains("A2")) {
        	 

        	 Word locs = findLocs(predicate);
        	 if(argValA2 == null){
        		 argValA2 = locs;
        	 }
        	 
        	 if(argValA2 != null){
        		 ProgramStatement predargs = new PredArgs(predicate, argValA1, argValA2);

        		 if(hasIf(predicate)){
        			 ProgramStatement ifCond = checkIfConditions(predicate, ignore);
        			 if(ifCond != null)predargs = new IfPredArgs(ifCond, predargs); 
        		 }

        		 if(hasCondition(predicate)){
        			 
        			 ProgramStatement cond = checkConditions(predicate, ignore, "until");

            		
            		 if(cond != null)predargs = new UntilPredArgs(predargs, cond); 
        			 
        		 }
        		 if(isParalell(predicate)){
        			 ProgramStatement cond = checkConditions(predicate, ignore, "while");
        		
        			 if(cond != null)predargs = new WhilePredArgs(predargs, cond); 
        			 
        		 }
                
                 return predargs;
        	 }

        	 
        	 ProgramStatement predargs = new PredArgs(predicate, argValA1);
             
    		 if(hasIf(predicate)){

    			
    			 ProgramStatement ifCond = checkIfConditions(predicate, ignore);
    			 if(ifCond != null)predargs = new IfPredArgs(ifCond, predargs); 
    		 } if(hasCondition(predicate)){

    		
    			 ProgramStatement cond = checkConditions(predicate, ignore, "until");

       
        		 if(cond != null)predargs = new UntilPredArgs(predargs, cond); 
    			 
    		 } if(isParalell(predicate)){
    			 ProgramStatement cond = checkConditions(predicate, ignore, "while");
    			 if(cond != null)predargs = new WhilePredArgs(predargs, cond); 
    			 
    		 }
            
             return predargs;
        	 
         } // has A2
         for (Word w : predicate.getArgMap().keySet()) {
             if (predicate.getArgMap().get(w).equals("A2")) {
                 argValA2 = w;
             }
         }
         ProgramStatement predargs;
         if(argValA1 == null && argValA2 != null){
        	 argValA1 = argValA2;
        	 argValA2 = null;
        	 predargs = new PredArgs(predicate, argValA1);
             
         }else{
         predargs = new PredArgs(predicate, argValA1, argValA2);
         }
		 if(hasIf(predicate)){

			 ProgramStatement ifCond = checkIfConditions(predicate, ignore);
			 predargs = new IfPredArgs(ifCond, predargs); 
		 } 
	
		 if(hasCondition(predicate)){

			 
			 ProgramStatement cond = checkConditions(predicate, ignore, "until");
			
			if(cond!=null)predargs = new UntilPredArgs(predargs, cond); 
			 
			 
		 }
		 if(isParalell(predicate)){
			 ProgramStatement cond = checkConditions(predicate, ignore, "while");

    		
			if(cond != null)predargs = new WhilePredArgs(predargs, cond); 
			 
		 }
        
         return predargs;
  
    	
    }
    
    
    
    private static PredCond checkCC(Word u,HashSet<Predicate> ignore){
    	//PredCond s = null;
    	for(Word w: u.getChildren()){
    		if(w.getPOS().equals("CC")){
    			ProgramStatement scc =  searchConditions(w, ignore);
    			if(scc!= null) {

    				PredCond cond = new PredCond();
    				cond.add(scc);
    				cond.cc = w.getForm().toUpperCase();
    				return cond;
    			}
    		}
    		
    	}
    	for(Word z: u.getChildren()){

        	for(Word w: z.getChildren()){
    		if(w.getPOS().equals("CC")){
    			ProgramStatement scc =  searchConditions(w, ignore);
    			if(scc!= null) {

        			PredCond cond = new PredCond();
    				cond.add(scc);
    				//System.out.println("added " + w.getForm() + " " + scc);
    				cond.cc = w.getForm().toUpperCase();
    				return cond;
    			}
    		}
    		
    	}
    	}
    	
    	
    	return null;
    }
    
    private static ProgramStatement searchConditions(Word u,HashSet<Predicate> ignore ){
		for(Word w: u.getChildren()){

	    	//System.out.println("Checking " + w + " is pred " + w.isPred());
	    	
	    	try{
	    	Predicate pred = (Predicate) w;
			if(pred != null){

		    	
				ProgramStatement stm = createArgs((Predicate) w, ignore);

		    	//System.out.println("Created args " + stm);
				ignore.add(pred);
				PredCond cc = checkCC(w, ignore);
				if(cc!= null){
					cc.add(stm);
					return cc;
				}else{
				
				return stm;
				}
			}
	    	}catch(ClassCastException ce){}
		}
		PredCond cond = null;
		List<Word> children = new LinkedList<Word>();
		children.addAll(u.getChildren());
		
		while(!children.isEmpty()){
				Word w = children.remove(0);
				if(w.getPOS().equals("NN") || w.getPOS().equals("NNS") || w.getPOS().equals("NNP") ){
					cond = new PredCond();
					cond.add(w);
					PredCond cc = checkCC(w, ignore);
					if(cc!= null){
						
						cc.add(cond);
						return cc;
					}else{

						cond.add(w);
						return cond;
					}
					
				}else if(w.getPOS().equals("CD")){
					cond = new PredCond();
					cond.add(w);
				}
				children.addAll(w.getChildren());
			}	
			
		
    	return cond;
    	
    
    }
    

    
    private static ProgramStatement checkIfConditions(Predicate p, HashSet<Predicate> ignoreList){
    	//System.out.println("Preidcate " + p);
    	ProgramStatement cond = null;
		//System.out.println("Searching ... " + p);
		//HashMap<Word, String> map = p.getArgMap();
		for(Word w: p.getChildren()){
			//System.out.println("Searching words " + w);
			if(w.getForm().toLowerCase().equals("if") || w.getForm().toLowerCase().equals("when")){
				// this can be until!
				
				ProgramStatement nn = searchConditions(w, ignoreList);
				if(nn!= null){
					cond = nn;
				}
				
			}
			
			
			if(w.isPred()){
				Predicate pp = new Predicate(w);
				ProgramStatement cond2 = createArgs(pp, ignoreList);
				if(cond2 != null) {
					ignoreList.add(pp);
					return cond2;
					
				}
				break;
			}
			
			

		}
		
		
		return cond;
    	
    }
    
    private static ProgramStatement checkConditions(Predicate p, HashSet<Predicate> ignoreList, String s){
    	//System.out.println("Preidcate " + p);
    	ProgramStatement cond = null;
		//HashMap<Word, String> map = p.getArgMap();
		for(Word w: p.getChildren()){
			if(w.getForm().toLowerCase().equals(s)){
				// this can be until!

				ProgramStatement nn = searchConditions(w, ignoreList);
	
				if(nn!= null){
					cond = nn;
				}
				
			}
		
		}
		return cond;
    	
    	
    }

    private String makeHTTPRequest(String url, String sentence) throws IOException {
        byte[] postData = ("sentence=" + URLDecoder.decode(sentence, "UTF-8")).getBytes();
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setFixedLengthStreamingMode(postData.length);
        connection.connect();
        OutputStream os = connection.getOutputStream(); //Do not make multiple calls to getOutputStream with fixed length streaming mode (cf. http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6472250)
        os.write(postData);
        os.close();
        BufferedReader replyReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder ret = new StringBuilder();
        String line;
        while ((line = replyReader.readLine()) != null) {
            ret.append(line).append("\n");
        }
        replyReader.close();
        connection.disconnect();
        return ret.toString().trim();
    }
    
    private static int mapToInteger(String s){
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
    
    public static Word searchAmongChildren(Word w){
    //	System.out.println("searching " + w);

		if(w.getPOS().equals("NN") || w.getPOS().equals("NNS")){
			return w;}

		if(w.getPOS().equals("PRP")){
			return w;}
		if(w.getPOS().equals("VBN") || w.getPOS().equals("VB")){
			return null;}
		
    	for(Word c: w.getChildren()){
    	 	//System.out.println("children " + c);
    	    
    		if(c.getPOS().equals("NN") || c.getPOS().equals("NNS")){
    			return c;}
    		else{
    			for(Word cc: c.getChildren()){
    				Word cw = searchAmongChildren(cc);
    				if(cw != null) return cw;
    			}
    		}
    	}
    	return null;
    }
    
    public static String outputWord(Word w){

	//System.out.println("Word2 " + w);
    	String str = "";
    //	System.out.println(w);
    	switch(w.getPOS()){
    		case "NN": break;
    		case "NNS": str += " PLURAL ";
    			break;
    		case "CD": 
    			//System.out.println("Quant " + w.getForm());
    			int i = mapToInteger(w.getForm()) ;
			if(i >= 0){	
				String pl = "";
				if(i > 1) pl += "PLURAL ";
			str += pl + i + " ";}else{ str += w.getForm() + " ";}
			Word obj = searchAmongChildren(w);
			if(obj!= null) w = obj;
    			break;
    		case "DT": 
    			//Word obj2 = searchAmongChildren(w);
    			//if(obj2!= null) w = obj;
    			break;
    		case "PDT": break;
    		case "IN": 
    			Word obj3 = searchAmongChildren(w);
    			if(obj3!= null) w = obj3;
    			break;
    	}
    	String w_str = "";
    	for(Word c: w.getChildren()){
    	//	System.out.println("child " + c);
    		switch(c.getPOS()){
    		case "CD": 
    			int i = mapToInteger(c.getForm()) ;
				if(i >= 0){
				str += mapToInteger(c.getForm() ) + " ";}else{ str += c.getForm() + " ";}
    			break;
    		case "DT": 
    			str += c.getForm() + " ";
    			break;
    		case "PDT": 
    			str += c.getForm() + " ";
    			break;
    		}
    		/*if(c.getForm().toLowerCase().equals("the")){
        		str += c.getForm() + "_";}
    			if(c.getPOS().equals("CD")){
    				int i = mapToInteger(c.getForm()) ;
    				if(i >= 0){
    				str += mapToInteger(c.getForm() ) + " ";}else{ str += c.getForm() + " ";}
    			}
    		}*/
    	}
    	str += w_str + w.getForm() + " ";
    	//System.out.println(w);
    	str += w.getLemma();
    	
    	return str;
    	
    }
    
    public static HashMap<String, String> mapping;
    public static Set<String> handledPredicates = new HashSet<>();
    public static void addPredicate(String p, String a){
    
    	handledPredicates.add(p);
    	mapping.put(p,a);
    }
/*
    static {
        handledPredicates.add("insert.01");
        handledPredicates.add("pick.01");
        handledPredicates.add("pick.04");
        handledPredicates.add("place.01");
        handledPredicates.add("put.01");
        handledPredicates.add("take.01");
        handledPredicates.add("hit.02");
        handledPredicates.add("hit.01");
        handledPredicates.add("push.02");
        handledPredicates.add("push.01");
        handledPredicates.add("find.01");
        handledPredicates.add("locate.02");
        handledPredicates.add("destroy.01");
        handledPredicates.add("tilt.01");
        handledPredicates.add("search.01");
        handledPredicates.add("kill.01");
        handledPredicates.add("shove.01");
        handledPredicates.add("calibrate.01");

        handledPredicates.add("keep.04");
        handledPredicates.add("hold.01");
        handledPredicates.add("restart.01");

        handledPredicates.add("detect.01");

        handledPredicates.add("move.01");
        handledPredicates.add("appraoch.01");
        handledPredicates.add("retract.01");
        handledPredicates.add("release.01");
        handledPredicates.add("uncharge.01");
        handledPredicates.add("charge.01");
        handledPredicates.add("assemble.02");
        handledPredicates.add("use.01");
        handledPredicates.add("stop.01");
        handledPredicates.add("measure.01");
        handledPredicates.add("slow.03");
        handledPredicates.add("see.01");
        handledPredicates.add("sense.01");
        handledPredicates.add("go.01");
        handledPredicates.add("open.01");
        handledPredicates.add("release.01");
        handledPredicates.add("close.01");
           
    }
    
    */
    
    private static HashMap<String, String> mappedPredicates = new HashMap<String, String>();

    static {
    	mappedPredicates.put("go", "move");        
    }
    
    
    private static final Set<String> ignoredPredicates = new HashSet<String>();

    static {
    	ignoredPredicates.add("do.01");
    	ignoredPredicates.add("do.02");
        
        
    }
}
