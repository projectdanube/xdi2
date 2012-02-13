package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that can iterate over D, and for every item in D, an iterator over items of type T is
 * obtained. In the end, all Ts obtained from all Ds are returned.
 * 
 * @author markus
 */
public abstract class DescendingIterator<D, T> extends ReadOnlyIterator<T> {

	protected Iterator<D> d;
	protected Iterator<T> t;

	public DescendingIterator(Iterator<D> iterator) {

		this.d = iterator;
		this.t = null;
	}

	public boolean hasNext() {

		while (this.t == null || ! this.t.hasNext()) {

			if (! this.d.hasNext()) return false;
			this.t = this.descend(this.d.next());
		}

		return true;
	}

	public T next() {

		while (this.t == null || ! this.t.hasNext()) {

			if (! this.d.hasNext()) return null;
			this.t = this.descend(this.d.next());
		}

		return this.t.next();
	}

	public abstract Iterator<T> descend(D item);
}
