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
package xdi2.impl;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.util.iterators.CompositeIterator;
import xdi2.util.iterators.DescendingIterator;
import xdi2.util.iterators.FirstIteratorItem;
import xdi2.util.iterators.IteratorCounter;
import xdi2.util.iterators.SelectingIterator;
import xdi2.xri3.impl.XRI3SubSegment;

public abstract class AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 7937255194345376190L;

	protected Graph graph;
	protected ContextNode contextNode;

	public AbstractContextNode(Graph graph, ContextNode contextNode) {

		this.graph = graph;
		this.contextNode = contextNode;
	}

	public Graph getGraph() {

		return this.graph;
	}

	public ContextNode getContextNode() {

		return this.contextNode;
	}

	public synchronized void delete() {

		this.getContextNode().deleteContextNode(this.getArcXri());
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	public ContextNode getContextNode(final XRI3SubSegment arcXri) {

		Iterator<ContextNode> selectingIterator = new SelectingIterator<ContextNode> (this.getContextNodes()) {

			@Override
			public boolean select(ContextNode contextNode) {

				return(contextNode.getArcXri().equals(arcXri));
			}
		};

		return(new FirstIteratorItem<ContextNode> (selectingIterator).item());
	}

	public Iterator<ContextNode> getAllContextNodes() {

		DescendingIterator<ContextNode, ContextNode> descendingIterator = new DescendingIterator<ContextNode, ContextNode> (this.getContextNodes()) {

			@Override
			public Iterator<ContextNode> descend(ContextNode contextNode) {

				return(contextNode.getAllContextNodes());
			}
		};

		List<Iterator<ContextNode> > list = new ArrayList<Iterator<ContextNode> > ();
		list.add(this.getContextNodes());
		list.add(descendingIterator);

		return new CompositeIterator<ContextNode> (list.iterator());
	}

	public boolean containsContextNode(XRI3SubSegment arcXri) {

		return(this.getContextNode(arcXri) != null);
	}

	public boolean containsContextNodes() {

		return(this.getContextNodeCount() > 0);
	}

	public int getContextNodeCount() {

		return(new IteratorCounter(this.getContextNodes()).count());
	}

	public int getAllContextNodeCount() {

		return(new IteratorCounter(this.getAllContextNodes()).count());
	}

	/*
	 * Methods related to relations of this context node
	 */

	public Relation getRelation(final XRI3SubSegment arcXri) {

		Iterator<Relation> selectingIterator = new SelectingIterator<Relation> (this.getRelations()) {

			@Override
			public boolean select(Relation relation) {

				return(relation.getArcXri().equals(arcXri));
			}
		};

		return(new FirstIteratorItem<Relation> (selectingIterator).item());
	}

	public Iterator<Relation> getAllRelations() {

		DescendingIterator<ContextNode, Relation> descendingIterator = new DescendingIterator<ContextNode, Relation> (this.getContextNodes()) {

			@Override
			public Iterator<Relation> descend(ContextNode contextNode) {

				return(contextNode.getAllRelations());
			}
		};

		List<Iterator<Relation> > list = new ArrayList<Iterator<Relation> > ();
		list.add(this.getRelations());
		list.add(descendingIterator);

		return new CompositeIterator<Relation> (list.iterator());
	}

	public boolean containsRelation(XRI3SubSegment arcXri) {

		return(this.getRelation(arcXri) != null);
	}

	public boolean containsRelations() {

		return(this.getRelationCount() > 0);
	}

	public int getRelationCount() {

		return(new IteratorCounter(this.getRelations()).count());
	}

	public int getAllRelationCount() {

		return(new IteratorCounter(this.getAllRelations()).count());
	}

	/*
	 * Methods related to literals of this context node
	 */

	public Literal getLiteral(final XRI3SubSegment arcXri) {

		Iterator<Literal> selectingIterator = new SelectingIterator<Literal> (this.getLiterals()) {

			@Override
			public boolean select(Literal literal) {

				return(literal.getArcXri().equals(arcXri));
			}
		};

		return(new FirstIteratorItem<Literal> (selectingIterator).item());
	}

	public Iterator<Literal> getAllLiterals() {

		DescendingIterator<ContextNode, Literal> descendingIterator = new DescendingIterator<ContextNode, Literal> (this.getContextNodes()) {

			@Override
			public Iterator<Literal> descend(ContextNode contextNode) {

				return(contextNode.getAllLiterals());
			}
		};

		List<Iterator<Literal> > list = new ArrayList<Iterator<Literal> > ();
		list.add(this.getLiterals());
		list.add(descendingIterator);

		return new CompositeIterator<Literal> (list.iterator());
	}

	public boolean containsLiteral(XRI3SubSegment arcXri) {

		return(this.getLiteral(arcXri) != null);
	}

	public boolean containsLiterals() {

		return(this.getLiteralCount() > 0);
	}

	public int getLiteralCount() {

		return(new IteratorCounter(this.getLiterals()).count());
	}

	public int getAllLiteralCount() {

		return(new IteratorCounter(this.getAllLiterals()).count());
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getXri().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof ContextNode)) return false;
		if (object == this) return(true);

		ContextNode other = (ContextNode) object;

		// two predicates are equal if their XRIs are equal

		if (this.getXri() == null && other.getXri() != null) return false;
		if (this.getXri() != null && other.getXri() == null) return false;
		if (this.getXri() != null && other.getXri() != null && ! this.getXri().equals(other.getXri())) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.getXri() == null ? 0 : this.getXri().hashCode());

		return hashCode;
	}

	public int compareTo(ContextNode other) {

		if (other == null || other == this) return 0;

		return this.getXri().compareTo(other.getXri());
	}
}
