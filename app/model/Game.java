package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import util.IObserver;
import util.IObserverable;
import model.Card.Color;
import model.Card.Value;

/**
 * Spiel, liefert Methoden zur Spielogik
 * @author Dominic
 *
 */
public class Game implements IObserverable{
	private List<IObserver> observers;
	
	private String gameName;
	private int gameID;
	
	private ArrayList<Player> players;
	private ArrayList<Card>	cardSet;
	private ArrayList<Card>	cardTray;
	
	private int currentPlayer;
	private int currentDirection;
	
	private int numberOfPlayers;
	private boolean finished = false;
	
	/**Konstruktor*/
	
	public Game(){
		//Defaulkonstruktor
	}
	
	public Game(String gameName, int gameID){
		this.observers = new ArrayList<IObserver>();
		this.gameName = gameName;
		this.gameID = gameID;
		this.numberOfPlayers = 0;
		this.players = new ArrayList<Player>();
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
	
	public Player getPlayer(int index) {
		return players.get(index);
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
	
	public int getCurrentNumberOfPlayers() {
		return numberOfPlayers;
	}
	
	public Card getCardTray() {
		return cardTray.get(cardTray.size()-1);
	}
	
	public void addPlayer(Player player){
		this.numberOfPlayers = numberOfPlayers + 1;
		players.add(player);
	}
	
	public int getDirection() {
		return currentDirection;
	}

	public void setDirection(int direction) {
		this.currentDirection = direction;
	}
	
	public boolean isFull() {
		if(players.size() < 2){
			return true;
		}else{
			return false;
		}
	}
	
	public void finished() {
		this.finished = true;
		notifyObservers();
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

	public boolean play(Player player, int cardID) {
		Card card = player.playCard(cardID, cardTray.get(cardTray.size()-1));
		System.out.println("Kartenstapel vor Spielzug: ColorID: " + cardTray.get(cardTray.size()-1).getColorID() + " ValueID: " + cardTray.get(cardTray.size()-1).getValueID());
		if(card != null) {
			System.out.println("KatenID: " + card.getiD());
			System.out.println("Karte kann gespielt werden!");
			cardTray.add(card);
			System.out.println("Kartenstapel nach Spielzug: ColorID: " + cardTray.get(cardTray.size()-1).getColorID() + " ValueID: " + cardTray.get(cardTray.size()-1).getValueID());
			return true;
		}else{
			System.out.println("Karte nicht spielbar!");
			return false;
		}
		
	}
	
	public void startGame() {
		//Ermittelt Startspieler
		this.currentPlayer = /*(int) (Math.random()) * players.size() + 1*/ 0;
		this.currentDirection = 1;
		this.numberOfPlayers = numberOfPlayers;
		this.cardSet = new ArrayList<Card>();
		this.cardTray = new ArrayList<Card>();
		this.players = players;
		
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
		
		//Teile jedem Spieler 5 Karten aus
		for(Player player : players) {
			draw(player, 5);
		}
		
		//Setze erste Karte des cardTray => Startkarte
		this.cardTray.add(0, cardSet.remove(0));
	}
	
	/**Implementierung des Observerable Patterns */
	
	//Game registriert sich beim Observer
	public void register(IObserver observer) {
		this.observers.add(observer);
		
	}
	
	//Game meldet sich vom Observer ab
	public void unregister(IObserver observer) {
		this.observers.remove(observer);
		
	}

	//Iteriert über die Liste aller Observer und ruft deren update() Methode auf
	public void notifyObservers() {
		for(IObserver observer : this.observers){
			observer.update();
		}
		
	}

	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}
}
