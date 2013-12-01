package org.simplebatchframework.core.file;

import java.util.List;

import org.simplebatchframework.core.bean.IBean;

public interface IFileReader {
	public <T extends IBean> List<T> read(String path, String fileName, String encodeType, Class<T> beanClass);
}
