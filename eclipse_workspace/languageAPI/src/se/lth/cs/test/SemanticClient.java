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

/**
 *
 * @author pierre
 */
public class SemanticClient {

    /**
     * @param args the command line arguments
     */
    private static final long serialVersionUID = 1L;
    private static String host = "http://localhost:8080";//"http://vm25.cs.lth.se";
//    private static String host = "http://vm25.cs.lth.se";
//    private static String host = "http://kif.cs.lth.se";//"http://vm25.cs.lth.se";
//  

    public static void main(String[] args) throws Exception {
    	 //  String narrative = "Search in the z-direction until contact or sensing 5 N."; // while holding 5 N in the y-direction.";//"Search in the x-direction until contact or timeout.";//"Search in the x-direction until you measure 5 N while holding 4 N in z-direction.";//"Crash the tower.";
    	     
    	   String narrative = "Open the left gripper."; // while holding 5 N in the y-direction.";//"Search in the x-direction until contact or timeout.";//"Search in the x-direction until you measure 5 N while holding 4 N in z-direction.";//"Crash the tower.";
      //  String str = createStr();

       // System.out.println("HELOOSSS");

        narrative = URLEncoder.encode(narrative, "UTF-8");
       // str = URLEncoder.encode(str, "UTF-8");
        
        URL semanticServer = new URL(host + "/semantics3/badaboum?text=" + narrative);

     //  URL semanticServer2 = new URL(host + "/semantics3/badaboum?verbs=" + str);
        InputStream is = semanticServer.openStream();
        BufferedReader bReader =
                new BufferedReader(new InputStreamReader(is));
       // System.out.println("HELOsasfasda");
        String line;
        while ((line = bReader.readLine()) != null) {
            System.out.println(URLDecoder.decode(line, "UTF-8"));
        }
      //  System.out.println("HEssssss");

    }
    
    
}
