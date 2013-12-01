package org.simplebatchframework.core.dao;

import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.IResult;

public interface IDao {
	public IResult execute(IBean bean);
}
