package org.simplebatchframework.core.exception;

public class BatchRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BatchRuntimeException(String message) {
		super(message);
	}
	
	public BatchRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BatchRuntimeException(Throwable cause) {
		super(cause);
	}
}
