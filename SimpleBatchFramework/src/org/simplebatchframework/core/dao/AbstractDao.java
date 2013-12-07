package org.simplebatchframework.core.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.simplebatchframework.core.AbstractComponent;
import org.simplebatchframework.core.advice.AdviceProcessor;
import org.simplebatchframework.core.annotation.Inject;
import org.simplebatchframework.core.annotation.Transactional;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.IResult;
import org.simplebatchframework.core.bean.Result;
import org.simplebatchframework.core.exception.BatchRuntimeException;
import org.simplebatchframework.core.exception.BatchDataBaseRuntimeException;

public abstract class AbstractDao extends AbstractComponent implements IDao {

	@Inject
	protected AdviceProcessor adviceProcessor;

	@Override
	public IResult execute(IBean bean) {
		adviceProcessor.processBefore(this.getClass(), bean, null);
		IResult result = null;
		try {
			result = executeDao(bean);
		} catch (BatchRuntimeException e) {
			try {
				context.getConnection().rollback();
				logger.info("rollbacked");
			} catch (SQLException e2) {
			}
			throw e;
		}
		Transactional annotation = this.getClass().getAnnotation(Transactional.class);
		try {
			try {
				if (annotation != null && context.getConnection().getAutoCommit() == false) {
					context.getConnection().commit();
					logger.info("commited");
				}
			} catch (BatchRuntimeException e) {
				context.getConnection().rollback();
				logger.info("rollbacked");
				throw e;
			}
		} catch (SQLException e) {
			throw new BatchDataBaseRuntimeException(e);
		}
		adviceProcessor.processAfter(this.getClass(), null, result);
		return result;
	}

	@SuppressWarnings("unchecked")
	protected <T extends IBean> IResult executeDao(T bean) {
		Result<T> result = new Result<T>();
		if (this.getClass().getSimpleName().toLowerCase().contains("select")) {
			ResultSet resultSet = context.getDialect().executeQuery(bean,
					this.getClass());
			List<T> list = (List<T>) context.getDialect().resultSetToBean(
					resultSet, bean.getClass(), this.getClass());
			result.setBeanList(list);
		} else {
			int updateCount = context.getDialect().executeUpdate(bean,
					this.getClass());
			result.setUpdateCount(updateCount);
		}
		return result;
	}
}
