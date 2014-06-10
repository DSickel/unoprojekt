package model;

import java.util.ArrayList;
import java.util.List;

import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.WebSocket;

public class Web_Socket {
	
	  private static List<WebSocket.Out<String>> connections = new ArrayList<WebSocket.Out<String>>();
	    
	    public static void start(WebSocket.In<String> in, WebSocket.Out<String> out){
	        
	        connections.add(out);
	        
	        in.onMessage(new Callback<String>(){
	            public void invoke(String event){
	               Web_Socket.notifyAll(event);
	            }
	        });
	        
	        in.onClose(new Callback0(){
	            public void invoke(){
	               Web_Socket.notifyAll("A connection closed");
	            }
	        });
	    }
	    
	    // Iterate connection list and write incoming message
	    public static void notifyAll(String message){
	        for (WebSocket.Out<String> out : connections) {
	            out.write(message);
	        }
	    }
}
