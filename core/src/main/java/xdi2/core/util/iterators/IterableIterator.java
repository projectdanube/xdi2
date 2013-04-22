package xdi2.core.util.iterators;

import java.util.Iterator;

public abstract class IterableIterator<T> implements Iterator<T>, Iterable<T> {

	@Override
	public final Iterator<T> iterator() {

		return this;
	}
}
