package xdi2.core.util.iterators;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class InsertableIterator<T> implements Iterator<T> {

	protected Iterator<T> iterator;
	protected Deque<Iterator<T>> iterators;
	protected boolean append;

	public InsertableIterator(Iterator<T> iterator, boolean append) {

		this.iterator = iterator;
		this.iterators = new ArrayDeque<Iterator<T>> ();
		this.append = append;
	}

	@Override
	public boolean hasNext() {

		this.refresh();

		return this.iterator.hasNext();
	}

	@Override
	public T next() {

		this.refresh();

		return this.iterator.next();
	}

	@Override
	public void remove() {

		this.refresh();

		this.iterator.remove();
	}

	public void insert(Iterator<T> iterator) {

		if (! iterator.hasNext()) return;

		if (this.append) {

			this.iterators.addLast(iterator);
		} else {

			this.iterators.addFirst(this.iterator);
			this.iterator = iterator;
		}
	}

	private void refresh() {

		while ((! this.iterator.hasNext()) && (! this.iterators.isEmpty())) {

			if (this.append) {

				this.iterator = this.iterators.removeFirst();
			} else {

				this.iterator = this.iterators.removeLast();
			}
		}
	}
}
