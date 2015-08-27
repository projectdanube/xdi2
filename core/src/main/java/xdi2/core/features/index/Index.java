package xdi2.core.features.index;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiAttribute;
import xdi2.core.features.nodetypes.XdiAttributeCollection;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.ReadOnlyIterator;

public class Index {

	private Index() { }

	public static XdiEntityCollection getEntityIndex(Graph graph, XDIArc indexXDIArc, boolean create) {

		XDIArc xdiEntityCollectionXDIArc = XdiEntityCollection.createXDIArc(indexXDIArc);

		ContextNode contextNode = create ? graph.getRootContextNode().setContextNode(xdiEntityCollectionXDIArc) : graph.getRootContextNode().getContextNode(xdiEntityCollectionXDIArc);
		if (contextNode == null) return null;

		return XdiEntityCollection.fromContextNode(contextNode);
	}

	public static XdiAttributeCollection getAttributeIndex(Graph graph, XDIArc indexXDIArc, boolean create) {

		XDIArc xdiAttributeCollectionXDIArc = XdiAttributeCollection.createXDIArc(indexXDIArc);

		ContextNode contextNode = create ? graph.getRootContextNode().getContextNode(xdiAttributeCollectionXDIArc) : graph.getRootContextNode().getContextNode(xdiAttributeCollectionXDIArc);
		if (contextNode == null) return null;

		return XdiAttributeCollection.fromContextNode(contextNode);
	}

	public static void setEntityIndexAggregation(XdiEntityCollection xdiEntityCollection, XdiEntity xdiEntity) {

		Equivalence.setAggregationContextNode(xdiEntityCollection.getContextNode(), xdiEntity.getContextNode());
	}

	public static void setAttributeIndexAggregation(XdiAttributeCollection xdiAttributeCollection, XdiAttribute xdiAttribute) {

		Equivalence.setAggregationContextNode(xdiAttributeCollection.getContextNode(), xdiAttribute.getContextNode());
	}

	public static ReadOnlyIterator<XdiEntity> getEntityIndexAggregations(XdiEntityCollection xdiEntityCollection) {

		return new XdiAbstractEntity.MappingContextNodeXdiEntityIterator(Equivalence.getAggregationContextNodes(xdiEntityCollection.getContextNode()));
	}

	public static ReadOnlyIterator<XdiAttribute> getAttributeIndexAggregations(XdiAttributeCollection xdiAttributeCollection) {

		return new XdiAbstractAttribute.MappingContextNodeXdiAttributeIterator(Equivalence.getAggregationContextNodes(xdiAttributeCollection.getContextNode()));
	}
}
