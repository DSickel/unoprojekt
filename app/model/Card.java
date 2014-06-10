package model;

public class Card {
	
	public enum Color {
		GREEN, BLUE, YELLOW, RED;
	}
	
	public enum Value {
		ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, ZERO, EXPOSE, CD, DRAWTWO;
	}
	
	private Color color;
	private Value value;
	
	public Card() {
		//Default Konstruktor tut nichts
	}
	
	public Card(Color color, Value value) {
		this.color = color;
		this.value = value;
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
	
	public boolean playable(Card tableCard) {
		if(tableCard.value.equals(value) || tableCard.color.equals(color)){
			return true;
		}
		return false;
	}
	
	public void playEffect(Game game) {
		//TODO
	}
}
