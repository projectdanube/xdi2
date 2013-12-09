package xdi2.core.features.linkcontracts;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.xri3.XDI3Segment;

/**
 * An XDI public link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class PublicLinkContract extends LinkContract {

	private static final long serialVersionUID = -5384390106585674311L;

	protected PublicLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI public link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI public link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (! XDILinkContractConstants.XRI_S_PUBLIC_DO.equals(xdiEntity.getXri())) return false;

			return true;
		} else {

			return false;
		}
	}

	/**
	 * Factory method that creates an XDI public link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI public link contract.
	 * @return The XDI public link contract.
	 */
	public static PublicLinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new PublicLinkContract(xdiEntity);
	}

	/**
	 * Factory method that finds or creates an XDI public link contract for a graph.
	 * @return The XDI public link contract.
	 */
	public static PublicLinkContract findPublicLinkContract(Graph graph, boolean create) {

		XDI3Segment publicLinkContractXri = XDILinkContractConstants.XRI_S_PUBLIC_DO;

		ContextNode publicLinkContractContextNode = create ? graph.setDeepContextNode(publicLinkContractXri) : graph.getDeepContextNode(publicLinkContractXri);
		if (publicLinkContractContextNode == null) return null;

		return new PublicLinkContract(XdiAbstractEntity.fromContextNode(publicLinkContractContextNode));
	}
}
