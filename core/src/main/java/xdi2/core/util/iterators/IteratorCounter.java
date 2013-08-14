package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * A class that can count the number of elements in an Iterator.
 * 
 * @author markus
 */
public final class IteratorCounter {

	private long count;
	private Iterator<?> iterator;

	public IteratorCounter(Iterator<?> iterator) {

		if (iterator == null) throw new NullPointerException();

		this.count = -1;
		this.iterator = iterator;
	}

	/**
	 * Counts and returns the number of elements in an Iterator.
	 * @return The number of elements.
	 */
	public long count() {

		if (this.count != -1) return this.count;

		this.count = 0;
		while (this.iterator.hasNext()) {

			this.iterator.next();
			this.count++;
		}

		return this.count;
	}
}
