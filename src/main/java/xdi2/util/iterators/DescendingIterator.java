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

import org.eclipse.higgins.xdi4j.util.iterators.ReadOnlyIterator;

/**
 * An iterator that can iterate over D, and for every item in D, an iterator over items of type T is
 * obtained. In the end, all Ts obtained from all Ds are returned.
 * 
 * @author msabadello at parityinc dot net
 */
public abstract class DescendingIterator<D, T> extends ReadOnlyIterator<T> {

	protected Iterator<D> d;
	protected Iterator<T> t;

	public DescendingIterator(Iterator<D> iterator) {

		this.d = iterator;
		this.t = null;
	}

	public boolean hasNext() {

		while (this.t == null || ! this.t.hasNext()) {

			if (! this.d.hasNext()) return(false);
			this.t = this.descend(this.d.next());
		}

		return(true);
	}

	public T next() {

		while (this.t == null || ! this.t.hasNext()) {

			if (! this.d.hasNext()) return(null);
			this.t = this.descend(this.d.next());
		}

		return(this.t.next());
	}

	public abstract Iterator<T> descend(D item);
}
