package com.danlogan.pegsandjokers.domain;

import java.util.ArrayList;

public class PlayerView {
	
	private Game game;
	private int playerNumber;
	private PlayerHand playerHand;
	
	public PlayerView(Game game, int playerNumber) throws PlayerNotFoundException
	{
		this.game = game;
		this.playerNumber = playerNumber;
		
		this.playerHand = this.game.getPlayerHand(playerNumber);
	}
	
	public PlayerHand getPlayerHand()
	{
		return this.playerHand;
	}
	
	public Color getPlayerColor()
	{
		return Color.values()[this.playerNumber-1];
	}
	
	public Board getBoard()
	{
		return this.game.getBoard();
	}
	
	public String getPlayerMessage()
	{
		String message;
		if (this.game.getCurrentPlayer().getNumber() == this.playerNumber)
		{
			message =  "It's your turn";
		}
		else
		{
			message = "It's not your turn";
		}
		
		return message;
	}
	
	public ArrayList<PlayerPosition> getPlayerPositions()
	{
		return this.game.getPlayerPositions(this.playerNumber);
	}
	
	public ArrayList<ArrayList<PlayerPosition>> getOtherPlayerPositions()
	{
		ArrayList result = new ArrayList<ArrayList<PlayerPosition>>();
		
		for(Player p : this.game.getPlayers())
		{
			if (this.playerNumber != p.getNumber())
			{
				result.add(this.game.getPlayerPositions(p.getNumber()));
			}
			
		}
		return result;
	}
	
	public ArrayList<Move> getAllowedMoves()
	{
		ArrayList<Move> allowedMoves =  new ArrayList<Move>();
		
		//for each card in the player's hand, look at each of the player's positions to 
		//determine which moves would be allowed
		
		for (Card card : this.playerHand.getCards())
		{
			for (PlayerPosition currentPostion : this.game.getPlayerPositions(this.playerNumber))
			{
				//determine the moves this card will allow the player's peg at this position to do
			}
		}
			return allowedMoves;
	}

}
