package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class Game {
	
	private String gameName;
	
	private ArrayList<Player> players;
	private ArrayList<Card>	cardSet;
	private ArrayList<Card>	cardTray;
	
	private int currentPlayer;
	private int direction;
	
	private final int numberOfPlayers;
	
	/**Konstruktor*/
	
	public Game(ArrayList<Player> players, int numberOfPlayers){
		this.players = players;
		this.direction = 1;
		this.numberOfPlayers = numberOfPlayers;
		
	}

	
	/**Getter und Setter*/
	
	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public Collection<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
	/**Game Handling*/
	
	public void shuffle(ArrayList<Card> cards) {
		Collections.shuffle(cards);
	}
	
	
}
