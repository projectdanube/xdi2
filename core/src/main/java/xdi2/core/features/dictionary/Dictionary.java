package xdi2.core.features.dictionary;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingContextNodeXriIterator;
import xdi2.core.util.iterators.MappingRelationContextNodeIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.xri3.impl.XDI3Segment;
import xdi2.core.xri3.impl.XDI3SubSegment;
import xdi2.core.xri3.impl.XRI3Constants;

public class Dictionary {

	private Dictionary() { }

	/*
	 * Methods for dictionary XRIs
	 */

	public static XDI3SubSegment instanceXriToDictionaryXri(XDI3SubSegment instanceXri) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_PLUS + "(" + instanceXri + ")");
	}

	public static XDI3SubSegment dictionaryXriToInstanceXri(XDI3SubSegment dictionaryXri) {

		if (! XRI3Constants.GCS_PLUS.equals(dictionaryXri.getGCS())) return null;
		if (dictionaryXri.hasLCS()) return null;
		if (! dictionaryXri.hasXRef()) return null;

		return new XDI3SubSegment(dictionaryXri.getXRef().getValue());
	}

	public static XDI3SubSegment nativeIdentifierToInstanceXri(String nativeIdentifier) {

		return new XDI3SubSegment("" + XRI3Constants.GCS_PLUS + "(" + nativeIdentifier + ")");
	}

	public static String instanceXriToNativeIdentifier(XDI3SubSegment instanceXri) {

		if (! instanceXri.hasXRef()) return null;

		return instanceXri.getXRef().getValue();
	}

	/*
	 * Methods for canonical context nodes and relations.
	 */

	public static Relation getCanonicalRelation(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS);
	}

	public static ContextNode getCanonicalContextNode(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS);
		if (relation == null) return null;

		return relation.follow();
	}

	public static void setCanonicalContextNode(ContextNode contextNode, ContextNode canonicalContextNode) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, canonicalContextNode);
	}

	public static Relation getPrivateCanonicalRelation(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS_BANG);
	}

	public static ContextNode getPrivateCanonicalContextNode(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS_BANG);
		if (relation == null) return null;

		return relation.follow();
	}

	public static void setPrivateCanonicalContextNode(ContextNode contextNode, ContextNode canonicalContextNode) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_BANG, canonicalContextNode);
	}

	/*
	 * Methods for equivalence context nodes.
	 * These are the sources of incoming $is or $is! relations.
	 */

	public static Iterator<ContextNode> getEquivalenceContextNodes(ContextNode contextNode) {

		Iterator<Relation> canonicalRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_IS);
		Iterator<Relation> privateCanonicalRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_IS_BANG);

		List<Iterator<Relation>> iterators = new ArrayList<Iterator<Relation>> ();
		iterators.add(canonicalRelations);
		iterators.add(privateCanonicalRelations);

		return new MappingRelationContextNodeIterator(new CompositeIterator<Relation> (iterators.iterator()));
	}

	/*
	 * Methods for equivalence relations.
	 */

	public static Iterator<Relation> getEquivalenceRelations(Graph graph) {

		return new SelectingIterator<Relation> (graph.getRootContextNode().getAllRelations()) {

			@Override
			public boolean select(Relation equivalenceRelation) {

				return XDIDictionaryConstants.XRI_S_IS.equals(equivalenceRelation.getArcXri()) || XDIDictionaryConstants.XRI_S_IS_BANG.equals(equivalenceRelation.getArcXri());
			}
		};
	}

	/*
	 * Methods for types of context nodes.
	 */

	public static Iterator<XDI3Segment> getContextNodeTypes(ContextNode contextNode) {

		return new MappingContextNodeXriIterator(new MappingRelationTargetContextNodeIterator(contextNode.getRelations(XDIDictionaryConstants.XRI_S_IS_TYPE)));
	}

	public static XDI3Segment getContextNodeType(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XRI_S_IS_TYPE).getTargetContextNodeXri();
	}

	public static boolean isContextNodeType(ContextNode contextNode, XDI3Segment type) {

		return contextNode.containsRelation(XDIDictionaryConstants.XRI_S_IS_TYPE, type);
	}

	public static void addContextNodeType(ContextNode contextNode, XDI3Segment type) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS_TYPE, type);
	}

	public static void removeContextNodeType(ContextNode contextNode, XDI3Segment type) {

		contextNode.deleteRelation(XDIDictionaryConstants.XRI_S_IS_TYPE, type);
	}

	public static void removeContextNodeTypes(ContextNode contextNode) {

		contextNode.deleteRelations(XDIDictionaryConstants.XRI_S_IS_TYPE);
	}

	public static void setContextNodeType(ContextNode contextNode, XDI3Segment type) {

		removeContextNodeTypes(contextNode);
		addContextNodeType(contextNode, type);
	}
}
