package com.danlogan.pegsandjokers.domain;

public class PlayerPosition {
	
	private BoardPosition boardPosition;
	private int playerNumber;
	
	public PlayerPosition(int playerNumber, BoardPosition bp)
	{
		this.playerNumber = playerNumber;
		this.boardPosition = bp;
	}
	
	public int getPlayerNumber()
	{
		return this.playerNumber;
	}
	
	public BoardPosition getPlayerBoardPosition()
	{
		return  this.boardPosition;
	}
	
	public void moveTo(BoardPosition newPosition) throws CannotMoveToAPositionYouOccupyException
	{
		//Make sure the new position is not occupied already by another one of the player's pegs
		if (this.getPlayerBoardPosition().getPegColor() == newPosition.getPegColor())
		{
			throw new CannotMoveToAPositionYouOccupyException("You cannot move to a position with one of your own pegs in it.");
		}
		
		Peg peg = boardPosition.removePeg();
		System.out.println("moving peg from: " + boardPosition.id);
		
		newPosition.addPeg(peg);
		
		System.out.println("moved pegged to: " + newPosition.id);
		
		this.boardPosition = newPosition;
	}
	
	public Color getPegColor()
	{
		return this.boardPosition.getPegColor();
	}

}
