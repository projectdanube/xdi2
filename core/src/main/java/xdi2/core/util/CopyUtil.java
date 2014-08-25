package xdi2.core.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiCommonRoot;
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

		copyContextNodeContents(graph.getRootContextNode(true), targetGraph.getRootContextNode(false), copyStrategy);
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

		XDIAddress relationcontextNodeXDIAddress = relation.getContextNode().getXDIAddress();
		ContextNode targetContextNode = targetGraph.setDeepContextNode(relationcontextNodeXDIAddress);

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

		XDIAddress relationcontextNodeXDIAddress = relation.getContextNode().getXDIAddress();
		XDIAddress relationAddress = relation.getXDIAddress();
		XDIAddress relationtargetContextNodeXDIAddress = relation.getTargetContextNodeXDIAddress();

		XDIAddress targetContextNodeXDIAddress = targetContextNode.getXDIAddress();

		XdiRoot relationContextNodeXdiRoot = XdiCommonRoot.findCommonRoot(relation.getContextNode().getGraph()).getRoot(relationcontextNodeXDIAddress, false);
		XdiRoot targetContextNodeXdiRoot = XdiCommonRoot.findCommonRoot(targetContextNode.getGraph()).getRoot(targetContextNodeXDIAddress, false);

		XDIAddress relativeRelationcontextNodeXDIAddress = relationContextNodeXdiRoot.absoluteToRelativeXDIAddress(relationcontextNodeXDIAddress);
		XDIAddress relativeRelationtargetContextNodeXDIAddress = relationContextNodeXdiRoot.absoluteToRelativeXDIAddress(relationtargetContextNodeXDIAddress);
		XDIAddress relativetargetContextNodeXDIAddress = targetContextNodeXdiRoot.absoluteToRelativeXDIAddress(targetContextNodeXDIAddress);

		Relation targetRelation;

		// check if this relation establishes an inner root

		if (relativeRelationtargetContextNodeXDIAddress != null &&
				relativeRelationtargetContextNodeXDIAddress.getNumXDIArcs() == 1 &&
				XdiInnerRoot.isValidXDIArc(relativeRelationtargetContextNodeXDIAddress.getFirstXDIArc()) &&
				XdiInnerRoot.getSubjectOfInnerRootXDIArc(relativeRelationtargetContextNodeXDIAddress.getFirstXDIArc()).equals(relativeRelationcontextNodeXDIAddress) &&
				XdiInnerRoot.getPredicateOfInnerRootXDIArc(relativeRelationtargetContextNodeXDIAddress.getFirstXDIArc()).equals(relationAddress)) {

			// if the target context node is not the same, we need to adjust the inner root

			if (! targetContextNodeXDIAddress.equals(relationcontextNodeXDIAddress)) {

				relativeRelationtargetContextNodeXDIAddress = XDIAddress.fromComponent(XdiInnerRoot.createInnerRootXDIArc(relativetargetContextNodeXDIAddress, relationAddress));
				relationtargetContextNodeXDIAddress = targetContextNodeXdiRoot.relativeToAbsoluteXDIAddress(relativeRelationtargetContextNodeXDIAddress);
			}

			targetRelation = targetContextNode.setRelation(relationAddress, relationtargetContextNodeXDIAddress);

			// also need to copy the contents of the inner root

			copyContextNodeContents(relation.follow(), targetRelation.follow(), copyStrategy);
		} else {

			targetRelation = targetContextNode.setRelation(relationAddress, relationtargetContextNodeXDIAddress);
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
	 * @param literal A literal from any graph.
	 * @param targetGraph The target graph.
	 * @param copyStrategy The strategy to determine what to copy.
	 * @return The copied literal in the target graph.
	 */
	public static Literal copyLiteral(Literal literal, Graph targetGraph, CopyStrategy copyStrategy) {

		if (literal == null) throw new NullPointerException();
		if (targetGraph == null) throw new NullPointerException();
		if (copyStrategy == null) copyStrategy = allCopyStrategy;

		XDIAddress literalcontextNodeXDIAddress = literal.getContextNode().getXDIAddress();
		ContextNode targetContextNode = targetGraph.setDeepContextNode(literalcontextNodeXDIAddress);

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

		Object literalData = literal.getLiteralData();

		Literal targetLiteral = targetContextNode.setLiteral(literalData);

		return targetLiteral;
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

	/*
	 * Other copy methods
	 */

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

		targetGraph.setStatement(statement.getStatement());

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
			XDIAddress targetContextNodeXDIAddress = relation.getTargetContextNodeXDIAddress();

			XDIAddress replacedTargetContextNodeXDIAddress = targetContextNodeXDIAddress;

			for (Entry<XDIArc, XDIAddress> replacement : this.replacements.entrySet()) {

				targetContextNodeXDIAddress = XDIAddressUtil.replaceXDIAddress(
						targetContextNodeXDIAddress, 
						replacement.getKey(), 
						replacement.getValue());
			}

			if (log.isDebugEnabled()) log.debug("Replaced " + targetContextNodeXDIAddress + " with " + replacedTargetContextNodeXDIAddress);

			if (targetContextNodeXDIAddress.equals(replacedTargetContextNodeXDIAddress)) return super.replaceRelation(relation);

			Relation replacedRelation = GraphUtil.relationFromComponents(contextNodeXDIAddress, XDIaddress, replacedTargetContextNodeXDIAddress);

			return replacedRelation;
		}
	}
}
