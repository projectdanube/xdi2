package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.util.XDILinkContractConstants;
import xdi2.core.xri3.impl.XRI3Segment;

public class LinkContracts {

	/*
	 * //this map will hold tuples of source context nodes and LC objects
	 * created on a source context node. These are outgoing $do contextual arcs
	 * from a node. private Map<Graph, Map<ContextNode,LinkContract>>
	 * ownedLinkContracts = new LinkedHashMap<Graph,
	 * Map<ContextNode,LinkContract>>();
	 * 
	 * //this map will hold tuples of source context nodes and LC objects
	 * assigned from a source context node. These are incoming $is$do contextual
	 * arcs from a node.
	 * 
	 * private Map<Graph, Map<ContextNode,LinkContract>> assignedLinkContracts;
	 */
	private LinkContracts() {
	}

	/**
	 * Given a graph, lists all link contracts.
	 * 
	 * @param graph
	 *            The graph.
	 * @return An iterator over link contracts.
	 */
	public static Iterator<LinkContract> getAllLinkContracts(Graph graph) {

		return null;
	}

	/**
	 * Returns an existing XDI link contract under a context node, or creates a
	 * new one.
	 * 
	 * @param create
	 *            Whether to create an XDI link contract if it does not exist.
	 * @return The existing or newly created XDI link contract.
	 */
	public static LinkContract getLinkContract(ContextNode contextNode,
			boolean create) {

		ContextNode linkContractContextNode = contextNode
				.getContextNode(XDILinkContractConstants.XRI_SS_DO);
		if (linkContractContextNode == null && create)
			linkContractContextNode = contextNode
					.createContextNode(XDILinkContractConstants.XRI_SS_DO);
		if (linkContractContextNode == null)
			return null;

		LinkContract lc = new LinkContract(linkContractContextNode);

		return lc;
	}
	/**
	 * Find a Link Contract object given a graph and the XRI of the object
	 * @param graph The graph where this Link Contract is supposed to exist
	 * @param address The fully qualified XRI of the Link Contract object
	 * @return The Link Contract object if found or null
	 */

	public static LinkContract findLinkContractByAddress(Graph graph,
			XRI3Segment address) {

		return null;
	}

}
