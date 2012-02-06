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
import org.eclipse.higgins.xdi4j.impl.AbstractLiteral;

/**
 * In-memory based literal implementation.
 * 
 * @author msabadello at parityinc dot net
 */
public class MemoryLiteral extends AbstractLiteral {

	private static final long serialVersionUID = -1217044418709376782L;

	MemoryPredicate predicate;
	String data;
	String preComment;
	String postComment;

	MemoryLiteral(Graph containingGraph) {

		super(containingGraph);
		
		this.predicate = null;
		this.data = null;
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

	public String getData() {

		return(this.data);
	}

	public synchronized void setData(String data) {

		this.data = data;
	}
}
