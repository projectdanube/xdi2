package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns only items of another iterator that satisfy a certain condition.
 *  
 * @author markus
 */
public abstract class SelectingIterator<T> extends LookaheadIterator<T> {

	protected Iterator<? extends T> iterator;

	public SelectingIterator(Iterator<? extends T> iterator) {

		this.iterator = iterator;

		this.lookahead();
	}

	@Override
	protected void lookahead() {

		this.hasNext = false;

		while (this.iterator.hasNext()) {

			T item = this.iterator.next();

			if (this.select(item)) {

				this.hasNext = true;
				this.nextItem = item;
				break;
			}
		}
	}

	public abstract boolean select(T item);
}
