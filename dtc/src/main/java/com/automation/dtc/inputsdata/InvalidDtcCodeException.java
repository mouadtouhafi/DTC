package com.automation.dtc.inputsdata;

public class InvalidDtcCodeException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidDtcCodeException(String message) {
        super(message);
    }
}