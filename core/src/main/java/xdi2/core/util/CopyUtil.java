package xdi2.core.util;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.xri3.impl.XRI3Segment;

/**
 * Various utility methods for copying statements between graphs.
 * 
 * @author markus
 */
public final class CopyUtil {

	private static final CopyStrategy allCopyStrategy = new AllCopyStrategy();

	private CopyUtil() { }

	/**
	 * Copies a whole graph into another graph.
	 * @param graph A graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 */
	public static void copyGraph(Graph graph, Graph targetGraph, CopyStrategy copyStrategy) {

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

		if (contextNode == null) return null;

		ContextNode targetContextNode;

		if (contextNode.isRootContextNode()) {

			targetContextNode = targetGraph.getRootContextNode();
		} else {

			XRI3Segment parentContextNodeXri = contextNode.getContextNode().getXri();
			ContextNode targetParentContextNode = targetGraph.findContextNode(parentContextNodeXri, true);
			targetContextNode = targetParentContextNode.createContextNode(contextNode.getArcXri());
		}

		CopyUtil.copyContextNodeContents(contextNode, targetContextNode, copyStrategy);

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

		if (relation == null) return null;

		XRI3Segment parentContextNodeXri = relation.getContextNode().getXri();
		ContextNode targetParentContextNode = targetGraph.findContextNode(parentContextNodeXri, true);
		Relation targetRelation = targetParentContextNode.createRelation(relation.getArcXri(), relation.getTargetContextNodeXri());

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

		if (literal == null) return null;

		XRI3Segment parentContextNodeXri = literal.getContextNode().getXri();
		ContextNode targetParentContextNode = targetGraph.findContextNode(parentContextNodeXri, true);
		Literal targetliteral = targetParentContextNode.createLiteral(literal.getLiteralData());

		return targetliteral;
	}

	/**
	 * Copies the contents of a context node (context nodes, relations, and the literal) into a target context node.
	 * @param contextNode A context node from any graph.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 */
	public static void copyContextNodeContents(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) return;

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

		if (contextNode == null) return null;
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

			ContextNode innerContextNode = innerContextNodes.next();
			if ((innerContextNode = copyStrategy.replaceContextNode(innerContextNode)) == null) continue;

			ContextNode targetInnerContextNode = targetContextNode.getContextNode(innerContextNode.getArcXri());
			if (targetInnerContextNode == null) targetInnerContextNode = targetContextNode.createContextNode(innerContextNode.getArcXri());

			copyContextNodeContents(innerContextNode, targetInnerContextNode, copyStrategy);
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

		if (contextNode == null) return null;
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		for (Iterator<Relation> relations = contextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();
			if ((relation = copyStrategy.replaceRelation(relation)) == null) continue;

			targetContextNode.createRelation(relation.getArcXri(), relation.getTargetContextNodeXri());
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

		if (contextNode == null) return null;
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return null;
		if ((literal = copyStrategy.replaceLiteral(literal)) == null) return null;

		targetContextNode.createLiteral(literal.getLiteralData());

		return targetContextNode.getLiteral();
	}

	/**
	 * An interface that can determine what to copy and what not.
	 */
	public static interface CopyStrategy {

		/**
		 * Strategies can replace a context node that is being copied.
		 * @param contextNode The original context node.
		 * @return The replacement (or null if it should not be copied).
		 */
		public abstract ContextNode replaceContextNode(ContextNode contextNode);

		/**
		 * Strategies can replace a relation that is being copied.
		 * @param relation The original relation.
		 * @return The replacement (or null if it should not be copied).
		 */
		public abstract Relation replaceRelation(Relation relation);

		/**
		 * Strategies can replace a literal that is being copied.
		 * @param literal The original literal.
		 * @return The replacement (or null if it should not be copied).
		 */
		public abstract Literal replaceLiteral(Literal literal);
	}

	/**
	 * The default strategy that copies everything.
	 */
	public static class AllCopyStrategy implements CopyStrategy {

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			return contextNode;
		}

		@Override
		public Relation replaceRelation(Relation relation) {

			return relation;
		}

		@Override
		public Literal replaceLiteral(Literal literal) {

			return literal;
		}
	}

	/**
	 * A strategy that does not copy arcs that already exist in a target graph
	 */
	public static class NoDuplicatesCopyStrategy implements CopyStrategy {

		private Graph targetGraph;

		public NoDuplicatesCopyStrategy(Graph targetGraph) {

			this.targetGraph = targetGraph;
		}

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			if (contextNode == null) throw new NullPointerException();

			if (this.targetGraph.containsContextNode(contextNode.getXri())) return null;

			return contextNode;
		}

		@Override
		public Relation replaceRelation(Relation relation) {

			if (relation == null) throw new NullPointerException();

			if (this.targetGraph.containsRelation(relation.getContextNode().getXri(), relation.getArcXri(), relation.getTargetContextNodeXri())) return null;

			return relation;
		}

		@Override
		public Literal replaceLiteral(Literal literal) {

			if (literal == null) throw new NullPointerException();

			if (this.targetGraph.containsLiteral(literal.getContextNode().getXri())) return null;

			return literal;
		}
	}
}
