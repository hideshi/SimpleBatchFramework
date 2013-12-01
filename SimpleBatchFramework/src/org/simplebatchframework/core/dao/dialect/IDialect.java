package org.simplebatchframework.core.dao.dialect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.dao.IDao;

public interface IDialect {
	public Connection createConnection();
	public <T extends IBean, T2 extends IDao> ResultSet executeQuery(T bean, Class<T2> daoClass);
	public <T extends IBean, T2 extends IDao> int executeUpdate(T bean, Class<T2> daoClass);
	public <T extends IBean, T2 extends IDao> List<T> resultSetToBean(ResultSet rs, Class<T> clazz, Class<T2> daoClass);
}
