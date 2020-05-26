package com.danlogan.pegsandjokers.domain;

public class Joker extends Card {
	
	public Joker()
	{
		super(null,null);
				
	}
	
	public boolean isJoker() {
		return true;
	}
	
	public String getName() {
		
		return "JOKER";
	}

}
