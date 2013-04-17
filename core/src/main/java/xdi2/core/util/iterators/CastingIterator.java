package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that doesn't alter any elements but casts them to a desired type.
 *  
 * @author markus
 */
public class CastingIterator<I, O> extends IterableIterator<O> {

	private Iterator<? extends I> iterator;
	private Class<? extends O> o;
	private boolean safe;

	public CastingIterator(Iterator<? extends I> iterator, Class<? extends O> o, boolean safe) {

		this.iterator = iterator;
		this.o = o;
		this.safe = safe;
	}

	public CastingIterator(Iterator<? extends I> iterator) {

		this(iterator, null, false);
	}

	@Override
	public boolean hasNext() {

		return this.iterator.hasNext();
	}

	@Override
	@SuppressWarnings("unchecked")
	public O next() {

		Object next = this.iterator.next();

		if (this.isSafe() && (! this.getO().isAssignableFrom(next.getClass()))) return null;

		return (O) next;
	}

	@Override
	public void remove() {

		this.iterator.remove();
	}

	public Class<? extends O> getO() {

		return this.o;
	}

	public boolean isSafe() {

		return this.safe;
	}
}
