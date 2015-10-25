package com.bitzware.exm.model;

import java.util.List;

/**
 * Data source of a pager.
 * 
 * @author finagle
 */
public interface PagerSource<T> {

	/**
	 * Returns total number of elements.
	 */
	int getSize();
	
	/**
	 * Returns a list of elements.
	 * 
	 * @param offset index of the first returned element.
	 * @param limit maximum number of retrieved elements.
	 */
	List<T> getData(int offset, int limit);
	
}
