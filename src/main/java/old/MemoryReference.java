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
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.impl.AbstractReference;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

/**
 * In-memory based reference implementation.
 * 
 * @author msabadello at parityinc dot net
 */
public class MemoryReference extends AbstractReference {

	private static final long serialVersionUID = -3926947471571693574L;

	MemoryPredicate predicate;
	XRI3Segment referenceXri;
	String preComment;
	String postComment;

	MemoryReference(Graph containingGraph) {

		super(containingGraph);
		
		this.predicate = null;
		this.referenceXri = null;
		this.preComment = null;
		this.postComment = null;
	}

	/*
	 * GraphComponent methods
	 */

	public String getPreComment() {
		
		return(this.preComment);
	}

	public synchronized void setPreComment(String preComment) {
		
		this.preComment = preComment;
	}

	public String getPostComment() {
		
		return(this.postComment);
	}

	public synchronized void setPostComment(String postComment) {
		
		this.postComment = postComment;
	}

	/*
	 * General methods
	 */
	
	public Predicate getPredicate() {

		return(this.predicate);
	}

	public XRI3Segment getReferenceXri() {

		return(this.referenceXri);
	}
}
