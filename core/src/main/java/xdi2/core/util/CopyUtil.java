package xdi2.core.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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
import xdi2.core.constants.XDIConstants;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiCommonRoot;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.impl.DummyLiteralNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;

/**
 * Various utility methods for copying statements between graphs.
 * 
 * @author markus
 */
public final class CopyUtil {

	private static final Logger log = LoggerFactory.getLogger(CopyUtil.class);

	private static final CopyStrategy DEFAULT_COPY_STRATEGY = new AllCopyStrategy();

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

		ContextNode targetContextNode;

		XDIAddress contextNodeXDIAddress = contextNode.getXDIAddress();

		if (contextNode.isRootContextNode()) {

			targetContextNode = targetGraph.getRootContextNode(false);

			copyContextNodeContents(contextNode, targetContextNode, copyStrategy);
		} else {

			targetContextNode = targetGraph.setDeepContextNode(contextNodeXDIAddress);

			copyContextNodeContents(contextNode, targetContextNode, copyStrategy);
		}

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

		List<ContextNode> copyContextNodes = copyStrategy.replaceContextNode(contextNode);
		if (copyContextNodes == null) copyContextNodes = Collections.singletonList(contextNode); // TODO: maybe avoid creating a list object here

		ContextNode targetCopiedContextNode = null;

		for (ContextNode copyContextNode : copyContextNodes) {

			XDIArc copyContextNodeXDIArc = copyContextNode.getXDIArc();

			if (copyContextNodeXDIArc != null)
				targetCopiedContextNode = targetContextNode.setContextNode(copyContextNodeXDIArc);
			else
				targetCopiedContextNode = targetContextNode;

			copyContextNodeContents(copyContextNode, targetCopiedContextNode, copyStrategy);
		}

		return copyContextNodes.size() > 1 ? null : targetCopiedContextNode;
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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

		List<Relation> copyRelations = copyStrategy.replaceRelation(relation);
		if (copyRelations == null) copyRelations = Collections.singletonList(relation); // TODO: maybe avoid creating a list object here

		Relation targetRelation = null;

		for (Relation copyRelation : copyRelations) {

			XDIAddress relationContextNodeXDIAddress = copyRelation.getContextNode().getXDIAddress();
			XDIAddress relationXDIAddress = copyRelation.getXDIAddress();
			XDIAddress relationTargetXDIAddress = copyRelation.getTargetXDIAddress();

			XDIAddress targetXDIAddress = targetContextNode.getXDIAddress();

			XdiRoot relationContextNodeXdiRoot = XdiCommonRoot.findCommonRoot(copyRelation.getContextNode().getGraph()).getRoot(relationContextNodeXDIAddress, false);
			XdiRoot targetContextNodeXdiRoot = XdiCommonRoot.findCommonRoot(targetContextNode.getGraph()).getRoot(targetXDIAddress, false);

			XDIAddress relativeRelationContextNodeXDIAddress = relationContextNodeXdiRoot.absoluteToRelativeXDIAddress(relationContextNodeXDIAddress);
			XDIAddress relativeRelationTargetXDIAddress = relationContextNodeXdiRoot.absoluteToRelativeXDIAddress(relationTargetXDIAddress);
			XDIAddress relativeTargetXDIAddress = targetContextNodeXdiRoot.absoluteToRelativeXDIAddress(targetXDIAddress);

			// check if this relation establishes an inner root

			if (relativeRelationTargetXDIAddress != null &&
					relativeRelationTargetXDIAddress.getNumXDIArcs() == 1 &&
					XdiInnerRoot.isValidXDIArc(relativeRelationTargetXDIAddress.getFirstXDIArc()) &&
					XdiInnerRoot.getSubjectOfInnerRootXDIArc(relativeRelationTargetXDIAddress.getFirstXDIArc()).equals(relativeRelationContextNodeXDIAddress) &&
					XdiInnerRoot.getPredicateOfInnerRootXDIArc(relativeRelationTargetXDIAddress.getFirstXDIArc()).equals(relationXDIAddress)) {

				// if the target context node is not the same, we need to adjust the inner root

				if (! targetXDIAddress.equals(relationContextNodeXDIAddress)) {

					relativeRelationTargetXDIAddress = XDIAddress.fromComponent(XdiInnerRoot.createInnerRootXDIArc(relativeTargetXDIAddress, relationXDIAddress));
					relationTargetXDIAddress = targetContextNodeXdiRoot.relativeToAbsoluteXDIAddress(relativeRelationTargetXDIAddress);
				}

				targetRelation = targetContextNode.setRelation(relationXDIAddress, relationTargetXDIAddress);

				// also need to copy the contents of the inner root

				copyContextNodeContents(copyRelation.followContextNode(), targetRelation.followContextNode(), copyStrategy);
			} else {

				targetRelation = targetContextNode.setRelation(relationXDIAddress, relationTargetXDIAddress);
			}
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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

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
		if (copyStrategy == null) copyStrategy = DEFAULT_COPY_STRATEGY;

		targetGraph.setStatement(statement.getXDIStatement());

		return null;
	}

	/*
	 * Helper classes
	 */

	public interface CopyStrategy {

		public List<ContextNode> replaceContextNode(ContextNode contextNode);
		public List<Relation> replaceRelation(Relation relation);
		public LiteralNode replaceLiteralNode(LiteralNode literalNode);
	}

	/**
	 * Just copy everything without change.
	 */
	public static abstract class AbstractCopyStrategy implements CopyStrategy {

		/**
		 * Strategies can replace a context node that is being copied.
		 * @param contextNode The original context node.
		 * @return The replacement context node(s). Return null to copy the original context node.
		 * Return the empty list to not copy any context nodes.
		 */
		@Override
		public List<ContextNode> replaceContextNode(ContextNode contextNode) {

			if (log.isTraceEnabled()) log.trace("Copying context node " + contextNode);

			return null;
		}

		/**
		 * Strategies can replace a relation that is being copied.
		 * @param relation The original relation.
		 * @return The replacement relation(s). Return null to copy the original relation.
		 * Return the empty list to not copy any relations.
		 */
		@Override
		public List<Relation> replaceRelation(Relation relation) {

			if (log.isTraceEnabled()) log.trace("Copying relation " + relation);

			return null;
		}

		/**
		 * Strategies can replace a literal that is being copied.
		 * @param literalNode The original literal node.
		 * @return The original or replacement literal. Return null to not copy any literal node.
		 */
		@Override
		public LiteralNode replaceLiteralNode(LiteralNode literalNode) {

			if (log.isTraceEnabled()) log.trace("Copying literal node " + literalNode);

			return literalNode;
		}
	}

	/**
	 * The default strategy that copies everything.
	 */
	public static class AllCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

	}

	/**
	 * A strategy that replaces literal strings.
	 */
	public static class ReplaceRegexLiteralStringCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

		private Map<String, String> replacements;

		public ReplaceRegexLiteralStringCopyStrategy(Map<String, String> replacements) {

			this.replacements = replacements;
		}

		public ReplaceRegexLiteralStringCopyStrategy(String regex, String replacement) {

			this(Collections.singletonMap(regex, replacement));
		}

		protected ReplaceRegexLiteralStringCopyStrategy() {

			this(null);
		}

		@Override
		public LiteralNode replaceLiteralNode(LiteralNode literalNode) {

			String literalDataString = literalNode.getLiteralDataString();
			if (literalDataString == null) return literalNode;

			for (Map.Entry<String, String> replacement : this.replacements.entrySet()) {

				literalDataString = literalDataString.replaceAll(replacement.getKey(), replacement.getValue());
			}

			return new DummyLiteralNode(literalNode.getContextNode(), literalDataString);
		}
	}

	/**
	 * A strategy that replaces XDI addresses.
	 */
	public static class ReplaceXDIAddressCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

		private Map<XDIArc, Object> replacements;

		public ReplaceXDIAddressCopyStrategy(Map<XDIArc, Object> replacements) {

			this.replacements = replacements;
		}

		public ReplaceXDIAddressCopyStrategy(XDIArc oldXDIArc, Object newXDIArcOrXDIAddressOrList) {

			this(Collections.singletonMap(oldXDIArc, newXDIArcOrXDIAddressOrList));
		}

		protected ReplaceXDIAddressCopyStrategy() {

			this(null);
		}

		@Override
		public List<ContextNode> replaceContextNode(ContextNode contextNode) {

			XDIAddress contextNodeXDIAddress = contextNode.getXDIAddress();
			XDIArc contextNodeXDIArc = contextNode.getXDIArc();

			XDIAddress baseXDIAddress = XDIAddress.fromComponent(contextNodeXDIArc);

			Map<XDIArc, Object> replacements = this.getReplacements(baseXDIAddress);
			if (replacements == null) return null;

			List<XDIAddress> replacedContextNodeXDIAddresses = new ArrayList<XDIAddress> ();

			for (Entry<XDIArc, Object> replacement : replacements.entrySet()) {

				XDIArc oldXDIArc = replacement.getKey();
				Object newXDIArcOrXDIAddressOrList = replacement.getValue();

				List<XDIAddress> newXDIAddresses;

				if (newXDIArcOrXDIAddressOrList instanceof XDIAddress) newXDIAddresses = Collections.singletonList((XDIAddress) newXDIArcOrXDIAddressOrList);
				else if (newXDIArcOrXDIAddressOrList instanceof XDIArc) newXDIAddresses = Collections.singletonList(XDIAddress.fromComponent((XDIArc) newXDIArcOrXDIAddressOrList));
				else if (newXDIArcOrXDIAddressOrList instanceof List) newXDIAddresses = (List<XDIAddress>) newXDIArcOrXDIAddressOrList;
				else throw new IllegalArgumentException("Illegal replacement: " + newXDIArcOrXDIAddressOrList.getClass().getCanonicalName());

				for (XDIAddress newXDIAddress : newXDIAddresses) {

					XDIAddress replacedContextNodeXDIAddress = XDIAddressUtil.replaceXDIAddress(
							baseXDIAddress, 
							oldXDIArc, 
							newXDIAddress);

					replacedContextNodeXDIAddresses.add(replacedContextNodeXDIAddress);
				}
			}

			List<ContextNode> replacedContextNodes = new ArrayList<ContextNode> ();

			for (XDIAddress replacedContextNodeXDIAddress : replacedContextNodeXDIAddresses) {

				replacedContextNodeXDIAddress = XDIAddressUtil.concatXDIAddresses(XDIAddressUtil.parentXDIAddress(contextNodeXDIAddress, -1), replacedContextNodeXDIAddress);

				if (log.isTraceEnabled()) log.trace("Replaced " + contextNodeXDIAddress + " with " + replacedContextNodeXDIAddress);

				if (contextNodeXDIAddress.equals(replacedContextNodeXDIAddress)) return super.replaceContextNode(contextNode);

				ContextNode replacedContextNode = GraphUtil.contextNodeFromComponents(replacedContextNodeXDIAddress);
				CopyUtil.copyContextNodeContents(contextNode, replacedContextNode, null);

				int additionalArcs = replacedContextNodeXDIAddress.getNumXDIArcs() - contextNodeXDIAddress.getNumXDIArcs();
				replacedContextNode = replacedContextNode.getContextNode(additionalArcs);

				replacedContextNodes.add(replacedContextNode);
			}

			return replacedContextNodes;
		}

		@Override
		public List<Relation> replaceRelation(Relation relation) {

			XDIAddress contextNodeXDIAddress = relation.getContextNode().getXDIAddress();
			XDIAddress XDIaddress = relation.getXDIAddress();
			XDIAddress targetXDIAddress = relation.getTargetXDIAddress();

			XDIAddress baseXDIAddress = targetXDIAddress;

			Map<XDIArc, Object> replacements = this.getReplacements(baseXDIAddress);
			if (replacements == null) return super.replaceRelation(relation);

			List<XDIAddress> replacedTargetXDIAddresses = new ArrayList<XDIAddress> ();

			for (Entry<XDIArc, Object> replacement : replacements.entrySet()) {

				XDIArc oldXDIArc = replacement.getKey();
				Object newXDIArcOrXDIAddressOrList = replacement.getValue();

				List<XDIAddress> newXDIAddresses;

				if (newXDIArcOrXDIAddressOrList instanceof XDIAddress) newXDIAddresses = Collections.singletonList((XDIAddress) newXDIArcOrXDIAddressOrList);
				else if (newXDIArcOrXDIAddressOrList instanceof XDIArc) newXDIAddresses = Collections.singletonList(XDIAddress.fromComponent((XDIArc) newXDIArcOrXDIAddressOrList));
				else if (newXDIArcOrXDIAddressOrList instanceof List) newXDIAddresses = (List<XDIAddress>) newXDIArcOrXDIAddressOrList;
				else throw new IllegalArgumentException("Illegal replacement: " + newXDIArcOrXDIAddressOrList.getClass().getCanonicalName());

				for (XDIAddress newXDIAddress : newXDIAddresses) {

					XDIAddress replacedTargetXDIAddress = XDIAddressUtil.replaceXDIAddress(
							baseXDIAddress, 
							oldXDIArc, 
							newXDIAddress);

					replacedTargetXDIAddresses.add(replacedTargetXDIAddress);
				}
			}

			List<Relation> replacedRelations = new ArrayList<Relation> ();

			for (XDIAddress replacedTargetXDIAddress : replacedTargetXDIAddresses) {

				if (log.isTraceEnabled()) log.trace("Replaced " + targetXDIAddress + " with " + replacedTargetXDIAddress);

				if (targetXDIAddress.equals(replacedTargetXDIAddress)) return super.replaceRelation(relation);

				Relation replacedRelation = GraphUtil.relationFromComponents(contextNodeXDIAddress, XDIaddress, replacedTargetXDIAddress);

				replacedRelations.add(replacedRelation);
			}

			return replacedRelations;
		}

		protected Map<XDIArc, Object> getReplacements(XDIAddress XDIaddress) {

			Map<XDIArc, Object> replacements = null;

			for (XDIArc XDIarc : XDIaddress.getXDIArcs()) {

				if (this.replacements.containsKey(XDIarc)) {

					if (replacements == null) replacements = new HashMap<XDIArc, Object> ();
					replacements.put(XDIarc, this.replacements.get(XDIarc));
				} else if (XDIarc.hasXRef() && XDIarc.getXRef().hasPartialSubjectAndPredicate()) {

					for (XDIArc partialSubjectXDIArc : XDIarc.getXRef().getPartialSubject().getXDIArcs()) {

						if (this.replacements.containsKey(partialSubjectXDIArc)) {

							if (replacements == null) replacements = new HashMap<XDIArc, Object> ();
							replacements.put(partialSubjectXDIArc, this.replacements.get(partialSubjectXDIArc));
						}
					}

					for (XDIArc partialPredicateXDIArc : XDIarc.getXRef().getPartialSubject().getXDIArcs()) {

						if (this.replacements.containsKey(partialPredicateXDIArc)) {

							if (replacements == null) replacements = new HashMap<XDIArc, Object> ();
							replacements.put(partialPredicateXDIArc, this.replacements.get(partialPredicateXDIArc));
						}
					}
				}
			}

			return replacements;
		}
	}

	/**
	 * A strategy that concats XDI addresses.
	 */
	public static class ConcatXDIAddressCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

		private XDIAddress startXDIAddress;

		public ConcatXDIAddressCopyStrategy(XDIAddress startXDIAddress) {

			this.startXDIAddress = startXDIAddress;
		}

		@Override
		public List<ContextNode> replaceContextNode(ContextNode contextNode) {

			if (contextNode.isRootContextNode()) return null;
			if (! contextNode.getContextNode().isRootContextNode()) return null;

			XDIAddress contextNodeXDIAddress = contextNode.getXDIAddress();

			XDIAddress replacedContextNodeXDIAddress = replaceXDIAddress(contextNodeXDIAddress);

			if (log.isTraceEnabled()) log.trace("Replaced " + contextNodeXDIAddress + " with " + replacedContextNodeXDIAddress);

			if (contextNodeXDIAddress.equals(replacedContextNodeXDIAddress)) return super.replaceContextNode(contextNode);

			ContextNode replacedContextNode = GraphUtil.contextNodeFromComponents(replacedContextNodeXDIAddress);
			CopyUtil.copyContextNodeContents(contextNode, replacedContextNode, null);

			int additionalArcs = replacedContextNodeXDIAddress.getNumXDIArcs() - contextNodeXDIAddress.getNumXDIArcs();
			replacedContextNode = replacedContextNode.getContextNode(additionalArcs);

			return Collections.singletonList(replacedContextNode);
		}

		@Override
		public List<Relation> replaceRelation(Relation relation) {

			XDIAddress contextNodeXDIAddress = relation.getContextNode().getXDIAddress();
			XDIAddress XDIaddress = relation.getXDIAddress();
			XDIAddress targetXDIAddress = relation.getTargetXDIAddress();

			XDIAddress replacedContextNodeXDIAddress = replaceXDIAddress(contextNodeXDIAddress);
			XDIAddress replacedTargetXDIAddress = replaceXDIAddress(targetXDIAddress);

			if (log.isTraceEnabled()) log.trace("Replaced " + contextNodeXDIAddress + " with " + replacedContextNodeXDIAddress);
			if (log.isTraceEnabled()) log.trace("Replaced " + targetXDIAddress + " with " + replacedTargetXDIAddress);

			Relation replacedRelation = GraphUtil.relationFromComponents(replacedContextNodeXDIAddress, XDIaddress, replacedTargetXDIAddress);

			return Collections.singletonList(replacedRelation);
		}

		private XDIAddress replaceXDIAddress(XDIAddress XDIaddress) {

			return XDIAddressUtil.concatXDIAddresses(this.startXDIAddress, XDIaddress);
		}
	}

	/**
	 * A strategy that extracts XDI addresses.
	 */
	public static class ExtractXDIAddressCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

		private Class<? extends XdiContext<?>>[] clazzes;
		private boolean keepOnlyFirstFound;
		private boolean keepOnlyLastFound;
		private boolean keepAllFound;
		private boolean keepParent;
		private boolean keepLocal;

		public ExtractXDIAddressCopyStrategy(Class<? extends XdiContext<?>>[] clazzes, boolean keepOnlyFirstFound, boolean keepOnlyLastFound, boolean keepAllFound, boolean keepParent, boolean keepLocal) {

			this.clazzes = clazzes;
			this.keepOnlyFirstFound = keepOnlyFirstFound;
			this.keepOnlyLastFound = keepOnlyLastFound;
			this.keepAllFound = keepAllFound;
			this.keepParent = keepParent;
			this.keepLocal = keepLocal;
		}

		public ExtractXDIAddressCopyStrategy(Class<? extends XdiContext<?>> clazz, boolean keepOnlyFirstFound, boolean keepOnlyLastFound, boolean keepAllFound, boolean keepParent, boolean keepLocal) {

			@SuppressWarnings("unchecked")
			Class<? extends XdiContext<?>>[] clazzes = (Class<? extends XdiContext<?>>[]) Array.newInstance(clazz.getClass(), 1);
			clazzes[0] = clazz;

			this.clazzes = clazzes;
			this.keepOnlyFirstFound = keepOnlyFirstFound;
			this.keepOnlyLastFound = keepOnlyLastFound;
			this.keepAllFound = keepAllFound;
			this.keepParent = keepParent;
			this.keepLocal = keepLocal;
		}

		@Override
		public List<ContextNode> replaceContextNode(ContextNode contextNode) {

			XDIAddress contextNodeXDIAddress = contextNode.getXDIAddress();

			XDIAddress replacedContextNodeXDIAddress = replaceXDIAddress(contextNodeXDIAddress);

			ContextNode replacedContextNode = GraphUtil.contextNodeFromComponents(replacedContextNodeXDIAddress);
			CopyUtil.copyContextNodeContents(contextNode, replacedContextNode, null);

			return Collections.singletonList(replacedContextNode);
		}

		@Override
		public List<Relation> replaceRelation(Relation relation) {

			XDIAddress contextNodeXDIAddress = relation.getContextNode().getXDIAddress();
			XDIAddress XDIaddress = relation.getXDIAddress();
			XDIAddress targetXDIAddress = relation.getTargetXDIAddress();

			XDIAddress replacedTargetXDIAddress = replaceXDIAddress(targetXDIAddress);

			Relation replacedRelation = GraphUtil.relationFromComponents(contextNodeXDIAddress, XDIaddress, replacedTargetXDIAddress);

			return Collections.singletonList(replacedRelation);
		}

		private XDIAddress replaceXDIAddress(XDIAddress XDIaddress) {

			XDIAddress replacedXDIAddress = XDIAddressUtil.extractXDIAddress(XDIaddress, this.clazzes, this.keepOnlyFirstFound, this.keepOnlyLastFound, this.keepAllFound, this.keepParent, this.keepLocal);

			return replacedXDIAddress != null ? replacedXDIAddress : XDIaddress;
		}
	}

	/**
	 * A strategy for replacing escaped variables.
	 */

	public static class ReplaceEscapedVariablesCopyStrategy extends ReplaceXDIAddressCopyStrategy implements CopyStrategy {

		@Override
		protected Map<XDIArc, Object> getReplacements(XDIAddress XDIaddress) {

			Map<XDIArc, Object> replacements = null;

			for (XDIArc XDIarc : XDIaddress.getXDIArcs()) {

				if (! XDIConstants.CS_CLASS_RESERVED.equals(XDIarc.getCs())) continue;
				if (! XDIarc.isVariable()) continue;
				if (! XDIarc.isRelative()) continue;

				if (replacements == null) replacements = new HashMap<XDIArc, Object> ();

				XDIArc newXDIArc = XDIArc.fromComponents(XDIarc.getCs(), true, XDIarc.isDefinition(), XDIarc.isCollection(), XDIarc.isAttribute(), XDIarc.isImmutable(), false, XDIarc.getLiteral(), XDIarc.getXRef());

				replacements.put(XDIarc, newXDIArc);
			}

			return replacements;
		}
	}

	/**
	 * A strategy for replacing literal variables.
	 */

	public static class ReplaceLiteralVariablesCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

		private Map<XDIArc, Object> replacements;

		public ReplaceLiteralVariablesCopyStrategy(Map<XDIArc, Object> replacements) {

			this.replacements = replacements;
		}

		public ReplaceLiteralVariablesCopyStrategy(XDIArc oldXDIArc, Object newXDIArc) {

			this(Collections.singletonMap(oldXDIArc, newXDIArc));
		}

		@Override
		public List<ContextNode> replaceContextNode(ContextNode contextNode) {

			Relation relation = contextNode.getRelation(XDIConstants.XDI_ADD_LITERAL_VARIABLE);
			if (relation == null) return null;

			XDIAddress targetXDIAddress = relation.getTargetXDIAddress();
			if (targetXDIAddress.getNumXDIArcs() > 1) return null;

			Object replacement = this.replacements.get(targetXDIAddress.getFirstXDIArc());

			ContextNode replacedContextNode = CloneUtil.cloneContextNode(contextNode);

			replacedContextNode.delRelations(XDIConstants.XDI_ADD_LITERAL_VARIABLE);
			replacedContextNode.delLiteralNode();
			replacedContextNode.setLiteralNode(replacement);

			if (log.isTraceEnabled()) log.trace("Replaced " + targetXDIAddress + " with " + replacement);

			return Collections.singletonList(replacedContextNode);
		}
	}

	/**
	 * A compound strategy.
	 */

	public static class CompoundCopyStrategy extends AbstractCopyStrategy implements CopyStrategy {

		private List<CopyStrategy> copyStrategies;

		public CompoundCopyStrategy(CopyStrategy... copyStrategies) {

			this.copyStrategies = Arrays.asList(copyStrategies);
		}

		@Override
		public List<ContextNode> replaceContextNode(ContextNode contextNode) {

			List<ContextNode> compoundCopyContextNodes = Collections.singletonList(contextNode);

			for (CopyStrategy copyStrategy : this.copyStrategies) {

				List<ContextNode> nextCompoundCopyContextNodes = new ArrayList<ContextNode> ();

				for (ContextNode compoundCopyContextNode : compoundCopyContextNodes) {

					List<ContextNode> copyContextNodes = copyStrategy.replaceContextNode(compoundCopyContextNode);

					if (copyContextNodes != null)
						nextCompoundCopyContextNodes.addAll(copyContextNodes);
					else
						nextCompoundCopyContextNodes.add(compoundCopyContextNode);
				}

				compoundCopyContextNodes = nextCompoundCopyContextNodes;
			}

			return compoundCopyContextNodes;
		}

		@Override
		public List<Relation> replaceRelation(Relation relation) {

			List<Relation> compoundCopyRelations = Collections.singletonList(relation);

			for (CopyStrategy copyStrategy : this.copyStrategies) {

				List<Relation> nextCompoundCopyRelations = new ArrayList<Relation> ();

				for (Relation compoundCopyRelation : compoundCopyRelations) {

					List<Relation> copyRelations = copyStrategy.replaceRelation(compoundCopyRelation);

					if (copyRelations != null)
						nextCompoundCopyRelations.addAll(copyRelations);
					else
						nextCompoundCopyRelations.add(compoundCopyRelation);
				}

				compoundCopyRelations = nextCompoundCopyRelations;
			}

			return compoundCopyRelations;
		}

		@Override
		public LiteralNode replaceLiteralNode(LiteralNode literalNode) {

			for (CopyStrategy copyStrategy : this.copyStrategies) literalNode = copyStrategy.replaceLiteralNode(literalNode);

			return literalNode;
		}
	}
}
