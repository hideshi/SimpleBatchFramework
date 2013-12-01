package org.simplebatchframework.core.file;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.simplebatchframework.core.IntegerComparator;
import org.simplebatchframework.core.Message;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.annotation.InputFile;
import org.simplebatchframework.core.exception.BatchFrameworkRuntimeException;
import org.simplebatchframework.core.exception.BatchRuntimeException;

/**
 * Supported Type : String, Integer, Long, BigDecimal
 */
public class CSVFileReader extends AbstractFileReader {
	public <T extends IBean> List<T> read(String path, String fileName, String encodeType, Class<T> beanClass) {
		List<String> data = readFile(path, fileName,encodeType);
		Field[] fields = beanClass.getDeclaredFields();
		Map<Integer, Field> columns = new TreeMap<Integer, Field>(new IntegerComparator());
		for(Field field : fields) {
			InputFile in = field.getAnnotation(InputFile.class);
			if(in != null) {
				columns.put(in.value(), field);
			}
		}
		List<T> result = new ArrayList<T>();
		try {
			for(String line : data) {
				T bean = beanClass.newInstance();
				String[] separatedLine = line.split(",");
				if(separatedLine.length == 0) continue;
				if(separatedLine.length != columns.size()) throw new BatchRuntimeException(Message.ERROR_NOT_CORRECT_CSV_FILE_DATA + line);
				for(Iterator<Map.Entry<Integer, Field>> it = columns.entrySet().iterator(); it.hasNext();) {
					Map.Entry<Integer, Field> entry = it.next();
					Integer column = entry.getKey();
					Field field = entry.getValue();
					if(column > columns.size()) throw new BatchRuntimeException(Message.ERROR_NOT_CORRECT_FIXED_LENGTH_FILE_DEFINITION + beanClass.toString());
					Class<?> fieldClass = field.getType();
					Constructor<?> constructor = fieldClass.getConstructor(String.class);
					Object fieldValue = null;
					if(!"NULL".equals(separatedLine[column - 1])) {
						fieldValue = constructor.newInstance(separatedLine[column - 1]);
					}
					field.setAccessible(true);
					field.set(bean, fieldValue);
				}
				result.add(bean);
			}
		} catch(IllegalAccessException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch(InstantiationException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch(NoSuchMethodException e) {
			throw new BatchFrameworkRuntimeException(e);
		} catch(InvocationTargetException e) {
			throw new BatchFrameworkRuntimeException(e);
		}
		return result;
	}
}
