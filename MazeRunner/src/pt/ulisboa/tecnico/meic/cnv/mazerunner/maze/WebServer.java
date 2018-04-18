package pt.ulisboa.tecnico.meic.cnv.mazerunner.maze;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import com.sun.glass.ui.Menu;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import pt.ulisboa.tecnico.meic.cnv.mazerunner.maze.exceptions.CantGenerateOutputFileException;
import pt.ulisboa.tecnico.meic.cnv.mazerunner.maze.exceptions.CantReadMazeInputFileException;
import pt.ulisboa.tecnico.meic.cnv.mazerunner.maze.exceptions.InvalidCoordinatesException;
import pt.ulisboa.tecnico.meic.cnv.mazerunner.maze.exceptions.InvalidMazeRunningStrategyException;

import java.util.concurrent.Executors;
import java.util.LinkedHashMap;
import java.util.Map;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.io.UnsupportedEncodingException;

public class WebServer {
 	static int count=0;
    public static void main(String[] args) throws Exception {
   
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(Executors.newCachedThreadPool()); // creates a default executor
        server.start();
        
    }

    static class MyHandler implements  HttpHandler{
       
        @Override
        public void handle(HttpExchange t) throws IOException {
        	Map<String, String> query_pairs ;
        	query_pairs = splitQuery(t.getRequestURI().getQuery());
        String[] args = {query_pairs.get("x0") ,query_pairs.get("y0"), query_pairs.get("x1"), query_pairs.get("y1") ,query_pairs.get("v"),query_pairs.get("s"), query_pairs.get("m"),"TestOutput"+ count+".html"};
    	
    	try {
			Main.main(args);
		} catch (InvalidMazeRunningStrategyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCoordinatesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CantGenerateOutputFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CantReadMazeInputFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        File file = new File("TestOutput"+count+ ".html");
        	t.sendResponseHeaders(200, file.length());
         OutputStream os = t.getResponseBody();
    	     Files.copy(file.toPath(), os);
         os.close();
        count++;
        System.out.println("count :"+ count);
        }
    }
    
  public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
       // String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    } 

}
