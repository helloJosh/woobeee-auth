package com.woobeee.auth.exception;

public class TokenTypeNotAllowedException extends RuntimeException {
	public TokenTypeNotAllowedException(String message) {
		super(message);
	}
}
