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
import views.html.*;
import model.Card;
import model.Game;
import model.GameController;
import model.Player;
import model.User;
import play.data.*;

public class Application extends Controller {
	
	//Erstellt ein Formular für den Login
	final static Form<User> loginForm = Form.form(User.class); 
	
	//Liste der WebSocketTeilnehmer für die Wartelobby
	private static List<WebSocket.Out<JsonNode>> lobbyConnection = new ArrayList<WebSocket.Out<JsonNode>>(); 

	private static HashMap<Integer, GameObserver> gameObservers = new HashMap<Integer, GameObserver>();

	
	/**
	 * Rendert die Index Seite
	 */
    public static Result index() {
        return ok(index.render());
    }
   
    /**
     * Rendert die Login Seite
     */
    public static Result login() {
    	session().clear();
    	Form<User> userForm = Form.form(User.class);
    	return ok(login.render(userForm));
    }
    
    /**
     * Rendert die Startseite (POST)
     */
    public static Result startseite() {
    	Form<User> userForm = Form.form(User.class).bindFromRequest();
    	if(userForm.hasErrors()){
    		System.out.println("Errors gefunden!");
    		return badRequest(login.render(userForm));
    	}else{
    		User user = userForm.get();
    		session().clear();
    		session("User1", user.userName);
    		return ok(startseite.render(user));
    	}
    }
    
    /**
     * Rendert die Startseite (GET)
     */
    public static Result startseiteGet(){
    	String username = session("User1");
    	if(username != null){
    		return ok(startseite.render(new User(username)));
    	}else{
    		return redirect("/login");
    	}
    }
   
    
    /**
     * Rendert die Regelwerkseite
     */
    public static Result regelwerk() {
    	String username = session("User1");
    	if(username != null){
    		return ok(regelwerk.render());
    	}else{
    		return redirect("/login");
    	}
	}
    
    public static Result impressum() {
    	return TODO;
    }
    
    /**
     * Rendert den ChatRoom
     */
    public static Result testChat(){
    	String username = session("User1");
    	if(username != null){
    		return TODO;
    	}else{
    		return redirect("/login");
    	}
    }
   
    /**
     * Erstellt ein Spiel und added den Ersteller unter die Liste der Spieler
     */
    public static Result spiel_erstellen() {
    	String username = session("User1");
    	GameController gameController = GameController.getInstance();
    	int gameID = gameController.addGame(username); 
    	Game game = gameController.getGame(gameID);
    	Player player = new Player(username, game.getPlayers().size()+1);
    	System.out.println("GEADDETER SPIELER: " + player.getPlayerName() + " mit der ID: " + player.getiD());
    	game.addPlayer(player);
    	System.out.println("Game: " + game.getGameName() + " ID " + game.getGameID() + " #Spieler " + game.getCurrentNumberOfPlayers());
    	
    	session("ID", Integer.toString(player.getiD()));
    	session("gameID", Integer.toString(gameID));
    	session("host", "true");
    	
    	return redirect("/warteLobby");
    }
    
    /**
     * Rendert die Wartelobby
     */
    public static Result warteLobby() {
    	String username = session("User1");
    	if(username != null) {
    		return ok(wartelobby.render(username));
    	}else{
    		return redirect("/login");
    	}
    }
    
  	/**
  	 * Rendert Spiel-beitreten Seite
  	 */
    public static Result spielliste() {
    	String username = session("User1");
    	if(username != null) {
    		return ok(spielliste.render(username));
    	}else{
    		return redirect("/login");
    	}
    }
    
    /**
     * Aktualisiert die Liste der Spiele in Spiel-beitreten
     */
    public static Result refresh(){
    	System.out.println("AKTUALISIEREN WIRD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	Map<Integer, Game> games = gameController.getAvailableGames();
    	
    	//Erstelle ein JSON
    	String result = "[";
        for(Integer key : games.keySet()){
          result += "{\"gameID\" : " + key + ", \"gameName\" : " + "\"" + games.get(key).getGameName() + "\""+ "},";
        }
        result = result.substring(0, result.length() - 1) + "]";
        
        System.out.println(result);
    	return ok(result);
  	}
    
    /**
     * Aktualisiert die Liste der Aktiven Spieler in der Lobby
     */
    public static Result refreshSpieler() {
    	System.out.println("REFRESH SPIELER WIERD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	Game game = gameController.getGame(Integer.parseInt(session("gameID")));
    	
    	//Erstelle ein JSON
    	String result = "[";
    	for(Player player : game.getPlayers()){
    		result += "{\"playerID\" : " + player.getiD() + ", \"playerName\" : " + "\"" + player.getPlayerName() + "\""+ "},";
    	}
    	result = result.substring(0, result.length() - 1) + "]";
    	
        System.out.println(result);
    	return ok(result);
    }
    
    /**
     * Lässt Spieler einem Spiel beitreten
     * @param username Name des Spielers
     * @param gameID ID des Spiels
     * @return
     */
    public static Result beitreten(String username, String gameID) {
    	GameController gameController = GameController.getInstance();
    	Game game = gameController.getGame(Integer.parseInt(gameID));
    	Player player = new Player(username, game.getPlayers().size()+1);
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
   
    /**
     * Startet ein Spiel
     */
    public static Result starten() {
    	String username = session("User1");
    	String userID = session("ID");
    	String player = session("host");
    	if(username != null) {
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
	    	System.out.println("HOST: " + player);
	    	
	    	if(player.equals("false")) {
	    		return ok(spielfeld2.render(player1, player2, username, userID));
	    	}
	    	
	    	return ok(spielfeld.render(player1, player2, username, userID));
    	}else{
    		return redirect("/login");
    	}
    }
    
    /**
     * Lädt die Karten der Spieler auf die Spielfeldseite
     */
    public static Result loadCardPlayer(String user) {
    	System.out.println("LOAD GAME WIRD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	Integer userID = Integer.parseInt(user);
    	if(session("gameID") == null){
    		return redirect("/startseite");
    	}
    	Integer gameID = Integer.parseInt(session("gameID"));
    	System.out.println("GameID" + gameID);
    	Game game = gameController.getGame(gameID);
    	
    	//Erstelle ein JSON
    	String result = "[";
    	for(Card card : game.getPlayer(userID).getHandCards()){
    		result += "{\"cardID\" : " + card.getID() + ", \"currentPlayer\" : " + "\"" + game.getCurrentPlayer().getiD() + "\""+ "},";
    	}
    	result = result.substring(0, result.length() - 1) + "]";
    	
    	System.out.println("--- Load Player "+ user + "  ------");
    	System.out.println(result);
    	System.out.println("=========================");
    	System.out.println("SPIELER " + game.getCurrentPlayer().getPlayerName() + " IST AM ZUG");
    	System.out.println("SPIELER " + game.getCurrentPlayer().getiD() + " IST AM ZUG");
    	System.out.println("==========================");
    	
    	return ok(result);
    }
    
    /**
     * Lädt die oberste Ablagekarte und den "aktuellen" Spieler auf die Spielfeldseite
     */
    public static Result loadTrayCard() {
    	System.out.println("LOAD TRAYCARD WIRD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	if(session("gameID") == null){
    		return redirect("/startseite");
    	}
    	Integer gameID = Integer.parseInt(session("gameID"));
    	System.out.println(gameID);
    	Game game = gameController.getGame(gameID);
    	//Erstelle ein JSON
    	String result = "{\"cardID\" : " + game.getCardTray().getID() + ", \"currentPlayer\" : " + "\"" + game.getCurrentPlayer().getiD() + "\""+ "}";
    	
    	System.out.println(result);
    	System.out.println("=========================");
    	return ok(result);
    }
    
    /**
     * WebSocket für das eigentliche Spiel
     */
    public static WebSocket<JsonNode> gameWebSocket() {
    	final String user = session("User1");
    	System.out.println("User WEBSOCKET: "+ user);
    	final Integer gameID = new Integer(session("gameID"));
		final boolean host = new Boolean(session("host"));
		final GameController gameController = GameController.getInstance();
		System.out.println("SpielID: " + gameID);
		final Game game = gameController.getGame(gameID);
		System.out.println("GameID: " + game.getGameID());
    	return new WebSocket<JsonNode>() {
    	
    		public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {
    				//Überprüfe ob GameObserver schon vorhanden ist
    				if(gameObservers.containsKey(gameID)){
    					//GameObserver schon da
    				}else{
    					//GameObserver noch niht da
    					gameObservers.put(gameID, new GameObserver(game));
    				}	
    					if(host){
    						gameObservers.get(gameID).host = out;
    					} else {
    						gameObservers.get(gameID).guest = out;
    					}
    				
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
    						
    						//Spielt die Karte
    						if(game.play(game.getCurrentPlayer(), cardId)) {
    							//Karte kann gespielt werden
    							System.out.println("Game.play == TRUE");
    						
    						}else {
    							//Karte kann nicht gespielt werden
    							System.out.println("Game.play == FALSE");
    						}
    					}
    					//Spieler will/muss eine Karte ziehen
    					if(action.equals("draw")) {
    						System.out.println("ERREICHT DRAW BEFEHLE");
    						Integer drawNumber = event.get("number").asInt();
    						game.draw(game.getCurrentPlayer(), drawNumber);
    					}
    					
    					//Wird aufgerufen wenn eine Chatnachricht verschickt wird
    					if(action.equals("message")){
    						//Chat Nachricht kann ohne Behandlung direkt an beide Teilnehmer weiter geschickt werden
    						System.out.println("ERREICHT MESSAGE BEFEHLE");
    						gameObservers.get(gameID).guest.write(event);
    						gameObservers.get(gameID).host.write(event);
    					}
    					
    				}
    				
    			});
    			in.onClose(new Callback0() {
    				
					public void invoke() throws Throwable {
						//Websocket Verbindung wurde getrennt, Spiel kann nicht weiter gespielt werden
						game.setGameVerfallen();
						System.out.println("NODE USER CLOSE: " + user);
					}
				});
    		}
    	};
    }
    
    /**
     * WebSocket für die Wartelobby
     */
    public static WebSocket<JsonNode> lobbyWebSocket(){
    	final Integer gameID = new Integer(session("gameID")); 
        final String user = new String(session("User1"));
    	final GameController gameController = GameController.getInstance();
		System.out.println("SpielID: " + gameID);
		final Game game = gameController.getGame(gameID);
		System.out.println("GameID: " + game.getGameID());
        return new WebSocket<JsonNode>(){
           
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
            		
                    lobbyConnection.add(out);
                    System.out.println("NODE USER ONREADY: " + gameID +" "+user);
                    ObjectNode node = Json.newObject();
                    node.put("type", "join");
                    node.put("user", user);
                    for(WebSocket.Out<JsonNode> outs : lobbyConnection) {
						outs.write(node);
					}
                    in.onMessage(new Callback<JsonNode>() {
                    	
                    	public void invoke(JsonNode event) throws Throwable {
                    		String action = event.get("type").asText();
        					System.out.println(action);
        					ObjectNode node = Json.newObject();
                    		if(action.equals("start")){
                    			if(game.getCurrentNumberOfPlayers() != 2){
                    				node.put("type", "notStart");
                    			}else{
                    				node.put("type", "start");
                    				node.put("user", event.get("user"));
                    				node.put("players", game.getCurrentNumberOfPlayers());
                    			}
                    		}
                    		for(WebSocket.Out<JsonNode> out : lobbyConnection) {
    							out.write(node);
    						}
                    	}
                    });
                    in.onClose(new Callback0() {

						public void invoke() throws Throwable {
							if (game.getCurrentNumberOfPlayers() == 1) {
								
						    	try {

						    		if (game.getCurrentNumberOfPlayers() == 1) {
						      			game.addPlayer(new Player("FILLER", -1));
						    		}

						    	} catch(NumberFormatException e){
						    		//Match not there - we can ignore it
						    	} catch (NullPointerException e){
						    		//Match not there - we can ignore it
						    	}
							}else{
								ObjectNode node = Json.newObject();
								node.put("type", "close");
								node.put("user", user);
								for(WebSocket.Out<JsonNode> out : lobbyConnection) {
	    							out.write(node);
	    						}
							}
						}
					});
            }
        };   
    }
    
	
}	


