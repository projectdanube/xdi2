package xdi2.core.features.linkcontracts.instance;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityInstance;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * An XDI connect link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class ConnectLinkContract extends GenericLinkContract {

	private static final long serialVersionUID = -853618290391848761L;

	protected ConnectLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI connect link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI connect link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (! GenericLinkContract.isValid(xdiEntity)) return false;

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (GenericLinkContract.getAuthorizingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (GenericLinkContract.getRequestingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (GenericLinkContract.getTemplateAuthorityAndId(xdiEntity.getXDIAddress()) != null) return false;

			if (! XDILinkContractConstants.XDI_ADD_CONNECT.equals(GenericLinkContract.getRequestingAuthority(xdiEntity.getXDIAddress()))) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityInstance) {

			return false;
		} else {

			return false;
		}
	}

	/**
	 * Factory method that creates an XDI connect link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI connect link contract.
	 * @return The XDI connect link contract.
	 */
	public static ConnectLinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new ConnectLinkContract(xdiEntity);
	}

	public static XDIAddress createConnectLinkContractXDIAddress(XDIAddress ownerXDIAddress) {

		return GenericLinkContract.createGenericLinkContractXDIAddress(ownerXDIAddress, XDILinkContractConstants.XDI_ADD_CONNECT, null, null);
	}

	/**
	 * Factory method that finds or creates an XDI connect link contract for a graph.
	 * @return The XDI connect link contract.
	 */
	public static ConnectLinkContract findConnectLinkContract(Graph graph, boolean create) {

		XDIAddress ownerXDIAddress = GraphUtil.getOwnerXDIAddress(graph);
		if (ownerXDIAddress == null) return null;

		GenericLinkContract genericLinkContract = GenericLinkContract.findGenericLinkContract(graph, ownerXDIAddress, XDILinkContractConstants.XDI_ADD_CONNECT, null, null, create);
		if (genericLinkContract == null) return null;

		return fromXdiEntity(genericLinkContract.getXdiEntity());
	}
}
