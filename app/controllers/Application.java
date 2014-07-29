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
	
	//Liste der Websocket Teilnehmer für das Spiel
	private static List<WebSocket.Out<JsonNode>> connection = new ArrayList<WebSocket.Out<JsonNode>>();
	
	//Liste der WebSocketTeilnehmer für die Wartelobby
	private static List<WebSocket.Out<JsonNode>> lobbyConnection = new ArrayList<WebSocket.Out<JsonNode>>(); 

	
	
	//Rendert die Index Seite
    public static Result index() {
        return ok(index.render());
    }
   
    //Rendert die Login Seite
    public static Result login() {
    	session().clear();
    	Form<User> userForm = Form.form(User.class);
    	return ok(login.render(userForm));
    }
    
    //Rendert die Startseite (POST)
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
    
    //Rendert die Startseite (GET)
    public static Result startseiteGet(){
    	String username = session("User1");
    	if(username != null){
    		return ok(startseite.render(new User(username)));
    	}else{
    		return redirect("/login");
    	}
    }
   
    
    //Rendert die Regelwerkseite
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
    
    //Rendert den ChatRoom
    public static Result testChat(){
    	String username = session("User1");
    	
    	return ok(ChatRoom.render(username));
    }
   
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
    	if(username != null) {
    		return ok(wartelobby.render(username));
    	}else{
    		return redirect("/login");
    	}
    }
    
   
    
  	//Rendert Spiel-beitreten Seite
    public static Result spielliste() {
    	String username = session("User1");
    	if(username != null) {
    		return ok(spielliste.render(username));
    	}else{
    		return redirect("/login");
    	}
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
    
    public static Result refreshSpieler() {
    	System.out.println("REFRESH SPIELER WIERD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	Game game = gameController.getGame(Integer.parseInt(session("gameID")));
    	
    	String result = "[";
    	
    	for(Player player : game.getPlayers()){
    		result += "{\"playerID\" : " + player.getiD() + ", \"playerName\" : " + "\"" + player.getPlayerName() + "\""+ "},";
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
    
    
    public static Result loadCardPlayerOne(String user) {
    	System.out.println("LOAD GAME WIRD AUFGERUFEN!");
    	GameController gameController = GameController.getInstance();
    	Integer userID = Integer.parseInt(user);
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
    	for(Card card : game.getPlayer(userID).getHandCards()){
    		result += "{\"cardID\" : " + card.getID() + ", \"currentPlayer\" : " + "\"" + game.getCurrentPlayer().getiD() + "\""+ "},";
    	}
    	result = result.substring(0, result.length() - 1) + "]";
    	System.out.println("--- Load Player One ------");
    	System.out.println(result);
    	System.out.println("=========================");
    	System.out.println("SPIELER " + game.getCurrentPlayer().getPlayerName() + " IST AM ZUG");
    	System.out.println("SPIELER " + game.getCurrentPlayer().getiD() + " IST AM ZUG");
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
    	String result = "{\"cardID\" : " + game.getCardTray().getID() + ", \"currentPlayer\" : " + "\"" + game.getCurrentPlayer().getiD() + "\""+ "}";
    	
    	System.out.println(result);
    	System.out.println("=========================");
    	return ok(result);
    }
    
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
    			gameConnections.put(gameID, out);
    			connection.add(out);
    			ObjectNode node = Json.newObject();
                node.put("type", "start");
                node.put("startPlayerName", game.getCurrentPlayer().getPlayerName());
                node.put("startPlayerID", game.getCurrentPlayer().getiD());
                for(WebSocket.Out<JsonNode> outs : connection) {
						outs.write(node);
				}
    			in.onMessage(new Callback<JsonNode>() {
    				public void invoke(JsonNode event) throws Throwable {
    					String action = event.get("type").asText();
    					System.out.println(action);
    					
    					//Erstelle ein leeres JSON Object
    					ObjectNode node = Json.newObject();
    					
    					//Wird aufgerufen wenn eine Karte gespielt wird
    					if(action.equals("cardEvent")){
    						Integer cardId = event.get("card").asInt();
    						System.out.println("------ cardEvent ---------");
    						System.out.println("ERREICHT CARDEVENT BEFEHLE");
    						System.out.println("Card ID ist: " + cardId);
    						System.out.println("GAMETEST: " +game.getGameID() + ", " +game.getGameName());
    						
    						//Spielt die Karte
    						if(game.play(game.getCurrentPlayer(), cardId)) {
    							System.out.println("Game.play == TRUE");
    							String user = event.get("user").asText();
    							
    							//Überprüfen ob Spiel fertig ist
    							if(game.finished()){
    								node.put("type", "finished");
    								node.put("userID", game.getCurrentPlayer().getiD());
    								node.put("userName", game.getCurrentPlayer().getPlayerName());
    							
    							}else{//Spiel ist noch nicht fertig
    						
        							node.put("type", "cardEvent");
        							node.put("card", cardId);
        							node.put("trayCard", game.getCardTray().getID());
        							node.put("text", user + " macht seinen/ihren Zug..");
        							
        							//Nächster Spieler an der Reihe
        							game.nextPlayer();
        							game.getCardTray().playEffect(game);
        							//Schreib ID und Name des nächsten Spielers in ein JSON
        							node.put("userID", game.getCurrentPlayer().getiD());
        							node.put("userName", game.getCurrentPlayer().getPlayerName());
        							
        							System.out.println("SPIELER " + game.getCurrentPlayer().getPlayerName() + " IST AM ZUG");
        							System.out.println("=====================");
    							}
    							
    							
    						}else {
    							//Karte kann nicht gespielt werden
    							String userID = Integer.toString(game.getCurrentPlayer().getiD());
    							System.out.println("Game.play == FALSE");
    							node.put("type", "unplayable");
    							node.put("userName", game.getCurrentPlayer().getPlayerName());
    							node.put("userID", userID);
    							node.put("card", cardId);
    						}
    						
    					
    						for(WebSocket.Out<JsonNode> out : connection) {
    							out.write(node);
    						}
    					}
    					//Spieler will/muss eine Karte ziehen
    					if(action.equals("draw")) {
    						System.out.println("ERREICHT DRAW BEFEHLE");
    						String user = event.get("user").asText();
    						Integer drawNumber = event.get("number").asInt();
    						game.draw(game.getCurrentPlayer(), drawNumber);
    						node.put("type", "draw");
							node.put("text", user + " muss eine Karte ziehen");
							game.nextPlayer();
							node.put("userName", game.getCurrentPlayer().getPlayerName());
							node.put("userID", game.getCurrentPlayer().getiD());
							for(WebSocket.Out<JsonNode> out : connection) {
    							out.write(node);
    						}
    					}
    					
    					//Wird aufgerufen wenn eine Chatnachricht verschickt wird
    					if(action.equals("message")){
    						System.out.println("ERREICHT MESSAGE BEFEHLE");
    				
    						for(WebSocket.Out<JsonNode> out : connection) {
    							out.write(event);
    						}
    					}
    					
    				}
    			});
    			in.onClose(new Callback0() {
    				
					public void invoke() throws Throwable {
						ObjectNode node = Json.newObject();
						node.put("type", "close");
						node.put("text", "has closed connection!");
						node.put("user", user);
						System.out.println("NODE USER CLOSE: " + user);
						for(WebSocket.Out<JsonNode> out : connection) {
							out.write(node);
						}
						
					}
				});
    		}
    	};
    }
    
    public static WebSocket<JsonNode> wsToInformAboutSecondPlayer(){
    	final Integer gameID = new Integer(session("gameID")); //"cache" here so it's available within the onReady method.
        final String user = new String(session("User1"));
    	final GameController gameController = GameController.getInstance();
		System.out.println("SpielID: " + gameID);
		final Game game = gameController.getGame(gameID);
		System.out.println("GameID: " + game.getGameID());
        return new WebSocket<JsonNode>(){
           
            
            // called when websocket handshake is done
            public void onReady(WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out){
            		//Add the user which created this websocket to the lists of hosts waiting
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
							//GameController matchController = GameController.getInstance();
							//Game match = matchController.getGame(gameID);
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
    
    //Interiert über alle Einträge in der Liste der WebSocket Verbindungen
	public void update() {
		for(WebSocket.Out<JsonNode> con : gameConnections.values()){
			ObjectNode event = Json.newObject();
			con.notify();
		}
		
	}
	
	
	
}	


