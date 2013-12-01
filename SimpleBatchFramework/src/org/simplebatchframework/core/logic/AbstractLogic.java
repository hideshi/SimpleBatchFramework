package org.simplebatchframework.core.logic;

import java.sql.SQLException;

import org.simplebatchframework.core.AbstractComponent;
import org.simplebatchframework.core.advice.AdviceProcessor;
import org.simplebatchframework.core.annotation.Inject;
import org.simplebatchframework.core.annotation.Transactional;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.IResult;
import org.simplebatchframework.core.exception.BatchDataBaseRuntimeException;

public abstract class AbstractLogic extends AbstractComponent implements ILogic {
	
	@Inject
	protected AdviceProcessor adviceProcessor;

	@Override
	public IResult execute(IBean bean) {
		adviceProcessor.processBefore(this.getClass(), null, null);
		IResult result = executeLogic(bean);
		Transactional annotation = this.getClass().getAnnotation(Transactional.class);
		try {
			if(annotation != null && context.getConnection().getAutoCommit() == false) {
				context.getConnection().commit();
				logger.debug("commited");
			}
		} catch (SQLException e) {
			throw new BatchDataBaseRuntimeException(e);
		}
		adviceProcessor.processAfter(this.getClass(), null, result);
		return result;
	}

	protected abstract <T extends IBean> IResult executeLogic(T bean);
}
