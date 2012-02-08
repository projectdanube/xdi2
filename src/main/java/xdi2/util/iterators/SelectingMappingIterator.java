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
 * An iterator that returns only elements of another iterator that satisfy a certain condition.
 * In addition, it maps the elements to other elements.
 * 
 * In order for the hasNext() function to behave correctly, the iterator always looks ahead one
 * element.
 * 
 * I is the type of elements read by the iterator.
 * O is the type of elements returned by the iterator.
 *  
 * @author markus
 */
public abstract class SelectingMappingIterator<I, O> extends ReadOnlyIterator<O> {

	protected Iterator<I> iterator;
	protected O nextElement;

	public SelectingMappingIterator(Iterator<I> iterator) {

		this.iterator = iterator;
		
		this.lookahead();
	}

	public boolean hasNext() {

		return this.nextElement != null;
	}

	public O next() {

		O element = this.nextElement;

		this.lookahead();

		return element;
	}

	protected void lookahead() {

		this.nextElement = null;

		while (this.iterator.hasNext()) {

			I element = this.iterator.next();

			if (this.select(element)) {

				this.nextElement = this.map(element);
				break;
			}
		}
	}

	public abstract boolean select(I item);

	public abstract O map(I item);
}
