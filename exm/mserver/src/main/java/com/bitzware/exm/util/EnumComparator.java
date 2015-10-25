package com.bitzware.exm.util;

import java.util.Comparator;

public class EnumComparator<T extends Enum<?>> implements Comparator<T> {

	@Override
	public int compare(T o1, T o2) {
		return o1.name().compareTo(o2.name());
	}

}
