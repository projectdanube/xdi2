package xdi2.core.util.iterators;

import java.util.Arrays;
import java.util.Iterator;

/**
 * An iterator that iterates over all items of multiple other iterators.
 * 
 * @author markus
 */
public class CompositeIterator<T> extends DescendingIterator<Iterator<? extends T>, T> {

	public CompositeIterator(Iterator<Iterator<? extends T>> iterators) {

		super(iterators);
	}

	public CompositeIterator(Iterator<? extends T>... iterators) {

		this(Arrays.asList(iterators).iterator());
	}

	@Override
	public Iterator<T> descend(Iterator<? extends T> item) {

		return new CastingIterator<T, T> (item);
	}
}
