package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.*;
import play.libs.Json;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.mvc.*;
import util.IObserver;
import views.html.*;
import model.GameController;
import model.User;
import play.data.*;

public class Application extends Controller implements IObserver{
	
	//Erstellt ein Formular für den Login
	final static Form<User> loginForm = Form.form(User.class); 
	
	//Liste um alle Websocket Connections zu speichern
	private static List<WebSocket.Out<JsonNode>> connections = new ArrayList<WebSocket.Out<JsonNode>>();
	
	//HashMap um alle Member zu speichern
	private static Map<String, WebSocket<JsonNode>> members = new HashMap<String, WebSocket<JsonNode>>();
	
	
	//Rendert die Index Seite
    public static Result index() {
        return ok(index.render());
    }
   
    
    //Rendert die Login Seite
    public static Result login() {
    	Form<User> userForm = loginForm.fill(new User("Max Mustermann"));
    	return ok(login.render(userForm));
    }
    
    
    //Rendert die Startseite (POST)
    public static Result startseite() {
    	Form<User> userForm = loginForm.bindFromRequest();
    	
    	if(userForm.hasErrors()){
    		return badRequest(login.render(userForm));
    	}else{
    		User user = userForm.get();
    		session().clear();
    		session("User1", user.username);
    		return ok(startseite.render(user));
    	}
    }
    
    
    //Rendert die Startseite (GET)
    public static Result startseiteGet(){
    	String username = session("User1");
    	if(username != null){
    		return ok(startseite.render(new User(username)));
    	}else{
    		return redirect("/login");
    	}
    }
    
    
    //Rendert den ChatRoom
    public static Result testChat(){
    	String username = session("User1");
    	return ok(ChatRoom.render(username));
    }
    
    
  	
  	//WebSocket

    
    
  	//Rendert Spiel-beitreten Seite
  	public static Result spiel_beitreten(){
  		GameController gameController = GameController.getInstance();
  	
  		return ok(spiel_beitreten.render());
  	}
  	
  	
  	//Rendert spielfeld Seite
  	public static Result spielfeld(){
  		return ok(spielfeld.render("name"));
  	}
  	
    
    //Interiert über alle Einträge in der Liste der WebSocket Verbindungen
	public  void update() {
		/*for(WebSocket con : connections){
			ObjectNode event = Json.newObject();
			con.notify();
		}*/
		
	}
	
	public static WebSocket<JsonNode> webSocket() {
    	
    	return new WebSocket<JsonNode>() {
			public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
    
				connections.add(out);
				
				in.onMessage(new Callback<JsonNode>() {

					public void invoke(JsonNode argument) throws Throwable {
			
						for(WebSocket.Out<JsonNode> out : connections) {
							out.write(argument);
						}
						
					}	
				});
				
				in.onClose(new Callback0() {

					public void invoke() throws Throwable {
						ObjectNode node = Json.newObject();
						node.put("value1", "test");
						for(WebSocket.Out<JsonNode> out : connections) {
							out.write(node.get("value1"));
						}
						
					}
				});
			}
    	};	
    }
	
}	






	/* public static void WebSocket(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
  
  connections.add(out);
  
  in.onMessage(new Callback<JsonNode>(){
      public void invoke(JsonNode event){
      	
         WebSocket.notifyAll(event);
      }
  });
  
  in.onClose(new Callback0(){
      public void invoke(){
      	ObjectNode node = Json.newObject();
      	node.put("close", "connection closed");
         WebSocket.notifyAll(node);
      }
  });
}

// Iterate connection list and write incoming message
public static void notifyAll(JsonNode message){
  for (WebSocket.Out<JsonNode> out : connections) {
      out.write(message);
  }
}*/
