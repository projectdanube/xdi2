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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.GraphComponent;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Statement;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.io.XDIWriter;
import org.eclipse.higgins.xdi4j.io.XDIWriterRegistry;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3SubSegment;

import xdi2.util.iterators.CompositeIterator;
import xdi2.util.iterators.DescendingIterator;
import xdi2.util.iterators.EmptyIterator;
import xdi2.util.iterators.FirstIteratorItem;
import xdi2.util.iterators.IteratorCounter;
import xdi2.util.iterators.SelectingIterator;
import xdi2.util.iterators.SelectingMappingIterator;
import xdi2.util.iterators.SingleItemIterator;

public abstract class AbstractSubject implements Subject {

	private static final long serialVersionUID = -6963415733981657854L;

	protected Graph containingGraph;

	public AbstractSubject(Graph containingGraph) {

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

		return(this.getContainingGraph());
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

	public synchronized void deleteFromGraph() {

		this.getContainingGraph().deleteSubject(this.getSubjectXri());
	}

	public synchronized void clear() {

		this.deletePredicates();
	}

	public boolean isEmpty() {

		return(! this.containsPredicates());
	}

	public String toString(String format, Properties parameters) {

		XDIWriter writer = XDIWriterRegistry.forFormat(format);
		StringWriter buffer = new StringWriter();

		try {

			writer.write(this, buffer, parameters);
		} catch (IOException ex) {

			buffer.write("Exception while serializing: " + ex.getMessage());
		}

		return(buffer.toString());
	}

	/*
	 * Methods related to predicates of the subject
	 */

	public Iterator<Predicate> getPredicatesStartingWith(final XRI3Segment predicateXri) {
		
		return new SelectingIterator<Predicate> (this.getPredicates()) {

			@Override
			@SuppressWarnings("unchecked")
			public boolean select(Predicate predicate) {

				return(predicate.getPredicateXri().startsWith((XRI3SubSegment[]) predicateXri.getSubSegments().toArray(new XRI3SubSegment[predicateXri.getNumSubSegments()])));
			}
		};
	}
	
	public Predicate getPredicate(final XRI3Segment predicateXri) {

		Iterator<Predicate> selectingIterator = new SelectingIterator<Predicate> (this.getPredicates()) {

			@Override
			public boolean select(Predicate predicate) {

				return(predicate.getPredicateXri().equals(predicateXri));
			}
		};

		return(new FirstIteratorItem<Predicate> (selectingIterator).item());
	}

	public Iterator<Reference> getReferences() {

		return(new DescendingIterator<Predicate, Reference> (this.getPredicates()) {

			@Override
			public Iterator<Reference> descend(Predicate predicate) {

				return(predicate.getReferences());
			}
		});
	}

	public Iterator<Literal> getLiterals() {

		return(new SelectingMappingIterator<Predicate, Literal> (this.getPredicates()) {

			@Override
			public boolean select(Predicate predicate) {

				return(predicate.containsLiteral());
			}

			@Override
			public Literal map(Predicate predicate) {

				return(predicate.getLiteral());
			}
		});
	}

	public Iterator<Graph> getInnerGraphs() {

		return(new SelectingMappingIterator<Predicate, Graph> (this.getPredicates()) {

			@Override
			public boolean select(Predicate predicate) {

				return(predicate.containsInnerGraph());
			}

			@Override
			public Graph map(Predicate predicate) {

				return(predicate.getInnerGraph());
			}
		});
	}

	public boolean containsPredicates() {

		return(this.getPredicateCount() > 0);
	}

	public boolean containsPredicate(XRI3Segment predicateXri) {

		return(this.getPredicate(predicateXri) != null);
	}

	public int getPredicateCount() {

		return(new IteratorCounter(this.getPredicates()).count());
	}

	public int getReferenceCount() {

		return(new IteratorCounter(this.getReferences()).count());
	}

	public int getLiteralCount() {

		return(new IteratorCounter(this.getLiterals()).count());
	}

	public int getInnerGraphCount() {

		return(new IteratorCounter(this.getInnerGraphs()).count());
	}

	/*
	 * Methods related to statements
	 */

	public synchronized Statement createStatement(XRI3Segment predicateXri) {

		Predicate predicate = this.createPredicate(predicateXri);

		return(new FirstIteratorItem<Statement> (predicate.getStatements()).item());
	}

	public synchronized Statement createStatement(XRI3Segment predicateXri, XRI3Segment referenceXri) {

		Predicate predicate = this.getPredicate(predicateXri);
		if (predicate == null) predicate = this.createPredicate(predicateXri);

		return(predicate.createStatement(referenceXri));
	}

	public synchronized Statement createStatement(XRI3Segment predicateXri, String data) {

		Predicate predicate = this.getPredicate(predicateXri);
		if (predicate == null) predicate = this.createPredicate(predicateXri);

		return(predicate.createStatement(data));
	}

	public synchronized Statement createStatement(XRI3Segment predicateXri, Graph innerGraph) {

		Predicate predicate = this.getPredicate(predicateXri);
		if (predicate == null) predicate = this.createPredicate(predicateXri);

		return(predicate.createStatement(innerGraph));
	}

	public Iterator<Statement> getStatements() {

		if (this.containsPredicates()) {

			return(new DescendingIterator<Predicate, Statement> (this.getPredicates()) {

				@Override
				public Iterator<Statement> descend(Predicate predicate) {

					return(predicate.getStatements());
				}
			});
		}

		return(new SingleItemIterator<Statement> (this.statement));
	}

	public Iterator<Statement> getStatements(XRI3Segment predicateXri) {

		Predicate predicate = this.getPredicate(predicateXri);
		if (predicate == null) return(new EmptyIterator<Statement> ());

		return(predicate.getStatements());
	}

	public Statement getStatement(XRI3Segment predicateXri) {

		return(new FirstIteratorItem<Statement> (this.getStatements(predicateXri)).item());
	}

	public Statement getStatement(XRI3Segment predicateXri, XRI3Segment referenceXri) {

		Predicate predicate = this.getPredicate(predicateXri);
		if (predicate == null) return(null);

		return(predicate.getStatement(referenceXri));
	}

	public Statement getStatement(XRI3Segment predicateXri, String data) {

		Predicate predicate = this.getPredicate(predicateXri);
		if (predicate == null) return(null);

		return(predicate.getStatement(data));
	}

	public Statement getStatement(XRI3Segment predicateXri, Graph innerGraph) {

		Predicate predicate = this.getPredicate(predicateXri);
		if (predicate == null) return(null);

		return(predicate.getStatement(innerGraph));
	}

	public boolean containsStatement(XRI3Segment predicateXri) {

		return(this.getStatement(predicateXri) != null);
	}

	public boolean containsStatement(XRI3Segment predicateXri, XRI3Segment referenceXri) {

		return(this.getStatement(predicateXri, referenceXri) != null);
	}

	public boolean containsStatement(XRI3Segment predicateXri, String data) {

		return(this.getStatement(predicateXri, data) != null);
	}

	public boolean containsStatement(XRI3Segment predicateXri, Graph innerGraph) {

		return(this.getStatement(predicateXri, innerGraph) != null);
	}

	public synchronized void deleteStatements() {

		for (Iterator<Statement> statements = this.getStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();
			statement.delete();
		}
	}

	public synchronized void deleteStatements(XRI3Segment predicateXri) {

		for (Iterator<Statement> statements = this.getStatements(predicateXri); statements.hasNext(); ) {

			Statement statement = statements.next();
			statement.delete();
		}
	}

	public synchronized void deleteStatement(XRI3Segment predicateXri, XRI3Segment referenceXri) {

		Statement statement = this.getStatement(predicateXri, referenceXri);
		if (statement != null) statement.delete();
	}

	public synchronized void deleteStatement(XRI3Segment predicateXri, String data) {

		Statement statement = this.getStatement(predicateXri, data);
		if (statement != null) statement.delete();
	}

	public synchronized void deleteStatement(XRI3Segment predicateXri, Graph innerGraph) {

		Statement statement = this.getStatement(predicateXri, innerGraph);
		if (statement != null) statement.delete();
	}

	public int getStatementCount() {

		return(new IteratorCounter(this.getStatements()).count());
	}

	public int getStatementCount(XRI3Segment predicateXri) {

		return(new IteratorCounter(this.getStatements(predicateXri)).count());
	}

	/*
	 * Methods related to comments
	 */

	@SuppressWarnings("unchecked")
	public Iterator<String> getComments() {

		Iterator<String>[] iterators = new Iterator[3];

		iterators[0] = new SingleItemIterator<String> (this.getPreComment());
		iterators[1] = new SingleItemIterator<String> (this.getPostComment());

		iterators[2] = new DescendingIterator<Predicate, String> (this.getPredicates()) {

			@Override
			public Iterator<String> descend(Predicate item) {

				return(item.getComments());
			}
		};

		return(new CompositeIterator<String> (iterators));
	}

	public int getCommentCount() {

		return(new IteratorCounter(this.getComments()).count());
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return(this.getSubjectXri().toString());
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Subject)) return(false);
		if (object == this) return(true);

		Subject other = (Subject) object;

		// two subjects are equal if their XRIs are equal

		if (this.getSubjectXri() == null && other.getSubjectXri() != null) return(false);
		if (this.getSubjectXri() != null && other.getSubjectXri() == null) return(false);
		if (this.getSubjectXri() != null && other.getSubjectXri() != null && ! this.getSubjectXri().equals(other.getSubjectXri())) return(false);

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.getSubjectXri() == null ? 0 : this.getSubjectXri().hashCode());

		return(hashCode);
	}

	public int compareTo(Subject other) {

		if (other == null || other == this) return(0);

		return(this.getSubjectXri().compareTo(other.getSubjectXri()));
	}

	/**
	 * A class representing a statement that contains a subject.
	 */
	private Statement statement = new AbstractStatement() {

		private static final long serialVersionUID = -4476313384256775711L;

		public Graph getContainingGraph() {

			return(AbstractSubject.this.getContainingGraph());
		}

		public Subject getSubject() {

			return(AbstractSubject.this);
		}

		public Predicate getPredicate() {

			return(null);
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
