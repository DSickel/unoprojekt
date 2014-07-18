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
import model.Card;
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
    	int gameID = gameController.addGame(username); //Observer übergeben
    	Game game = gameController.getGame(gameID);
    	Player player = new Player(username, game.getPlayers().size());
    	System.out.println("GEADDETER SPIELER: " + player.getPlayerName() + " mit der ID: " + player.getiD());
    	game.addPlayer(player);
    	System.out.println("Game: " + game.getGameName() + " ID " + game.getGameID() + " #Spieler " + game.getCurrentNumberOfPlayers());
    	
    	session("ID", Integer.toString(player.getiD()));
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
    	Player player = new Player(username, game.getPlayers().size());
    	game.addPlayer(player);
    	
    	System.out.println("--------------- beitreten -------------------");
    	System.out.println("DEBUG AUSGABE: Spieleranzahl " + game.getCurrentNumberOfPlayers());
    	System.out.println("GEADDETER SPIELER: " + player.getPlayerName() + " mit der ID: " + player.getiD());
    	System.out.println("GAME ID: " + Integer.parseInt(gameID));
    	System.out.println("SPIELER IM SPIEL: " + game.getPlayers());
    	System.out.println("LAUFENDE SPIELE: " + gameController.getAvailableGames().toString());
    	System.out.println("=============================================");
    	
    	session("ID", Integer.toString(player.getiD()));
    	session("gameID", gameID);
    	session("host", "false");
    	
    	return redirect("/warteLobby");
    }
   
    public static Result starten() {
    	String username = session("User1");
    	String userID = session("ID");
    	GameController gameController = GameController.getInstance();
    	if(session("gameID") == null){
    		return redirect("/startseite");
    	}
    	Integer gameID = Integer.parseInt(session("gameID"));
    	Game game = gameController.getGame(gameID);
    	String player1 = game.getPlayer(0).getPlayerName();
    	String player2 = game.getPlayer(1).getPlayerName();
    	
    	//Überprüfen ob Spieler 1 schon Karten auf der Hand hat, wenn ja muss das Spiel nicht mehr gestartet werden!
    	if(game.getPlayer(0).getHandCards().isEmpty()){
    		game.startGame();
    	}
    	
    	System.out.println("DEBUG: Spiel welches gestartet werden soll: " + gameID );
    	
    	String player = session("host");
    	System.out.println("HOST: " + player);
    	if(player.equals("false")) {
    		return ok(spielfeld2.render(player1, player2, username, userID));
    	}
    	
    	return ok(spielfeld.render(player1, player2, username, userID));
    }
    
    public static Result loadCardPlayerTwo() {
    	System.out.println("LOAD GAME WIRD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	if(session("gameID") == null){
    		return redirect("/startseite");
    	}
    	Integer gameID = Integer.parseInt(session("gameID"));
    	System.out.println(gameID);
    	Game game = gameController.getGame(gameID);
    	String result = "[";
    	/*for(Card card : game.getPlayer(0).getHandCards()){
            result += "{\"cardID\" : " + card.getID() + ", \"color\" : " + "\"" + card.getColor() + ", \"value\" : " + "\"" + card.getValue() + "\"" +"},";
          }*/
    	for(Card card : game.getPlayer(1).getHandCards()){
    		result += "{\"cardID\" : " + card.getID() + ", \"color\" : " + "\"" + card.getColor() + "\""+ "},";
    	}
    	result = result.substring(0, result.length() - 1) + "]";
    	System.out.println("--- Load Player Two ------");
    	System.out.println(result);
    	System.out.println("==========================");
    	
    	return ok(result);
    }
    
    public static Result loadCardPlayerOne() {
    	System.out.println("LOAD GAME WIRD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	if(session("gameID") == null){
    		return redirect("/startseite");
    	}
    	Integer gameID = Integer.parseInt(session("gameID"));
    	System.out.println(gameID);
    	Game game = gameController.getGame(gameID);
    	String result = "[";
    	/*for(Card card : game.getPlayer(0).getHandCards()){
            result += "{\"cardID\" : " + card.getID() + ", \"color\" : " + "\"" + card.getColor() + ", \"value\" : " + "\"" + card.getValue() + "\"" +"},";
          }*/
    	for(Card card : game.getPlayer(0).getHandCards()){
    		result += "{\"cardID\" : " + card.getID() + ", \"color\" : " + "\"" + card.getColor() + "\""+ "},";
    	}
    	result = result.substring(0, result.length() - 1) + "]";
    	System.out.println("--- Load Player One ------");
    	System.out.println(result);
    	System.out.println("==========================");
    	
    	return ok(result);
    }
    
    public static Result loadTrayCard() {
    	System.out.println("LOAD TRAYCARD WIRD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	if(session("gameID") == null){
    		return redirect("/startseite");
    	}
    	Integer gameID = Integer.parseInt(session("gameID"));
    	System.out.println(gameID);
    	Game game = gameController.getGame(gameID);
    	String result = "{\"cardID\" : " + game.getCardTray().getID() + ", \"color\" : " + "\"" + game.getCardTray().getColor() + "\""+ "}";
    	
    	System.out.println(result);
    	System.out.println("=========================");
    	return ok(result);
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
    						System.out.println("------ cardEvent ---------");
    						System.out.println("ERREICHT CARDEVENT BEFEHLE");
    						System.out.println("Card ID ist: " + cardId);
    						System.out.println("GAMETEST: " +game.getGameID() + ", " +game.getGameName());
    						ObjectNode node = Json.newObject();
    						//Spielt die Karte
    						if(game.play(game.getCurrentPlayer(), cardId) == true) {
    						
    							System.out.println("Game.play == TRUE");
    							node.put("type", "cardEvent");
    							//Zweiter NODE Test
    							System.out.println("2.JSON Test: " + node.asText());
    							node.put("userName", game.getCurrentPlayer().getPlayerName());
    							//Dritter NODE Test
    							node.put("userID", game.getCurrentPlayer().getiD());
    							System.out.println("3.JSON Test: " + game.getCurrentPlayer().getPlayerName() + " ist am Zug");
    							node.put("card", cardId);
    							//Vierter NODE Test
    							node.put("trayCard", game.getCardTray().getID());
    							System.out.println("4.JSON Test: KartenID " + game.getCardTray().getID() + " auf dem Kartenstapel");
    							node.put("text", "macht seinen Zug..");
    							
    							//Nächster Spieler an der Reihe
    							game.nextPlayer();
    							System.out.println("=====================");
    						}else {
    							String userID = Integer.toString(game.getCurrentPlayer().getiD());
    							System.out.println("Game.play == FALSE");
    							node.put("type", "unplayable");
    							node.put("userName", game.getCurrentPlayer().getPlayerName());
    							node.put("userID", userID);
    							node.put("card", cardId);
    						}
    						
    						/*System.out.println("JSON das zu versenden ist: " + node.asText());
    						for(WebSocket.Out<JsonNode> out : gameConnections.values()) {
    							out.write(node);
    						}*/
    						for(WebSocket.Out<JsonNode> out : connection) {
    							out.write(node);
    						}
    					}
    					
    					if(action.equals("draw")) {
    						System.out.println("ERREICHT DRAW BEFEHLE");
    						game.draw(game.getCurrentPlayer(), 1);
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
    					
    					/*if(action.equals("loadGame")) {
    						System.out.println("ERREICHT LOADGAME BEFEHLE");
    						ObjectNode node = Json.newObject();
    						node.put("type", "loadGame");
    						ArrayNode player1 = node.putArray("player1");
    						for(Card card : game.getPlayer(0).getHandCards()) {
    							player1.add(card.getID());
    						}
    						ArrayNode player2 = node.putArray("player2");
    						for(Card card : game.getPlayer(1).getHandCards()) {
    							player2.add(card.getID());
    						}
    						
    						for(WebSocket.Out<JsonNode> out : connection) {
    							out.write(node);
    						}
    					}*/
    					
    					
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


