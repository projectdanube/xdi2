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

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.GraphComponent;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Statement;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import xdi2.util.iterators.CompositeIterator;
import xdi2.util.iterators.DescendingIterator;
import xdi2.util.iterators.FirstIteratorItem;
import xdi2.util.iterators.IteratorCounter;
import xdi2.util.iterators.SelectingIterator;
import xdi2.util.iterators.SingleItemIterator;

public abstract class AbstractPredicate implements Predicate {

	private static final long serialVersionUID = 7937255194345376190L;

	protected Graph containingGraph;

	public AbstractPredicate(Graph containingGraph) {

		this.containingGraph = containingGraph;
	}

	/*
	 * GraphComponent methods
	 */

	public Graph getContainingGraph() {

		return(this.containingGraph);
	}

	public Graph getTopLevelGraph() {

		Graph topLevelGraph = this.getContainingGraph();

		while (topLevelGraph != null && topLevelGraph.getContainingGraph() != null) topLevelGraph = topLevelGraph.getContainingGraph();

		return(topLevelGraph);
	}

	public GraphComponent getParentGraphComponent() {
		
		return(this.getSubject());
	}
	
	public boolean isLeaf() {
		
		return(this.isEmpty());
	}

	public boolean containsPreComment() {

		return(this.getPreComment() != null);
	}

	public boolean containsPostComment() {

		return(this.getPostComment() != null);
	}

	/*
	 * General methods
	 */

	public synchronized void deleteFromSubject() {

		this.getSubject().deletePredicate(this.getPredicateXri());
	}

	public synchronized void clear() {

		this.deleteReferences();
		this.deleteLiteral();
		this.deleteInnerGraph();
	}

	public boolean isEmpty() {

		return(! (this.containsReferences() || this.containsLiteral() || this.containsInnerGraph()));
	}

	/*
	 * Methods related to references of the predicate
	 */

	public Reference getReference(final XRI3Segment referenceXri) {

		Iterator<Reference> selectingIterator = new SelectingIterator<Reference> (this.getReferences()) {

			@Override
			public boolean select(Reference predicate) {

				return(predicate.getReferenceXri().equals(referenceXri));
			}
		};

		return(new FirstIteratorItem<Reference> (selectingIterator).item());
	}

	public Reference getReference() {

		return(new FirstIteratorItem<Reference> (this.getReferences()).item());
	}

	public boolean containsReferences() {

		return(this.getReferenceCount() > 0);
	}

	public boolean containsReference(XRI3Segment referenceXri) {

		return(this.getReference(referenceXri) != null);
	}

	public int getReferenceCount() {

		return(new IteratorCounter(this.getReferences()).count());
	}

	/*
	 * Methods related to the literal of the predicate
	 */

	public boolean containsLiteral() {

		return(this.getLiteral() != null);
	}

	/*
	 * Methods related to the inner graph of the predicate
	 */

	public boolean containsInnerGraph() {

		return(this.getInnerGraph() != null);
	}

	/*
	 * Methods related to statements
	 */

	public synchronized Statement createStatement(XRI3Segment referenceXri) {

		Reference reference = this.createReference(referenceXri);

		return(reference.getStatement());
	}

	public synchronized Statement createStatement(String data) {

		Literal literal = this.createLiteral(data);

		return(literal.getStatement());
	}

	public synchronized Statement createStatement(Graph graph) {

		Graph innerGraph = this.createInnerGraph(graph);

		return(innerGraph.getStatement());
	}

	public Iterator<Statement> getStatements() {

		if (this.containsReferences()) {

			return(new DescendingIterator<Reference, Statement> (this.getReferences()) {

				@Override
				public Iterator<Statement> descend(Reference reference) {

					return(new SingleItemIterator<Statement> (reference.getStatement()));
				}
			});
		}

		if (this.containsLiteral()) {

			return(new SingleItemIterator<Statement> (this.getLiteral().getStatement()));
		}

		if (this.containsInnerGraph()) {

			return(new SingleItemIterator<Statement> (this.getInnerGraph().getStatement()));
		}

		return(new SingleItemIterator<Statement> (this.statement));
	}

	public Statement getStatement(XRI3Segment referenceXri) {

		Reference reference = this.getReference(referenceXri);
		if (reference == null) return(null);

		return(reference.getStatement());
	}

	public Statement getStatement(String data) {

		Literal literal = this.getLiteral();
		if (literal == null) return(null);
		if (! literal.getData().equals(data)) return(null);

		return(literal.getStatement());
	}

	public Statement getStatement(Graph graph) {

		Graph innerGraph = this.getInnerGraph();
		if (innerGraph == null) return(null);
		if (! innerGraph.equals(graph)) return(null);

		return(innerGraph.getStatement());
	}

	public boolean containsStatement(XRI3Segment referenceXri) {

		return(this.getStatement(referenceXri) != null);
	}

	public boolean containsStatement(String data) {

		return(this.getStatement(data) != null);
	}

	public boolean containsStatement(Graph innerGraph) {

		return(this.getStatement(innerGraph) != null);
	}

	public synchronized void deleteStatements() {

		for (Iterator<Statement> statements = this.getStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();
			statement.delete();
		}
	}

	public synchronized void deleteStatement(XRI3Segment referenceXri) {

		Statement statement = this.getStatement(referenceXri);
		if (statement != null) statement.delete();
	}

	public synchronized void deleteStatement(String data) {

		Statement statement = this.getStatement(data);
		if (statement != null) statement.delete();
	}

	public synchronized void deleteStatement(Graph innerGraph) {

		Statement statement = this.getStatement(innerGraph);
		if (statement != null) statement.delete();
	}

	public int getStatementCount() {

		return(new IteratorCounter(this.getStatements()).count());
	}

	/*
	 * Methods related to comments
	 */

	public Iterator<String> getComments() {

		List<Iterator<String>> iterators = new Vector<Iterator<String>> ();

		if (this.containsPreComment()) iterators.add(new SingleItemIterator<String> (this.getPreComment()));
		if (this.containsPostComment()) iterators.add(new SingleItemIterator<String> (this.getPostComment()));

		if (this.containsReferences()) {

			for (Iterator<Reference> references = this.getReferences(); references.hasNext(); ) {

				Reference reference = references.next();

				if (reference.containsPreComment()) iterators.add(new SingleItemIterator<String> (reference.getPreComment()));
				if (reference.containsPostComment()) iterators.add(new SingleItemIterator<String> (reference.getPostComment()));
			}
		} else if (this.containsLiteral()) {

			Literal literal = this.getLiteral();

			if (literal.containsPreComment())  iterators.add(new SingleItemIterator<String> (literal.getPreComment()));
			if (literal.containsPostComment())  iterators.add(new SingleItemIterator<String> (literal.getPostComment()));
		} else if (this.containsInnerGraph()) {

			Graph innerGraph = this.getInnerGraph();

			iterators.add(innerGraph.getComments());
		}

		return(new CompositeIterator<String> (iterators.iterator()));
	}

	public int getCommentCount() {

		return(new IteratorCounter(this.getComments()).count());
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return(this.getPredicateXri().toString());
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Predicate)) return(false);
		if (object == this) return(true);

		Predicate other = (Predicate) object;

		// two predicates are equal if their XRIs are equal

		if (this.getPredicateXri() == null && other.getPredicateXri() != null) return(false);
		if (this.getPredicateXri() != null && other.getPredicateXri() == null) return(false);
		if (this.getPredicateXri() != null && other.getPredicateXri() != null && ! this.getPredicateXri().equals(other.getPredicateXri())) return(false);

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.getPredicateXri() == null ? 0 : this.getPredicateXri().hashCode());

		return(hashCode);
	}

	public int compareTo(Predicate other) {

		if (other == null || other == this) return(0);

		return(this.getPredicateXri().compareTo(other.getPredicateXri()));
	}

	/**
	 * A class representing a statement that contains a subject and predicate.
	 */
	private Statement statement = new AbstractStatement() {

		private static final long serialVersionUID = 3254628272559897740L;

		public Graph getContainingGraph() {

			return(AbstractPredicate.this.getContainingGraph());
		}

		public Subject getSubject() {

			return(AbstractPredicate.this.getSubject());
		}

		public Predicate getPredicate() {

			return(AbstractPredicate.this);
		}

		public Reference getReference() {

			return(null);
		}

		public Literal getLiteral() {

			return(null);
		}

		public Graph getInnerGraph() {

			return(null);
		}
	};
}
