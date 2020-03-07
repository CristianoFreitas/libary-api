 package com.br.libraryapi.exception;

public class BusinessException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4211412176013788252L;

	public BusinessException(String message) {
		super(message);
	}
}
