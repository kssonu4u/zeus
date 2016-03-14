package com.zeus.exception;

public class ZeusException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1551954000103636879L;
	
	public ZeusException() {
	}

	public ZeusException(String message) {
		super(message);
	}

	public ZeusException(Throwable cause) {
		super(cause);
	}

	public ZeusException(String message, Throwable cause) {
		super(message, cause);
	}

	public ZeusException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
