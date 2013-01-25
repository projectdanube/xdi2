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
	 * Methods for equivalence links ($is).
	 */

	public static Iterator<Relation> getEquivalenceRelations(ContextNode contextNode) {

		return contextNode.getRelations(XDIDictionaryConstants.XRI_S_IS);
	}

	public static Iterator<ContextNode> getEquivalenceContextNodes(ContextNode contextNode) {

		return new MappingRelationTargetContextNodeIterator(getEquivalenceRelations(contextNode));
	}

	public static void addEquivalenceContextNode(ContextNode contextNode, ContextNode equivalenceContextNode) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_IS, equivalenceContextNode);
	}

	public static Iterator<Relation> getAllEquivalenceRelations(ContextNode contextNode) {

		return new SelectingIterator<Relation> (contextNode.getAllRelations()) {

			@Override
			public boolean select(Relation relation) {

				if (XDIDictionaryConstants.XRI_S_IS.equals(relation.getArcXri())) return true;

				return false;
			}
		};
	}

	public static Iterator<Relation> getIncomingEquivalenceRelations(ContextNode contextNode) {

		Iterator<Relation> equivalenceRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_IS);

		List<Iterator<Relation>> iterators = new ArrayList<Iterator<Relation>> ();
		iterators.add(equivalenceRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static Iterator<ContextNode> getIncomingEquivalenceContextNodes(ContextNode contextNode) {

		Iterator<Relation> incomingEquivalenceRelations = getIncomingEquivalenceRelations(contextNode);

		return new MappingRelationContextNodeIterator(incomingEquivalenceRelations);
	}

	/*
	 * Methods for reference links ($ref, $ref!).
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

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_REF, referenceContextNode);
	}

	public static Relation getPrivateReferenceRelation(ContextNode contextNode) {

		return contextNode.getRelation(XDIDictionaryConstants.XRI_S_REF_BANG);
	}

	public static ContextNode getPrivateReferenceContextNode(ContextNode contextNode) {

		Relation relation = getPrivateReferenceRelation(contextNode);
		if (relation == null) return null;

		return relation.follow();
	}

	public static void setPrivateReferenceContextNode(ContextNode contextNode, ContextNode privateReferenceContextNode) {

		contextNode.createRelation(XDIDictionaryConstants.XRI_S_REF_BANG, privateReferenceContextNode);
	}

	public static Iterator<Relation> getAllReferenceAndPrivateReferenceRelations(ContextNode contextNode) {

		return new SelectingIterator<Relation> (contextNode.getAllRelations()) {

			@Override
			public boolean select(Relation relation) {

				if (XDIDictionaryConstants.XRI_S_REF.equals(relation.getArcXri())) return true;
				if (XDIDictionaryConstants.XRI_S_REF_BANG.equals(relation.getArcXri())) return true;

				return false;
			}
		};
	}

	public static Iterator<Relation> getIncomingReferenceAndPrivateReferenceRelations(ContextNode contextNode) {

		Iterator<Relation> referenceRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_REF);
		Iterator<Relation> privateReferenceRelations = contextNode.getIncomingRelations(XDIDictionaryConstants.XRI_S_REF_BANG);

		List<Iterator<Relation>> iterators = new ArrayList<Iterator<Relation>> ();
		iterators.add(referenceRelations);
		iterators.add(privateReferenceRelations);

		return new CompositeIterator<Relation> (iterators.iterator());
	}

	public static Iterator<ContextNode> getIncomingReferenceAndPrivateReferenceContextNodes(ContextNode contextNode) {

		Iterator<Relation> incomingReferenceAndPrivateReferenceRelations = getIncomingReferenceAndPrivateReferenceRelations(contextNode);

		return new MappingRelationContextNodeIterator(incomingReferenceAndPrivateReferenceRelations);
	}
}
