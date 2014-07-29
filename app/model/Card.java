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
	
	public Card(){
		
	}
	
	public Card(Integer iD) {
		this.iD = iD;
	}
	
	public Card(Color color, Value value) {
		this.color = color;
		this.value = value;
		this.colorID = createColorID();
		this.valueID = createValueID();
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
	
	public Integer getColorID() {
		return colorID;
	}

	public void setColorID(Integer colorID) {
		this.colorID = colorID;
	}

	public Integer getValueID() {
		return valueID;
	}

	public void setValueID(Integer valueID) {
		this.valueID = valueID;
	}

	public Integer getiD() {
		return iD;
	}

	public void setiD(Integer iD) {
		this.iD = iD;
	}


	/**
	 * Erstellt eine eindeutige FarbID 
	 * Blau => 0
	 * Rot  => 15
	 * Grün => 30
	 * Gelb => 45
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
		
	
	public boolean playable(Card trayCard) {
		if(trayCard.getColorID() == this.colorID) {
			return true;
		}
		if((trayCard.getID() - trayCard.getColorID()) == this.valueID) {
			return true;
		}
		return false;
	}
	
	/*public boolean playable(Card tableCard) {
		if(tableCard.value.equals(value) || tableCard.color.equals(color)){
			return true;
		}
		return false;
	}*/
	
	public void playEffect(Game game) {
		System.out.println("PLAY EFFEKT");
		String value =  getValueID().toString();
		System.out.println("WERT: " + value);
		switch(value) {
			case "12": 
				game.nextPlayer();
				System.out.println("ERREICHT EXPOSE EFFEKT");
				break;
				
			case "10": 
				if(game.getDirection() > 0) {
					game.setDirection(-1);
				}
				game.setDirection(1);
				System.out.println("ERREICHT CD EFFEKT");
				break;
				
			case "11":
				game.draw(game.getCurrentPlayer(), 2);
				System.out.println("PLUS 2 EFFEKT WIRD AUFGERUFEN");
				break;
				
			default:
				System.out.println("KEIN EFFEKT");
				
		}
		
	}
	
	
}
