package org.simplebatchframework.core.logic;

import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.IResult;

public interface ILogic {
	public <T extends IBean> IResult execute(T bean);
}
