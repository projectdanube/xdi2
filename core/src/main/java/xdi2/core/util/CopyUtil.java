package xdi2.core.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Node;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

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
	 * Copies a whole graph into a target graph.
	 * @param graph A graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 */
	public static void copyGraph(Graph graph, Graph targetGraph, CopyStrategy copyStrategy) {

		if (graph == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		if (graph == targetGraph) throw new Xdi2RuntimeException("Source and target graph cannot be the same.");

		copyContextNodeContents(graph.getRootContextNode(true), targetGraph, copyStrategy);
	}

	/*
	 * Methods for copying context nodes
	 */

	/**
	 * Copies a context node into a target graph.
	 * @param contextNode A context node from any graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied context node in the target graph.
	 */
	public static ContextNode copyContextNode(ContextNode contextNode, Graph targetGraph, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		if ((contextNode = copyStrategy.replaceContextNode(contextNode)) == null) return null;

		XDIAddress contextNodeXDIAddress = contextNode.getXDIAddress();

		ContextNode targetContextNode;

		if (contextNode.isRootContextNode()) {

			targetContextNode = targetGraph.getRootContextNode(false);
		} else {

			targetContextNode = targetGraph.setDeepContextNode(contextNodeXDIAddress);
		}

		copyContextNodeContents(contextNode, targetContextNode, copyStrategy);

		return targetContextNode;
	}

	/**
	 * Copies a context node into a target context node.
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

		if (contextNode.isRootContextNode()) throw new IllegalArgumentException("Cannot copy root context node.");
		
		XDIArc contextNodeXDIArc = contextNode.getXDIArc();

		ContextNode targetInnerContextNode = targetContextNode.setContextNode(contextNodeXDIArc);

		copyContextNodeContents(contextNode, targetInnerContextNode, copyStrategy);

		return targetContextNode;
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

	/*
	 * Methods for copying relations
	 */

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

		XDIAddress relationContextNodeXDIAddress = relation.getContextNode().getXDIAddress();
		ContextNode targetContextNode = targetGraph.setDeepContextNode(relationContextNodeXDIAddress);

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

		XDIAddress relationContextNodeXDIAddress = relation.getContextNode().getXDIAddress();
		XDIAddress relationXDIAddress = relation.getXDIAddress();
		XDIAddress relationTargetXDIAddress = relation.getTargetXDIAddress();

		XDIAddress targetXDIAddress = targetContextNode.getXDIAddress();

		XdiRoot relationContextNodeXdiRoot = XdiCommonRoot.findCommonRoot(relation.getContextNode().getGraph()).getRoot(relationContextNodeXDIAddress, false);
		XdiRoot targetContextNodeXdiRoot = XdiCommonRoot.findCommonRoot(targetContextNode.getGraph()).getRoot(targetXDIAddress, false);

		XDIAddress relativeRelationcontextNodeXDIAddress = relationContextNodeXdiRoot.absoluteToRelativeXDIAddress(relationContextNodeXDIAddress);
		XDIAddress relativeRelationTargetXDIAddress = relationContextNodeXdiRoot.absoluteToRelativeXDIAddress(relationTargetXDIAddress);
		XDIAddress relativeTargetXDIAddress = targetContextNodeXdiRoot.absoluteToRelativeXDIAddress(targetXDIAddress);

		Relation targetRelation;

		// check if this relation establishes an inner root

		if (relativeRelationTargetXDIAddress != null &&
				relativeRelationTargetXDIAddress.getNumXDIArcs() == 1 &&
				XdiInnerRoot.isValidXDIArc(relativeRelationTargetXDIAddress.getFirstXDIArc()) &&
				XdiInnerRoot.getSubjectOfInnerRootXDIArc(relativeRelationTargetXDIAddress.getFirstXDIArc()).equals(relativeRelationcontextNodeXDIAddress) &&
				XdiInnerRoot.getPredicateOfInnerRootXDIArc(relativeRelationTargetXDIAddress.getFirstXDIArc()).equals(relationXDIAddress)) {

			// if the target context node is not the same, we need to adjust the inner root

			if (! targetXDIAddress.equals(relationContextNodeXDIAddress)) {

				relativeRelationTargetXDIAddress = XDIAddress.fromComponent(XdiInnerRoot.createInnerRootXDIArc(relativeTargetXDIAddress, relationXDIAddress));
				relationTargetXDIAddress = targetContextNodeXdiRoot.relativeToAbsoluteXDIAddress(relativeRelationTargetXDIAddress);
			}

			targetRelation = targetContextNode.setRelation(relationXDIAddress, relationTargetXDIAddress);

			// also need to copy the contents of the inner root

			copyContextNodeContents(relation.followContextNode(), targetRelation.followContextNode(), copyStrategy);
		} else {

			targetRelation = targetContextNode.setRelation(relationXDIAddress, relationTargetXDIAddress);
		}

		return targetRelation;
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

	/*
	 * Methods for copying literals
	 */

	/**
	 * Copies a literal into another graph.
	 * @param literalNode A literal from any graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied literal in the target graph.
	 */
	public static LiteralNode copyLiteralNode(LiteralNode literalNode, Graph targetGraph, CopyStrategy copyStrategy) {

		if (literalNode == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		XDIAddress literalNodeContextNodeXDIAddress = literalNode.getContextNode().getXDIAddress();
		ContextNode targetContextNode = targetGraph.setDeepContextNode(literalNodeContextNodeXDIAddress);

		return copyLiteralNode(literalNode, targetContextNode, copyStrategy);
	}

	/**
	 * Copies a literal into another context node.
	 * @param literalNode A literal from any context node.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied literal in the target context node.
	 */
	public static LiteralNode copyLiteralNode(LiteralNode literalNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (literalNode == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		if ((literalNode = copyStrategy.replaceLiteralNode(literalNode)) == null) return null;

		Object literalData = literalNode.getLiteralData();

		LiteralNode targetLiteral = targetContextNode.setLiteralNode(literalData);

		return targetLiteral;
	}

	/**
	 * Copies a literal of a context node into a target context node.
	 * @param contextNode A context node from any graph.
	 * @param targetContextNode The target context node.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied literal in the target graph.
	 */
	public static LiteralNode copyLiteralNode(ContextNode contextNode, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		LiteralNode literalNode = contextNode.getLiteralNode();
		if (literalNode == null) return null;

		return copyLiteralNode(literalNode, targetContextNode, copyStrategy);
	}

	/*
	 * Other copy methods
	 */

	public static Node copyNode(Node node, Graph targetGraph, CopyStrategy copyStrategy) {

		if (node instanceof ContextNode) {

			return copyContextNode((ContextNode) node, targetGraph, copyStrategy);
		} else if (node instanceof LiteralNode) {

			return copyLiteralNode((LiteralNode) node, targetGraph, copyStrategy);
		} else {

			throw new IllegalArgumentException("Invalid node: " + node);
		}
	}

	public static Node copyNode(Node node, ContextNode targetContextNode, CopyStrategy copyStrategy) {

		if (node instanceof ContextNode) {

			return copyContextNode((ContextNode) node, targetContextNode, copyStrategy);
		} else if (node instanceof LiteralNode) {

			return copyLiteralNode((LiteralNode) node, targetContextNode, copyStrategy);
		} else {

			throw new IllegalArgumentException("Invalid node: " + node);
		}
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
		copyLiteralNode(contextNode, targetContextNode, copyStrategy);
	}

	/**
	 * Copies the contents of a context node (context nodes, relations, and the literal) into a target graph.
	 * @param contextNode A context node from any graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 */
	public static void copyContextNodeContents(ContextNode contextNode, Graph targetGraph, CopyStrategy copyStrategy) {

		if (contextNode == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		copyContextNodes(contextNode, targetGraph.getRootContextNode(false), copyStrategy);
		copyRelations(contextNode, targetGraph.getRootContextNode(false), copyStrategy);
		copyLiteralNode(contextNode, targetGraph.getRootContextNode(false), copyStrategy);
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

		targetGraph.setStatement(statement.getXDIStatement());

		return null;
	}

	/*
	 * Helper classes
	 */

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
		public LiteralNode replaceLiteralNode(LiteralNode literal) {

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
	 * A strategy that replaces certain XDI identifiers.
	 */
	public static class ReplaceXDIAddressCopyStrategy extends CopyStrategy {

		Map<XDIArc, XDIAddress> replacements;

		public ReplaceXDIAddressCopyStrategy(Map<XDIArc, XDIAddress> replacements) {

			this.replacements = replacements;
		}

		public ReplaceXDIAddressCopyStrategy(XDIArc oldXDIArc, XDIAddress newXDIArc) {

			this(Collections.singletonMap(oldXDIArc, newXDIArc));
		}

		@Override
		public ContextNode replaceContextNode(ContextNode contextNode) {

			XDIAddress contextNodeXDIAddress = contextNode.getXDIAddress();
			XDIArc contextNodeXDIArc = contextNode.getXDIArc();

			XDIAddress replacedContextNodeXDIAddress = XDIAddress.fromComponent(contextNodeXDIArc);

			for (Entry<XDIArc, XDIAddress> replacement : this.replacements.entrySet()) {

				replacedContextNodeXDIAddress = XDIAddressUtil.replaceXDIAddress(
						replacedContextNodeXDIAddress, 
						replacement.getKey(), 
						replacement.getValue());
			}

			replacedContextNodeXDIAddress = XDIAddressUtil.concatXDIAddresses(XDIAddressUtil.parentXDIAddress(contextNodeXDIAddress, -1), replacedContextNodeXDIAddress);

			if (log.isDebugEnabled()) log.debug("Replaced " + contextNodeXDIAddress + " with " + replacedContextNodeXDIAddress);

			if (contextNodeXDIAddress.equals(replacedContextNodeXDIAddress)) return super.replaceContextNode(contextNode);

			ContextNode replacedContextNode = GraphUtil.contextNodeFromComponents(replacedContextNodeXDIAddress);
			CopyUtil.copyContextNodeContents(contextNode, replacedContextNode, null);

			int additionalArcs = replacedContextNodeXDIAddress.getNumXDIArcs() - contextNodeXDIAddress.getNumXDIArcs();

			replacedContextNode = replacedContextNode.getContextNode(additionalArcs);

			return replacedContextNode;
		}

		@Override
		public Relation replaceRelation(Relation relation) {

			XDIAddress contextNodeXDIAddress = relation.getContextNode().getXDIAddress();
			XDIAddress XDIaddress = relation.getXDIAddress();
			XDIAddress targetXDIAddress = relation.getTargetXDIAddress();

			XDIAddress replacedTargetXDIAddress = targetXDIAddress;

			for (Entry<XDIArc, XDIAddress> replacement : this.replacements.entrySet()) {

				replacedTargetXDIAddress = XDIAddressUtil.replaceXDIAddress(
						replacedTargetXDIAddress, 
						replacement.getKey(), 
						replacement.getValue());
			}

			if (log.isDebugEnabled()) log.debug("Replaced " + targetXDIAddress + " with " + replacedTargetXDIAddress);

			if (targetXDIAddress.equals(replacedTargetXDIAddress)) return super.replaceRelation(relation);

			Relation replacedRelation = GraphUtil.relationFromComponents(contextNodeXDIAddress, XDIaddress, replacedTargetXDIAddress);

			return replacedRelation;
		}
	}
}
