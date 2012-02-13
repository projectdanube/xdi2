package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns only all non-null elements from another iterator.
 * 
 * @author markus
 */
public class NotNullIterator<T> extends SelectingIterator<T> {

	public NotNullIterator(Iterator<T> iterator) {

		super(iterator);
	}

	@Override
	public boolean select(T item) {

		return item != null;
	}
}
