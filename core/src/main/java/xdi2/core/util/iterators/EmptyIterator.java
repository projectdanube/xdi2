package xdi2.core.util.iterators;


/**
 * An iterator that has no elements.
 * 
 * @author markus
 */
public class EmptyIterator<T> extends ReadOnlyIterator<T> {

	public EmptyIterator() {

	}

	public boolean hasNext() {

		return false;
	}

	public T next() {

		return null;
	}
}