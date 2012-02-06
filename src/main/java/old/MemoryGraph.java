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
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.exceptions.Xdi4jGraphException;
import org.eclipse.higgins.xdi4j.impl.AbstractGraph;
import org.eclipse.higgins.xdi4j.util.iterators.CastingIterator;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

/**
 * In-memory based graph implementation.
 * 
 * @author msabadello at parityinc dot net
 */
public class MemoryGraph extends AbstractGraph {

	private static final long serialVersionUID = -9133567069952736719L;

	Predicate predicate;
	String preComment;
	String postComment;
	Map<XRI3Segment, MemorySubject> subjects;
	int sortmode;

	MemoryGraph(Graph containingGraph, int sortmode) {

		super(containingGraph);

		this.predicate = null;
		this.preComment = null;
		this.postComment = null;
		this.sortmode = sortmode;

		if (sortmode == MemoryGraphFactory.SORTMODE_ALPHA) {

			this.subjects = new TreeMap<XRI3Segment, MemorySubject> ();
		} else  if (sortmode == MemoryGraphFactory.SORTMODE_ORDER) {

			this.subjects = new LinkedHashMap<XRI3Segment, MemorySubject> ();
		} else {

			this.subjects = new HashMap<XRI3Segment, MemorySubject> ();
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

	public Predicate getPredicate() {

		return(this.predicate);
	}

	public synchronized void close() {

	}

	public synchronized void setComment(String comment) {

		this.preComment = comment;
	}

	/*
	 * Methods related to subjects of the graph
	 */

	public synchronized Subject createSubject(XRI3Segment subjectXri) {

		if (subjectXri == null) throw new NullPointerException();

		if (this.containsSubject(subjectXri)) throw new Xdi4jGraphException("Graph already contains the subject " + subjectXri + ".");

		MemorySubject subject = new MemorySubject(this);
		subject.subjectXri = subjectXri;

		this.subjects.put(subjectXri, subject);

		return(subject);
	}

	public synchronized Iterator<Subject> getSubjects() {

		return(new CastingIterator<Subject> (this.subjects.values().iterator()));
	}

	@Override
	public synchronized Subject getSubject(XRI3Segment subjectXri) {

		return(this.subjects.get(subjectXri));
	}

	@Override
	public boolean containsSubjects() {

		return(! this.subjects.isEmpty());
	}

	@Override
	public boolean containsSubject(XRI3Segment subjectXri) {

		return(this.subjects.containsKey(subjectXri));
	}

	public synchronized void deleteSubjects() {

		this.subjects.clear();
	}

	public synchronized void deleteSubject(XRI3Segment subjectXri) {

		this.subjects.remove(subjectXri);
	}

	@Override
	public int getSubjectCount() {

		return(this.subjects.size());
	}
}
