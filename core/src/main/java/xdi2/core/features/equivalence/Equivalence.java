package xdi2.core.features.equivalence;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Relation;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.MappingRelationContextNodeIterator;
import xdi2.core.util.iterators.MappingRelationTargetContextNodeIterator;
import xdi2.core.util.iterators.SelectingIterator;

public class Equivalence {

	private Equivalence() { }

	/*
	 * Methods for identity links ($is).
	 */

	public static Iterator<Relation> getIdentityRelations(ContextNode contextNode) {

		return contextNode.getRelations(XDIDictionaryConstants.XRI_S_IS);
	}

	public static Iterator<ContextNode> getIdentityContextNodes(ContextNode contextNode) {

		return new MappingRelationTargetContextNodeIterator(getIdentityRelations(contextNode));
	}

	public static void addIdentityContextNode(ContextNode contextNode, ContextNode identityContextNode) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, identityContextNode);
	}

	public static Iterator<Relation> getIncomingIdentityRelations(ContextNode contextNode) {

		Iterator<Relation> identityRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_IS);

		List<Iterator<? extends Relation>> iterators = new ArrayList<Iterator<? extends Relation>> ();
		iterators.add(identityRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static Iterator<ContextNode> getIncomingIdentityContextNodes(ContextNode contextNode) {

		Iterator<Relation> incomingIdentityRelations = getIncomingIdentityRelations(contextNode);

		return new MappingRelationContextNodeIterator(incomingIdentityRelations);
	}

	/*
	 * Methods for reference links ($ref).
	 */

	public static Relation getReferenceRelation(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XRI_S_REF);
	}

	public static ContextNode getReferenceContextNode(ContextNode contextNode) {

		Relation relation = getReferenceRelation(contextNode);
		if (relation == null) return null;

		return relation.follow();
	}

	public static void setReferenceContextNode(ContextNode contextNode, ContextNode referenceContextNode) {

		Relation referenceRelation = getReferenceRelation(contextNode);
		if (referenceRelation != null) referenceRelation.delete();

		Relation replacementRelation = getReplacementRelation(contextNode);
		if (replacementRelation != null) replacementRelation.delete();

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_REF, referenceContextNode);
	}

	public static Iterator<Relation> getIncomingReferenceRelations(ContextNode contextNode) {

		Iterator<Relation> referenceRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_REF);

		List<Iterator<? extends Relation>> iterators = new ArrayList<Iterator<? extends Relation>> ();
		iterators.add(referenceRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static Iterator<ContextNode> getIncomingReferenceContextNodes(ContextNode contextNode) {

		Iterator<Relation> incomingReferenceRelations = getIncomingReferenceRelations(contextNode);

		return new MappingRelationContextNodeIterator(incomingReferenceRelations);
	}

	/*
	 * Methods for replacement links ($rep).
	 */

	public static Relation getReplacementRelation(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XRI_S_REP);
	}

	public static ContextNode getReplacementContextNode(ContextNode contextNode) {

		Relation relation = getReplacementRelation(contextNode);
		if (relation == null) return null;

		return relation.follow();
	}

	public static void setReplacementContextNode(ContextNode contextNode, ContextNode replacementContextNode) {

		Relation referenceRelation = getReferenceRelation(contextNode);
		if (referenceRelation != null) referenceRelation.delete();

		Relation replacementRelation = getReplacementRelation(contextNode);
		if (replacementRelation != null) replacementRelation.delete();

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_REP, replacementContextNode);
	}

	public static Iterator<Relation> getIncomingReplacementRelations(ContextNode contextNode) {

		Iterator<Relation> replacementRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_REP);

		List<Iterator<? extends Relation>> iterators = new ArrayList<Iterator<? extends Relation>> ();
		iterators.add(replacementRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static Iterator<ContextNode> getIncomingReplacementContextNodes(ContextNode contextNode) {

		Iterator<Relation> incomingReplacementRelations = getIncomingReplacementRelations(contextNode);

		return new MappingRelationContextNodeIterator(incomingReplacementRelations);
	}

	/*
	 * Methods for reference and replacement links ($ref, $rep).
	 */

	public static Iterator<Relation> getAllReferenceAndReplacementRelations(ContextNode contextNode) {

		return new SelectingIterator<Relation> (contextNode.getAllRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getArcXri().equals(XDIDictionaryConstants.XRI_S_REF) || relation.getArcXri().equals(XDIDictionaryConstants.XRI_S_REP);
			}
		};
	}
}
