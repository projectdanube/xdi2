package xdi2.core.features.linkcontracts.instance;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityInstance;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;

/**
 * An XDI generic link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class GenericLinkContract extends LinkContract {

	private static final long serialVersionUID = 6840561339666839961L;

	protected GenericLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI generic link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI generic link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (! ((XdiEntitySingleton) xdiEntity).getXDIArc().equals(XDILinkContractConstants.XDI_ARC_DO)) return false;

			if (getAuthorizingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (getRequestingAuthority(xdiEntity.getXDIAddress()) == null) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityInstance && ((XdiEntityInstance) xdiEntity).getXdiCollection() != null) {

			if (! ((XdiEntityInstance) xdiEntity).getXdiCollection().getXDIArc().equals(XDILinkContractConstants.XDI_ARC_EC_DO)) return false;

			if (getAuthorizingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (getRequestingAuthority(xdiEntity.getXDIAddress()) == null) return false;

			return true;
		} else {

			return false;
		}
	}

	/**
	 * Factory method that creates an XDI generic link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI generic link contract.
	 * @return The XDI generic link contract.
	 */
	public static GenericLinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new GenericLinkContract(xdiEntity);
	}

	public static XDIAddress createGenericLinkContractXDIAddress(XDIAddress authorizingAuthority, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId, XDIArc instanceXDIArc) {

		if (authorizingAuthority == null) throw new NullPointerException();
		if (requestingAuthority == null) throw new NullPointerException();

		if (authorizingAuthority.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot use literal address of authorizing authority " + authorizingAuthority);
		if (requestingAuthority.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot use literal address requesting authority " + requestingAuthority);
		if (templateAuthorityAndId != null && templateAuthorityAndId.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot use literal address of template authority and ID " + templateAuthorityAndId);

		List<XDIArc> genericLinkContractXDIArcs = new ArrayList<XDIArc> ();

		XDIArc linkContractInnerRootXDIArc = XdiInnerRoot.createInnerRootXDIArc(
				authorizingAuthority, 
				requestingAuthority);

		genericLinkContractXDIArcs.add(linkContractInnerRootXDIArc);

		if (templateAuthorityAndId != null) {

			genericLinkContractXDIArcs.addAll(templateAuthorityAndId.getXDIArcs());
		}

		if (instanceXDIArc == null) {

			genericLinkContractXDIArcs.add(XDILinkContractConstants.XDI_ARC_DO);
		} else {

			genericLinkContractXDIArcs.add(XDILinkContractConstants.XDI_ARC_EC_DO);
			genericLinkContractXDIArcs.add(instanceXDIArc);
		}

		return XDIAddress.fromComponents(genericLinkContractXDIArcs);
	}

	public static XDIAddress createGenericLinkContractXDIAddress(XDIAddress authorizingAuthority, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId) {

		return createGenericLinkContractXDIAddress(authorizingAuthority, requestingAuthority, templateAuthorityAndId, null);
	}

	/**
	 * Factory method that finds or creates an XDI generic link contract for a graph.
	 * @return The XDI generic link contract.
	 */
	public static GenericLinkContract findGenericLinkContract(Graph graph, XDIAddress authorizingAuthority, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId, XDIArc instanceXDIArc, boolean create) {

		XDIAddress genericLinkContractXDIAddress = createGenericLinkContractXDIAddress(authorizingAuthority, requestingAuthority, templateAuthorityAndId, instanceXDIArc);

		ContextNode genericLinkContractContextNode = create ? graph.setDeepContextNode(genericLinkContractXDIAddress) : graph.getDeepContextNode(genericLinkContractXDIAddress, true);
		if (genericLinkContractContextNode == null) return null;

		return new GenericLinkContract(XdiAbstractEntity.fromContextNode(genericLinkContractContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDIAddress getAuthorizingAuthority(XDIAddress XDIaddress) {

		XDIArc linkContractInnerRootXDIArc = XDIaddress.getFirstXDIArc();
		if (! XdiInnerRoot.isValidXDIArc(linkContractInnerRootXDIArc)) return null;

		return XdiInnerRoot.getSubjectOfInnerRootXDIArc(linkContractInnerRootXDIArc);
	}

	public static XDIAddress getRequestingAuthority(XDIAddress XDIaddress) {

		XDIArc linkContractInnerRootXDIArc = XDIaddress.getFirstXDIArc();
		if (! XdiInnerRoot.isValidXDIArc(linkContractInnerRootXDIArc)) return null;

		return XdiInnerRoot.getPredicateOfInnerRootXDIArc(linkContractInnerRootXDIArc);
	}

	public static XDIAddress getTemplateAuthorityAndId(XDIAddress XDIaddress) {

		int index = XDIAddressUtil.indexOfXDIArc(XDIaddress, XDILinkContractConstants.XDI_ARC_DO);
		if (index < 0) index = XDIAddressUtil.indexOfXDIArc(XDIaddress, XdiEntityCollection.createXDIArc(XDILinkContractConstants.XDI_ARC_DO));

		XDIAddress templateAuthorityAndId = XDIAddressUtil.subXDIAddress(XDIaddress, 1, index);
		if (XDIConstants.XDI_ADD_ROOT.equals(templateAuthorityAndId)) return null;

		return templateAuthorityAndId;
	}

	/*
	 * Instance methods
	 */

	public XDIAddress getAuthorizingAuthority() {

		return getAuthorizingAuthority(this.getContextNode().getXDIAddress());
	}

	public XDIAddress getRequestingAuthority() {

		return getRequestingAuthority(this.getContextNode().getXDIAddress());
	}

	public XDIAddress getTemplateAuthorityAndId() {

		return getTemplateAuthorityAndId(this.getContextNode().getXDIAddress());
	}
}
