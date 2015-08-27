package xdi2.core.features.equivalence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingRelationContextNodeIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;

public class Equivalence {

	private Equivalence() { }

	/*
	 * Methods for identity links ($is).
	 */

	public static ReadOnlyIterator<Relation> getIdentityRelations(ContextNode contextNode) {

		ReadOnlyIterator<Relation> relations = contextNode.getRelations(XDIDictionaryConstants.XDI_ADD_IS);

		return relations;
	}

	public static ReadOnlyIterator<ContextNode> getIdentityContextNodes(ContextNode contextNode) {

		ReadOnlyIterator<Relation> relations = getIdentityRelations(contextNode);

		return new ReadOnlyIterator<ContextNode> (new MappingRelationTargetContextNodeIterator(relations));
	}

	public static ContextNode getIdentityContextNode(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_IS);

		return relation == null ? null : relation.followContextNode();
	}

	public static void setIdentityContextNode(ContextNode contextNode, ContextNode identityContextNode) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS, identityContextNode);
	}

	public static void setIdentityContextNode(ContextNode contextNode, XDIAddress identitycontextNodeXDIAddress) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_IS, identitycontextNodeXDIAddress);
	}

	public static ReadOnlyIterator<Relation> getIncomingIdentityRelations(ContextNode contextNode) {

		ReadOnlyIterator<Relation> identityRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XDI_ADD_IS);

		List<Iterator<? extends Relation>> iterators = new ArrayList<Iterator<? extends Relation>> ();
		iterators.add(identityRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static ReadOnlyIterator<ContextNode> getIncomingIdentityContextNodes(ContextNode contextNode) {

		ReadOnlyIterator<Relation> incomingIdentityRelations = getIncomingIdentityRelations(contextNode);

		return new ReadOnlyIterator<ContextNode> (new MappingRelationContextNodeIterator(incomingIdentityRelations));
	}

	/*
	 * Methods for reference links ($ref).
	 */

	public static Relation getReferenceRelation(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_REF);
	}

	public static ContextNode getReferenceContextNode(ContextNode contextNode) {

		Relation relation = getReferenceRelation(contextNode);
		if (relation == null) return null;

		return relation.followContextNode();
	}

	public static void setReferenceContextNode(ContextNode contextNode, ContextNode referenceContextNode) {

		Relation referenceRelation = getReferenceRelation(contextNode);
		if (referenceRelation != null) referenceRelation.delete();

		Relation replacementRelation = getReplacementRelation(contextNode);
		if (replacementRelation != null) replacementRelation.delete();

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_REF, referenceContextNode);
	}

	public static void setReferenceContextNode(ContextNode contextNode, XDIAddress referencecontextNodeXDIAddress) {

		Relation referenceRelation = getReferenceRelation(contextNode);
		if (referenceRelation != null) referenceRelation.delete();

		Relation replacementRelation = getReplacementRelation(contextNode);
		if (replacementRelation != null) replacementRelation.delete();

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_REF, referencecontextNodeXDIAddress);
	}

	public static ReadOnlyIterator<Relation> getIncomingReferenceRelations(ContextNode contextNode) {

		ReadOnlyIterator<Relation> referenceRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XDI_ADD_REF);

		List<Iterator<? extends Relation>> iterators = new ArrayList<Iterator<? extends Relation>> ();
		iterators.add(referenceRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static ReadOnlyIterator<ContextNode> getIncomingReferenceContextNodes(ContextNode contextNode) {

		Iterator<Relation> incomingReferenceRelations = getIncomingReferenceRelations(contextNode);

		return new ReadOnlyIterator<ContextNode> (new MappingRelationContextNodeIterator(incomingReferenceRelations));
	}

	/*
	 * Methods for replacement links ($rep).
	 */

	public static Relation getReplacementRelation(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_REP);
	}

	public static ContextNode getReplacementContextNode(ContextNode contextNode) {

		Relation relation = getReplacementRelation(contextNode);
		if (relation == null) return null;

		return relation.followContextNode();
	}

	public static void setReplacementContextNode(ContextNode contextNode, ContextNode replacementContextNode) {

		Relation referenceRelation = getReferenceRelation(contextNode);
		if (referenceRelation != null) referenceRelation.delete();

		Relation replacementRelation = getReplacementRelation(contextNode);
		if (replacementRelation != null) replacementRelation.delete();

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_REP, replacementContextNode);
	}

	public static void setReplacementContextNode(ContextNode contextNode, XDIAddress replacementcontextNodeXDIAddress) {

		Relation referenceRelation = getReferenceRelation(contextNode);
		if (referenceRelation != null) referenceRelation.delete();

		Relation replacementRelation = getReplacementRelation(contextNode);
		if (replacementRelation != null) replacementRelation.delete();

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_REP, replacementcontextNodeXDIAddress);
	}

	public static ReadOnlyIterator<Relation> getIncomingReplacementRelations(ContextNode contextNode) {

		ReadOnlyIterator<Relation> replacementRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XDI_ADD_REP);

		List<Iterator<? extends Relation>> iterators = new ArrayList<Iterator<? extends Relation>> ();
		iterators.add(replacementRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static ReadOnlyIterator<ContextNode> getIncomingReplacementContextNodes(ContextNode contextNode) {

		Iterator<Relation> incomingReplacementRelations = getIncomingReplacementRelations(contextNode);

		return new ReadOnlyIterator<ContextNode> (new MappingRelationContextNodeIterator(incomingReplacementRelations));
	}

	/*
	 * Methods for aggregation links ($has).
	 */

	public static ReadOnlyIterator<Relation> getAggregationRelations(ContextNode contextNode) {

		ReadOnlyIterator<Relation> relations = contextNode.getRelations(XDIDictionaryConstants.XDI_ADD_HAS);

		return relations;
	}

	public static ReadOnlyIterator<ContextNode> getAggregationContextNodes(ContextNode contextNode) {

		ReadOnlyIterator<Relation> relations = getAggregationRelations(contextNode);

		return new ReadOnlyIterator<ContextNode> (new MappingRelationTargetContextNodeIterator(relations));
	}

	public static ContextNode getAggregationContextNode(ContextNode contextNode) {

		Relation relation = contextNode.getRelation(XDIDictionaryConstants.XDI_ADD_HAS);

		return relation == null ? null : relation.followContextNode();
	}

	public static void setAggregationContextNode(ContextNode contextNode, ContextNode identityContextNode) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_HAS, identityContextNode);
	}

	public static void setAggregationContextNode(ContextNode contextNode, XDIAddress identitycontextNodeXDIAddress) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_HAS, identitycontextNodeXDIAddress);
	}

	public static ReadOnlyIterator<Relation> getIncomingAggregationRelations(ContextNode contextNode) {

		ReadOnlyIterator<Relation> identityRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XDI_ADD_HAS);

		List<Iterator<? extends Relation>> iterators = new ArrayList<Iterator<? extends Relation>> ();
		iterators.add(identityRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static ReadOnlyIterator<ContextNode> getIncomingAggregationContextNodes(ContextNode contextNode) {

		ReadOnlyIterator<Relation> incomingAggregationRelations = getIncomingAggregationRelations(contextNode);

		return new ReadOnlyIterator<ContextNode> (new MappingRelationContextNodeIterator(incomingAggregationRelations));
	}

	/*
	 * Methods for reference and replacement links ($ref, $rep).
	 */

	public static Iterator<Relation> getAllReferenceAndReplacementRelations(ContextNode contextNode) {

		return new SelectingIterator<Relation> (contextNode.getAllRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getXDIAddress().equals(XDIDictionaryConstants.XDI_ADD_REF) || relation.getXDIAddress().equals(XDIDictionaryConstants.XDI_ADD_REP);
			}
		};
	}
}
