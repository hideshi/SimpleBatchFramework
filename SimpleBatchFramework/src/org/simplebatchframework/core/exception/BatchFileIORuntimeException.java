package org.simplebatchframework.core.exception;


public class BatchFileIORuntimeException extends BatchRuntimeException {

	private static final long serialVersionUID = 1L;

	public BatchFileIORuntimeException(String message) {
		super(message);
	}
	
	public BatchFileIORuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BatchFileIORuntimeException(Throwable cause) {
		super(cause);
	}
}
