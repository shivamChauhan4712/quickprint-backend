package com.quickPrint.QuickPrint.exception;

public class CafeAlreadyExistsException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CafeAlreadyExistsException(String message) {
        super(message);
    }
}