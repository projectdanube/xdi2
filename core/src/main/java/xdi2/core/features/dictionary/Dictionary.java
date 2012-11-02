package xdi2.core.features.dictionary;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.iterators.MappingContextNodeXriIterator;
import xdi2.core.util.iterators.MappingRelationContextNodeIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;
import xdi2.core.xri3.impl.XRI3Constants;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class Dictionary {

	private Dictionary() { }

	/*
	 * Methods for dictionary XRIs
	 */

	public static XRI3SubSegment instanceXriToDictionaryXri(XRI3SubSegment instanceXri) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_PLUS + "(" + instanceXri + ")");
	}

	public static XRI3SubSegment dictionaryXriToInstanceXri(XRI3SubSegment dictionaryXri) {

		if (! XRI3Constants.GCS_PLUS.equals(dictionaryXri.getGCS())) return null;
		if (dictionaryXri.hasLCS()) return null;
		if (! dictionaryXri.hasXRef()) return null;
		if (! dictionaryXri.getXRef().hasXRIReference()) return null;

		return new XRI3SubSegment("" + dictionaryXri.getXRef().getXRIReference());
	}

	public static XRI3SubSegment nativeIdentifierToInstanceXri(String nativeIdentifier) {

		return new XRI3SubSegment("" + XRI3Constants.GCS_PLUS + "(" + nativeIdentifier + ")");
	}

	public static String instanceXriToNativeIdentifier(XRI3SubSegment instanceXri) {

		if (! instanceXri.hasXRef()) return null;
		if (! instanceXri.getXRef().hasXRIReference()) return null;

		return instanceXri.getXRef().getXRIReference().toString();
	}

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
	 * Methods for types of context nodes.
	 */

	public static Iterator<XRI3Segment> getContextNodeTypes(ContextNode contextNode) {

		return new MappingContextNodeXriIterator(new MappingRelationTargetContextNodeIterator(contextNode.getRelations(XDIDictionaryConstants.XRI_S_IS_TYPE)));
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
