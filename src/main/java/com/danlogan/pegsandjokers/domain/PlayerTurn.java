package com.danlogan.pegsandjokers.domain;

public class PlayerTurn {
	
	private int playerNumber;
	private String cardName;
	private MoveType moveType;
	private int playerPositionNumber;
	private int moveDistance;
	//split move array if it is used will be of format [playerPos1, distance1, playerPos2, distance2]
	private int[] splitMoveArray;
	private String targetBoardPositionId;
	
	public static class Builder
	{
		//Builder fields
		private int playerNumber;
		private String cardName;
		private MoveType moveType;
		private int playerPositionNumber;
		private int moveDistance;
		private int[] splitMoveArray;
		private String targetBoardPositionId;
		
		public static Builder newInstance()
		{
			return new Builder();
		}
		
		private Builder()
		{
			
		}
		
		public Builder withPlayerNumber(int number)
		{
			this.playerNumber = number;
			return this;
		}
		
		public Builder withSplitMoveArray(int[] splitMoveArray)
		{
			this.splitMoveArray = splitMoveArray;
			return this;
		}
		
		public Builder withCardName(String cardName)
		{
			this.cardName = cardName;
			return this;
		}
		
		public Builder withMoveType(MoveType moveType)
		{
			this.moveType = moveType;
			return this;
		}
		
		
		public Builder withMoveDistance(int moveDistance)
		{
			this.moveDistance = moveDistance;
			return this;
		}
		
		public PlayerTurn build()
		{
			return new PlayerTurn(this);
		}

		public Builder withPositionNumber(int positionNumber) {

			this.playerPositionNumber = positionNumber;
			
			return this;
		}
		
		public Builder withTargetBoardPositionId(String targetBoardPositionId)
		{
			this.targetBoardPositionId = targetBoardPositionId;
			
			return this;
		}
	}
	
	public PlayerTurn(Builder builder) {
		this.cardName = builder.cardName;
		this.moveType = builder.moveType;
		this.playerNumber = builder.playerNumber;
		this.playerPositionNumber = builder.playerPositionNumber;
		this.moveDistance = builder.moveDistance;
		this.splitMoveArray = builder.splitMoveArray;
		this.targetBoardPositionId = builder.targetBoardPositionId;
	}
	
	public PlayerTurn()
	{
		
	}

	public int getPlayerNumber() {
		return this.playerNumber;
	}
	
	public int getSplitMovePosition1()
	{	
		return splitMoveArray[0];
	}
	
	public int getSplitMovePosition2()
	{
		return splitMoveArray[2];
	}
	
	public int getSplitMoveDistance1()
	{
		return splitMoveArray[1];
	}
	
	public int getSplitMoveDistance2()
	{
		return splitMoveArray[3];
	}

	public String getCardName() {
		return this.cardName;
	}

	public MoveType getMoveType() {
		return this.moveType;
	}

	public int getPlayerPositionNumber() {
		return this.playerPositionNumber;
	}
	
	public int getmoveDistance()
	{
		return this.moveDistance;
	}
	
	public String getTargetBoardPositionId()
	{
		return this.targetBoardPositionId;
	}

}
