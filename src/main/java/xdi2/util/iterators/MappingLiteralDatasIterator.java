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

import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.util.iterators.MappingIterator;

/**
 * A MappingIterator that maps XDI literals to their datas.
 * 
 * @author msabadello at parityinc dot net
 */
public class MappingLiteralDatasIterator extends MappingIterator<Literal, String> {

	public MappingLiteralDatasIterator(Iterator<Literal> literals) {

		super(literals);
	}

	@Override
	public String map(Literal item) {

		return(item.getData());
	}
}
