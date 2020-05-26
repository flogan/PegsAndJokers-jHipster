package com.danlogan.pegsandjokers.infrastructure;

public class GameNotFoundException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6606306903774783536L;

	public GameNotFoundException(String message) {
		super(message);
	}

}
