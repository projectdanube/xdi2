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
 * An iterator that doesn't alter any elements but casts them to a desired type.
 *  
 * @author msabadello at parityinc dot net
 */
public class CastingIterator<T> implements Iterator<T> {

	protected Iterator<?> iterator;

	public CastingIterator(Iterator<?> iterator) {

		this.iterator = iterator;
	}

	public boolean hasNext() {

		return(this.iterator.hasNext());
	}

	@SuppressWarnings("unchecked")
	public T next() {

		return((T) this.iterator.next());
	}

	public void remove() {

		this.iterator.remove();
	}
}
