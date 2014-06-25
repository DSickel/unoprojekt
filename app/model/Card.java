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
	
	private int iD;
	private Color color;
	private Value value;
	
	public Card() {
		//Default Konstruktor tut nichts
	}
	
	public Card(Color color, Value value) {
		this.color = color;
		this.value = value;
		this.iD = value.ordinal() + color.ordinal();
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
	
	public int getID() {
		return iD;
	}
	
	/**
	 * Erstellt eine eindeutige ID 
	 * Grün von  0 - 11
	 * Blau von 20 - 31
	 * Gelb von	40 - 51
	 * Rot  von 60 - 71
	 */
	public void createID(){
		int a = 0;
		int b = 0;
		
		//Farbüberprüfung
		if (color == Color.BLUE) {
			a = 20;
		}

		if (color == Color.RED) {
			a = 60;
		}

		if (color == Color.GREEN) {
			a = 0;
		}

		if (color == Color.YELLOW) {
			a = 40;
		}
		
		//Werteberprüfung
		if (value == Value.ONE) {
			b = 1;
		}
		
		if (value == Value.TWO) {
			b = 2;
		}
		
		if (value == Value.THREE) {
			b = 3;
		}
		
		if (value == Value.FOUR) {
			b = 4;
		}
		
		if (value == Value.FIVE) {
			b = 5;
		}
		
		if (value == Value.SIX) {
			b = 6;
		}
		
		if (value == Value.SEVEN) {
			b = 7;
		}
		
		if (value == Value.EIGHT) {
			b = 8;
		}
		
		if (value == Value.NINE) {
			b = 9;
		}
		
		if (value == Value.ZERO) {
			b = 0;
		}
		
		if (value == Value.CD) {
			b = 10;
		}
		
		if (value == Value.DRAWTWO) {
			b = 11;
		}
		
		if (value == Value.EXPOSE) {
			b = 12;
		}
		
		this.iD = a + b;
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
