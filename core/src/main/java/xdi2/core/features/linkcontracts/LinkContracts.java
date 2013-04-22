package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiAbstractSubGraph;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiSubGraph;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Segment;


public class LinkContracts {


	private LinkContracts() {
	}

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
	public static LinkContract getLinkContract(ContextNode contextNode, boolean create) {

		XdiEntitySingleton xdiEntitySingleton = XdiAbstractSubGraph.fromContextNode(contextNode).getXdiEntitySingleton(XDILinkContractConstants.XRI_SS_DO, create);
		if (xdiEntitySingleton == null) return null;

		return LinkContract.fromXdiEntity(xdiEntitySingleton);
	}

	/**
	 * Find an XDI link contract given a graph and the XRI of the XDI link contract
	 * @param graph The graph of the XDI link contract
	 * @param contextNodeXri The fully qualified XRI of the XDI link contract
	 * @return The XDI link contract, or null
	 */
	public static LinkContract getLinkContract(Graph graph, XDI3Segment contextNodeXri, boolean create) {

		ContextNode contextNode = graph.findContextNode(contextNodeXri, create);

		return getLinkContract(contextNode, create);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeLinkContractIterator extends NotNullIterator<LinkContract> {

		public MappingContextNodeLinkContractIterator(Iterator<ContextNode> iterator) {

			super(new MappingIterator<ContextNode, LinkContract> (iterator) {

				@Override
				public LinkContract map(ContextNode contextNode) {

					XdiSubGraph xdiSubGraph = XdiAbstractSubGraph.fromContextNode(contextNode);
					if (! (xdiSubGraph instanceof XdiEntity)) return null;

					return LinkContract.fromXdiEntity((XdiEntity) xdiSubGraph);
				}
			});
		}
	}
}
