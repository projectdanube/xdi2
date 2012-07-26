package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns only items of another iterator that satisfy a certain condition.
 * In addition, it maps the items to other items.
 * 
 * @author markus
 */
public abstract class SelectingMappingIterator<I, O> extends LookaheadIterator<O> {

	protected Iterator<I> iterator;

	public SelectingMappingIterator(Iterator<I> iterator) {

		this.iterator = iterator;

		this.lookahead();
	}

	@Override
	protected void lookahead() {

		this.hasNext = false;

		while (this.iterator.hasNext()) {

			I item = this.iterator.next();

			if (this.select(item)) {

				this.hasNext = true;
				this.nextItem = this.map(item);
				break;
			}
		}
	}

	public abstract boolean select(I item);

	public abstract O map(I item);
}
