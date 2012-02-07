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
 * An iterator that returns only all non-null elements from another iterator.
 * 
 * @author msabadello at parityinc dot net
 */
public class NotNullIterator<T> extends SelectingIterator<T> {

	public NotNullIterator(Iterator<T> iterator) {

		super(iterator);
	}

	@Override
	public boolean select(T item) {

		return(item != null);
	}
}
