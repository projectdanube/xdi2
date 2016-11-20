package xdi2.core.features.linkcontracts.instance;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityInstance;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * An XDI public link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class PublicLinkContract extends RelationshipLinkContract {

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

		if (! RelationshipLinkContract.isValid(xdiEntity)) return false;

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (RelationshipLinkContract.getAuthorizingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (RelationshipLinkContract.getRequestingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (RelationshipLinkContract.getTemplateAuthorityAndId(xdiEntity.getXDIAddress()) != null) return false;

			if (! XDILinkContractConstants.XDI_ADD_PUBLIC.equals(RelationshipLinkContract.getRequestingAuthority(xdiEntity.getXDIAddress()))) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityInstance) {

			return false;
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

	public static XDIAddress createPublicLinkContractXDIAddress(XDIAddress ownerXDIAddress) {

		return RelationshipLinkContract.createRelationshipLinkContractXDIAddress(ownerXDIAddress, XDILinkContractConstants.XDI_ADD_PUBLIC, null, null);
	}

	/**
	 * Factory method that finds or creates an XDI public link contract for a graph.
	 * @return The XDI public link contract.
	 */
	public static PublicLinkContract findPublicLinkContract(Graph graph, boolean create) {

		XDIAddress ownerXDIAddress = GraphUtil.getOwnerXDIAddress(graph);
		if (ownerXDIAddress == null) return null;

		RelationshipLinkContract relationshipLinkContract = RelationshipLinkContract.findRelationshipLinkContract(graph, ownerXDIAddress, XDILinkContractConstants.XDI_ADD_PUBLIC, null, null, create);
		if (relationshipLinkContract == null) return null;

		return fromXdiEntity(relationshipLinkContract.getXdiEntity());
	}
}
