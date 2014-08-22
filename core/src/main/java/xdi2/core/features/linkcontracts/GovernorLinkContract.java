package xdi2.core.features.linkcontracts;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityMember;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.AddressUtil;

/**
 * An XDI governor link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class GovernorLinkContract extends LinkContract {

	private static final long serialVersionUID = -4713483572204567144L;

	protected GovernorLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI governor link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI governor link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (! ((XdiEntitySingleton) xdiEntity).getArc().equals(XDILinkContractConstants.XDI_ARC_DO)) return false;

			if (getRequestingAuthority(xdiEntity.getAddress()) == null) return false;
			if (getTemplateAuthorityAndId(xdiEntity.getAddress()) == null) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityMember) {

			if (! ((XdiEntityMember) xdiEntity).getXdiCollection().getArc().equals(XDILinkContractConstants.XDI_ARC_EC_DO)) return false;

			if (getRequestingAuthority(xdiEntity.getAddress()) == null) return false;
			if (getTemplateAuthorityAndId(xdiEntity.getAddress()) == null) return false;

			return true;
		} else {

			return false;
		}
	}

	/**
	 * Factory method that creates an XDI governor link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI governor link contract.
	 * @return The XDI governor link contract.
	 */
	public static GovernorLinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new GovernorLinkContract(xdiEntity);
	}

	public static XDIAddress createGovernorLinkContractAddress(XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId) {

		if (requestingAuthority == null) throw new NullPointerException();
		if (templateAuthorityAndId == null) throw new NullPointerException();

		List<XDIArc> governorLinkContractarcs = new ArrayList<XDIArc> ();

		XDIArc linkContractInnerRootarc = XdiInnerRoot.createInnerRootarc(
				requestingAuthority, 
				AddressUtil.concatAddresses(templateAuthorityAndId, XDILinkContractConstants.XDI_ARC_DO_VARIABLE));

		governorLinkContractarcs.add(linkContractInnerRootarc);

		governorLinkContractarcs.add(XDILinkContractConstants.XDI_ARC_DO);

		return XDIAddress.fromComponents(governorLinkContractarcs);
	}

	/**
	 * Factory method that finds or creates an XDI governor link contract for a graph.
	 * @return The XDI governor link contract.
	 */
	public static GovernorLinkContract findGovernorLinkContract(Graph graph, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId, boolean create) {

		XDIAddress governorLinkContractAddress = createGovernorLinkContractAddress(requestingAuthority, templateAuthorityAndId);

		ContextNode governorLinkContractContextNode = create ? graph.setDeepContextNode(governorLinkContractAddress) : graph.getDeepContextNode(governorLinkContractAddress, true);
		if (governorLinkContractContextNode == null) return null;

		return new GovernorLinkContract(XdiAbstractEntity.fromContextNode(governorLinkContractContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDIAddress getRequestingAuthority(XDIAddress xri) {

		XDIArc linkContractInnerRootarc = xri.getFirstArc();
		if (! XdiInnerRoot.isInnerRootarc(linkContractInnerRootarc)) return null;

		XDIAddress subjectAddress = XdiInnerRoot.getSubjectOfInnerRootAddress(linkContractInnerRootarc);
		XDIAddress requestingAuthority = subjectAddress;

		return requestingAuthority;
	}

	public static XDIAddress getTemplateAuthorityAndId(XDIAddress xri) {

		XDIArc linkContractInnerRootarc = xri.getFirstArc();
		if (! XdiInnerRoot.isInnerRootarc(linkContractInnerRootarc)) return null;

		XDIAddress predicateAddress = XdiInnerRoot.getPredicateOfInnerRootAddress(linkContractInnerRootarc);
		if (AddressUtil.endsWith(predicateAddress, XDILinkContractConstants.XDI_ADD_DO_VARIABLE) == null) return null;

		XDIAddress templateAuthorityAndId = AddressUtil.parentAddress(predicateAddress, -1);

		return templateAuthorityAndId;
	}

	/*
	 * Instance methods
	 */

	public XDIAddress getRequestingAuthority() {

		return getRequestingAuthority(this.getContextNode().getAddress());
	}

	public XDIAddress getTemplateAuthorityAndId() {

		return getTemplateAuthorityAndId(this.getContextNode().getAddress());
	}
}
