package model;

import java.util.ArrayList;
import java.util.Collection;

import model.Card.Color;

/**
 * Used to create Players for the Uno-Game
 * @author Dominic
 *
 */
public class Player {
	
	private String playerName;
	private Integer iD;
	private ArrayList<Card>	handCards;
	
	public Player(String playerName, Integer iD){
		this.playerName = playerName;
		this.iD = iD;
		handCards = new ArrayList<Card>();
	}

	public String getPlayerName() {
		return playerName;
	}

	public Integer getiD() {
		return iD;
	}
	
	public void setiD(Integer iD) {
		this.iD = iD;
	}
	
	public Collection<Card> getHandCards() {
		return handCards;
	}

	public void drawCard(Card setCard){
		handCards.add(setCard);
	}
	
	public Card playCard(int cardId, Card trayCard){
		int i = 0;
		for(Card card : handCards){
			
			System.out.println(i+". KarteID " + card.getiD());
			i++;
		}
		Card card = new Card();
		if(cardId < 13) {
			System.out.println("BLAUE KARTE!");
			int colorID = 0;
			int valueID = cardId - 0;
			card.setColorID(colorID);
			card.setValueID(valueID);
			
		}
		if(cardId > 14 && cardId < 28) {
			System.out.println("ROTE KARTE!");
			int colorID = 15;
			int valueID = cardId - 15;
			card.setColorID(colorID);
			card.setValueID(valueID);
			
		}
		if(cardId > 29 && cardId < 43) {
			System.out.println("GRÜNE KARTE!");
			int colorID = 30;
			int valueID = cardId - 30;
			card.setColorID(colorID);
			card.setValueID(valueID);
			
		}
		if(cardId > 44 && cardId < 58) {
			System.out.println("GELBE KARTE!");
			int colorID = 45;
			int valueID = cardId - 45;
			card.setColorID(colorID);
			card.setValueID(valueID);
			
		}
		card.setiD(card.getColorID() + card.getValueID());
		System.out.println("Karte ID: " + card.getID());
		
		System.out.println("Karte ColorID: " + card.getColorID());
		System.out.println("Karte ValueID: " + card.getValueID());
		
		System.out.println("TrayCard ColorID: " +trayCard.getColorID());
		System.out.println("TrayCard ValueID: " +trayCard.getValueID());
		
		if(card.playable(trayCard) == true) {
			int j = 0;
			int remove = 0;
			for(Card c : handCards){
				System.out.println(j+". KarteID " + c.getID());
				if(card.getID() == c.getID()){
					System.out.println("Karte stimmt mit HandCards an Index " + j + " überrein!");
					remove = j;
				}
				j++;
			}
			handCards.remove(remove);
			return card;
		}
		return null;
	}
	
	/*
	public Card playCard(int index, Card trayCard){
		if(handCards.get(index).playable(trayCard)) {
			return handCards.remove(index);
		}
		return null;
	}*/
	
	
	
	
}
