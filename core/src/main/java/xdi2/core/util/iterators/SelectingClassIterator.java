package xdi2.core.util.iterators;

import java.util.Iterator;

/**
 * An iterator that returns only elements of a certain class.
 *  
 * @author markus
 */
public class SelectingClassIterator<I, O> extends CastingIterator<I, O> {

	public SelectingClassIterator(Iterator<I> iterator, final Class<O> clazz) {

		super(new SelectingIterator<I> (iterator) {

			@Override
			public boolean select(I item) {

				return clazz.isAssignableFrom(item.getClass());
			}
		});
	}
}
