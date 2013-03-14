package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that does not support the remove() method.
 *  
 * @author markus
 */
public class ReadOnlyIterator<T> extends IterableIterator<T> {

	private Iterator<T> iterator;

	public ReadOnlyIterator(Iterator<T> iterator) {

		this.iterator = iterator;
	}

	protected ReadOnlyIterator() {

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

		throw new UnsupportedOperationException("remove() is not supported.");
	}

	@Override
	public Iterator<T> iterator() {

		return this;
	}
}
