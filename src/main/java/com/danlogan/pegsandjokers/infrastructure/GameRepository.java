package com.danlogan.pegsandjokers.infrastructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import com.danlogan.pegsandjokers.domain.Game;

public class GameRepository {
	
	private ConcurrentHashMap<String,Game> games = new ConcurrentHashMap<String,Game>();
	
	public GameRepository() {
		super();
	}
	
	public Game findGameById(String id) throws GameNotFoundException {
		
		Game game = games.get(id);
		if (game == null) {
			throw new GameNotFoundException("Game not found for id = "+id);
		}
		
		return game;
	}
	
	public void addGame(Game game) {
		games.put(game.getId().toString(), game);
	}
	
	public int getNumberOfGames() {
		return games.size();
	}
	
	public ArrayList<Game> getAllGames(){
	
		Collection<Game> values = games.values();
		
		ArrayList<Game> list = new ArrayList<Game>(values);
				
		return list;
	}

}
