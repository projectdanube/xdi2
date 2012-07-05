package xdi2.core.features.linkcontracts;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;

public class LinkContracts {

	private LinkContracts() { }

	/**
	 * Given a graph, lists all link contracts.
	 * @param graph The graph.
	 * @return An iterator over link contracts.
	 */
	public static Iterator<LinkContract> getAllLinkContracts(Graph graph) {

		return null;
	}

	/**
	 * Returns an existing XDI link contract under a context node, or creates a new one.
	 * @param create Whether to create an XDI link contract if it does not exist.
	 * @return The existing or newly created XDI link contract.
	 */
	public static LinkContract getLinkContract(ContextNode contextNode, boolean create) {

		ContextNode linkContractContextNode = contextNode.getContextNode(XDILinkContractConstants.XRI_SS_DO);
		if (linkContractContextNode == null && create) linkContractContextNode = contextNode.createContextNode(XDILinkContractConstants.XRI_SS_DO); 
		if (linkContractContextNode == null) return null;

		return new LinkContract(linkContractContextNode);
	}
}
