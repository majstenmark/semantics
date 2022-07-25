package se.lth.cs.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import se.lth.cs.main.Main;

/**
 *
 * @author pierre
 */
public class SetPredicates {

    /**
     * @param args the command line arguments
     */
    private static final long serialVersionUID = 1L;
   private static String host = "http://localhost:8080";//"http://vm25.cs.lth.se";
//    private static String host = "http://vm25.cs.lth.se";
   // private static String host = "http://kif.cs.lth.se";//"http://vm25.cs.lth.se";
//  
    
    private static String createStr(){
    	StringBuilder sb = new StringBuilder();
    	for(String key: h.keySet()){
    		String m = h.get(key);
    		sb.append(key + " " + m + ";");
    	}
    	return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String str = createStr();

       // System.out.println("HELOOSSS");

        str = URLEncoder.encode(str, "UTF-8");
        

       //URL semanticServer2 = new URL(host + "/semantics3/badaboum?verbs=" + str);
       Main.setup("verbs", str);
    }
    
    
    private static HashMap<String, String> h = new HashMap<String, String>();

	    static {
	
        h.put("insert.01", "insert");
        h.put("pick.01", "pick");
        h.put("pick.04", "pick");
        h.put("place.01", "place");
        h.put("put.01", "place");
        h.put("take.01", "pick");
        h.put("hit.02", "hit");
        h.put("hit.01", "hit");
        h.put("push.02", "hit");
        h.put("push.01", "hit");
        h.put("find.01", "locate");

        h.put("locate.01",  "locate");
        h.put("locate.02",  "locate");
        h.put("destroy.01", "hit");
        h.put("tilt.01", "hit");
        h.put("search.01",  "search");
        h.put("kill.01", "hit");
        h.put("shove.01", "hit");
        h.put("calibrate.01", "calibrate");

        h.put("keep.04", "hold");
        h.put("hold.01", "hold");
        h.put("restart.01", "restart");

        h.put("detect.01", "measure");

        h.put("move.01", "move");
        h.put("approach.01", "approach");
        h.put("retract.01", "retract");
        h.put("release.01", "open");
        //handledPredicates.add("uncharge.01");
        //handledPredicates.add("charge.01");
        h.put("assemble.02", "assemble");
        h.put("use.01", "use");
        h.put("utilize.01", "use");
        h.put("stop.01", "stop");
        h.put("measure.01", "measure");
        //handledPredicates.add("slow.03");
        h.put("see.01", "measure");
        h.put("sense.01", "measure");
        h.put("go.01", "move");
        h.put("go.10", "move");
        h.put("open.01", "open");
        h.put("release.01", "open");
        h.put("close.01", "close");
        h.put("bear.02", "bear");
        h.put("crash.01", "hit");   
        h.put("grasp.01", "close");   
        h.put("scrow.01", "screw");   
        h.put("change.01", "change");
        h.put("run.01", "run");
        h.put("stop.01", "stop");
        h.put("rerun.01", "restart");
        h.put("restart.01", "restart");
        h.put("rotate.01", "rotate");
        h.put("turn.01", "rotate");
        h.put("apply.02", "use");
        
        
        
    }
}
