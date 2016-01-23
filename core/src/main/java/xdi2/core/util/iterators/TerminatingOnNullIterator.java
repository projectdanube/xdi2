package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns elements of another iterator until a null element is hit.
 * 
 * In order for the hasNext() function to behave correctly, the iterator always looks ahead one
 * element.
 *  
 * @author markus
 */
public class TerminatingOnNullIterator<T> extends TerminatingIterator<T> {

	public TerminatingOnNullIterator(Iterator<T> iterator) {

		super(iterator);
	}

	@Override
	public boolean terminate(T item) {

		return item == null;
	}
}
