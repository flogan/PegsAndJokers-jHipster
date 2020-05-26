package com.danlogan.pegsandjokers.domain;

public class InvalidMoveException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1756556689013366031L;

	public InvalidMoveException(String reason)
	{
		super(reason);
	}
}
