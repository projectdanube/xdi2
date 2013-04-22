package xdi2.core.util.iterators;

import java.util.NoSuchElementException;

/**
 * An iterator that contains only a single item.
 * 
 * @author markus
 */
public class SingleItemIterator<T> extends ReadOnlyIterator<T> {

	private boolean hasNext;
	private T item;
	
	public SingleItemIterator(T item) {

		super(null);
		
		this.hasNext = true;
		this.item = item;
	}

	@Override
	public boolean hasNext() {

		return this.hasNext;
	}

	@Override
	public T next() {

		if (! this.hasNext) throw new NoSuchElementException();
		
		T item = this.item;

		this.hasNext = false;
		
		return item;
	}
}