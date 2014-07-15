package controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
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
import model.Player;
import model.User;
import play.data.*;

public class Application extends Controller implements IObserver{
	
	//Erstellt ein Formular für den Login
	final static Form<User> loginForm = Form.form(User.class); 
	
	//Liste um alle Websocket Connections zu speichern
	private static HashMap<Integer, WebSocket.Out<JsonNode>> gameConnections = new HashMap<Integer, WebSocket.Out<JsonNode>>();
	
	private static List<WebSocket.Out<JsonNode>> connection = new ArrayList<WebSocket.Out<JsonNode>>();
	

	
	
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
    //bis hierhin alles fertig
    
    
    //Rendert den ChatRoom
    public static Result testChat(){
    	String username = session("User1");
    	
    	return ok(ChatRoom.render(username));
    }
    //bis hierhin fertig
    
    
    
    //Erstellt ein Spiel - FERTIG!
    public static Result spiel_erstellen() {
    	String username = session("User1");
    	GameController gameController = GameController.getInstance();
    	int gameID = gameController.addGame(username);
    	Game game = gameController.getGame(gameID);
    	Player player = new Player(username);
    	game.addPlayer(player);
    	System.out.println("Game: " + game.getGameName() + " ID " + game.getGameID() + " #Spieler " + game.getCurrentNumberOfPlayers());
    	
    	session("gameID", Integer.toString(gameID));
    	session("host", "true");
    	
    	return redirect("/warteLobby");
    }
    
    public static Result warteLobby() {
    	String username = session("User1");
    	return ok(wartelobby.render(username));
    }
    
  	//Rendert Spiel-beitreten Seite
    public static Result spielliste() {
    	String username = session("User1");
    	return ok(spielliste.render(username));
    }
    
    //Aktualisiert die Liste der Spiele - FERTIG!
    public static Result refresh(){
    	System.out.println("AKTUALISIEREN WIRD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	Map<Integer, Game> games = gameController.getAvailableGames();
    	
    	String result = "[";
         
        //iterate over openMatches and write key and name in result string
        for(Integer key : games.keySet()){
          result += "{\"gameID\" : " + key + ", \"gameName\" : " + "\"" + games.get(key).getGameName() + "\""+ "},";
        }
      
        result = result.substring(0, result.length() - 1) + "]";
        System.out.println(result);
    	return ok(result);
  	}
    
    public static Result beitreten(String username, String gameID) {
    	GameController gameController = GameController.getInstance();
    	Game game = gameController.getGame(Integer.parseInt(gameID));
    	Player player = new Player(username);
    	game.addPlayer(player);
    	
    	System.out.println("DEBUG AUSGABE: Spieleranzahl " + game.getCurrentNumberOfPlayers());
    	System.out.println("GEADDETER SPIELER: " + player.getPlayerName());
    	System.out.println("GAME ID: " + Integer.parseInt(gameID));
    	System.out.println("SPIELER IM SPIEL: " + game.getPlayers());
    	System.out.println("LAUFENDE SPIELE: " + gameController.getAvailableGames().toString());
    	
    	session("gameID", gameID);
    	session("host", "false");
    	
    	return redirect("/warteLobby");
    }
   
    public static Result starten() {
    	String username = session("User1");
    	GameController gameController = GameController.getInstance();
    	if(session("gameID") == null){
    		return redirect("/startseite");
    	}
    	Integer gameID = Integer.parseInt(session("gameID"));
    	Game game = gameController.getGame(gameID);
    	game.startGame();
    	System.out.println("DEBUG: Spiel welches gestartet werden soll: " + gameID );
    	
    	return ok(spielfeld.render(username));
    }
    
    
    
    
    public static WebSocket<JsonNode> gameWebSocket() {
    	final String user = session("User1");
    	final Integer gameID = new Integer(session("gameID"));
		final boolean host = new Boolean(session("host"));
		final GameController gameController = GameController.getInstance();
		System.out.println("SpielID: " + gameID);
		final Game game = gameController.getGame(gameID);
		System.out.println("GameID: " + game.getGameID());
    	return new WebSocket<JsonNode>() {
    	
    		public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
    			gameConnections.put(gameID, out);
    			connection.add(out);
    			in.onMessage(new Callback<JsonNode>() {
    				public void invoke(JsonNode event) throws Throwable {
    					String action = event.get("type").asText();
    					System.out.println(action);
    					
    					//Wird aufgerufen wenn eine Karte gespielt wird
    					if(action.equals("cardEvent")){
    						Integer cardId = event.get("card").asInt();
    						
    						System.out.println("ERREICHT CARDEVENT BEFEHLE");
    						System.out.println("Card ID ist: " + cardId);
    						System.out.println("GAMETEST: " +game.getGameID() + ", " +game.getGameName());
    						game.play(game.getCurrentPlayer(), cardId);
    						System.out.println("NACH GAME.PLAY");
    						ObjectNode node = Json.newObject();
    						//Erster NODE Test
    						System.out.println("1.JSON Test: " + node.asText());
    						node.put("type", "cardEvent");
    						//Zweiter NODE Test
    						System.out.println("2.JSON Test: " + node.asText());
    						node.put("user", game.getCurrentPlayer().getPlayerName());
    						//Dritter NODE Test
    						System.out.println("3.JSON Test: " + node.fields().toString());
    						node.put("card", cardId);
    						//Vierter NODE Test
    						System.out.println("4.JSON Test: " + node.asText());
    						node.put("text", "macht seinen Zug..");
    						
    						/*System.out.println("JSON das zu versenden ist: " + node.asText());
    						for(WebSocket.Out<JsonNode> out : gameConnections.values()) {
    							out.write(node);
    						}*/
    						for(WebSocket.Out<JsonNode> out : connection) {
    							out.write(node);
    						}
    					}
    					
    					//Wird aufgerufen wenn eine Chatnachricht verschickt wird
    					if(action.equals("message")){
    						System.out.println("ERREICHT MESSAGE BEFEHLE");
    						/*for(WebSocket.Out<JsonNode> out : gameConnections.values()) {
    							out.write(event);
    						}*/
    						
    						for(WebSocket.Out<JsonNode> out : connection) {
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
						for(WebSocket.Out<JsonNode> out : gameConnections.values()) {
							out.write(node);
						}
						
					}
				});
    		}
    	};
    }
    
    
    
    //Interiert über alle Einträge in der Liste der WebSocket Verbindungen
	public void update() {
		for(WebSocket.Out<JsonNode> con : gameConnections.values()){
			ObjectNode event = Json.newObject();
			con.notify();
		}
		
	}
	
	
	
}	


