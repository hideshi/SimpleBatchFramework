package org.simplebatchframework.core.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.simplebatchframework.core.AbstractComponent;
import org.simplebatchframework.core.bean.IBean;
import org.simplebatchframework.core.exception.BatchFileIORuntimeException;

public abstract class AbstractFileWriter extends AbstractComponent implements IFileWriter {

	@Override
	public abstract <T extends IBean, T2 extends IBean> void write(List<T> beans, String path, String fileName, String encodeType, boolean makeDirectory, Class<T2> beanClass);

	protected void writeFile(List<String> data, String path, String fileName, String encodeType, boolean makeDirectory) {
		File directory = new File(path);
		if(makeDirectory && !directory.exists()) directory.mkdirs();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path + fileName), encodeType));
			for(String line : data) {
				writer.write(line);
				writer.newLine();
			}
			writer.flush();
		} catch (UnsupportedEncodingException e) {
			throw new BatchFileIORuntimeException(e);
		} catch (FileNotFoundException e) {
			throw new BatchFileIORuntimeException(e);
		} catch (IOException e) {
			throw new BatchFileIORuntimeException(e);
		} finally {
			if(writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
