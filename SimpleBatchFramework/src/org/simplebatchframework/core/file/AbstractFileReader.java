package org.simplebatchframework.core.file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.simplebatchframework.core.AbstractComponent;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.exception.BatchFileIORuntimeException;

public abstract class AbstractFileReader extends AbstractComponent implements IFileReader {

	@Override
	public abstract <T extends IBean> List<T> read(String path, String fileName, String encodeType, Class<T> beanClass);

	protected List<String> readFile(String path, String fileName, String encodeType) {
		List<String> data = new ArrayList<String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(path + fileName), encodeType));
			String line;
			while((line = reader.readLine()) != null) {
				data.add(line);
			}
		} catch(FileNotFoundException e) {
			throw new BatchFileIORuntimeException(e);
		} catch(IOException e) {
			throw new BatchFileIORuntimeException(e);
		} finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}
		return data;
	}
}
