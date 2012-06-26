package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that reads elements from one iterator and maps them to other elements.
 * 
 * @author markus
 */
public abstract class MappingIterator<I, O> implements Iterator<O> {

	protected Iterator<I> iterator;

	public MappingIterator(Iterator<I> iterator) {

		this.iterator = iterator;
	}

	public boolean hasNext() {

		return this.iterator.hasNext();
	}

	public O next() {

		return this.map(this.iterator.next());
	}

	public void remove() {

		this.iterator.remove();
	}

	public abstract O map(I item);
}
