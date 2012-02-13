package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that does not support the remove() method.
 *  
 * @author markus
 */
public abstract class ReadOnlyIterator<T> implements Iterator<T> {

	public void remove() {

		throw new RuntimeException("remove() is not supported.");
	}
}
