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
package old;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.impl.AbstractGraphFactory;

/**
 * GraphFactory that creates in-memory graphs.
 * 
 * @author msabadello at parityinc dot net
 */
public final class MemoryGraphFactory extends AbstractGraphFactory {

	public static final int SORTMODE_NONE = 0;
	public static final int SORTMODE_ORDER = 1;
	public static final int SORTMODE_ALPHA = 2;

	private static MemoryGraphFactory instance = null;
	
	private int sortmode;

	public MemoryGraphFactory() { 
		
		this.sortmode = SORTMODE_NONE;
	}

	public static MemoryGraphFactory getInstance() {

		if (instance == null) instance = new MemoryGraphFactory();

		return(instance);
	}

	public Graph openGraph() {

		// create new graph

		return(new MemoryGraph(null, this.sortmode));
	}

	public int getSortmode() {
		
		return this.sortmode;
	}

	public void setSortmode(int sortmode) {

		this.sortmode = sortmode;
	}
}
