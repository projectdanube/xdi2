package xdi2.core.util.iterators;

import java.util.NoSuchElementException;

/**
 * An iterator that always looks ahead one item.
 *  
 * @author markus
 */
public abstract class LookaheadIterator<T> extends ReadOnlyIterator<T> {

	protected boolean hasNext;
	protected T nextItem;

	public LookaheadIterator() {

		super(null);
	}

	@Override
	public boolean hasNext() {

		return this.hasNext;
	}

	@Override
	public T next() {

		if (! this.hasNext) throw new NoSuchElementException();

		T element = this.nextItem;

		this.lookahead();

		return element;
	}

	protected abstract void lookahead();
}
