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
 * A class that can count the number of elements in an Iterator.
 * 
 * @author markus
 */
public final class IteratorCounter {

	private int count;
	private Iterator<?> iterator;

	public IteratorCounter(Iterator<?> iterator) {

		if (iterator == null) throw new NullPointerException();

		this.count = -1;
		this.iterator = iterator;
	}

	/**
	 * Counts and returns the number of elements in an Iterator.
	 * @return The number of elements.
	 */
	public int count() {

		if (this.count != -1) return this.count;

		this.count = 0;
		while (this.iterator.hasNext()) {

			iterator.next();
			this.count++;
		}

		return this.count;
	}
}
