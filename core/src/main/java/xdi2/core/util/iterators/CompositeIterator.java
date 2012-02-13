package xdi2.core.util.iterators;

import java.util.Arrays;
import java.util.Iterator;

/**
 * An iterator that iterates over all items of multiple other iterators.
 * 
 * @author markus
 */
public class CompositeIterator<T> extends DescendingIterator<Iterator<T>, T> {

	protected Iterator<Iterator<T>> iterators;
	protected Iterator<T> currentIterator;
	protected boolean allowRemove;

	public CompositeIterator(Iterator<Iterator<T>> iterators) {

		super(iterators);
	}

	public CompositeIterator(Iterator<T>... iterators) {

		this(Arrays.asList(iterators).iterator());
	}

	@Override
	public Iterator<T> descend(Iterator<T> item) {

		return item;
	}
}
