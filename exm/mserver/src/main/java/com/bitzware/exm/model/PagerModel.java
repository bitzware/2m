package com.bitzware.exm.model;

import java.util.List;

/**
 * Class that contains model data for a 'pager' (table divided to pages).
 * 
 * @author finagle
 */
public class PagerModel<T> {

	private int resultsOnPage;
	private PagerSource<T> source;
	private int size;
	
	private int page;

	/**
	 * Constructor.
	 * 
	 * @param resultsOnPage maximum number of entries on one page.
	 * @param source data source.
	 */
	public PagerModel(final int resultsOnPage, final PagerSource<T> source) {
		this.resultsOnPage = resultsOnPage;
		this.source = source;
		this.size = source.getSize();
	}

	/**
	 * Returns total number of pages.
	 */
	public int getPagesCount() {
		int pagesCount = ((size + resultsOnPage - 1) / resultsOnPage);
		if (pagesCount < 1) {
			pagesCount = 1;
		}
		
		return pagesCount;
	}

	/**
	 * Returns current page (starting from 0).
	 */
	public int getPage() {
		return page;
	}

	/**
	 * Sets current page (starting from 0).
	 */
	public void setPage(final int page) {
		int pagesCount = getPagesCount();
		if (page >= pagesCount) {
			this.page = pagesCount - 1;
		} else {
			this.page = page;
		}
	}
	
	/**
	 * Returns the offset of the first displayed item.
	 */
	public int getOffset() {
		return page * resultsOnPage;
	}

	/**
	 * Returns total number of items (including those not displayed).
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Returns a list of displayed items.
	 */
	public List<T> getData() {
		return source.getData(page * resultsOnPage, resultsOnPage);
	}
	
}
