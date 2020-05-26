package com.danlogan.pegsandjokers.domain;

public class CannotStartGameWithoutPlayersException extends InvalidGameStateException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3899090061932324117L;

	public CannotStartGameWithoutPlayersException(String reason) {
		super(reason);
	}
}
