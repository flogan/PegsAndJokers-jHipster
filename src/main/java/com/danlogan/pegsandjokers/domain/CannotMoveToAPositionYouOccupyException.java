package com.danlogan.pegsandjokers.domain;

public class CannotMoveToAPositionYouOccupyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6514562042254236816L;
	
	public CannotMoveToAPositionYouOccupyException(String message)
	{
		super(message);
	}

}

