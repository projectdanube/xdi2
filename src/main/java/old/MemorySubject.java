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
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.exceptions.Xdi4jGraphException;
import org.eclipse.higgins.xdi4j.impl.AbstractSubject;
import org.eclipse.higgins.xdi4j.util.iterators.CastingIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

/**
 * In-memory based subject implementation.
 * 
 * @author msabadello at parityinc dot net
 */
public class MemorySubject extends AbstractSubject {

	private static final long serialVersionUID = -8951744082310059447L;

	XRI3Segment subjectXri;
	String preComment;
	String postComment;
	Map<XRI3Segment, MemoryPredicate> predicates;

	MemorySubject(Graph containingGraph) {

		super(containingGraph);

		this.subjectXri = null;
		this.preComment = null;
		this.postComment = null;

		if (((MemoryGraph) containingGraph).sortmode == MemoryGraphFactory.SORTMODE_ALPHA) {

			this.predicates = new TreeMap<XRI3Segment, MemoryPredicate> ();
		} else if (((MemoryGraph) containingGraph).sortmode == MemoryGraphFactory.SORTMODE_ORDER) {

			this.predicates = new LinkedHashMap<XRI3Segment, MemoryPredicate> ();
		} else {

			this.predicates = new HashMap<XRI3Segment, MemoryPredicate> ();
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

	public XRI3Segment getSubjectXri() {

		return(this.subjectXri);
	}

	public void setSubjectXri(XRI3Segment subjectXri) {

		if (subjectXri == null) throw new NullPointerException();

		if (this.getContainingGraph().containsSubject(subjectXri)) throw new Xdi4jGraphException("Graph already contains the subject " + subjectXri + ".");

		((MemoryGraph) this.getContainingGraph()).subjects.remove(this.subjectXri);
		this.subjectXri = subjectXri;
		((MemoryGraph) this.getContainingGraph()).subjects.put(this.subjectXri, this);
	}

	/*
	 * Methods related to predicates of the subject
	 */

	public synchronized Predicate createPredicate(XRI3Segment predicateXri) {

		if (predicateXri == null) throw new NullPointerException();

		if (this.containsPredicate(predicateXri)) throw new Xdi4jGraphException("Subject " + this.getSubjectXri() + " already contains the predicate " + predicateXri + ".");

		MemoryPredicate predicate = new MemoryPredicate(this.getContainingGraph());
		predicate.subject = this;
		predicate.predicateXri = predicateXri;

		this.predicates.put(predicateXri, predicate);

		return(predicate);
	}

	public Iterator<Predicate> getPredicates() {

		return(new CastingIterator<Predicate>(this.predicates.values().iterator()));
	}

	@Override
	public Predicate getPredicate(XRI3Segment predicateXri) {

		return(this.predicates.get(predicateXri));
	}

	@Override
	public boolean containsPredicates() {

		return(! this.predicates.isEmpty());
	}

	@Override
	public boolean containsPredicate(XRI3Segment predicateXri) {

		return(this.predicates.containsKey(predicateXri));
	}

	public synchronized void deletePredicates() {

		this.predicates.clear();
	}

	public synchronized void deletePredicate(XRI3Segment predicateXri) {

		this.predicates.remove(predicateXri);
	}

	@Override
	public int getPredicateCount() {

		return(this.predicates.size());
	}
}
