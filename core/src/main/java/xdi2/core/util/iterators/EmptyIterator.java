package xdi2.core.util.iterators;

import java.util.NoSuchElementException;


/**
 * An iterator that has no elements.
 * 
 * @author markus
 */
public class EmptyIterator<T> extends ReadOnlyIterator<T> {

	public EmptyIterator() {

		super(null);
	}

	@Override
	public boolean hasNext() {

		return false;
	}

	@Override
	public T next() {

		throw new NoSuchElementException();
	}
}