package org.simplebatchframework.core;

import java.sql.Connection;
import java.util.Properties;

import org.simplebatchframework.core.annotation.Param;
import org.simplebatchframework.core.annotation.ValidateRegexp;
import org.simplebatchframework.core.dao.dialect.IDialect;

public class CommonContext {
	
	private Properties configuration;
	
	private IDialect dialect;
	
	private Connection connection;
	
	@Param(column=0)
	@ValidateRegexp(regexp="^[0-9A-Za-z]{2,40}$")
	private String id;

	public Properties getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Properties configuration) {
		this.configuration = configuration;
	}

	public IDialect getDialect() {
		return dialect;
	}

	public void setDialect(IDialect dialect) {
		this.dialect = dialect;
		connection = dialect.createConnection();
	}

	public Connection getConnection() {
		return connection;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
