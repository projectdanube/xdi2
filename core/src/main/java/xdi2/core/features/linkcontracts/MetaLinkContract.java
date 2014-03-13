package xdi2.core.features.linkcontracts;

import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityMember;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.util.GraphUtil;
import xdi2.core.xri3.XDI3Segment;

/**
 * An XDI meta link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class MetaLinkContract extends GenericLinkContract {

	private static final long serialVersionUID = 1373222090414868359L;

	protected MetaLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI meta link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI meta link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (getRequestingAuthority(xdiEntity.getXri()) == null) return false;
			if (getTemplateAuthorityAndId(xdiEntity.getXri()) == null) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityMember) {

			if (getRequestingAuthority(xdiEntity.getXri()) == null) return false;
			if (getTemplateAuthorityAndId(xdiEntity.getXri()) == null) return false;

			return true;
		} else {

			return false;
		}
	}

	/**
	 * Factory method that creates an XDI meta link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI meta link contract.
	 * @return The XDI meta link contract.
	 */
	public static MetaLinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new MetaLinkContract(xdiEntity);
	}

	public static XDI3Segment createLinkContractXri(XDI3Segment requestingAuthority, XDI3Segment templateAuthorityAndId) {

		return GenericLinkContract.createGenericLinkContractXri(requestingAuthority, null, templateAuthorityAndId);
	}

	/**
	 * Factory method that finds or creates an XDI meta link contract for a graph.
	 * @return The XDI meta link contract.
	 */
	public static MetaLinkContract findMetaLinkContract(Graph graph, XDI3Segment requestingAuthority, XDI3Segment templateAuthorityAndId, boolean create) {

		XDI3Segment ownerXri = GraphUtil.getOwnerXri(graph);
		if (ownerXri == null) return null;

		GenericLinkContract genericLinkContract = GenericLinkContract.findGenericLinkContract(graph, requestingAuthority, null, templateAuthorityAndId, create);
		if (genericLinkContract == null) return null;

		return fromXdiEntity(genericLinkContract.getXdiEntity());
	}

	/*
	 * Instance methods
	 */

	public void setLinkContractTemplate(LinkContractTemplate linkContractTemplate) {

		this.setPermissionTargetAddress(XDILinkContractConstants.XRI_S_SET, linkContractTemplate.getXdiEntity().getXri());
	}
}
