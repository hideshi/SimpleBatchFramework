package org.simplebatchframework.core;

import java.util.Comparator;

public class IntegerComparator implements Comparator<Integer> {

	@Override
	public int compare(Integer i1, Integer i2) {
		return i1 - i2;
	}
}
