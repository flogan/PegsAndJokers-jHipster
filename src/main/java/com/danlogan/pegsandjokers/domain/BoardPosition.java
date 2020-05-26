package com.danlogan.pegsandjokers.domain;

public class BoardPosition {
	
	Peg peg;
	String id;

	public BoardPosition(String id) {
		this.id = id;
	}
	
	//Constructor for start positions where Peg is inserted as board is laid out
	public BoardPosition(Peg peg, String id)
	{
		this.peg = peg;
		this.id = id;
	}
	
	public boolean getHasPeg()
	{
		if(peg == null)
		{
			return false;
		}
		
		return true;
	}
	
	public Color getPegColor()
	{
		return getHasPeg() ? peg.getColor() : null;
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public boolean isStartPosition()
	{
		return this.id.contains("Start");
	}
	
	public boolean isHomePosition()
	{
		return this.id.contains("Home");
	}
	
	public int getHomePositionNumber()
	{
		if (isHomePosition())
		{
				return Integer.parseInt(this.id.substring(this.id.indexOf("-")+1));	
		}
		else
		{
			return -1;
		}
	}
	
	public Peg removePeg()
	{
		Peg temp = this.peg;
		this.peg = null;
		return temp;
	}
	
	public void addPeg(Peg pegToAdd)
	{
		this.peg = pegToAdd;
	}

	public boolean isMainTrackPosition() {

		if (this.isStartPosition() || this.isHomePosition())
		{
			return false;
		}
		else { return true; }
		
	}
}
