package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that does not support the remove() method.
 *  
 * @author markus
 */
public class ReadOnlyIterator<T> extends WrappingIterator<T> {

	public ReadOnlyIterator(Iterator<T> iterator) {

		super(iterator);
	}

	@Override
	public final void remove() {

		throw new UnsupportedOperationException("remove() is not supported.");
	}
}
