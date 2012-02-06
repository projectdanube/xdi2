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

import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.util.iterators.MappingIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

/**
 * A MappingIterator that maps XDI references to their XRIs.
 * 
 * @author msabadello at parityinc dot net
 */
public class MappingReferenceXrisIterator extends MappingIterator<Reference, XRI3Segment> {

	public MappingReferenceXrisIterator(Iterator<Reference> references) {

		super(references);
	}

	@Override
	public XRI3Segment map(Reference item) {

		return(item.getReferenceXri());
	}
}
