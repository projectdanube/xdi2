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
package xdi2.util;

import java.util.Iterator;

import org.eclipse.higgins.xdi4j.Graph;
import org.eclipse.higgins.xdi4j.GraphComponent;
import org.eclipse.higgins.xdi4j.Literal;
import org.eclipse.higgins.xdi4j.Predicate;
import org.eclipse.higgins.xdi4j.Reference;
import org.eclipse.higgins.xdi4j.Statement;
import org.eclipse.higgins.xdi4j.Subject;
import org.eclipse.higgins.xdi4j.impl.memory.MemoryGraphFactory;
import org.eclipse.higgins.xdi4j.util.CopyStatementStrategy;
import org.eclipse.higgins.xdi4j.util.CopyUtil;
import org.eclipse.higgins.xdi4j.xri3.impl.XRI3Segment;

import xdi2.util.iterators.FirstIteratorItem;

/**
 * Various utility methods for copying statements between graphs.
 * 
 * @author msabadello at parityinc dot net
 */
public final class CopyUtil {

	private CopyUtil() { }

	/**
	 * Copies all statements of a graph component to another graph.
	 * @param graphComponent The graph component (e.g. graph, subject, predicate, reference) to copy.
	 * @param target The destination graph for the statements.
	 * @param copyStatementStrategy The strategy to use to determine the statements to be copied. If null, all statements will be copied.
	 * @return The newly created graph component with the copied statements.
	 */
	public static GraphComponent copyStatements(GraphComponent graphComponent, Graph target, CopyStatementStrategy copyStatementStrategy) {

		if (graphComponent instanceof Graph) {

			Graph graph = (Graph) graphComponent;

			copyStatements(graph.getStatements(), target, copyStatementStrategy);
			return(target);
		} else if (graphComponent instanceof Subject) {

			Subject subject = (Subject) graphComponent;

			copyStatements(subject.getStatements(), target, copyStatementStrategy);
			return(target.getSubject(subject.getSubjectXri()));
		} else if (graphComponent instanceof Predicate) {

			Predicate predicate = (Predicate) graphComponent;
			Subject subject = predicate.getSubject();

			copyStatements(predicate.getStatements(), target, copyStatementStrategy);
			return(target.getSubject(subject.getSubjectXri()).getPredicate(predicate.getPredicateXri()));
		} else if (graphComponent instanceof Reference) {

			Reference reference = (Reference) graphComponent;
			Predicate predicate = reference.getPredicate();
			Subject subject = predicate.getSubject();

			copyStatement(reference.getStatement(), target, copyStatementStrategy);
			return(target.getSubject(subject.getSubjectXri()).getPredicate(predicate.getPredicateXri()).getReference(reference.getReferenceXri()));
		} else if (graphComponent instanceof Literal) {

			Literal literal = (Literal) graphComponent;
			Predicate predicate = literal.getPredicate();
			Subject subject = predicate.getSubject();

			copyStatement(literal.getStatement(), target, copyStatementStrategy);
			return(target.getSubject(subject.getSubjectXri()).getPredicate(predicate.getPredicateXri()).getLiteral());
		} else {

			return(null);
		}
	}

	/**
	 * Copies all statements of a graph component to another subject.
	 * @param graphComponent The graph component (e.g. graph, subject, predicate, reference) to copy.
	 * @param target The destination subject for the statements.
	 * @param copyStatementStrategy The strategy to use to determine the statements to be copied. If null, all statements will be copied.
	 */
	public static void copyStatements(GraphComponent graphComponent, Subject target, CopyStatementStrategy copyStatementStrategy) {

		if (graphComponent instanceof Graph) {

			copyStatements(((Graph) graphComponent).getStatements(), target, copyStatementStrategy);
		} else if (graphComponent instanceof Subject) {

			copyStatements(((Subject) graphComponent).getStatements(), target, copyStatementStrategy);
		} else if (graphComponent instanceof Predicate) {

			copyStatements(((Predicate) graphComponent).getStatements(), target, copyStatementStrategy);
		} else if (graphComponent instanceof Reference) {

			copyStatement(((Reference) graphComponent).getStatement(), target, copyStatementStrategy);
		} else if (graphComponent instanceof Literal) {

			copyStatement(((Literal) graphComponent).getStatement(), target, copyStatementStrategy);
		}
	}

	/**
	 * Copies statements to an existing graph.
	 * @param statements An iterator over statement from any graph.
	 * @param target The destination graph for the statements.
	 * @param copyStatementStrategy The strategy to use to determine the statements to be copied. If null, all statements will be copied.
	 */
	public static void copyStatements(Iterator<Statement> statements, Graph target, CopyStatementStrategy copyStatementStrategy) {

		for (; statements.hasNext(); ) {

			Statement statement = statements.next();

			CopyUtil.copyStatement(statement, target, copyStatementStrategy);
		}
	}

	/**
	 * Copies statements to an existing subject.
	 * @param statements An iterator over statement from any graph.
	 * @param target The destination subject for the statements.
	 * @param copyStatementStrategy The strategy to use to determine the statements to be copied. If null, all statements will be copied.
	 */
	public static void copyStatements(Iterator<Statement> statements, Subject target, CopyStatementStrategy copyStatementStrategy) {

		for (; statements.hasNext(); ) {

			Statement statement = statements.next();

			if (copyStatementStrategy != null && ! copyStatementStrategy.doCopy(statement, target.getContainingGraph())) continue;

			CopyUtil.copyStatement(statement, target, null);
		}
	}

	/**
	 * Copies a statement to an existing graph.
	 * @param statement A statement from any graph.
	 * @param target The destination graph for the statement.
	 * @param copyStatementStrategy The strategy to use to determine the statements to be copied. If null, all statements will be copied.
	 */
	public static Statement copyStatement(Statement statement, Graph target, CopyStatementStrategy copyStatementStrategy) {

		if (statement == null) return(null);

		if (copyStatementStrategy == null) copyStatementStrategy = ALLCOPYSTATEMENTSTRATEGY;
		if (! copyStatementStrategy.doCopy(statement, target)) return(null);
		statement = copyStatementStrategy.replaceStatement(statement);

		// read subject xri and comment

		XRI3Segment subjectXri = copyStatementStrategy.replaceSubjectXri(statement.getSubject());

		if (statement.containsPredicate()) {

			// if subject does not exist already, create it

			Subject subject = target.getSubject(subjectXri);

			if (subject == null) {

				subject = target.createSubject(subjectXri);
				if (statement.getSubject().containsPreComment()) subject.setPreComment(statement.getSubject().getPreComment());
				if (statement.getSubject().containsPostComment()) subject.setPostComment(statement.getSubject().getPostComment());
			}

			// read predicate xri and comment

			XRI3Segment predicateXri = copyStatementStrategy.replacePredicateXri(statement.getPredicate());

			if (statement.containsReference()) {

				// if predicate does not exist already, create it

				Predicate predicate = subject.getPredicate(predicateXri);

				if (predicate == null) {

					predicate = subject.createPredicate(predicateXri);
					if (statement.getPredicate().containsPreComment()) predicate.setPreComment(statement.getPredicate().getPreComment());
					if (statement.getPredicate().containsPostComment()) predicate.setPostComment(statement.getPredicate().getPostComment());
				}

				// read reference xri and comment

				XRI3Segment referenceXri = copyStatementStrategy.replaceReferenceXri(statement.getReference());

				// create reference

				Reference reference = predicate.createReference(referenceXri);
				if (statement.getReference().containsPreComment()) reference.setPreComment(statement.getReference().getPreComment());
				if (statement.getReference().containsPostComment()) reference.setPostComment(statement.getReference().getPostComment());

				return(reference.getStatement());
			} else if (statement.containsLiteral()) {

				// if predicate does not exist already, create it

				Predicate predicate = subject.getPredicate(predicateXri);

				if (predicate == null) {

					predicate = subject.createPredicate(predicateXri);
					if (statement.getPredicate().containsPreComment()) predicate.setPreComment(statement.getPredicate().getPreComment());
					if (statement.getPredicate().containsPostComment()) predicate.setPostComment(statement.getPredicate().getPostComment());
				}

				// read literal data and comment

				String data = copyStatementStrategy.replaceLiteralData(statement.getLiteral());

				// create literal

				Literal literal = predicate.createLiteral(data);
				if (statement.getLiteral().containsPreComment()) literal.setPreComment(statement.getLiteral().getPreComment());
				if (statement.getLiteral().containsPostComment()) literal.setPostComment(statement.getLiteral().getPostComment());

				return(literal.getStatement());
			} else if (statement.containsInnerGraph()) {

				// if predicate does not exist already, create it

				Predicate predicate = subject.getPredicate(predicateXri);

				if (predicate == null) {

					predicate = subject.createPredicate(predicateXri);
					if (statement.getPredicate().containsPreComment()) predicate.setPreComment(statement.getPredicate().getPreComment());
					if (statement.getPredicate().containsPostComment()) predicate.setPostComment(statement.getPredicate().getPostComment());
				}

				// read inner graph and comment

				Graph innerGraph = statement.getInnerGraph();
				if (copyStatementStrategy != null) innerGraph = copyStatementStrategy.replaceInnerGraph(innerGraph);

				// create inner graph

				Graph graph = predicate.getInnerGraph();
				if (graph == null) graph = predicate.createInnerGraph(null);
				copyStatements(innerGraph, graph, copyStatementStrategy);
				if (statement.getInnerGraph().containsPreComment()) innerGraph.setPreComment(statement.getInnerGraph().getPreComment());
				if (statement.getInnerGraph().containsPostComment()) innerGraph.setPostComment(statement.getInnerGraph().getPostComment());

				return(graph.getStatement());
			} else {

				// create predicate

				Predicate predicate = subject.createPredicate(predicateXri);
				if (statement.getPredicate().containsPreComment()) predicate.setPreComment(statement.getPredicate().getPreComment());
				if (statement.getPredicate().containsPostComment()) predicate.setPostComment(statement.getPredicate().getPostComment());

				return(new FirstIteratorItem<Statement> (predicate.getStatements()).item());
			}
		} else {

			// create subject

			Subject subject = target.createSubject(subjectXri);
			if (statement.getSubject().containsPreComment()) subject.setPreComment(statement.getSubject().getPreComment());
			if (statement.getSubject().containsPostComment()) subject.setPostComment(statement.getSubject().getPostComment());

			return(new FirstIteratorItem<Statement> (subject.getStatements()).item());
		}
	}

	/**
	 * Copies a statement to an existing subject.
	 * @param statement A statement from any graph.
	 * @param target The destination subject for the statement.
	 * @param copyStatementStrategy The strategy to use to determine the statements to be copied. If null, all statements will be copied.
	 */
	public static Statement copyStatement(Statement statement, Subject target, CopyStatementStrategy copyStatementStrategy) {

		if (copyStatementStrategy == null) copyStatementStrategy = ALLCOPYSTATEMENTSTRATEGY;
		if (! copyStatementStrategy.doCopy(statement, target.getContainingGraph())) return(null);
		statement = copyStatementStrategy.replaceStatement(statement);

		if (statement.containsPredicate()) {

			XRI3Segment predicateXri = copyStatementStrategy.replacePredicateXri(statement.getPredicate());

			Predicate predicate = target.getPredicate(predicateXri);

			if (predicate == null) {

				predicate = target.createPredicate(predicateXri);
				if (statement.getPredicate().containsPreComment()) predicate.setPreComment(statement.getPredicate().getPreComment());
				if (statement.getPredicate().containsPostComment()) predicate.setPostComment(statement.getPredicate().getPostComment());
			}

			if (statement.containsReference()) {

				XRI3Segment referenceXri = copyStatementStrategy.replaceReferenceXri(statement.getReference());

				Reference reference = predicate.createReference(referenceXri);
				if (statement.getReference().containsPreComment()) reference.setPreComment(statement.getReference().getPreComment());
				if (statement.getReference().containsPostComment()) reference.setPostComment(statement.getReference().getPostComment());

				return(reference.getStatement());
			} else if (statement.containsLiteral()) {

				String data = copyStatementStrategy.replaceLiteralData(statement.getLiteral());

				Literal literal = predicate.createLiteral(data);
				if (statement.getLiteral().containsPreComment()) literal.setPreComment(statement.getLiteral().getPreComment());
				if (statement.getLiteral().containsPostComment()) literal.setPostComment(statement.getLiteral().getPostComment());

				return(literal.getStatement());
			} else if (statement.containsInnerGraph()) {

				Graph innerGraph = copyStatementStrategy.replaceInnerGraph(statement.getInnerGraph());

				Graph graph = predicate.createInnerGraph(innerGraph);
				if (statement.getInnerGraph().containsPreComment()) innerGraph.setPreComment(statement.getInnerGraph().getPreComment());
				if (statement.getInnerGraph().containsPostComment()) innerGraph.setPostComment(statement.getInnerGraph().getPostComment());

				return(graph.getStatement());
			} else {

				return(new FirstIteratorItem<Statement> (predicate.getStatements()).item());
			}
		} else {

			return(new FirstIteratorItem<Statement> (target.getStatements()).item());
		}
	}

	public static Graph graphComponentToGraph(GraphComponent graphComponent) {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		copyStatements(graphComponent, graph, null);

		return(graph);
	}

	/**
	 * An interface that can determine whether a statement should be copied by the CopyUtil methods.
	 * @author msabadello at parityinc dot net
	 */
	public static abstract class CopyStatementStrategy {

		/**
		 * Check whether the given statement should be copied.
		 */
		public abstract boolean doCopy(Statement statement, Graph target);

		/**
		 * Strategies can replace the whole statement that is being copied.
		 * @param statement The original statement.
		 * @return The replacement.
		 */
		public Statement replaceStatement(Statement statement) {

			return(statement);
		}

		/**
		 * Strategies can replace the subject XRI when a statement is copied.
		 * @param subject The original subject.
		 * @return The replacement.
		 */
		public XRI3Segment replaceSubjectXri(Subject subject) {

			return(subject.getSubjectXri());
		}

		/**
		 * Strategies can replace the predicate XRI when a statement is copied.
		 * @param predicate The original predicate.
		 * @return The replacement.
		 */
		public XRI3Segment replacePredicateXri(Predicate predicate) {

			return(predicate.getPredicateXri());
		}

		/**
		 * Strategies can replace the reference XRI when a statement is copied.
		 * @param reference The original reference.
		 * @return The replacement.
		 */
		public XRI3Segment replaceReferenceXri(Reference reference) {

			return(reference.getReferenceXri());
		}

		/**
		 * Strategies can replace the literal data when a statement is copied.
		 * @param subjectXri The original literal data.
		 * @return The replacement.
		 */
		public String replaceLiteralData(Literal literal) {

			return(literal.getData());
		}

		/**
		 * Strategies can replace the inner graph when a statement is copied.
		 * @param subjectXri The original inner graph.
		 * @return The replacement.
		 */
		public Graph replaceInnerGraph(Graph innerGraph) {

			return(innerGraph);
		}
	}

	/**
	 * The default strategy that copies everything.
	 */
	public static CopyStatementStrategy ALLCOPYSTATEMENTSTRATEGY = new CopyStatementStrategy() {

		@Override
		public boolean doCopy(Statement statement, Graph target) {

			return true;
		}
	};

	/**
	 * A strategy that copies only statements that don't yet exist in a target
	 * graph.
	 */
	public static CopyStatementStrategy SAFECOPYSTATEMENTSTRATEGY = new CopyStatementStrategy() {

		@Override
		public boolean doCopy(Statement statement, Graph target) {

			return(! target.containsStatement(statement));
		}
	};
}
