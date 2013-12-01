package org.simplebatchframework.core;

public enum CharacterCode {
	 UTF_8("UTF-8")
	,EUC_JP("EUC-JP")
	,SHIFT_JIS("Shift-JIS")
	;
	private final String text;
	private CharacterCode(final String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return this.text;
	}
}
