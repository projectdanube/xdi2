package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns items of another iterator until a certain condition is satisfied.
 * 
 * In order for the hasNext() function to behave correctly, the iterator always looks ahead one item.
 *  
 * @author markus
 */
public abstract class TerminatingIterator<T> extends LookaheadIterator<T> {

	protected Iterator<T> iterator;

	public TerminatingIterator(Iterator<T> iterator) {

		this.iterator = iterator;

		this.lookahead();
	}

	@Override
	protected void lookahead() {

		this.hasNext = false;

		if (! this.iterator.hasNext()) return;

		T item = this.iterator.next();
		if (this.terminate(item)) return;

		this.hasNext = true;
		this.nextItem = item;
	}

	public abstract boolean terminate(T item);
}
