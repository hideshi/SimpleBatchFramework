package org.simplebatchframework.core;

import org.apache.log4j.Logger;
import org.simplebatchframework.core.annotation.Inject;

public abstract class AbstractComponent {

	protected Logger logger = Logger.getLogger(this.getClass());

	@Inject
	protected CommonContext context;
}
