package xdi2.core.features.linkcontracts;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityMember;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.AddressUtil;

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

			if (! ((XdiEntitySingleton) xdiEntity).getArc().equals(XDILinkContractConstants.XDI_ARC_DO)) return false;

			if (getAuthorizingAuthority(xdiEntity.getAddress()) == null) return false;
			if (getRequestingAuthority(xdiEntity.getAddress()) == null) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityMember) {

			if (! ((XdiEntityMember) xdiEntity).getXdiCollection().getArc().equals(XDILinkContractConstants.XDI_ARC_EC_DO)) return false;

			if (getAuthorizingAuthority(xdiEntity.getAddress()) == null) return false;
			if (getRequestingAuthority(xdiEntity.getAddress()) == null) return false;

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

	public static XDIAddress createGenericLinkContractAddress(XDIAddress authorizingAuthority, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId) {

		if (authorizingAuthority == null) throw new NullPointerException();
		if (requestingAuthority == null) throw new NullPointerException();

		List<XDIArc> genericLinkContractarcs = new ArrayList<XDIArc> ();

		XDIArc linkContractInnerRootarc = XdiInnerRoot.createInnerRootarc(
				authorizingAuthority, 
				requestingAuthority);

		genericLinkContractarcs.add(linkContractInnerRootarc);

		if (templateAuthorityAndId != null) {

			genericLinkContractarcs.addAll(templateAuthorityAndId.getArcs());
		}

		genericLinkContractarcs.add(XDILinkContractConstants.XDI_ARC_DO);

		return XDIAddress.fromComponents(genericLinkContractarcs);
	}

	/**
	 * Factory method that finds or creates an XDI generic link contract for a graph.
	 * @return The XDI generic link contract.
	 */
	public static GenericLinkContract findGenericLinkContract(Graph graph, XDIAddress authorizingAuthority, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId, boolean create) {

		XDIAddress genericLinkContractAddress = createGenericLinkContractAddress(authorizingAuthority, requestingAuthority, templateAuthorityAndId);

		ContextNode genericLinkContractContextNode = create ? graph.setDeepContextNode(genericLinkContractAddress) : graph.getDeepContextNode(genericLinkContractAddress, true);
		if (genericLinkContractContextNode == null) return null;

		return new GenericLinkContract(XdiAbstractEntity.fromContextNode(genericLinkContractContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDIAddress getAuthorizingAuthority(XDIAddress xri) {

		XDIArc linkContractInnerRootarc = xri.getFirstArc();
		if (! XdiInnerRoot.isInnerRootArc(linkContractInnerRootarc)) return null;

		return XdiInnerRoot.getSubjectOfInnerRootArc(linkContractInnerRootarc);
	}

	public static XDIAddress getRequestingAuthority(XDIAddress xri) {

		XDIArc linkContractInnerRootarc = xri.getFirstArc();
		if (! XdiInnerRoot.isInnerRootArc(linkContractInnerRootarc)) return null;

		return XdiInnerRoot.getPredicateOfInnerRootArc(linkContractInnerRootarc);
	}

	public static XDIAddress getTemplateAuthorityAndId(XDIAddress xri) {

		int index = AddressUtil.indexOfAddress(xri, XDILinkContractConstants.XDI_ARC_DO);
		if (index < 0) index = AddressUtil.indexOfAddress(xri, XdiEntityCollection.createarc(XDILinkContractConstants.XDI_ARC_DO));

		return AddressUtil.subAddress(xri, 1, index);
	}

	/*
	 * Instance methods
	 */

	public XDIAddress getAuthorizingAuthority() {

		return getAuthorizingAuthority(this.getContextNode().getAddress());
	}

	public XDIAddress getRequestingAuthority() {

		return getRequestingAuthority(this.getContextNode().getAddress());
	}

	public XDIAddress getTemplateAuthorityAndId() {

		return getTemplateAuthorityAndId(this.getContextNode().getAddress());
	}
}
