package com.quickPrint.QuickPrint.exception;

public class FileSizeExceededException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileSizeExceededException(String message) {
        super(message);
    }
}
