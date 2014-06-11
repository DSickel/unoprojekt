package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import model.Card.Color;
import model.Card.Value;

/**
 * Spiel, liefert Methoden zur Spielogik
 * @author Dominic
 *
 */
public class Game {
	
	private String gameName;
	
	private ArrayList<Player> players;
	private ArrayList<Card>	cardSet;
	private ArrayList<Card>	cardTray;
	
	private int currentPlayer;
	private int currentDirection;
	
	private int numberOfPlayers;
	
	/**Konstruktor*/
	
	public Game(){
		//Defaulkonstruktor
	}
	
	public Game(ArrayList<Player> players, int numberOfPlayers, String gameName){
		this.gameName = gameName;
		this.players = players;
		this.currentPlayer = (int) (Math.random()) * players.size() + 1;
		this.currentDirection = 1;
		this.numberOfPlayers = numberOfPlayers;
		this.cardSet = new ArrayList<Card>();
		
		//füge jeweils 4 Karten jeder Sorte dem cardSet hinzu
		for(Color color : Color.values()) {
			for(Value value : Value.values()) {
				for(int count = 4; count >= 4; count--) {
					Card card = new Card(color, value);
					cardSet.add(card);
				}
			}
		}
		//Mische das cardSet
		shuffle(cardSet);
		
		//Teile jedem Spieler 7 Karten aus
		for(Player player : players) {
			draw(player, 7);
		}
		
		//Setze erste Karte des cardTray => setze Startkarte
		this.cardTray.add(0, cardSet.remove(0));
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

	public Player getCurrentPlayer() {
		return players.get(currentPlayer);
	}

	public void setCurrentPlayer(int currentPlayer) {
		this.currentPlayer = currentPlayer;
	}

	public int getDirection() {
		return currentDirection;
	}

	public void setDirection(int direction) {
		this.currentDirection = direction;
	}
	
	
	/**Game Handling*/
	
	//Mische das Deck
	public void shuffle(ArrayList<Card> cards) {
		Collections.shuffle(cards);
	}
	
	//ermittle den nächsten Spieler
	public void nextPlayer() {
		currentPlayer = (currentPlayer + currentDirection + players.size()) % players.size(); 
	}
	
	//Karte(n) ziehen 
	public void draw(Player player, int number) {
		
		for(int i = 0; i < number; i++){
			
			//Überprüfung ob cardSet noch Karten beinhaltet, falls nicht mische den cardTray in das cardSet
			if(cardSet.size() == 0 && cardTray.size() > 0) {
				Card topCard = cardTray.get(0);
				cardSet.addAll(cardTray.subList(1, cardTray.size()));
				cardTray.clear();
				cardTray.add(topCard);
				shuffle(cardSet);
			}
			//ziehe die oberste Karte vom cardSet
			player.drawCard(cardSet.remove(0));
		}
	}
}
