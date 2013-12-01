package org.simplebatchframework.core;

public enum Message {
	  ERROR_CONFIGURATION_NOT_CONTAIN("Configuration doesn't contain this key:")
	 ,ERROR_INVALID_PARAMETER_LENGTH("Parameter doesn't have appropriate length:")
	 ,ERROR_INVALID_PARAMETERS_SIZE("Parameters don't have appropriate amount of arguments:")
	 ,ERROR_INVALID_PARAMETER_TYPE("Parameter doesn't have appropriate type:")
	 ,ERROR_INVALID_COLUMN_NAME("SQL file includes ambiguous column name:")
	 ,ERROR_FAILED_CREATE_CONNECTION("The creation of dataBase connection was failed.")
	 ,ERROR_NOT_CORRECT_FIXED_LENGTH_FILE_DEFINITION("Difinition for fixed length file isn't correct:")
	 ,ERROR_NOT_CORRECT_FIXED_LENGTH_FILE_DATA("Fixed length file data isn't correct:")
	 ,ERROR_NOT_CORRECT_CSV_FILE_DEFINITION("Difinition for csv file isn't correct:")
	 ,ERROR_NOT_CORRECT_CSV_FILE_DATA("CSV file data isn't correct:")
	 ;
	private final String text;
	private Message(final String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return this.text;
	}
}
