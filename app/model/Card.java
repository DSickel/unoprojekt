package model;



/**
 * Used to create Cards for Uno-Game
 * @author Dominic
 *
 */
public class Card {
	
	public enum Color {
		GREEN, BLUE, YELLOW, RED;
	}
	
	public enum Value {
		ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, EXPOSE, CD, DRAWTWO;
	}
	
	private Integer colorID;
	private Integer valueID;
	
	private Color color;
	private Value value;
	private Integer iD;
	
	public Card(Integer iD) {
		this.iD = iD;
	}
	
	public Card(Color color, Value value) {
		this.color = color;
		this.value = value;
		this.iD = createColorID() + createValueID();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Value getValue() {
		return value;
	}

	public void setValue(Value value) {
		this.value = value;
	}
	
	public Integer getID() {
		return iD;
	}
	
	
	public void setID(Integer iD) {
		this.iD = iD;
	}
	/**
	 * Erstellt eine eindeutige FarbID 
	 * Blau => 20
	 * Rot  => 21
	 * Grün => 22
	 * Gelb => 23
	 */
	public int createColorID(){
		int colorID = 0;
		
		//Farbüberprüfung
		if (color == Color.BLUE) {
			colorID = 0;
		}

		if (color == Color.RED) {
			colorID = 15;
		}

		if (color == Color.GREEN) {
			colorID = 30;
		}

		if (color == Color.YELLOW) {
			colorID = 45;
		}
		return colorID;
	}
	
	public int createValueID() {
		int valueID = 0;
		
		//Werteberprüfung
		if (value == Value.ONE) {
			valueID = 1;
		}
		
		if (value == Value.TWO) {
			valueID = 2;
		}
		
		if (value == Value.THREE) {
			valueID = 3;
		}
		
		if (value == Value.FOUR) {
			valueID = 4;
		}
		
		if (value == Value.FIVE) {
			valueID = 5;
		}
		
		if (value == Value.SIX) {
			valueID = 6;
		}
		
		if (value == Value.SEVEN) {
			valueID = 7;
		}
		
		if (value == Value.EIGHT) {
			valueID = 8;
		}
		
		if (value == Value.NINE) {
			valueID = 9;
		}
		
		if (value == Value.ZERO) {
			valueID = 0;
		}
		
		if (value == Value.CD) {
			valueID = 10;
		}
		
		if (value == Value.DRAWTWO) {
			valueID = 11;
		}
		
		if (value == Value.EXPOSE) {
			valueID = 12;
		}
		
		return valueID;
	}
		
	
	public boolean playable(Card tableCard) {
		if(tableCard.value.equals(value) || tableCard.color.equals(color)){
			return true;
		}
		return false;
	}
	
	public void playEffect(Game game) {
		//Default: No effect
	}
	
	
}
