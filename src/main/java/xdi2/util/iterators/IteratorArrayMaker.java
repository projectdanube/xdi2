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
 * A class that makes an array consisting of all items of an iterator.
 * 
 * @author markus
 */
public final class IteratorArrayMaker<I> {

	private I[] array;
	private Iterator<I> iterator;

	public IteratorArrayMaker(Iterator<I> iterator) {

		if (iterator == null) throw new NullPointerException();

		this.array = null;
		this.iterator = iterator;
	}

	/**
	 * Makes and returns the array consisting of all items of the iterator.
	 * @return The array.
	 */
	public I[] array(I[] array) {

		if (this.array == null) {

			List<I> list = new ArrayList<I> ();
			while (this.iterator.hasNext()) list.add(this.iterator.next());

			this.array = list.toArray(array);
		}

		return this.array;
	}
}
