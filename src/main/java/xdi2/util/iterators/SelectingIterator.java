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
 * An iterator that returns only elements of another iterator that satisfy a certain condition.
 * 
 * In order for the hasNext() function to behave correctly, the iterator always looks ahead one
 * element.
 *  
 * @author msabadello at parityinc dot net
 */
public abstract class SelectingIterator<T> extends ReadOnlyIterator<T> {

	protected Iterator<T> iterator;
	protected T nextElement;

	public SelectingIterator(Iterator<T> iterator) {

		this.iterator = iterator;
		
		this.lookahead();
	}

	public boolean hasNext() {

		return(this.nextElement != null);
	}

	public T next() {

		T element = this.nextElement;

		this.lookahead();

		return(element);
	}

	protected void lookahead() {

		this.nextElement = null;

		while (this.iterator.hasNext()) {

			T element = this.iterator.next();

			if (this.select(element)) {

				this.nextElement = element;
				break;
			}
		}
	}

	public abstract boolean select(T item);
}
