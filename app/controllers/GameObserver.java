package controllers;

import java.util.Observable;

import model.Game;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.WebSocket;

public class GameObserver implements java.util.Observer {
	
	public WebSocket.Out<JsonNode> host;
	public WebSocket.Out<JsonNode> guest;
	
	public Game game;
	
	public GameObserver(Game game) {
		this.game = game;
		game.addObserver(this);
	}

	@Override
	public void update(Observable o, Object event) {
		String action = String.valueOf(event);
		ObjectNode node = Json.newObject();
		switch(action){
		
		case "start":	node.put("type", "start");
						node.put("startPlayerName", game.getCurrentPlayer().getPlayerName());
						node.put("startPlayerID", game.getCurrentPlayer().getiD());
						host.write(node);
						guest.write(node);
						break;
						
		case "draw":	node.put("type", "cardEvent");
						//node.put("text", user + " muss eine Karte ziehen");
						//game.nextPlayer();
						node.put("userName", game.getCurrentPlayer().getPlayerName());
						node.put("userID", game.getCurrentPlayer().getiD());
						host.write(node);
						guest.write(node);
						break;
						
		case "cardEvent":	node.put("type", "cardEvent");
							//node.put("card", cardId);
							node.put("trayCard", game.getCardTray().getID());
							//node.put("text", user + " macht seinen/ihren Zug..");
		
							//Nächster Spieler an der Reihe
							//game.nextPlayer();
							//game.getCardTray().playEffect(game);
							//Schreib ID und Name des nächsten Spielers in ein JSON
							node.put("userID", game.getCurrentPlayer().getiD());
							node.put("userName", game.getCurrentPlayer().getPlayerName());
							host.write(node);
							guest.write(node);
							break;
							
		case "unplayable":	node.put("type", "unplayable");
							node.put("userName", game.getCurrentPlayer().getPlayerName());
							node.put("userID", game.getCurrentPlayer().getiD());
							host.write(node);
							guest.write(node);
							break;
							
		case "finished":	node.put("type", "finished");
							node.put("userID", game.getCurrentPlayer().getiD());
							node.put("userName", game.getCurrentPlayer().getPlayerName());
							host.write(node);
							guest.write(node);
							break;
							
		case "close":		node.put("type", "close");
							host.write(node);
							guest.write(node);
							break;
		
		}
		
	}
	
	
}
