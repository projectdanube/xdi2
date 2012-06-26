package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that doesn't alter any elements but casts them to a desired type.
 *  
 * @author markus
 */
public class CastingIterator<I, O> implements Iterator<O> {

	private Iterator<I> iterator;

	public CastingIterator(Iterator<I> iterator) {

		this.iterator = iterator;
	}

	public boolean hasNext() {

		return this.iterator.hasNext();
	}

	@SuppressWarnings("unchecked")
	public O next() {

		return (O) this.iterator.next();
	}

	public void remove() {

		this.iterator.remove();
	}
}
