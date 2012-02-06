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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.Xdi4jGraphException;
import org.eclipse.higgins.xdi4j.impl.AbstractPredicate;
import org.eclipse.higgins.xdi4j.util.CopyUtil;
import org.eclipse.higgins.xdi4j.util.iterators.CastingIterator;
import org.eclipse.higgins.xdi4j.util.iterators.FirstIteratorItem;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

/**
 * In-memory based predicate implementation.
 * 
 * @author msabadello at parityinc dot net
 */
public class MemoryPredicate extends AbstractPredicate {

	private static final long serialVersionUID = 7338609422423948643L;

	MemorySubject subject;
	XRI3Segment predicateXri;
	String preComment;
	String postComment;
	Map<XRI3Segment, MemoryReference> references;
	MemoryLiteral literal;
	MemoryGraph innerGraph;

	MemoryPredicate(Graph containingGraph) {

		super(containingGraph);

		this.subject = null;
		this.predicateXri = null;
		this.preComment = null;
		this.postComment = null;
		this.literal = null;
		this.innerGraph = null;

		if (((MemoryGraph) containingGraph).sortmode == MemoryGraphFactory.SORTMODE_ALPHA) {

			this.references = new TreeMap<XRI3Segment, MemoryReference> ();
		} else if (((MemoryGraph) containingGraph).sortmode == MemoryGraphFactory.SORTMODE_ORDER) {

			this.references = new LinkedHashMap<XRI3Segment, MemoryReference> ();
		} else {

			this.references = new HashMap<XRI3Segment, MemoryReference> ();
		}
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

	public Subject getSubject() {

		return(this.subject);
	}

	public XRI3Segment getPredicateXri() {

		return(this.predicateXri);
	}

	/*
	 * Methods related to references of the predicate
	 */

	public synchronized Reference createReference(XRI3Segment referenceXri) {

		if (referenceXri == null) throw new NullPointerException();

		if (this.containsReference(referenceXri)) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains the reference " + referenceXri + ".");
		if (this.containsLiteral()) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains a literal.");
		if (this.containsInnerGraph()) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains an inner graph.");

		MemoryReference reference = new MemoryReference(this.getContainingGraph());
		reference.predicate = this;
		reference.referenceXri = referenceXri;

		this.references.put(referenceXri, reference);

		return(reference);
	}

	public Iterator<Reference> getReferences() {

		return(new CastingIterator<Reference> (this.references.values().iterator()));
	}

	@Override
	public Reference getReference(XRI3Segment referenceXri) {

		return(this.references.get(referenceXri));
	}

	@Override
	public Reference getReference() {

		return(new FirstIteratorItem<MemoryReference> (this.references.values().iterator()).item());
	}

	@Override
	public boolean containsReferences() {

		return(! this.references.isEmpty());
	}

	@Override
	public boolean containsReference(XRI3Segment referenceXri) {

		return(this.references.containsKey(referenceXri));
	}

	public synchronized void deleteReferences() {

		this.references.clear();
	}

	public synchronized void deleteReference(XRI3Segment referenceXri) {

		this.references.remove(referenceXri);
	}

	/*
	 * Methods related to the literal of the predicate
	 */

	public synchronized Literal createLiteral(String data) {

		if (data == null) throw new NullPointerException();

		if (this.containsReferences()) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains one or more references.");
		if (this.containsLiteral()) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains a literal.");
		if (this.containsInnerGraph()) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains an inner graph.");

		MemoryLiteral literal = new MemoryLiteral(this.getContainingGraph());
		literal.predicate = this;
		literal.data = data;

		this.literal = literal;

		return(literal);
	}

	public Literal getLiteral() {

		return(this.literal);
	}

	@Override
	public boolean containsLiteral() {

		return(this.literal != null);
	}

	public synchronized void deleteLiteral() {

		this.literal = null;
	}

	/*
	 * Methods related to the inner graph of the predicate
	 */

	public synchronized Graph createInnerGraph(Graph graph) {

		if (this.containsReferences()) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains one or more references.");
		if (this.containsLiteral()) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains a literal.");
		if (this.containsInnerGraph()) throw new Xdi4jGraphException("Predicate " + this.getPredicateXri() + " already contains an inner graph.");

		MemoryGraph innerGraph = new MemoryGraph(this.getContainingGraph(), ((MemoryGraph) this.containingGraph).sortmode);
		innerGraph.predicate = this;
		if (graph != null) CopyUtil.copyStatements(graph, innerGraph, null);

		this.innerGraph = innerGraph;

		return(innerGraph);
	}

	public Graph getInnerGraph() {

		return(this.innerGraph);
	}

	@Override
	public boolean containsInnerGraph() {

		return(this.innerGraph != null);
	}

	public synchronized void deleteInnerGraph() {

		this.innerGraph = null;
	}
}
