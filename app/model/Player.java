package model;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Used to create Players for the Uno-Game
 * @author Dominic
 *
 */
public class Player {
	
	private String playerName;
	private String iD;
	private ArrayList<Card>	handCards;
	
	public Player(String playerName, String iD){
		this.playerName = playerName;
		this.iD = iD;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getiD() {
		return iD;
	}

	public Collection<Card> getHandCards() {
		return handCards;
	}

	public void drawCard(){
		//TODO
	}
	
	public void playCard(){
		//TODO
	}
	
	
}
