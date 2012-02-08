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
 * A class that conveniently gives you the first element of an iterator, or null if it has no elements.
 * 
 * @author markus
 */
public class FirstIteratorItem<T> {

	private T item;
	private Iterator<T> iterator;

	public FirstIteratorItem(Iterator<T> iterator) {

		if (iterator == null) throw new NullPointerException();

		this.item = null;
		this.iterator = iterator;
	}

	public T item() {

		if (this.item != null) return this.item;

		if (this.iterator.hasNext()) this.item = this.iterator.next();

		return this.item;
	}
}
