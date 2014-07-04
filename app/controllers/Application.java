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
import model.Game;
import model.GameController;
import model.User;
import play.data.*;

public class Application extends Controller implements IObserver{
	
	//Erstellt ein Formular für den Login
	final static Form<User> loginForm = Form.form(User.class); 
	
	//Liste um alle Websocket Connections zu speichern
	private static List<WebSocket.Out<JsonNode>> gameConnections = new ArrayList<WebSocket.Out<JsonNode>>();
	
	//Rendert die Index Seite
    public static Result index() {
    	System.out.println("Projekt startet");
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
    //bis hierhin alles fertig
    
    
    //Rendert den ChatRoom
    public static Result testChat(){
    	String username = session("User1");
    	return ok(ChatRoom.render(username));
    }
    //bis hierhin fertig
    
    
    
    
    public static Result spiel_erstellen() {
    	String username = session("User1");
    	GameController gameController = GameController.getInstance();
    	int gameID = gameController.addGame(username);
    	Game game = gameController.getGame(gameID);
    	System.out.println("Game: " + game.getGameName() + " " + game.getGameID());
    	session("matchID", Integer.toString(gameID));
    	return ok(spielfeld.render(username));
    }
    
  	//Rendert Spiel-beitreten Seite
    public static Result spielliste() {
    	String username = session("User1");
    	return ok(spielliste.render(username));
    }
    
    public static Result aktualisieren(){
    	GameController gameController = GameController.getInstance();
    	Map<Integer, Game> games = gameController.getAvailableGames();
    	ObjectNode result = Json.newObject();
    	for(Integer key : games.keySet()){
    		
    	}
    	
    	System.out.println("DEBUG AUSGABE: " + result);
  		return ok(result);
  	}
    
    public static Result beitreten(String username, String gameID) {
    	GameController gameController = GameController.getInstance();
    	Game game = gameController.getGame(Integer.parseInt(gameID));
    	game.addPlayer();
    	return redirect("/spielfeld");
    }
   
    public static Result starten() {
    	return ok();
    }
    
    public static WebSocket<JsonNode> gameWebSocket() {
    	final String user = session("User1");
    	return new WebSocket<JsonNode>() {
    		public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
    			gameConnections.add(out);
    			
    			in.onMessage(new Callback<JsonNode>() {
    				public void invoke(JsonNode event) throws Throwable {
    					String action = event.get("type").asText();
    					System.out.println(action);
    					if(action.equals("cardEvent")){
    						String id = event.get("iD").asText();
    						System.out.println("ERREICHT CARDEVENT BEFEHLE");
    						
    					}
    					
    					if(action.equals("message")){
    						System.out.println("ERREICHT MESSAGE BEFEHLE");
    						for(WebSocket.Out<JsonNode> out : gameConnections) {
    							out.write(event);
    						}
    					}
    					
    					
    				}
    			});
    			in.onClose(new Callback0() {
    				
					public void invoke() throws Throwable {
						ObjectNode node = Json.newObject();
						node.put("text", "has closed connection!");
						node.put("user", user);
						for(WebSocket.Out<JsonNode> out : gameConnections) {
							out.write(node);
						}
						
					}
				});
    		}
    	};
    }
    
    //Interiert über alle Einträge in der Liste der WebSocket Verbindungen
	public void update() {
		for(WebSocket.Out<JsonNode> con : gameConnections){
			ObjectNode event = Json.newObject();
			con.notify();
		}
		
	}
	
	/*public static WebSocket<JsonNode> webSocket() {
    	
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
    }*/
	
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
