package com.danlogan.pegsandjokers.domain;

import java.util.ArrayList;

public class Board {

	//Board fields
	private ArrayList<Side> playerSides;
	
	//Board Builder
	public static class Builder {

		private ArrayList<Side> playerSides = new ArrayList<Side>();
		
		static Builder newInstance()
		{
			return new Builder();
		}
		public Builder withNumberOfPlayers(int players)
		{
			//the board will be made up of one side for each Player
			//each side will be assigned a Color
		
			for (int player=0;player<players;player++)
			{
				Color nextColor = Color.values()[player];
				
				Side side = Side.Builder.newInstance().withColor(nextColor).build();
				playerSides.add(side);
							
			}
			
			return this;
			
		}
		public Board build()
		{
			return new Board(this);
		}
		
		private Builder() {}
	}
	
	//Board Methods
	public Board(Builder builder) {

		this.playerSides = builder.playerSides;
	}

	public ArrayList<Side> getPlayerSides()
	{
		return this.playerSides;
	}
	
	public int getBoardPositionSideIndex(Side side, BoardPosition boardPosition)
	{
		return side.getMainTrackPositions().indexOf(boardPosition);
	}
	
	public Side getBoardPositionSide(BoardPosition boardPosition)
	{
		Side returnSide = null;
		for (Side side : this.playerSides )
		{
			if(side.getMainTrackPositions().contains(boardPosition))
			{
				returnSide = side;
			}
		}

		return returnSide;
	}
	
	public int getSideIndex(Side sideToFind)
	{
		return this.playerSides.indexOf(sideToFind);

	}
	
	
	public BoardPosition getBoardPositionById(String positionId)
	{
		for (Side side : this.getPlayerSides())
		{
			for (BoardPosition position : side.getAllPositions())
			{
				if (position.getId().equals(positionId))
				{
					return position;
				}
			}
			
		}
		
		return null;
	}

	public BoardPosition getBoardPositionWithOffset(BoardPosition playerBoardPosition, int step) 
	{

		BoardPosition newBoardPosition=playerBoardPosition;

		//If on main track move forward accounting for 18 spaces per side

		if (playerBoardPosition.isMainTrackPosition())
		{
			Side boardPositionSide = this.getBoardPositionSide(playerBoardPosition);
			int sidePositionIndex = this.getBoardPositionSideIndex(boardPositionSide, playerBoardPosition);
	
			if(sidePositionIndex + step < 18)
			{
				newBoardPosition = boardPositionSide.getPosition(sidePositionIndex + step);
			}
			else
			{
				//wrap around to first side when on the last side
				int boardSideIndex = this.getSideIndex(boardPositionSide);
				Side nextSide = null;

				if (boardSideIndex < this.getPlayerSides().size()-1)
				{
					nextSide = this.getPlayerSides().get(this.getSideIndex(boardPositionSide)+1);
				}
				else
				{
					nextSide = this.getPlayerSides().get(0);
				}

				newBoardPosition = nextSide.getPosition(step - (18 - sidePositionIndex));

			}
		}

		return newBoardPosition;
	}
	
	public ArrayList<BoardPosition> getStartPositionsForPlayerNumber(int playerNumber)
	{
		Side playerSide = this.playerSides.get(playerNumber -1);
		
		return playerSide.getStartPositions();
		
	}
	
	public String getReadyToGoHomePositionIdForPlayerNumber(int playerNumber)
	{
		Side playerSide = this.playerSides.get(playerNumber - 1);
		
		return playerSide.getReadyToGoHomePosition().getId();
	}

}
