package org.simplebatchframework.core.advice;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.simplebatchframework.core.AbstractComponent;
import org.simplebatchframework.core.Message;
import org.simplebatchframework.core.annotation.After;
import org.simplebatchframework.core.annotation.Before;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.IResult;
import org.simplebatchframework.core.exception.BatchFrameworkRuntimeException;
import org.simplebatchframework.core.exception.BatchRuntimeException;

public class AdviceProcessor extends AbstractComponent {
	
	@SuppressWarnings("rawtypes")
	private <T extends IBean> void setContext(AbstractAdvice advice, Class adviceClass, Class componentClass, T conditionBean, IResult result) throws IllegalArgumentException, IllegalAccessException, SecurityException, NoSuchFieldException {
		Field[] fields = adviceClass.getDeclaredFields();
		for(Field field : fields) {
			if("context".equals(field.getName())) {
				field.set(advice, context);
			}
			if("logger".equals(field.getName())) {
				field.set(advice, Logger.getLogger(componentClass));
			}
			if("conditionBean".equals(field.getName())) {
				field.set(advice, conditionBean);
			}
			if("result".equals(field.getName())) {
				field.set(advice, result);
			}
		}
		if(adviceClass.getSuperclass() != null) {
			setContext(advice, adviceClass.getSuperclass(), componentClass, conditionBean, result);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent, T2 extends IBean> void processBefore(Class<T> componentClass, T2 conditionBean, IResult result) {
		try {
			Before before = componentClass.getAnnotation(Before.class);
			if(before != null) {
				String[] beforeAdviceKeys = before.value();
				for(String beforeAdviceKey : beforeAdviceKeys) {
					String beforeAdviceName = context.getConfiguration().getProperty(beforeAdviceKey);
					if(beforeAdviceName == null) throw new BatchRuntimeException(Message.ERROR_CONFIGURATION_NOT_CONTAIN + beforeAdviceKey);
					Class<AbstractAdvice> beforeAdviceClass = (Class<AbstractAdvice>) Class.forName(beforeAdviceName);
					AbstractAdvice advice = beforeAdviceClass.newInstance();
					setContext(advice, beforeAdviceClass, componentClass, conditionBean, result);
					advice.execute();
				}
			}
		} catch (ClassNotFoundException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (InstantiationException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (SecurityException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new BatchFrameworkRuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractComponent, T2 extends IBean> void processAfter(Class<T> componentClass, T2 conditionBean, IResult result) {
		try {
			After after = componentClass.getAnnotation(After.class);
			if(after != null) {
				String[] afterAdviceKeys = after.value();
				for(String afterAdviceKey : afterAdviceKeys) {
					String afterAdviceName = context.getConfiguration().getProperty(afterAdviceKey);
					if(afterAdviceName == null) throw new BatchRuntimeException(Message.ERROR_CONFIGURATION_NOT_CONTAIN + afterAdviceKey);
					Class<AbstractAdvice> afterAdviceClass = (Class<AbstractAdvice>) Class.forName(afterAdviceName);
					AbstractAdvice advice = afterAdviceClass.newInstance();
					setContext(advice, afterAdviceClass, componentClass, conditionBean, result);
					advice.execute();
				}
			}
		} catch (ClassNotFoundException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (InstantiationException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (SecurityException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (NoSuchFieldException e) {
			throw new BatchFrameworkRuntimeException(e);
		}
	}
}
