package xdi2.util;

import java.util.Iterator;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.xri3.impl.XRI3Authority;

/**
 * Various utility methods for copying statements between graphs.
 * 
 * @author markus
 */
public final class CopyUtil {

	private CopyUtil() { }

	/**
	 * Copies a context node into another graph.
	 * @param contextNode A context node from any graph.
	 * @param targetContextNode The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return
	 */
	public static ContextNode copyContextNode(ContextNode contextNode, Graph targetGraph, CopyStrategy copyStrategy) {

		if (contextNode == null) return null;
		if (contextNode.isRootContextNode()) return null;

		XRI3Authority parentContextNodeXri = contextNode.getContextNode().getXri();
		ContextNode targetParentContextNode = targetGraph.findContextNode(parentContextNodeXri, true);
		ContextNode targetContextNode = targetParentContextNode.createContextNode(contextNode.getArcXri());

		CopyUtil.copyContextNodeContents(contextNode, targetContextNode, copyStrategy);

		return targetContextNode;
	}

	/**
	 * Copies a relation into another graph.
	 * @param relation A relation from any graph.
	 * @param targetContextNode The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return
	 */
	public static Relation copyRelation(Relation relation, Graph targetGraph, CopyStrategy copyStrategy) {

		if (relation == null) return null;

		XRI3Authority parentContextNodeXri = relation.getContextNode().getXri();
		ContextNode targetParentContextNode = targetGraph.findContextNode(parentContextNodeXri, true);
		Relation targetRelation = targetParentContextNode.createRelation(relation.getArcXri(), relation.getRelationXri());

		return targetRelation;
	}

	/**
	 * Copies a literal into another graph.
	 * @param literal A literal from any graph.
	 * @param targetContextNode The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return
	 */
	public static Literal copyLiteral(Literal literal, Graph targetGraph, CopyStrategy copyStrategy) {

		if (literal == null) return null;

		XRI3Authority parentContextNodeXri = literal.getContextNode().getXri();
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
	 */
	public static Iterator<ContextNode> copyContextNodes(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) return null;
		if (copyStrategy == null) copyStrategy = ALLCOPYSTRATEGY;

		for (Iterator<ContextNode> innerContextNodes = contextNode.getContextNodes(); innerContextNodes.hasNext(); ) {

			ContextNode innerContextNode = innerContextNodes.next();
			innerContextNode = copyStrategy.replaceContextNode(innerContextNode);

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
	 */
	public static Iterator<Relation> copyRelations(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) return null;
		if (copyStrategy == null) copyStrategy = ALLCOPYSTRATEGY;

		for (Iterator<Relation> relations = contextNode.getRelations(); relations.hasNext(); ) {

			Relation relation = relations.next();
			if ((relation = copyStrategy.replaceRelation(relation)) == null) continue;

			targetContextNode.createRelation(relation.getArcXri(), relation.getRelationXri());
		}

		return targetContextNode.getRelations();
	}

	/**
	 * Copies a literal of a context node into a target context node.
	 * @param contextNode A context node from any graph.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 */
	public static Literal copyLiteral(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) return null;
		if (copyStrategy == null) copyStrategy = ALLCOPYSTRATEGY;

		Literal literal = contextNode.getLiteral();
		if ((literal = copyStrategy.replaceLiteral(literal)) == null) return null;

		targetContextNode.createLiteral(literal.getLiteralData());

		return targetContextNode.getLiteral();
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

			return contextNode;
		}

		/**
		 * Strategies can replace a relation that is being copied.
		 * @param relation The original relation.
		 * @return The replacement (or null if it should not be copied).
		 */
		public Relation replaceRelation(Relation relation) {

			return relation;
		}

		/**
		 * Strategies can replace a literal that is being copied.
		 * @param literal The original literal.
		 * @return The replacement (or null if it should not be copied).
		 */
		public Literal replaceLiteral(Literal literal) {

			return literal;
		}
	}

	/**
	 * 
	 * The default strategy that copies everything.
	 */
	public static final CopyStrategy ALLCOPYSTRATEGY = new CopyStrategy() {

	};
}
