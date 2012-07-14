package xdi2.core.util.iterators;


/**
 * An iterator that has no elements.
 * 
 * @author markus
 */
public class EmptyIterator<T> extends ReadOnlyIterator<T> {

	public EmptyIterator() {

	}

	@Override
	public boolean hasNext() {

		return false;
	}

	@Override
	public T next() {

		return null;
	}
}