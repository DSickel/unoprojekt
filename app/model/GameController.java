package model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
/**
 * Controller Klasse welche alle Spiele "verwaltet"
 * @author Dominic
 *
 */
public class GameController {
	//Singleton
	static private GameController instance;
	static public GameController getInstance() {
		if(instance == null) {
			instance = new GameController();
		}
		return instance;
	}
	
	private GameController() {
		
	}
	
	//Map mit allen aktuell laufenden Spielen
	private Map<Integer, Game> runningGames = new HashMap<Integer, Game>();
	private static int gameID = 0;
	
	/**
	 * Leifert die IDs aller laufenden Spiele
	 * @return IDs aller laufenden Spiele
	 */
	private Collection<Integer> getGameIDs() {
		return runningGames.keySet();
	}
	
	/**
	 * Erstellt ein Spiel, fügt es der Map hinzu und liefert dessen ID zurück
	 * @param gameName Name des Spiels
	 * @return ID des erstellten Spiels
	 */
	public int addGame(String gameName) {
		gameID++;
		Game game = new Game(gameName, gameID);
		runningGames.put(gameID, game);
		return gameID;
	}
	
	
	/**
	 * Liefert ein laufendes Spiel anhand der ID zurück
	 * @param iD ID des Spiels welches zurück geliefert wird
	 * @return Spiel mit der entsprechenden ID
	 */
	public Game getGame(int iD) {
		Integer gameID = new Integer(iD);
		return runningGames.get(gameID);
	}
	
	
	/**
	 * Liefert alle laufenden Spiele zurück
	 * @return Map mit allen laufenden Spielen
	 */
	public Map<Integer, Game> getAvailableGames(){
		Map<Integer, Game> games = new HashMap<Integer, Game>();
		Collection<Integer> allGameIDs = getGameIDs();
		for(Integer gameIDs : allGameIDs){
			Game g = runningGames.get(gameIDs);
			if(g.getCurrentNumberOfPlayers() < 2){
				games.put(gameIDs, g);
			}
		}
		return games;
	}
}
	