package xdi2.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.xri3.XDI3Segment;

/**
 * Various utility methods for copying statements between graphs.
 * 
 * @author markus
 */
public final class CopyUtil {

	private static final Logger log = LoggerFactory.getLogger(CopyUtil.class);

	private static final CopyStrategy allCopyStrategy = new AllCopyStrategy();

	private CopyUtil() { }

	/**
	 * Copies a whole graph into another graph.
	 * @param graph A graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 */
	public static void copyGraph(Graph graph, Graph targetGraph, CopyStrategy copyStrategy) {

		if (graph == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		copyContextNodeContents(graph.getRootContextNode(), targetGraph.getRootContextNode(), copyStrategy);
	}

	/**
	 * Copies a context node into another graph.
	 * @param contextNode A context node from any graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied context node in the target graph.
	 */
	public static ContextNode copyContextNode(ContextNode contextNode, Graph targetGraph, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		ContextNode targetContextNode;

		if (contextNode.isRootContextNode()) {

			targetContextNode = targetGraph.getRootContextNode();
		} else {

			targetContextNode = targetGraph.setDeepContextNode(contextNode.getXri());
		}

		copyContextNodeContents(contextNode, targetContextNode, copyStrategy);

		return targetContextNode;
	}

	/**
	 * Copies a context node into another context node.
	 * @param contextNode A context node from any context node.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied context node in the target context node.
	 */
	public static ContextNode copyContextNode(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		if ((contextNode = copyStrategy.replaceContextNode(contextNode)) == null) return null;

		ContextNode targetInnerContextNode = targetContextNode.setContextNode(contextNode.getArcXri());

		copyContextNodeContents(contextNode, targetInnerContextNode, copyStrategy);

		return targetContextNode;
	}

	/**
	 * Copies a relation into another graph.
	 * @param relation A relation from any graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied relation in the target graph.
	 */
	public static Relation copyRelation(Relation relation, Graph targetGraph, CopyStrategy copyStrategy) {

		if (relation == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		XDI3Segment contextNodeXri = relation.getContextNode().getXri();
		ContextNode targetContextNode = targetGraph.setDeepContextNode(contextNodeXri);

		return copyRelation(relation, targetContextNode, copyStrategy);
	}

	/**
	 * Copies a relation into another context node.
	 * @param relation A relation from any context node.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied relation in the target context node.
	 */
	public static Relation copyRelation(Relation relation, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (relation == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		if ((relation = copyStrategy.replaceRelation(relation)) == null) return null;

		Relation targetRelation = targetContextNode.setRelation(relation.getArcXri(), relation.getTargetContextNodeXri());

		return targetRelation;
	}

	/**
	 * Copies a literal into another graph.
	 * @param literal A literal from any graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied literal in the target graph.
	 */
	public static Literal copyLiteral(Literal literal, Graph targetGraph, CopyStrategy copyStrategy) {

		if (literal == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		XDI3Segment contextNodeXri = literal.getContextNode().getXri();
		ContextNode targetContextNode = targetGraph.setDeepContextNode(contextNodeXri);

		return copyLiteral(literal, targetContextNode, copyStrategy);
	}

	/**
	 * Copies a literal into another context node.
	 * @param literal A literal from any context node.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied literal in the target context node.
	 */
	public static Literal copyLiteral(Literal literal, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (literal == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		if ((literal = copyStrategy.replaceLiteral(literal)) == null) return null;

		Literal targetLiteral = targetContextNode.setLiteral(literal.getLiteralData());

		return targetLiteral;
	}

	/**
	 * Copies a statement into another graph.
	 * @param statement A statement from any graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied statement in the target graph.
	 */
	public static Statement copyStatement(Statement statement, Graph targetGraph, CopyStrategy copyStrategy) {

		if (statement == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		if (statement instanceof ContextNodeStatement) return copyContextNode(((ContextNodeStatement) statement).getContextNode(), targetGraph, null).getStatement();
		if (statement instanceof RelationStatement) return copyRelation(((RelationStatement) statement).getRelation(), targetGraph, null).getStatement();
		if (statement instanceof LiteralStatement) return copyLiteral(((LiteralStatement) statement).getLiteral(), targetGraph, null).getStatement();

		return null;
	}

	/**
	 * Copies the contents of a context node (context nodes, relations, and the literal) into a target context node.
	 * @param contextNode A context node from any graph.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 */
	public static void copyContextNodeContents(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		copyContextNodes(contextNode, targetContextNode, copyStrategy);
		copyRelations(contextNode, targetContextNode, copyStrategy);
		copyLiteral(contextNode, targetContextNode, copyStrategy);
	}

	/**
	 * Copies all context nodes of a context node into a target context node.
	 * @param contextNode A context node from any graph.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied context nodes in the target graph.
	 */
	public static Iterator<ContextNode> copyContextNodes(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

			ContextNode innerContextNode = innerContextNodes.next();

			copyContextNode(innerContextNode, targetContextNode, copyStrategy);
		}

		return targetContextNode.getContextNodes();
	}

	/**
	 * Copies all relations of a context node into a target context node.
	 * @param contextNode A context node from any graph.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied relations in the target graph.
	 */
	public static Iterator<Relation> copyRelations(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		for (Iterator<Relation> relations = contextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();

			copyRelation(relation, targetContextNode, copyStrategy);
		}

		return targetContextNode.getRelations();
	}

	/**
	 * Copies a literal of a context node into a target context node.
	 * @param contextNode A context node from any graph.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied literal in the target graph.
	 */
	public static Literal copyLiteral(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return null;

		return copyLiteral(literal, targetContextNode, copyStrategy);
	}

	/**
	 * An interface that can determine what to copy and what not.
	 */
	public static abstract class CopyStrategy {

		/**
		 * Strategies can replace a context node that is being copied.
		 * @param contextNode The original context node.
		 * @return The replacement (or null if it should not be copied).
		 */
		public ContextNode replaceContextNode(ContextNode contextNode) {

			if (log.isTraceEnabled()) log.trace("Copying context node " + contextNode);

			return contextNode;
		}

		/**
		 * Strategies can replace a relation that is being copied.
		 * @param relation The original relation.
		 * @return The replacement (or null if it should not be copied).
		 */
		public Relation replaceRelation(Relation relation) {

			if (log.isTraceEnabled()) log.trace("Copying relation " + relation);

			return relation;
		}

		/**
		 * Strategies can replace a literal that is being copied.
		 * @param literal The original literal.
		 * @return The replacement (or null if it should not be copied).
		 */
		public Literal replaceLiteral(Literal literal) {

			if (log.isTraceEnabled()) log.trace("Copying literal " + literal);

			return literal;
		}
	}

	/**
	 * The default strategy that copies everything.
	 */
	public static class AllCopyStrategy extends CopyStrategy {

	}

	/**
	 * A strategy that excludes certain context nodes.
	 */
	public static class ExcludeContextNodesCopyStrategy extends CopyStrategy {

		private Set<ContextNode> excludeContextNodes;

		public ExcludeContextNodesCopyStrategy(Collection<ContextNode> excludeContextNodes) {

			this.excludeContextNodes = new HashSet<ContextNode> ();
			this.excludeContextNodes.addAll(excludeContextNodes);
		}

		public ExcludeContextNodesCopyStrategy() {

			this(Arrays.asList(new ContextNode[0]));
		}

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			if (this.excludeContextNodes.contains(contextNode)) return null;

			return contextNode;
		}

		public void addExcludeContextNode(ContextNode contextNode) {

			this.excludeContextNodes.add(contextNode);
		}
	}

	/**
	 * A strategy that excludes duplicate context nodes.
	 */
	public static class ExcludeDuplicateContextNodesCopyStrategy extends ExcludeContextNodesCopyStrategy {

		public ExcludeDuplicateContextNodesCopyStrategy() {

			super();
		}

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			try {

				return super.replaceContextNode(contextNode);
			} finally {

				this.addExcludeContextNode(contextNode);
			}
		}
	}
}
