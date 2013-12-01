package org.simplebatchframework.core.bean;

import java.util.List;

public class Result <T extends IBean> implements IResult {

	@Override
	public String toString() {
		return "Result [updateCount=" + updateCount + ", beanList=" + beanList
				+ "]";
	}

	private int updateCount;

	private List<T> beanList;

	public int getUpdateCount() {
		return updateCount;
	}

	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}

	public List<T> getBeanList() {
		return beanList;
	}

	public void setBeanList(List<T> beanList) {
		this.beanList = beanList;
	}
	
}
