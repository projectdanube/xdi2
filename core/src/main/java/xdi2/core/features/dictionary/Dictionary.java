package xdi2.core.features.dictionary;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.iterators.MappingContextNodeXrisIterator;
import xdi2.core.util.iterators.MappingRelationContextNodeIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;
import xdi2.core.xri3.impl.XRI3Segment;

public class Dictionary {

	private Dictionary() { }

	/*
	 * Methods for the canonical context node.
	 * This is the target of a $is relation.
	 */

	public static ContextNode getCanonicalContextNode(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS);
		if (relation == null) return null;

		return relation.follow();
	}

	public static void setCanonicalContextNode(ContextNode contextNode, ContextNode canonicalContextNode) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, canonicalContextNode);
	}

	/*
	 * Methods for synonym context nodes.
	 * These are the sources of incoming $is relations.
	 */

	public static Iterator<ContextNode> getSynonymContextNodes(ContextNode contextNode) {

		Iterator<Relation> relations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_IS);

		return new MappingRelationContextNodeIterator(relations);
	}

	/*
	 * Methods for types.
	 */

	public static Iterator<XRI3Segment> getContextNodeTypes(ContextNode contextNode) {

		return new MappingContextNodeXrisIterator(new MappingRelationTargetContextNodeIterator(contextNode.getRelations(XDIDictionaryConstants.XRI_S_IS_TYPE)));
	}

	public static XRI3Segment getContextNodeType(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS_TYPE).getTargetContextNodeXri();
	}

	public static boolean isContextNodeType(ContextNode contextNode, XRI3Segment type) {

		return contextNode.containsRelation(XDIDictionaryConstants.XRI_S_IS_TYPE, type);
	}

	public static void addContextNodeType(ContextNode contextNode, XRI3Segment type) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_TYPE, type);
	}

	public static void removeContextNodeType(ContextNode contextNode, XRI3Segment type) {

		contextNode.deleteRelation(XDIDictionaryConstants.XRI_S_IS_TYPE, type);
	}

	public static void removeContextNodeTypes(ContextNode contextNode) {

		contextNode.deleteRelations(XDIDictionaryConstants.XRI_S_IS_TYPE);
	}

	public static void setContextNodeType(ContextNode contextNode, XRI3Segment type) {

		removeContextNodeTypes(contextNode);
		addContextNodeType(contextNode, type);
	}
}
