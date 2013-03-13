package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that doesn't alter any elements but casts them to a desired type.
 *  
 * @author markus
 */
public class CastingIterator<I, O> implements Iterator<O> {

	private Iterator<? extends I> iterator;

	public CastingIterator(Iterator<? extends I> iterator) {

		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {

		return this.iterator.hasNext();
	}

	@Override
	@SuppressWarnings("unchecked")
	public O next() {

		return (O) this.iterator.next();
	}

	@Override
	public void remove() {

		this.iterator.remove();
	}
}
