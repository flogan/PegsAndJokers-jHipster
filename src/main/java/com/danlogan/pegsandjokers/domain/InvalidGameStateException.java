package com.danlogan.pegsandjokers.domain;

public class InvalidGameStateException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2297468884628588585L;

	public InvalidGameStateException(String reason) {
		
		super(reason);
	}

}
