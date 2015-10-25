package com.bitzware.exm.util;

import java.util.Iterator;

public class TransformCollection<S, T> implements Iterable<T> {

	private class TransformIterator implements Iterator<T> {
		
		private Iterator<S> itSource;

		public TransformIterator(final Iterator<S> itSource) {
			this.itSource = itSource;
		}

		@Override
		public boolean hasNext() {
			return itSource.hasNext();
		}

		@Override
		public T next() {
			return transformer.transform(itSource.next());
		}

		@Override
		public void remove() {
			itSource.remove();
		}
		
	}
	
	private Iterable<S> source;
	private Transformer<S, T> transformer;

	public TransformCollection(final Iterable<S> source, final Transformer<S, T> transformer) {
		this.source = source;
		this.transformer = transformer;
	}

	@Override
	public Iterator<T> iterator() {
		return new TransformIterator(source.iterator());
	}
	
}
