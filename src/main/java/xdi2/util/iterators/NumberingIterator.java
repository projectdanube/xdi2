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

import org.eclipse.higgins.xdi4j.util.iterators.ReadOnlyIterator;


/**
 * An iterator that reads elements from another iterator and maps them to its own elements.
 * 
 * I is the type of elements read by the iterator.
 * O is the type of elements returned by the iterator.
 * 
 * @author msabadello at parityinc dot net
 */
public abstract class NumberingIterator<T> extends ReadOnlyIterator<T> {

	protected int current, to;

	public NumberingIterator(int from, int to) {

		this.current = from;
		this.to = to;
	}

	public boolean hasNext() {

		return(this.current <= this.to);
	}

	public T next() {

		return(this.get(this.current++));
	}

	public abstract T get(int current);
}
