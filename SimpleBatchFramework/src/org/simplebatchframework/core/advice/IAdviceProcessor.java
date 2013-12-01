package org.simplebatchframework.core.advice;

import org.simplebatchframework.core.AbstractComponent;

public interface IAdviceProcessor {
	public void processBefore(Class<AbstractComponent> clazz);
	public void processAfter(Class<AbstractComponent> clazz);
}
