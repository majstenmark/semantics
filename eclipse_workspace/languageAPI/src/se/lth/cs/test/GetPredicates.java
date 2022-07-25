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
public class GetPredicates {

    /**
     * @param args the command line arguments
     */
    private static final long serialVersionUID = 1L;
  //  private static String host = "http://kif.cs.lth.se";//"http://vm25.cs.lth.se";
 // 
    private static String host = "http://localhost:8080";//"http://vm25.cs.lth.se";
//    private static String host = "http://vm25.cs.lth.se";


    public static void main(String[] args) throws Exception {

       // System.out.println("HELOOSSS");

        
        URL semanticServer = new URL(host + "/semantics3/badaboum?getPredicates=" + "glag");

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

