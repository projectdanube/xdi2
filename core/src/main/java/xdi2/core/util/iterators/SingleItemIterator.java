package xdi2.core.util.iterators;

/**
 * An iterator that contains only a single item.
 * 
 * @author markus
 */
public class SingleItemIterator<T> extends ReadOnlyIterator<T> {

	private T item;
	
	public SingleItemIterator(T item) {

		this.item = item;
	}

	public boolean hasNext() {

		return this.item != null;
	}

	public T next() {

		T item = this.item;
		this.item = null;
		
		return item;
	}
}