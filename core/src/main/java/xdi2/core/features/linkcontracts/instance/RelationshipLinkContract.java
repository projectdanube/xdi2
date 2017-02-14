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
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;

/**
 * An XDI relationship link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class RelationshipLinkContract extends LinkContract {

	private static final long serialVersionUID = 6840561339666839961L;

	protected RelationshipLinkContract(XdiEntity xdiEntity) {

		super(xdiEntity);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI relationship link contract.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI relationship link contract.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (! ((XdiEntitySingleton) xdiEntity).getXDIArc().equals(XDILinkContractConstants.XDI_ARC_CONTRACT)) return false;

			if (getAuthorizingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (getRequestingAuthority(xdiEntity.getXDIAddress()) == null) return false;
		} else if (xdiEntity instanceof XdiEntityInstance && ((XdiEntityInstance) xdiEntity).getXdiCollection() != null) {

			if (! ((XdiEntityInstance) xdiEntity).getXdiCollection().getXDIArc().equals(XDILinkContractConstants.XDI_ARC_EC_CONTRACT)) return false;

			if (getAuthorizingAuthority(xdiEntity.getXDIAddress()) == null) return false;
			if (getRequestingAuthority(xdiEntity.getXDIAddress()) == null) return false;
		} else {

			return false;
		}

		return true;
	}

	/**
	 * Factory method that creates an XDI relationship link contract bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI relationship link contract.
	 * @return The XDI relationship link contract.
	 */
	public static RelationshipLinkContract fromXdiEntity(XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new RelationshipLinkContract(xdiEntity);
	}

	public static XDIAddress createRelationshipLinkContractXDIAddress(XDIAddress authorizingAuthority, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId, XDIArc instanceXDIArc) {

		if (authorizingAuthority == null) throw new NullPointerException();
		if (requestingAuthority == null) throw new NullPointerException();

		if (authorizingAuthority.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot use literal address of authorizing authority " + authorizingAuthority);
		if (requestingAuthority.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot use literal address requesting authority " + requestingAuthority);
		if (templateAuthorityAndId != null && templateAuthorityAndId.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot use literal address of template authority and ID " + templateAuthorityAndId);

		List<XDIArc> relationshipLinkContractXDIArcs = new ArrayList<XDIArc> ();

		XDIArc linkContractInnerRootXDIArc = XdiInnerRoot.createInnerRootXDIArc(
				authorizingAuthority, 
				requestingAuthority);

		relationshipLinkContractXDIArcs.add(linkContractInnerRootXDIArc);

		if (templateAuthorityAndId != null) {

			relationshipLinkContractXDIArcs.addAll(templateAuthorityAndId.getXDIArcs());
		}

		if (instanceXDIArc == null) {

			relationshipLinkContractXDIArcs.add(XDILinkContractConstants.XDI_ARC_CONTRACT);
		} else {

			relationshipLinkContractXDIArcs.add(XDILinkContractConstants.XDI_ARC_EC_CONTRACT);
			relationshipLinkContractXDIArcs.add(instanceXDIArc);
		}

		return XDIAddress.fromComponents(relationshipLinkContractXDIArcs);
	}

	public static XDIAddress createRelationshipLinkContractXDIAddress(XDIAddress authorizingAuthority, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId) {

		return createRelationshipLinkContractXDIAddress(authorizingAuthority, requestingAuthority, templateAuthorityAndId, null);
	}

	/**
	 * Factory method that finds or creates an XDI relationship link contract for a graph.
	 * @return The XDI relationship link contract.
	 */
	public static RelationshipLinkContract findRelationshipLinkContract(Graph graph, XDIAddress authorizingAuthority, XDIAddress requestingAuthority, XDIAddress templateAuthorityAndId, XDIArc instanceXDIArc, boolean create) {

		XDIAddress relationshipLinkContractXDIAddress = createRelationshipLinkContractXDIAddress(authorizingAuthority, requestingAuthority, templateAuthorityAndId, instanceXDIArc);

		ContextNode relationshipLinkContractContextNode = create ? graph.setDeepContextNode(relationshipLinkContractXDIAddress) : graph.getDeepContextNode(relationshipLinkContractXDIAddress, true);
		if (relationshipLinkContractContextNode == null) return null;

		return new RelationshipLinkContract(XdiAbstractEntity.fromContextNode(relationshipLinkContractContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDIAddress getAuthorizingAuthority(XDIAddress XDIaddress) {

		XDIaddress = XDIAddressUtil.extractXDIAddress(XDIaddress, XdiInnerRoot.class, false, true, false, false, true);
		if (XDIaddress == null) return null;

		XDIArc linkContractInnerRootXDIArc = XDIaddress.getFirstXDIArc();
		if (! XdiInnerRoot.isValidXDIArc(linkContractInnerRootXDIArc)) return null;

		return XdiInnerRoot.getSubjectOfInnerRootXDIArc(linkContractInnerRootXDIArc);
	}

	public static XDIAddress getRequestingAuthority(XDIAddress XDIaddress) {

		XDIaddress = XDIAddressUtil.extractXDIAddress(XDIaddress, XdiInnerRoot.class, false, true, false, false, true);
		if (XDIaddress == null) return null;

		XDIArc linkContractInnerRootXDIArc = XDIaddress.getFirstXDIArc();
		if (! XdiInnerRoot.isValidXDIArc(linkContractInnerRootXDIArc)) return null;

		return XdiInnerRoot.getPredicateOfInnerRootXDIArc(linkContractInnerRootXDIArc);
	}

	public static XDIAddress getTemplateAuthorityAndId(XDIAddress XDIaddress) {

		XDIaddress = XDIAddressUtil.extractXDIAddress(XDIaddress, XdiInnerRoot.class, false, true, false, false, true);
		if (XDIaddress == null) return null;

		int index = XDIAddressUtil.indexOfXDIArc(XDIaddress, XDILinkContractConstants.XDI_ARC_CONTRACT);
		if (index < 0) index = XDIAddressUtil.indexOfXDIArc(XDIaddress, XdiEntityCollection.createXDIArc(XDILinkContractConstants.XDI_ARC_CONTRACT));

		XDIAddress templateAuthorityAndId = XDIAddressUtil.subXDIAddress(XDIaddress, 1, index);
		if (XDIConstants.XDI_ADD_ROOT.equals(templateAuthorityAndId)) return null;

		return templateAuthorityAndId;
	}

	/*
	 * Instance methods
	 */

	public XdiInnerRoot getXdiInnerRoot() {

		XdiRoot xdiRoot = this.getXdiEntity().findRoot();
		if (! (xdiRoot instanceof XdiInnerRoot)) return null;

		return (XdiInnerRoot) xdiRoot;
	}

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
