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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.GraphComponent;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Statement;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.addressing.Addressing;
import org.eclipse.higgins.xdi4j.exceptions.MessagingException;
import org.eclipse.higgins.xdi4j.io.XDIWriter;
import org.eclipse.higgins.xdi4j.io.XDIWriterRegistry;
import org.eclipse.higgins.xdi4j.messaging.MessageEnvelope;
import org.eclipse.higgins.xdi4j.messaging.MessageResult;
import org.eclipse.higgins.xdi4j.messaging.server.ExecutionContext;
import org.eclipse.higgins.xdi4j.messaging.server.impl.graph.GraphMessagingTarget;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3SubSegment;

import xdi2.util.iterators.CastingIterator;
import xdi2.util.iterators.CompositeIterator;
import xdi2.util.iterators.DescendingIterator;
import xdi2.util.iterators.EmptyIterator;
import xdi2.util.iterators.FirstIteratorItem;
import xdi2.util.iterators.IteratorCounter;
import xdi2.util.iterators.SelectingIterator;
import xdi2.util.iterators.SingleItemIterator;

public abstract class AbstractGraph implements Graph {

	private static final long serialVersionUID = -8112898491554617751L;

	protected Graph containingGraph;

	public AbstractGraph(Graph containingGraph) {

		this.containingGraph = containingGraph;
	}

	/*
	 * GraphComponent methods
	 */

	public Graph getContainingGraph() {

		return(this.containingGraph);
	}

	public Graph getTopLevelGraph() {

		Graph topLevelGraph = this;

		while (topLevelGraph != null && topLevelGraph.getContainingGraph() != null) topLevelGraph = topLevelGraph.getContainingGraph();

		return(topLevelGraph);
	}

	public GraphComponent getParentGraphComponent() {

		return(this.getPredicate());
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

	public boolean isInnerGraph() {

		return(this.getPredicate() != null);
	}

	public synchronized void deleteFromPredicate() {

		this.getPredicate().deleteInnerGraph();
	}

	public synchronized void clear() {

		this.deleteSubjects();
	}

	public boolean isEmpty() {

		return(! this.containsSubjects());
	}

	public String toString(String format) {

		return this.toString(format, null);
	}

	public String toString(String format, Properties parameters) {

		XDIWriter writer = XDIWriterRegistry.forFormat(format);
		StringWriter buffer = new StringWriter();

		try {

			writer.write(this, buffer, parameters);
		} catch (IOException ex) {

			return("[Exception: " + ex.getMessage() + "]");
		}

		return(buffer.toString());
	}

	/*
	 * Methods related to subjects of the graph
	 */

	public Iterator<Subject> getSubjectsStartingWith(final XRI3Segment subjectXri) {

		return new SelectingIterator<Subject> (this.getSubjects()) {

			@Override
			@SuppressWarnings("unchecked")
			public boolean select(Subject subject) {

				return(subject.getSubjectXri().startsWith((XRI3SubSegment[]) subjectXri.getSubSegments().toArray(new XRI3SubSegment[subjectXri.getNumSubSegments()])));
			}
		};
	}

	public Subject getSubject(final XRI3Segment subjectXri) {

		Iterator<Subject> selectingIterator = new SelectingIterator<Subject> (this.getSubjects()) {

			@Override
			public boolean select(Subject subject) {

				return(subject.getSubjectXri().equals(subjectXri));
			}
		};

		return(new FirstIteratorItem<Subject> (selectingIterator).item());
	}

	public Iterator<Predicate> getPredicates() {

		return(new DescendingIterator<Subject, Predicate> (this.getSubjects()) {

			@Override
			public Iterator<Predicate> descend(Subject subject) {

				return(subject.getPredicates());
			}
		});
	}

	public Iterator<Reference> getReferences() {

		return(new DescendingIterator<Subject, Reference> (this.getSubjects()) {

			@Override
			public Iterator<Reference> descend(Subject subject) {

				return(subject.getReferences());
			}
		});
	}

	public Iterator<Literal> getLiterals() {

		return(new DescendingIterator<Subject, Literal> (this.getSubjects()) {

			@Override
			public Iterator<Literal> descend(Subject subject) {

				return(subject.getLiterals());
			}
		});
	}

	public Iterator<Graph> getInnerGraphs() {

		return(new DescendingIterator<Subject, Graph> (this.getSubjects()) {

			@Override
			public Iterator<Graph> descend(Subject subject) {

				return(subject.getInnerGraphs());
			}
		});
	}

	public Iterator<GraphComponent> getGraphComponents(boolean deep) {

		List<Iterator<GraphComponent>> iterators = new ArrayList<Iterator<GraphComponent>> ();

		iterators.add(new CastingIterator<GraphComponent> (this.getSubjects()));
		iterators.add(new CastingIterator<GraphComponent> (this.getPredicates()));
		iterators.add(new CastingIterator<GraphComponent> (this.getReferences()));
		iterators.add(new CastingIterator<GraphComponent> (this.getLiterals()));
		iterators.add(new CastingIterator<GraphComponent> (this.getInnerGraphs()));

		if (deep) {

			iterators.add(new DescendingIterator<Graph, GraphComponent> (this.getInnerGraphs()) {

				@Override
				public Iterator<GraphComponent> descend(Graph item) {

					return(item.getGraphComponents(true));
				}
			});
		}

		return(new CompositeIterator<GraphComponent> (iterators.iterator()));
	}

	public Iterator<GraphComponent> getGraphComponents(boolean deep, final Class<? extends GraphComponent> clazz) {

		return(new SelectingIterator<GraphComponent>(this.getGraphComponents(deep)) {

			@Override
			public boolean select(GraphComponent item) {

				return(clazz.isInstance(item));
			}
		});
	}

	public Iterator<GraphComponent> getLeafGraphComponents(boolean deep) {

		return(new SelectingIterator<GraphComponent> (this.getGraphComponents(deep)) {

			@Override
			public boolean select(GraphComponent item) {

				return(item.isLeaf());
			}
		});
	}

	public Iterator<GraphComponent> getLeafGraphComponents(boolean deep, final Class<? extends GraphComponent> clazz) {

		return(new SelectingIterator<GraphComponent> (this.getGraphComponents(deep, clazz)) {

			@Override
			public boolean select(GraphComponent item) {

				return(item.isLeaf());
			}
		});
	}

	public boolean containsSubjects() {

		return(this.getSubjectCount() > 0);
	}

	public boolean containsSubject(XRI3Segment subjectXri) {

		return(this.getSubject(subjectXri) != null);
	}

	public int getSubjectCount() {

		return(new IteratorCounter(this.getSubjects()).count());
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

	public int getGraphComponentCount(boolean deep) {

		return(new IteratorCounter(this.getGraphComponents(deep)).count());
	}

	/*
	 * Methods related to statements
	 */

	public synchronized Statement createStatement(XRI3Segment subjectXri) {

		Subject subject = this.createSubject(subjectXri);

		return(new FirstIteratorItem<Statement> (subject.getStatements()).item());
	}

	public synchronized Statement createStatement(XRI3Segment subjectXri, XRI3Segment predicateXri) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) subject = this.createSubject(subjectXri);

		return(subject.createStatement(predicateXri));
	}

	public synchronized Statement createStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, XRI3Segment referenceXri) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) subject = this.createSubject(subjectXri);

		return(subject.createStatement(predicateXri, referenceXri));
	}

	public synchronized Statement createStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, String data) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) subject = this.createSubject(subjectXri);

		return(subject.createStatement(predicateXri, data));
	}

	public synchronized Statement createStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, Graph innerGraph) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) subject = this.createSubject(subjectXri);

		return(subject.createStatement(predicateXri, innerGraph));
	}

	public Iterator<Statement> getStatements() {

		return(new DescendingIterator<Subject, Statement> (this.getSubjects()) {

			@Override
			public Iterator<Statement> descend(Subject subject) {

				return(subject.getStatements());
			}
		});
	}

	public Iterator<Statement> getStatements(XRI3Segment subjectXri) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) return(new EmptyIterator<Statement> ());

		return(subject.getStatements());
	}

	public Statement getStatement(XRI3Segment subjectXri) {

		return(new FirstIteratorItem<Statement> (this.getStatements(subjectXri)).item());
	}

	public Iterator<Statement> getStatements(XRI3Segment subjectXri, XRI3Segment predicateXri) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) return(new EmptyIterator<Statement> ());

		return(subject.getStatements(predicateXri));
	}

	public Statement getStatement(XRI3Segment subjectXri, XRI3Segment predicateXri) {

		return(new FirstIteratorItem<Statement> (this.getStatements(subjectXri, predicateXri)).item());
	}

	public Statement getStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, XRI3Segment referenceXri) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) return(null);

		return(subject.getStatement(predicateXri, referenceXri));
	}

	public Statement getStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, String data) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) return(null);

		return(subject.getStatement(predicateXri, data));
	}

	public Statement getStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, Graph innerGraph) {

		Subject subject = this.getSubject(subjectXri);
		if (subject == null) return(null);

		return(subject.getStatement(predicateXri, innerGraph));
	}

	public boolean containsStatement(Statement statement) {

		Subject subject = statement.getSubject();

		if (statement.containsPredicate()) {

			Predicate predicate = statement.getPredicate();

			if (statement.containsReference()) {

				Reference reference = statement.getReference();

				return(this.containsStatement(subject.getSubjectXri(), predicate.getPredicateXri(), reference.getReferenceXri()));
			} else if (statement.containsLiteral()) {

				Literal literal = statement.getLiteral();

				return(this.containsStatement(subject.getSubjectXri(), predicate.getPredicateXri(), literal.getData()));
			} else if (statement.containsInnerGraph()) {

				Graph innerGraph = statement.getInnerGraph();

				return(this.containsStatement(subject.getSubjectXri(), predicate.getPredicateXri(), innerGraph));
			} else {

				return(this.containsStatement(subject.getSubjectXri(), predicate.getPredicateXri()));
			}
		} else {

			return(this.containsSubject(subject.getSubjectXri()));
		}
	}

	public boolean containsStatement(XRI3Segment subjectXri) {

		return(this.getStatement(subjectXri) != null);
	}

	public boolean containsStatement(XRI3Segment subjectXri, XRI3Segment predicateXri) {

		return(this.getStatement(subjectXri, predicateXri) != null);
	}

	public boolean containsStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, XRI3Segment referenceXri) {

		return(this.getStatement(subjectXri, predicateXri, referenceXri) != null);
	}

	public boolean containsStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, String data) {

		return(this.getStatement(subjectXri, predicateXri, data) != null);
	}

	public boolean containsStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, Graph innerGraph) {

		return(this.getStatement(subjectXri, predicateXri, innerGraph) != null);
	}

	public synchronized void deleteStatements() {

		for (Iterator<Statement> statements = this.getStatements(); statements.hasNext(); ) {

			Statement statement = statements.next();
			statement.delete();
		}
	}

	public synchronized void deleteStatements(XRI3Segment subjectXri) {

		for (Iterator<Statement> statements = this.getStatements(subjectXri); statements.hasNext(); ) {

			Statement statement = statements.next();
			statement.delete();
		}
	}

	public synchronized void deleteStatements(XRI3Segment subjectXri, XRI3Segment predicateXri) {

		for (Iterator<Statement> statements = this.getStatements(subjectXri, predicateXri); statements.hasNext(); ) {

			Statement statement = statements.next();
			statement.delete();
		}
	}

	public synchronized void deleteStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, XRI3Segment referenceXri) {

		Statement statement = this.getStatement(subjectXri, predicateXri, referenceXri);
		if (statement != null) statement.delete();
	}

	public synchronized void deleteStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, String data) {

		Statement statement = this.getStatement(subjectXri, predicateXri, data);
		if (statement != null) statement.delete();
	}

	public synchronized void deleteStatement(XRI3Segment subjectXri, XRI3Segment predicateXri, Graph innerGraph) {

		Statement statement = this.getStatement(subjectXri, predicateXri, innerGraph);
		if (statement != null) statement.delete();
	}

	public int getStatementCount() {

		return(new IteratorCounter(this.getStatements()).count());
	}

	public int getStatementCount(XRI3Segment subjectXri) {

		return(new IteratorCounter(this.getStatements(subjectXri)).count());
	}

	public int getStatementCount(XRI3Segment subjectXri, XRI3Segment predicateXri) {

		return(new IteratorCounter(this.getStatements(subjectXri, predicateXri)).count());
	}

	public Statement getStatement() {

		return(this.statement);
	}

	/*
	 * Methods related to messages
	 */

	public Graph applyOperation(Graph operationGraph, XRI3Segment operationXri) throws MessagingException {

		MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(operationGraph, operationXri);
		MessageResult messageResult = MessageResult.newInstance();

		GraphMessagingTarget messagingTarget = new GraphMessagingTarget();
		messagingTarget.setGraph(this);

		messagingTarget.execute(messageEnvelope, messageResult, new ExecutionContext());

		return messageResult.getGraph();
	}

	public Graph applyOperation(XRI3 address, XRI3Segment operationXri) throws MessagingException {

		Graph operationGraph = Addressing.convertAddressToGraph(address);

		return this.applyOperation(operationGraph, operationXri);
	}

	/*
	 * Methods related to comments
	 */

	@SuppressWarnings("unchecked")
	public Iterator<String> getComments() {

		Iterator<String>[] iterators = new Iterator[3];

		iterators[0] = new SingleItemIterator<String> (this.getPreComment());
		iterators[1] = new SingleItemIterator<String> (this.getPostComment());

		iterators[2] = new DescendingIterator<Subject, String> (this.getSubjects()) {

			@Override
			public Iterator<String> descend(Subject item) {

				return(item.getComments());
			}
		};

		return(new CompositeIterator<String> (iterators));
	}

	public int getCommentCount() {

		return(new IteratorCounter(this.getComments()).count());
	}

	/*
	 * Methods related to transactions.
	 */

	public void beginTransaction() {

	}

	public void commitTransaction() {

	}

	public void rollbackTransaction() {

	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return(this.toString(XDIWriterRegistry.getDefault().getFormat(), null));
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Graph)) return(false);
		if (object == this) return(true);

		Graph other = (Graph) object;

		// two graphs are equal if all statements in one graph also exist in the other graph

		for (Iterator<Statement> i = this.getStatements(); i.hasNext(); ) {

			Statement statement = i.next();

			if (! (other.containsStatement(statement))) return(false);
		}

		for (Iterator<Statement> i = other.getStatements(); i.hasNext(); ) {

			Statement statement = i.next();

			if (! (this.containsStatement(statement))) return(false);
		}

		return(true);
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		for (Iterator<Statement> i = this.getStatements(); i.hasNext(); ) {

			hashCode = (hashCode * 31) + i.next().hashCode();
		}

		return(hashCode);
	}

	public int compareTo(Graph other) {

		if (other == null || other == this) return(0);

		int thisStatementCount = this.getStatementCount();
		int otherStatementCount = other.getStatementCount();

		if (thisStatementCount < otherStatementCount) {

			return(-1);
		} else if (thisStatementCount > otherStatementCount) {

			return(1);
		} else {

			return(0);
		}
	}

	/**
	 * A class representing a statement that contains a subject, predicate and inner graph.
	 */
	private Statement statement = new AbstractStatement() {

		private static final long serialVersionUID = 403933315836123089L;

		public Graph getContainingGraph() {

			return(AbstractGraph.this.getContainingGraph());
		}

		public Subject getSubject() {

			return(AbstractGraph.this.getPredicate().getSubject());
		}

		public Predicate getPredicate() {

			return(AbstractGraph.this.getPredicate());
		}

		public Reference getReference() {

			return(null);
		}

		public Literal getLiteral() {

			return(null);
		}

		public Graph getInnerGraph() {

			return(AbstractGraph.this);
		}
	};
}
