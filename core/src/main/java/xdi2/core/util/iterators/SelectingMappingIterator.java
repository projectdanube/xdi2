package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns only elements of another iterator that satisfy a certain condition.
 * In addition, it maps the elements to other elements.
 * 
 * In order for the hasNext() function to behave correctly, the iterator always looks ahead one
 * element.
 * 
 * @author markus
 */
public abstract class SelectingMappingIterator<I, O> extends ReadOnlyIterator<O> {

	protected Iterator<I> iterator;
	protected O nextElement;

	public SelectingMappingIterator(Iterator<I> iterator) {

		this.iterator = iterator;

		this.lookahead();
	}

	@Override
	public boolean hasNext() {

		return this.nextElement != null;
	}

	@Override
	public O next() {

		O element = this.nextElement;

		this.lookahead();

		return element;
	}

	protected void lookahead() {

		this.nextElement = null;

		while (this.iterator.hasNext()) {

			I element = this.iterator.next();

			if (this.select(element)) {

				this.nextElement = this.map(element);
				break;
			}
		}
	}

	public abstract boolean select(I item);

	public abstract O map(I item);
}
