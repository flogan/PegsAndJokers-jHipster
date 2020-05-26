package com.danlogan.pegsandjokers.domain;


public class Player {

	//Player properties
	private final String name;
	private final int number;

	//Player Class Builder
	public static class Builder{

		private String name;
		private int number;

		public static Builder newInstance() {
			return new Builder();
		}

		private Builder() {}

		//setter methods to configure builder

		public Builder withName(String name) {
			this.name = name;
			return this;
		}
		
		public Builder withNumber(int number)
		{
			this.number = number;
			return this;
		}

		//build method to return a new instance from Builder
		public Player build() {
			return new Player(this);
		}
	}

	//Player Class Methods
	public Player(Builder builder)
	{
		//set all properties from the builder
		this.name = builder.name;
		this.number = builder.number;
	}

	public String getName() {
		return name;
	}
	
	public int getNumber()
	{
		return number;
	}
}

