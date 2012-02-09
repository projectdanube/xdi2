package xdi2.util.iterators;

import java.util.Iterator;

/**
 * An iterator that doesn't alter any elements but casts them to a desired type.
 *  
 * @author markus
 */
public class CastingIterator<T> implements Iterator<T> {

	protected Iterator<?> iterator;

	public CastingIterator(Iterator<?> iterator) {

		this.iterator = iterator;
	}

	public boolean hasNext() {

		return this.iterator.hasNext();
	}

	@SuppressWarnings("unchecked")
	public T next() {

		return (T) this.iterator.next();
	}

	public void remove() {

		this.iterator.remove();
	}
}
