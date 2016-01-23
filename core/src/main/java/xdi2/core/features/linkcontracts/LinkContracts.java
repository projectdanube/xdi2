package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

public class LinkContracts {

	private LinkContracts() { }

	/**
	 * Given a graph, lists all link contracts.
	 * @param graph The graph.
	 * @return An iterator over link contracts.
	 */
	public static ReadOnlyIterator<LinkContract> getAllLinkContracts(Graph graph) {

		ContextNode root = graph.getRootContextNode(true);
		Iterator<ContextNode> allContextNodes = root.getAllContextNodes();

		return new MappingContextNodeLinkContractIterator(allContextNodes);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeLinkContractIterator extends NotNullIterator<LinkContract> {

		public MappingContextNodeLinkContractIterator(Iterator<ContextNode> iterator) {

			super(new MappingIterator<ContextNode, LinkContract> (iterator) {

				@Override
				public LinkContract map(ContextNode contextNode) {

					XdiEntity xdiEntity = XdiAbstractEntity.fromContextNode(contextNode);
					if (xdiEntity == null) return null;

					return LinkContract.fromXdiEntity(xdiEntity);
				}
			});
		}
	}
}
