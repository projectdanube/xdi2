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

import java.util.Iterator;

/**
 * An iterator that reads elements from another iterator and maps them to its own elements.
 * 
 * I is the type of elements read by the iterator.
 * O is the type of elements returned by the iterator.
 * 
 * @author markus
 */
public abstract class MappingIterator<I, O> implements Iterator<O> {

	protected Iterator<I> iterator;

	public MappingIterator(Iterator<I> iterator) {

		this.iterator = iterator;
	}

	public boolean hasNext() {

		return this.iterator.hasNext();
	}

	public O next() {

		return this.map(this.iterator.next());
	}

	public void remove() {

		this.iterator.remove();
	}

	public abstract O map(I item);
}
