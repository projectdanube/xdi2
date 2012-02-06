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

import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.util.iterators.MappingIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

/**
 * A MappingIterator that maps XDI predicates to their XRIs.
 * 
 * @author msabadello at parityinc dot net
 */
public class MappingPredicateXrisIterator extends MappingIterator<Predicate, XRI3Segment> {

	public MappingPredicateXrisIterator(Iterator<Predicate> predicates) {

		super(predicates);
	}

	@Override
	public XRI3Segment map(Predicate item) {

		return(item.getPredicateXri());
	}
}
