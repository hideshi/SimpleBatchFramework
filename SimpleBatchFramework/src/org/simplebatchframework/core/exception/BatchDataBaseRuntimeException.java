package org.simplebatchframework.core.exception;


public class BatchDataBaseRuntimeException extends BatchRuntimeException {

	private static final long serialVersionUID = 1L;

	public BatchDataBaseRuntimeException(String message) {
		super(message);
	}
	
	public BatchDataBaseRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public BatchDataBaseRuntimeException(Throwable cause) {
		super(cause);
	}
}
