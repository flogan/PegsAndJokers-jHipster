package com.danlogan.pegsandjokers.domain;

import java.util.ArrayList;

public class Side {
	
	//Side Fields
	private Color color;
	private ArrayList<BoardPosition> homePositions;
	private ArrayList<BoardPosition> startPositions;
	private ArrayList<BoardPosition> mainTrackPositions;
	private BoardPosition comeOutPosition;
	private BoardPosition readyToGoHomePosition;
	private ArrayList<Peg> pegs;
	

	//Side Builder
	public static class Builder {
		
		//Builder fields
		Color sideColor;
		private ArrayList<BoardPosition> homePositions = new ArrayList<BoardPosition>();
		private ArrayList<BoardPosition> startPositions = new ArrayList<BoardPosition>();
		private ArrayList<BoardPosition> mainTrackPositions = new ArrayList<BoardPosition>();
		private BoardPosition comeOutPosition;
		private BoardPosition readyToGoHomePosition;
		private ArrayList<Peg> pegs = new ArrayList<Peg>();
		 
		public static Builder newInstance()
		{
			return new Builder();
		}
		
		public Builder withColor(Color color)
		{
			this.sideColor = color;
			return this;
		}
		
		public Side build()
		{
			//add 18 main track positions per side
			for(int i=1;i<19;i++)
			{
				BoardPosition boardPosition = new BoardPosition(this.sideColor.toString()+"-"+i);
				mainTrackPositions.add(boardPosition);
			}
			
			//add 5 home positions per side
			for(int i=1;i<=5;i++)
			{
				BoardPosition boardPosition = new BoardPosition(this.sideColor.toString()+"Home-"+i);
				homePositions.add(boardPosition);
			}
			
			//add 5 start positions per side
			//each start position will begin with a peg for the player
			for(int i=1;i<=5;i++)
			{
				Peg peg = new Peg(this.sideColor, i);
				this.pegs.add(peg);
				
				BoardPosition boardPosition = new BoardPosition(peg,this.sideColor.toString()+"Start-"+i);
				startPositions.add(boardPosition);
			}
			
			//the ready to go home position is the third spot on the track (index of 2 in the array)
			this.readyToGoHomePosition = mainTrackPositions.get(2);
			
			//the come out position is the eighth spot on the track (index of 7 in the array)
			this.comeOutPosition = mainTrackPositions.get(7);

			return new Side(this);
		}
		
		private Builder() {}
		
	}
	
	
	//Side Methods
	public Side(Builder builder)
	{
		this.color = builder.sideColor;
		this.homePositions = builder.homePositions;
		this.startPositions = builder.startPositions;
		this.mainTrackPositions = builder.mainTrackPositions;
		this.comeOutPosition = builder.comeOutPosition;
		this.readyToGoHomePosition = builder.readyToGoHomePosition;
		this.pegs = builder.pegs;
	
	}
	
	public Color getColor()
	{
		return this.color;
	}
	
	public ArrayList<BoardPosition> getHomePositions()
	{
		return this.homePositions;
	}
	
	public ArrayList<BoardPosition> getAllPositions()
	{
		ArrayList<BoardPosition> allPositions = new ArrayList<BoardPosition>();
		
		allPositions.addAll(getMainTrackPositions());
		allPositions.addAll(getStartPositions());
		allPositions.addAll(getHomePositions());
		
		return allPositions;
	}


	public ArrayList<BoardPosition> getStartPositions()
	{ 
		return this.startPositions; 
	}

	public ArrayList<BoardPosition> getMainTrackPositions()
	{
		return this.mainTrackPositions;
	}

	public BoardPosition getComeOutPosition()
	{
		return this.comeOutPosition;
	}

	public BoardPosition getReadyToGoHomePosition()
	{
		return this.readyToGoHomePosition;
	}

	public BoardPosition getPosition(int index) {

		return this.getMainTrackPositions().get(index);
	}
	
	public BoardPosition getHomePositionByNumber(int homePositionNumber)
	{
		return this.homePositions.get(homePositionNumber-1);
	}
	  
}