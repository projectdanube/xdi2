package xdi2.core.features.aggregation;

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

public class Aggregation {

	private Aggregation() { }

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

	public static void setAggregationContextNode(ContextNode contextNode, ContextNode aggregationContextNode) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_HAS, aggregationContextNode);
	}

	public static void setAggregationContextNode(ContextNode contextNode, XDIAddress aggregationContextNodeXDIAddress) {

		contextNode.setRelation(XDIDictionaryConstants.XDI_ADD_HAS, aggregationContextNodeXDIAddress);
	}

	public static void delAggregationContextNode(ContextNode contextNode, XDIAddress aggregationContextNodeXDIAddress) {

		contextNode.delRelation(XDIDictionaryConstants.XDI_ADD_HAS, aggregationContextNodeXDIAddress);
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

	public static Iterator<Relation> getAllAggregationRelations(ContextNode contextNode) {

		return new SelectingIterator<Relation> (contextNode.getAllRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getXDIAddress().equals(XDIDictionaryConstants.XDI_ADD_HAS);
			}
		};
	}
}
