package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * A class that conveniently gives you the first element of an iterator, or null if it has no elements.
 * 
 * @author markus
 */
public final class IteratorFirstItem<T> {

	private T item;
	private Iterator<T> iterator;

	public IteratorFirstItem(Iterator<T> iterator) {

		if (iterator == null) throw new NullPointerException();

		this.item = null;
		this.iterator = iterator;
	}

	public T item() {

		if (this.item != null) return this.item;

		if (this.iterator.hasNext()) this.item = this.iterator.next();

		return this.item;
	}
}
