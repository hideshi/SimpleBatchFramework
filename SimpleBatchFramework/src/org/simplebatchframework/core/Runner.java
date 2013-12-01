package org.simplebatchframework.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.simplebatchframework.core.annotation.Inject;
import org.simplebatchframework.core.annotation.ValidateLength;
import org.simplebatchframework.core.annotation.Param;
import org.simplebatchframework.core.annotation.ValidateRegexp;
import org.simplebatchframework.core.dao.dialect.IDialect;
import org.simplebatchframework.core.exception.BatchFrameworkRuntimeException;
import org.simplebatchframework.core.exception.BatchRuntimeException;
import org.simplebatchframework.core.job.IJob;

public class Runner {

	protected static CommonContext context;

	@SuppressWarnings("unchecked")
	public static void run(String[] params) {
		assert(params.length >= 1);

		String jobClassKey = initialToLowerCase(params[0]) + "Job";
		String contextClassKey = initialToLowerCase(params[0]) + "Context";

		try {
			// Read property file
			InputStreamReader inStream = new InputStreamReader(new FileInputStream(new File(StaticString.PROPERTIES_FILE)), CharacterCode.UTF_8.toString());
			Properties configuration = new Properties();
			configuration.load(inStream);

			// Set parameters to context
			String contextClassName = configuration.getProperty(contextClassKey);
			if(contextClassName == null) throw new BatchRuntimeException(Message.ERROR_CONFIGURATION_NOT_CONTAIN + contextClassKey);
			Class<CommonContext> contextClass = (Class<CommonContext>) Class.forName(contextClassName);
			context = contextClass.newInstance();
			context.setConfiguration(configuration);
			List<String> invalidParameters = setContextFields(contextClass, context, params);
			if(invalidParameters.size() > 0) throw new BatchRuntimeException(invalidParameters.toString());

			// Set dialect to context
			String dialectClassName = configuration.getProperty(StaticString.DIALECT);
			if(dialectClassName != null) {
				Class<IDialect> dialectClass = (Class<IDialect>) Class.forName(dialectClassName);
				IDialect dialect = dialectClass.newInstance();
				injectInstances(dialectClass, dialect, context);
				context.setDialect(dialect);
			}

			// Instantiate job class
			String jobClassName = configuration.getProperty(jobClassKey);
			if(jobClassName == null) throw new BatchRuntimeException(Message.ERROR_CONFIGURATION_NOT_CONTAIN + jobClassKey);
			Class<IJob> jobClass = (Class<IJob>) Class.forName(jobClassName);
			IJob job = jobClass.newInstance();

			// Inject instances into job, logics and daos
			injectInstances(jobClass, job, context);

			// Execute job
			job.execute();

		} catch(ClassNotFoundException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch(IllegalAccessException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch(InstantiationException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch(FileNotFoundException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch(IOException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch (SecurityException e) {
			throw new BatchFrameworkRuntimeException(e);
		} finally {

		}
	}

	@SuppressWarnings("rawtypes")
	private static List<String> setContextFields(Class clazz, Object obj, String[] params) throws IllegalArgumentException, IllegalAccessException {
		return setContextFields(clazz, obj, params, new ArrayList<String>());
	}

	@SuppressWarnings("rawtypes")
	private static List<String> setContextFields(Class clazz, Object obj, String[] params, List<String> invalidParameters) throws IllegalArgumentException, IllegalAccessException {
		Field[] contextFields = clazz.getDeclaredFields();
		for(Field contextField : contextFields) {
			Param param = contextField.getAnnotation(Param.class);
			if(param != null) {
				if(param.column() >= params.length) throw new BatchRuntimeException(Message.ERROR_INVALID_PARAMETERS_SIZE + String.valueOf(param.column()));
				ValidateLength lengthValidator = contextField.getAnnotation(ValidateLength.class);
				if(lengthValidator != null) {
					contextField.setAccessible(true);
					int length = params[param.column()].length();
					if(length > lengthValidator.maxLength() || length < lengthValidator.minLength()) {
						invalidParameters.add(Message.ERROR_INVALID_PARAMETER_LENGTH + "param" + String.valueOf(param.column()) + "=" + params[param.column()]);
					}
					contextField.set(obj, params[param.column()]);
				}
				ValidateRegexp regexpParam = contextField.getAnnotation(ValidateRegexp.class);
				if(regexpParam != null) {
					contextField.setAccessible(true);
					Pattern pattern = Pattern.compile(regexpParam.regexp());
					Matcher matcher = pattern.matcher(params[param.column()]);
					if(!matcher.matches()) {
						invalidParameters.add(Message.ERROR_INVALID_PARAMETER_TYPE + "param" + String.valueOf(param.column()) + "=" + params[param.column()]);
					}
					contextField.set(obj, params[param.column()]);
				}
			}
		}
		if(clazz.getSuperclass() != null) {
			setContextFields(clazz.getSuperclass(), obj, params, invalidParameters);
		}
		return invalidParameters;
	}

	@SuppressWarnings("rawtypes")
	private static void injectInstances(Class clazz, Object obj, CommonContext context) throws IllegalArgumentException, IllegalAccessException, InstantiationException, ClassNotFoundException {
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			Inject inject = field.getAnnotation(Inject.class);
			if(inject != null) {
				String className = context.getConfiguration().getProperty(field.getName());
				field.setAccessible(true);
				if(StaticString.CONTEXT.equals(field.getName())) {
					field.set(obj, context);
				} else if(className != null) {
					Class clazz2 = Class.forName(className);
					field.set(obj, clazz2.newInstance());
					Object obj2 = field.get(obj);
					injectInstances(clazz2, obj2, context);
				} else {
					throw new BatchRuntimeException(Message.ERROR_CONFIGURATION_NOT_CONTAIN + field.getName());
				}
			}
		}
		if(clazz.getSuperclass() != null) {
			injectInstances(clazz.getSuperclass(), obj, context);
		}
	}

	private static String initialToLowerCase(String str) {
		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}
}
