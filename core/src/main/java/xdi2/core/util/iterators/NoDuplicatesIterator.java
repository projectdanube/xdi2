package xdi2.core.util.iterators;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An iterator that returns all elements without duplicates.
 * This requires quite a bit of memory to keep track of elements that occurred already.
 * 
 * @author markus
 */
public class NoDuplicatesIterator<T> extends SelectingIterator<T> {

	private Set<T> items;

	public NoDuplicatesIterator(Iterator<T> iterator) {

		super(iterator);
	}

	@Override
	public boolean select(T item) {

		if (this.items == null) this.items = new HashSet<T> ();
		
		if (this.items.contains(item)) return false;

		this.items.add(item);

		return true;
	}
}
