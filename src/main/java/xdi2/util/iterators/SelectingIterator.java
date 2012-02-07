package xdi2.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns only elements of another iterator that satisfy a certain condition.
 * 
 * In order for the hasNext() function to behave correctly, the iterator always looks ahead one
 * element.
 *  
 * @author markus
 */
public abstract class SelectingIterator<T> extends ReadOnlyIterator<T> {

	protected Iterator<T> iterator;
	protected T nextElement;

	public SelectingIterator(Iterator<T> iterator) {

		this.iterator = iterator;
		
		this.lookahead();
	}

	public boolean hasNext() {

		return(this.nextElement != null);
	}

	public T next() {

		T element = this.nextElement;

		this.lookahead();

		return(element);
	}

	protected void lookahead() {

		this.nextElement = null;

		while (this.iterator.hasNext()) {

			T element = this.iterator.next();

			if (this.select(element)) {

				this.nextElement = element;
				break;
			}
		}
	}

	public abstract boolean select(T item);
}
