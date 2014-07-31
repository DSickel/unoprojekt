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
public class Game extends java.util.Observable {
	
	private String gameName;
	private int gameID;
	
	private ArrayList<Player> players;
	private ArrayList<Card>	cardSet;
	private ArrayList<Card>	cardTray;
	
	private int currentPlayer;
	private int currentDirection;
	
	private int numberOfPlayers;
	private boolean gameVerfallen = false;
	
	/**Konstruktor*/
	
	public Game(){
		//Defaulkonstruktor
	}
	
	public Game(String gameName, int gameID){
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
	
	public void removePlayer(int index){
		this.numberOfPlayers = numberOfPlayers - 1;
		players.remove(index);
	}
	
	public int getDirection() {
		return currentDirection;
	}

	public void setDirection(int direction) {
		this.currentDirection = direction;
	}
	
	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}
	
	/**
	 * Überprüft ob das Spiel voll ist
	 * @return true, falls 2 Spieler im Spiel sind
	 */
	public boolean isFull() {
		if(players.size() < 2){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * Überprüft ob das Spiel beendet ist
	 * @return true, wenn Spiel beendet
	 * 		   false, wenn Spiel noch nicht beendet
	 */
	public boolean finished() {
		if(players.get(currentPlayer).getHandCards().isEmpty()){
		
			return true;
		}
		return false;
	}
	
	public void setGameVerfallen() {
		this.gameVerfallen = true;
		setChanged();
		notifyObservers("close");
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
	
	/**
	 * Spieler müssen im laufe des Spiels Karten ziehen
	 * @param player der Karten ziehen muss/will
	 * @param number Anzahl der Karten
	 */
	public void draw(Player player, int number) {
		System.out.println("GAME.DRAW");
		for(int i = 0; i < number; i++){
			System.out.println("Game.draw schleife " + i);
			//Überprüfung ob cardSet noch Karten beinhaltet, falls nicht mische den cardTray in das cardSet
			if(cardSet.size() == 0 && cardTray.size() > 0) {
				System.out.println("CARDSET leer");
				Card topCard = cardTray.get(0);
				cardSet.addAll(cardTray.subList(1, cardTray.size()));
				cardTray.clear();
				cardTray.add(topCard);
				shuffle(cardSet);
			}
			//ziehe die oberste Karte vom cardSet
			player.drawCard(cardSet.remove(0));
		}
		if(number != 2){
			System.out.println("DRAW METHODE NOTIFY!");
			nextPlayer();
			setChanged();
			notifyObservers("cardEvent");
		}
		
	}
	
	/**
	 * Spieler müssen während des Spiels Karten ausspielen
	 * @param player welcher eine Karte spielen möchte
	 * @param cardID ID der Karte welche gespielt werden soll
	 * @return true, falls die Karte spielbar ist
	 * 		   false, falls die Karte nicht spielbar ist
	 */
	public boolean play(Player player, int cardID) {
		Card card = player.playCard(cardID, cardTray.get(cardTray.size()-1));
		System.out.println("Kartenstapel vor Spielzug: ColorID: " + cardTray.get(cardTray.size()-1).getColorID() + " ValueID: " + cardTray.get(cardTray.size()-1).getValueID());
		if(card != null) {
			System.out.println("KatenID: " + card.getiD());
			System.out.println("Karte kann gespielt werden!");
			cardTray.add(card);
			System.out.println("Kartenstapel nach Spielzug: ColorID: " + cardTray.get(cardTray.size()-1).getColorID() + " ValueID: " + cardTray.get(cardTray.size()-1).getValueID());
			if(finished()){
				setChanged();
				notifyObservers("finished");
				return true;
			}else{
				nextPlayer();
				getCardTray().playEffect(this);
				setChanged();
				notifyObservers("cardEvent");
				return true;
			}
		}else{
			System.out.println("Karte nicht spielbar!");
			setChanged();
			notifyObservers("unplayable");
			return false;
		}
		
	}
	
	/**
	 * Wird einmalig zu Beginn des Spiels aufgerufen!
	 * Setzt die Startparameter, erstellt die Karten, mischt das Kartendeck und
	 * ermittelt den Startspieler per Zufallsgenerator
	 */
	public void startGame() {
		//Ermittelt Startspieler
		this.currentPlayer = (int)(Math.random()*players.size());
		this.currentDirection = 1;
		//this.numberOfPlayers = numberOfPlayers;
		this.cardSet = new ArrayList<Card>();
		this.cardTray = new ArrayList<Card>();
		//this.players = players;
		
		//füge jeweils 4 Karten jeder Sorte dem cardSet hinzu
		for(Color color : Color.values()) {
			for(Value value : Value.values()) {
				for(int count = 4; count >= 0; count--) {
					Card card = new Card(color, value);
					System.out.println("Karte erstellt: " + card.getColor() + " " + card.getValue() );
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
		setChanged();
		notifyObservers("start");
	}
	
}
