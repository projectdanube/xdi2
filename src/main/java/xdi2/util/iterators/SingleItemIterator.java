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
 * An iterator that contains only a single item.
 * 
 * @author msabadello at parityinc dot net
 */
public class SingleItemIterator<T> extends ReadOnlyIterator<T> {

	private T item;
	
	public SingleItemIterator(T item) {

		this.item = item;
	}

	public boolean hasNext() {

		return(this.item != null);
	}

	public T next() {

		T item = this.item;
		this.item = null;
		
		return(item);
	}
}