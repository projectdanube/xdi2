package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public class LinkContracts {

	private LinkContracts() { }

	/**
	 * Given a graph, lists all link contracts.
	 * @param graph The graph.
	 * @return An iterator over link contracts.
	 */
	public static Iterator<LinkContract> getAllLinkContracts(Graph graph) {

		ContextNode root = graph.getRootContextNode();
		Iterator<ContextNode> allContextNodes = root.getAllContextNodes();

		return new MappingContextNodeLinkContractIterator(allContextNodes);
	}

	/**
	 * Returns an existing XDI link contract under a context node, or creates a new one.
	 * @param create Whether to create an XDI link contract if it does not exist.
	 * @return The existing or newly created XDI link contract.
	 */
	public static LinkContract getLinkContract(ContextNode parentContextNode, boolean create) {

		ContextNode contextNode = create ? parentContextNode.setDeepContextNode(XDILinkContractConstants.XRI_S_DO) : parentContextNode.getDeepContextNode(XDILinkContractConstants.XRI_S_DO);
		if (contextNode == null) return null;

		XdiEntitySingleton xdiEntitySingleton = XdiEntitySingleton.fromContextNode(contextNode);
		if (xdiEntitySingleton == null) return null;

		return LinkContract.fromXdiEntity(xdiEntitySingleton);
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
