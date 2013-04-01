package xdi2.core.util.iterators;

/**
 * An iterator that reads elements in a certain range.
 * 
 * @author markus
 */
public abstract class RangeIterator<T> extends ReadOnlyIterator<T> {

	protected int current, to;

	public RangeIterator(int from, int to) {

		super(null);

		this.current = from;
		this.to = to;
	}

	@Override
	public boolean hasNext() {

		return this.current <= this.to;
	}

	@Override
	public T next() {

		return this.get(this.current++);
	}

	public abstract T get(int current);
}
