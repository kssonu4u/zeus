package com.zeus.exception;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorInfo {

	@JsonProperty(value="error_message")
	private String errorMessage;

	public ErrorInfo() {
		super();
	}

	public ErrorInfo(String errorMessage) {
		super();
		this.errorMessage = errorMessage;
	}
	
}
