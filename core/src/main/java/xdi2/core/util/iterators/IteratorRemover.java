package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * A class that can remove an element from an Iterator.
 * 
 * @author markus
 */
public final class IteratorRemover<T> {

	private Iterator<T> iterator;
	private T element;

	public IteratorRemover(Iterator<T> iterator, T element) {

		if (iterator == null) throw new NullPointerException();

		this.iterator = iterator;
		this.element = element;
	}

	/**
	 * Remove the element from the iterator.
	 */
	public void remove() {

		while (this.iterator.hasNext()) {

			if (this.iterator.next().equals(this.element)) {

				this.iterator.remove();
			}
		}
	}
}
