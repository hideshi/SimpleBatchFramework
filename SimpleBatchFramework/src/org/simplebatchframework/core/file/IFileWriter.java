package org.simplebatchframework.core.file;

import java.util.List;

import org.simplebatchframework.core.bean.IBean;

public interface IFileWriter {
	public <T extends IBean, T2 extends IBean> void write(List<T> beans, String path, String fileName, String encodeType, boolean makeDirectory, Class<T2> beanClass);
}
