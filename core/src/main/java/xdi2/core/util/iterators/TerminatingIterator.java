package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns elements of another iterator until a certain condition is satisfied.
 * 
 * In order for the hasNext() function to behave correctly, the iterator always looks ahead one
 * element.
 *  
 * @author markus
 */
public abstract class TerminatingIterator<T> implements Iterator<T> {

	protected Iterator<T> iterator;
	protected T nextElement;

	public TerminatingIterator(Iterator<T> iterator) {

		this.iterator = iterator;
		
		this.lookahead();
	}

	public boolean hasNext() {

		return this.nextElement != null;
	}

	public T next() {

		T element = this.nextElement;

		this.lookahead();

		return element;
	}

	public void remove() {

		throw new RuntimeException("Removing not supported.");
	}

	protected void lookahead() {

		this.nextElement = null;

		if (! this.iterator.hasNext()) return;
		
		T element = this.iterator.next();
		if (this.terminate(element)) return;

		this.nextElement = element;
	}

	public abstract boolean terminate(T item);
}
