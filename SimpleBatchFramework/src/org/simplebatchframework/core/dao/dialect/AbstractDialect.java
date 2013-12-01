package org.simplebatchframework.core.dao.dialect;

import org.simplebatchframework.core.AbstractComponent;
import org.simplebatchframework.core.CommonContext;
import org.simplebatchframework.core.annotation.Inject;

public abstract class AbstractDialect extends AbstractComponent implements IDialect {

	@Inject
	protected CommonContext context;

	protected final String DRIVER_NAME = "driverName";
	
	protected final String URL = "url";

	protected final String USER = "user";

	protected final String PASS = "pass";
	
	protected final String AUTO_COMMIT = "autoCommit";
	
	protected final String RAW_SQL_DUMP = "rawSqldump";

	protected final String BINDED_SQL_DUMP = "bindedSqldump";
}
