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
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

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

			if (! ((XdiEntitySingleton) xdiEntity).getArcXri().equals(XDILinkContractConstants.XRI_SS_DO)) return false;

			if (getAuthorizingAuthority(xdiEntity.getXri()) == null) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityMember) {

			if (! ((XdiEntityMember) xdiEntity).getXdiCollection().getArcXri().equals(XDILinkContractConstants.XRI_SS_EC_DO)) return false;

			if (getAuthorizingAuthority(xdiEntity.getXri()) == null) return false;

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

	public static XDI3Segment createGenericLinkContractXri(XDI3Segment authorizingAuthority, XDI3Segment requestingAuthority, XDI3Segment templateAuthorityAndId) {

		if (authorizingAuthority == null) throw new NullPointerException();
		if (requestingAuthority == null) throw new NullPointerException();

		List<XDI3SubSegment> genericLinkContractArcXris = new ArrayList<XDI3SubSegment> ();

		XDI3SubSegment linkContractInnerRootArcXri = XdiInnerRoot.createInnerRootArcXri(authorizingAuthority, requestingAuthority);

		genericLinkContractArcXris.add(linkContractInnerRootArcXri);

		if (templateAuthorityAndId != null) {

			genericLinkContractArcXris.addAll(templateAuthorityAndId.getSubSegments());
		}

		genericLinkContractArcXris.add(XDILinkContractConstants.XRI_SS_DO);

		return XDI3Segment.fromComponents(genericLinkContractArcXris);
	}

	/**
	 * Factory method that finds or creates an XDI generic link contract for a graph.
	 * @return The XDI generic link contract.
	 */
	public static GenericLinkContract findGenericLinkContract(Graph graph, XDI3Segment authorizingAuthority, XDI3Segment requestingAuthority, XDI3Segment templateAuthorityAndId, boolean create) {

		XDI3Segment genericLinkContractXri = createGenericLinkContractXri(authorizingAuthority, requestingAuthority, templateAuthorityAndId);

		ContextNode genericLinkContractContextNode = create ? graph.setDeepContextNode(genericLinkContractXri) : graph.getDeepContextNode(genericLinkContractXri, true);
		if (genericLinkContractContextNode == null) return null;

		return new GenericLinkContract(XdiAbstractEntity.fromContextNode(genericLinkContractContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDI3Segment getAuthorizingAuthority(XDI3Segment xri) {

		XDI3SubSegment linkContractInnerRootArcXri = xri.getFirstSubSegment();
		if (! XdiInnerRoot.isInnerRootArcXri(linkContractInnerRootArcXri)) return null;

		return XdiInnerRoot.getSubjectOfInnerRootXri(linkContractInnerRootArcXri);
	}

	public static XDI3Segment getRequestingAuthority(XDI3Segment xri) {

		XDI3SubSegment linkContractInnerRootArcXri = xri.getFirstSubSegment();
		if (! XdiInnerRoot.isInnerRootArcXri(linkContractInnerRootArcXri)) return null;

		return XdiInnerRoot.getPredicateOfInnerRootXri(linkContractInnerRootArcXri);
	}

	public static XDI3Segment getTemplateAuthorityAndId(XDI3Segment xri) {

		int index = XDI3Util.indexOfXri(xri, XDILinkContractConstants.XRI_SS_DO);
		if (index < 0) index = XDI3Util.indexOfXri(xri, XdiEntityCollection.createArcXri(XDILinkContractConstants.XRI_SS_DO));

		return XDI3Util.subXri(xri, 1, index);
	}

	/*
	 * Instance methods
	 */

	public XDI3Segment getAuthorizingAuthority() {

		return getAuthorizingAuthority(this.getContextNode().getXri());
	}

	public XDI3Segment getRequestingAuthority() {

		return getRequestingAuthority(this.getContextNode().getXri());
	}

	public XDI3Segment getTemplateAuthorityAndId() {

		return getTemplateAuthorityAndId(this.getContextNode().getXri());
	}
}
