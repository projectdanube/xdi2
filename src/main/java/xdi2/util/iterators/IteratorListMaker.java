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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class that makes a list consisting of all items of an iterator.
 * 
 * @author msabadello at parityinc dot net
 */
public final class IteratorListMaker<I> {

	private List<I> list;
	private Iterator<I> iterator;

	public IteratorListMaker(Iterator<I> iterator) {

		if (iterator == null) throw new NullPointerException();

		this.list = null;
		this.iterator = iterator;
	}

	/**
	 * Makes and returns the list consisting of all items of the iterator.
	 * @return The list.
	 */
	public List<I> list() {

		if (this.list == null) {

			this.list = new ArrayList<I> ();
			while (this.iterator.hasNext()) this.list.add(this.iterator.next());
		}

		return(this.list);
	}
}
