package org.simplebatchframework.core.file;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.simplebatchframework.core.IntegerComparator;
import org.simplebatchframework.core.Message;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.bean.annotation.OutputFile;
import org.simplebatchframework.core.exception.BatchFrameworkRuntimeException;
import org.simplebatchframework.core.exception.BatchRuntimeException;

public class CSVFileWriter extends AbstractFileWriter {
	public <T extends IBean, T2 extends IBean> void write(List<T> beans, String path, String fileName, String encodeType, boolean makeDirectory, Class<T2> beanClass) {
		if(beans.size() > 0) {
			if(!beans.get(0).getClass().equals(beanClass)) {
				throw new BatchFrameworkRuntimeException(Message.ERROR_INVALID_PARAMETER_TYPE + beans.get(0).getClass().toString() + "/" + beanClass.toString());
			}
			Field[] fields = beanClass.getDeclaredFields();
			Map<Integer, Field> columns = new TreeMap<Integer, Field>(new IntegerComparator());
			for(Field field : fields) {
				OutputFile in = field.getAnnotation(OutputFile.class);
				if(in != null) {
					columns.put(in.value(), field);
				}
			}
			List<String> data = new ArrayList<String>();
			for(IBean bean : beans) {
				StringBuilder line = new StringBuilder();
				for(Iterator<Map.Entry<Integer, Field>> it = columns.entrySet().iterator(); it.hasNext();) {
					Map.Entry<Integer, Field> entry = it.next();
					Integer column = entry.getKey();
					Field field = entry.getValue();
					if(column > columns.size()) throw new BatchRuntimeException(Message.ERROR_NOT_CORRECT_FIXED_LENGTH_FILE_DEFINITION + beanClass.toString());
					try {
						field.setAccessible(true);
						Object elem = field.get(bean);
						if(elem != null) {
							if(column != columns.size()) {
								line.append(elem.toString() + ",");
							} else {
								line.append(elem.toString());
							}
						} else {
							if(column != columns.size()) {
								line.append("NULL,");
							} else {
								line.append("NULL");
							}
						}
					} catch (IllegalArgumentException e) {
						throw new BatchFrameworkRuntimeException(e);
					} catch (IllegalAccessException e) {
						throw new BatchFrameworkRuntimeException(e);
					}
				}
				data.add(line.toString());
			}
			writeFile(data, path, fileName, encodeType, makeDirectory);
		}
	}
}
