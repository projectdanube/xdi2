/*******************************************************************************
 * Copyright (c) 2008 Parity Communications, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Markus Sabadello - Initial API and implementation
 *******************************************************************************/
package xdi2.util.iterators;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.higgins.xdi4j.util.iterators.DescendingIterator;

/**
 * An iterator that iterates over all items of multiple other iterators.
 * 
 * @author msabadello at parityinc dot net
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

		return(item);
	}
}
