package org.simplebatchframework.core.job;

import java.sql.SQLException;

import org.simplebatchframework.core.AbstractComponent;
import org.simplebatchframework.core.advice.AdviceProcessor;
import org.simplebatchframework.core.annotation.Inject;
import org.simplebatchframework.core.annotation.Transactional;
import org.simplebatchframework.core.exception.BatchDataBaseRuntimeException;

public abstract class AbstractJob extends AbstractComponent implements IJob {

	@Inject
	protected AdviceProcessor adviceProcessor;

	@Override
	public void execute() {
		adviceProcessor.processBefore(this.getClass(), null, null);
		executeJob();
		Transactional annotation = this.getClass().getAnnotation(Transactional.class);
		try {
			if(annotation != null && context.getConnection().getAutoCommit() == false) {
				context.getConnection().commit();
				logger.debug("commited");
			}
		} catch (SQLException e) {
			throw new BatchDataBaseRuntimeException(e);
		}
		adviceProcessor.processAfter(this.getClass(), null, null);
	}

	protected abstract void executeJob();
}
