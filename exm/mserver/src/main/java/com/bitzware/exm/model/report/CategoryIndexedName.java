package com.bitzware.exm.model.report;

/**
 * Used for chart datasets.
 * 
 * @author finagle
 */
public class CategoryIndexedName implements Comparable<CategoryIndexedName> {
	
	private final int index;
	private final String value;
	
	public CategoryIndexedName(final int index, final String value) {
		this.index = index;
		this.value = value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public int compareTo(final CategoryIndexedName o) {
		return index - o.index;
	}

}
