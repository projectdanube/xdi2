package xdi2.core.features.linkcontracts.instance;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityInstance;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * An XDI send link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class SendLinkContract extends RelationshipLinkContract {

	private static final long serialVersionUID = 3468212632891253054L;

	protected SendLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI send link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI send link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (! RelationshipLinkContract.isValid(xdiEntity)) return false;

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (RelationshipLinkContract.getAuthorizingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (RelationshipLinkContract.getRequestingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (RelationshipLinkContract.getTemplateAuthorityAndId(xdiEntity.getXDIAddress()) != null) return false;

			if (! XDILinkContractConstants.XDI_ADD_SEND.equals(RelationshipLinkContract.getRequestingAuthority(xdiEntity.getXDIAddress()))) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityInstance) {

			return false;
		} else {

			return false;
		}
	}

	/**
	 * Factory method that creates an XDI send link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI send link contract.
	 * @return The XDI send link contract.
	 */
	public static SendLinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new SendLinkContract(xdiEntity);
	}

	public static XDIAddress createSendLinkContractXDIAddress(XDIAddress ownerXDIAddress) {

		return RelationshipLinkContract.createRelationshipLinkContractXDIAddress(ownerXDIAddress, XDILinkContractConstants.XDI_ADD_SEND, null, null);
	}

	/**
	 * Factory method that finds or creates an XDI send link contract for a graph.
	 * @return The XDI send link contract.
	 */
	public static SendLinkContract findSendLinkContract(Graph graph, boolean create) {

		XDIAddress ownerXDIAddress = GraphUtil.getOwnerXDIAddress(graph);
		if (ownerXDIAddress == null) return null;

		RelationshipLinkContract relationshipLinkContract = RelationshipLinkContract.findRelationshipLinkContract(graph, ownerXDIAddress, XDILinkContractConstants.XDI_ADD_SEND, null, null, create);
		if (relationshipLinkContract == null) return null;

		return fromXdiEntity(relationshipLinkContract.getXdiEntity());
	}
}
