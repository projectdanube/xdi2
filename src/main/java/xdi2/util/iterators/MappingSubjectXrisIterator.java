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

import javax.security.auth.Subject;

import xdi2.xri3.impl.XRI3Segment;

/**
 * A MappingIterator that maps XDI subjects to their XRIs.
 * 
 * @author msabadello at parityinc dot net
 */
public class MappingSubjectXrisIterator extends MappingIterator<Subject, XRI3Segment> {

	public MappingSubjectXrisIterator(Iterator<Subject> subjects) {

		super(subjects);
	}

	@Override
	public XRI3Segment map(Subject item) {

		return(item.getSubjectXri());
	}
}
