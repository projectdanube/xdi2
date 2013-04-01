package xdi2.core.util.iterators;

import java.util.Iterator;

public class WrappingIterator<T> extends IterableIterator<T> {

	private Iterator<T> iterator;

	public WrappingIterator(Iterator<T> iterator) {

		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {

		return this.iterator.hasNext();
	}

	@Override
	public T next() {

		return this.iterator.next();
	}

	@Override
	public void remove() {

		this.iterator.remove();
	}
}
