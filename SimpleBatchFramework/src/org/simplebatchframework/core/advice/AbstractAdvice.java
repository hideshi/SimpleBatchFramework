package org.simplebatchframework.core.advice;

import org.apache.log4j.Logger;
import org.simplebatchframework.core.CommonContext;
import org.simplebatchframework.core.annotation.Inject;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.IResult;

public abstract class AbstractAdvice implements IAdvice {
	
	protected Logger logger = Logger.getLogger(this.getClass());

	@Inject
	protected CommonContext context;
	
	protected IBean conditionBean;

	protected IResult result;
	
	public abstract void execute();
}
