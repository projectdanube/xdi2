package xdi2.core.util.iterators;



/**
 * An iterator that reads elements from another iterator and maps them to its own elements.
 * 
 * I is the type of elements read by the iterator.
 * O is the type of elements returned by the iterator.
 * 
 * @author markus
 */
public abstract class NumberingIterator<T> extends ReadOnlyIterator<T> {

	protected int current, to;

	public NumberingIterator(int from, int to) {

		this.current = from;
		this.to = to;
	}

	public boolean hasNext() {

		return this.current <= this.to;
	}

	public T next() {

		return this.get(this.current++);
	}

	public abstract T get(int current);
}
