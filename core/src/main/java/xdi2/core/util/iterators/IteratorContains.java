package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * A class that can check if an Iterator contains an element.
 * 
 * @author markus
 */
public final class IteratorContains<T> {

	private Boolean contains;
	private Iterator<?> iterator;
	private T element;

	public IteratorContains(Iterator<T> iterator, T element) {

		if (iterator == null) throw new NullPointerException();

		this.contains = null;
		this.iterator = iterator;
		this.element = element;
	}

	/**
	 * Checks if the Iterator contains an element.
	 * @return The number of elements.
	 */
	public boolean contains() {

		if (this.contains != null) return this.contains.booleanValue();

		while (this.iterator.hasNext()) {

			if (this.iterator.next().equals(this.element)) {

				this.contains = Boolean.TRUE;
				return true;
			}
		}

		this.contains = Boolean.FALSE;
		return false;
	}
}
