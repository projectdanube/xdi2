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
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

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

			if (! ((XdiEntitySingleton) xdiEntity).getArcXri().equals(XDILinkContractConstants.XRI_SS_DO)) return false;

			if (getRequestingAuthority(xdiEntity.getXri()) == null) return false;
			if (getTemplateAuthorityAndId(xdiEntity.getXri()) == null) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityMember) {

			if (! ((XdiEntityMember) xdiEntity).getXdiCollection().getArcXri().equals(XDILinkContractConstants.XRI_SS_EC_DO)) return false;

			if (getRequestingAuthority(xdiEntity.getXri()) == null) return false;
			if (getTemplateAuthorityAndId(xdiEntity.getXri()) == null) return false;

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

	public static XDI3Segment createGovernorLinkContractXri(XDI3Segment requestingAuthority, XDI3Segment templateAuthorityAndId) {

		if (requestingAuthority == null) throw new NullPointerException();
		if (templateAuthorityAndId == null) throw new NullPointerException();

		List<XDI3SubSegment> governorLinkContractArcXris = new ArrayList<XDI3SubSegment> ();

		XDI3SubSegment linkContractInnerRootArcXri = XdiInnerRoot.createInnerRootArcXri(
				requestingAuthority, 
				XDI3Util.concatXris(templateAuthorityAndId, XDILinkContractConstants.XRI_SS_DO_VARIABLE));

		governorLinkContractArcXris.add(linkContractInnerRootArcXri);

		governorLinkContractArcXris.add(XDILinkContractConstants.XRI_SS_DO);

		return XDI3Segment.fromComponents(governorLinkContractArcXris);
	}

	/**
	 * Factory method that finds or creates an XDI governor link contract for a graph.
	 * @return The XDI governor link contract.
	 */
	public static GovernorLinkContract findGovernorLinkContract(Graph graph, XDI3Segment requestingAuthority, XDI3Segment templateAuthorityAndId, boolean create) {

		XDI3Segment governorLinkContractXri = createGovernorLinkContractXri(requestingAuthority, templateAuthorityAndId);

		ContextNode governorLinkContractContextNode = create ? graph.setDeepContextNode(governorLinkContractXri) : graph.getDeepContextNode(governorLinkContractXri, true);
		if (governorLinkContractContextNode == null) return null;

		return new GovernorLinkContract(XdiAbstractEntity.fromContextNode(governorLinkContractContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDI3Segment getRequestingAuthority(XDI3Segment xri) {

		XDI3SubSegment linkContractInnerRootArcXri = xri.getFirstSubSegment();
		if (! XdiInnerRoot.isInnerRootArcXri(linkContractInnerRootArcXri)) return null;

		XDI3Segment subjectXri = XdiInnerRoot.getSubjectOfInnerRootXri(linkContractInnerRootArcXri);
		XDI3Segment requestingAuthority = subjectXri;

		return requestingAuthority;
	}

	public static XDI3Segment getTemplateAuthorityAndId(XDI3Segment xri) {

		XDI3SubSegment linkContractInnerRootArcXri = xri.getFirstSubSegment();
		if (! XdiInnerRoot.isInnerRootArcXri(linkContractInnerRootArcXri)) return null;

		XDI3Segment predicateXri = XdiInnerRoot.getPredicateOfInnerRootXri(linkContractInnerRootArcXri);
		if (XDI3Util.endsWith(predicateXri, XDILinkContractConstants.XRI_S_DO_VARIABLE) == null) return null;

		XDI3Segment templateAuthorityAndId = XDI3Util.parentXri(predicateXri, -1);

		return templateAuthorityAndId;
	}

	/*
	 * Instance methods
	 */

	public XDI3Segment getRequestingAuthority() {

		return getRequestingAuthority(this.getContextNode().getXri());
	}

	public XDI3Segment getTemplateAuthorityAndId() {

		return getTemplateAuthorityAndId(this.getContextNode().getXri());
	}
}
