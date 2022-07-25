package se.lth.cs.main;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.lth.cs.semantics.*;


public class PrintToXml {
	
	private static Element createElement(ProgramStatement p, Document doc){
		switch(p.getType()){
		case "WHILE": 
			
			WhilePredArgs ifp = (WhilePredArgs) p;
			Element para = doc.createElement("While");
			Element p1 = doc.createElement("Cond");
			Element cond = createElement(ifp.whileThis, doc);
			Element action = createElement(ifp.doThis, doc);
			p1.appendChild(cond);
			para.appendChild(p1);

			para.appendChild(action);
			return para;
			
		case "UNTIL": 

			UntilPredArgs u = (UntilPredArgs) p;
			Element until = doc.createElement("Until");
			Element c1 = doc.createElement("Cond");
			 cond = createElement(u.until, doc);
			 action = createElement(u.doThis, doc);

			 until.appendChild(action);
			 if(cond.getTagName().equals("Skill")){
			 c1.appendChild(cond);
			
			until.appendChild(c1);
		}else{
			until.appendChild(cond);
		}
			
			return until;
		
		case "COND": 
			PredCond pc = (PredCond)p;
			Element pcc = doc.createElement("Cond");

			switch(pc.cc){
			case "AND": 
				pcc = doc.createElement("ANDCond");
				break;
			case "OR": 
				pcc = doc.createElement("ORCond");
				break;
			default: 
			Attr a = doc.createAttribute("value");
			a.setValue(pc.valueString());
			pcc.setAttributeNode(a);
				break;
			}
			for(ProgramStatement s: pc.predargs){
				Element le = createElement(s, doc);
				pcc.appendChild(le);
			}
			return pcc;
		
		case "IF": 

			IfPredArgs ifpp = (IfPredArgs) p;
			Element ife = doc.createElement("If");
			Element e1 = createElement(ifpp.cond, doc);
			Element e2 = createElement(ifpp.consequence, doc);
			ife.appendChild(e1);
			ife.appendChild(e2);
			return ife;
			
		default:
			PredArgs pa = (PredArgs) p;
			Element skill = doc.createElement("Skill");
			Attr attr = doc.createAttribute("action");
			String actionStr = pa.getPredicate().toLowerCase();
			String predicateStr = pa.getPredicateWithSense();
			if(SemanticSubmitter.mapping.containsKey(predicateStr)){
				Attr actionName = doc.createAttribute("actionName");
			
				actionName.setValue(actionStr);
				skill.setAttributeNode(actionName);
				actionStr= SemanticSubmitter.mapping.get(predicateStr);
				
			}
			
			attr.setValue(actionStr);
			skill.setAttributeNode(attr);
			Attr nbr = doc.createAttribute("sentenceNbr");
			nbr.setValue(""+pa.getSentenceNbr());
			skill.setAttributeNode(nbr);
			Attr obj1 = doc.createAttribute("obj1");
			System.err.println("Action " + pa.getPredicate().toLowerCase());
			String[] xmls = pa.outPutA1Xml();
			obj1.setValue(xmls[0]);
			
			skill.setAttributeNode(obj1);

			Attr obj1dt = doc.createAttribute("obj1dt");
			obj1dt.setValue(xmls[1]);
			skill.setAttributeNode(obj1dt);
			Attr obj1card = doc.createAttribute("obj1card");
			obj1card.setValue(xmls[2]);
			skill.setAttributeNode(obj1card);

			
			if(pa.getA2()!= null){
				Attr obj2 = doc.createAttribute("obj2");
				String[] xmls2 = pa.outPutA2Xml();
				obj2.setValue(xmls2[0]);
				skill.setAttributeNode(obj2);

				Attr obj2dt = doc.createAttribute("obj2dt");
				obj2dt.setValue(xmls2[1]);
				skill.setAttributeNode(obj2dt);
				Attr obj2card = doc.createAttribute("obj2card");
				obj2card.setValue(xmls2[2]);
				skill.setAttributeNode(obj2card);

				
			}
			return skill;
			
		
		}
		
	}
	
	public static String printToXml(List<ProgramStatement> stms){
		 try {
			 
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("Sequence");
				doc.appendChild(rootElement);
		 
				// staff elements
				for(ProgramStatement p: stms){
				Element skill = createElement(p, doc);
					//	System.out.println("Hepp " + skill.toString() );
						//doc.createElement("Skill");
				rootElement.appendChild(skill);
		 
				}
				
				// Output to console for testing
				try{
				DOMSource source = new DOMSource(doc);
			    StringWriter writer = new StringWriter();
			       StreamResult result = new StreamResult(writer);
			       TransformerFactory tf = TransformerFactory.newInstance();
			       Transformer transformer = tf.newTransformer();
			       transformer.transform(source, result);

				   String test =  writer.toString();
			     //  System.out.println(test);
			       return test;

}catch(Exception e){
	e.printStackTrace();
}			
				/*
				// set attribute to staff element
				Attr attr = doc.createAttribute("id");
				attr.setValue(p.getPredicate());
				skill.setAttributeNode(attr);
				
				
				Element obj1 = doc.createElement("Object");
				Attr attr1 = doc.createAttribute("id");
				attr1.setValue(p.getA1());
				
				obj1.setAttributeNode(attr1);
				Attr attr2 = doc.createAttribute("cardinality");
				//attr2.setValue(p.getA1());
				
				obj1.setAttributeNode(attr2);
				
				skill.appendChild(obj1);
		 
				}
				// shorten way
				// staff.setAttribute("id", "1");
		 
				// firstname elements
		 
						// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult result = new StreamResult(new File("C:\\file.xml"));
		 
				// Output to console for testing
				// StreamResult result = new StreamResult(System.out);
		 
				transformer.transform(source, result);
		 
				System.out.println("File saved!");
		 */
			  } catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			  } /*catch (TransformerException tfe) {
				tfe.printStackTrace();
			  }*/
			
	return "";
	}
	
	 private static HashMap<String, String> mappedPredicates = new HashMap<String, String>();

	    static {
	    	mappedPredicates.put("go", "move");        
	    }
	    

}
