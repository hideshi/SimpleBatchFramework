package org.simplebatchframework.core.exception;

public class BatchFrameworkRuntimeException extends BatchRuntimeException {

	private static final long serialVersionUID = 1L;

	public BatchFrameworkRuntimeException(String message) {
		super(message);
	}
	
	public BatchFrameworkRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BatchFrameworkRuntimeException(Throwable cause) {
		super(cause);
	}
}
