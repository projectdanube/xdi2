package xdi2.core.features.linkcontracts;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityMember;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI meta link contract, represented as an XDI entity.
 * 
 * @author markus
 */
public class MetaLinkContract extends LinkContract {

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

			if (! ((XdiEntitySingleton) xdiEntity).getBaseArcXri().equals(XdiAbstractContext.getBaseArcXri(XDILinkContractConstants.XRI_SS_DO))) return false;

			if (getRequestingParty(xdiEntity.getXri()) == null) return false;
			if (getTemplateId(xdiEntity.getXri()) == null) return false;

			return true;
		} else if (xdiEntity instanceof XdiEntityMember) {

			if (! ((XdiEntityMember) xdiEntity).getXdiCollection().getBaseArcXri().equals(XdiAbstractContext.getBaseArcXri(XDILinkContractConstants.XRI_SS_DO))) return false;

			if (getRequestingParty(xdiEntity.getXri()) == null) return false;
			if (getTemplateId(xdiEntity.getXri()) == null) return false;

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

	/**
	 * Factory method that finds or creates an XDI meta link contract for a graph.
	 * @return The XDI meta link contract.
	 */
	public static MetaLinkContract findMetaLinkContract(Graph graph, XDI3Segment requestingParty, XDI3Segment templateId, boolean create) {

		List<XDI3SubSegment> metaLinkContractArcXris = new ArrayList<XDI3SubSegment> ();
		metaLinkContractArcXris.add(XDILinkContractConstants.XRI_SS_TO_VARIABLE);
		metaLinkContractArcXris.addAll(requestingParty.getSubSegments());
		metaLinkContractArcXris.add(XDILinkContractConstants.XRI_SS_FROM);
		metaLinkContractArcXris.addAll(templateId.getSubSegments());
		metaLinkContractArcXris.add(XDILinkContractConstants.XRI_SS_DO);

		XDI3Segment metaLinkContractXri = XDI3Segment.fromComponents(metaLinkContractArcXris);

		ContextNode metaLinkContractContextNode = create ? graph.setDeepContextNode(metaLinkContractXri) : graph.getDeepContextNode(metaLinkContractXri);
		if (metaLinkContractContextNode == null) return null;

		return new MetaLinkContract(XdiAbstractEntity.fromContextNode(metaLinkContractContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDI3Segment getRequestingParty(XDI3Segment xri) {

		int index1 = XDI3Util.indexOfXri(xri, XDILinkContractConstants.XRI_SS_TO_VARIABLE);
		int index2 = XDI3Util.indexOfXri(xri, XDILinkContractConstants.XRI_SS_FROM);
		if (index1 < 0 || index2 < 0 || index1 >= index2) return null;

		return XDI3Util.subXri(xri, index1 + 1, index2);
	}

	public static XDI3Segment getTemplateId(XDI3Segment xri) {

		int index1 = XDI3Util.indexOfXri(xri, XDILinkContractConstants.XRI_SS_FROM);
		int index2 = XDI3Util.indexOfXri(xri, XDILinkContractConstants.XRI_SS_DO);
		if (index2 < 0) index2 = XDI3Util.indexOfXri(xri, XdiEntityCollection.createArcXri(XDILinkContractConstants.XRI_SS_DO));
		if (index1 < 0 || index2 < 0 || index1 >= index2) return null;

		return XDI3Util.subXri(xri, index1 + 1, index2);
	}

	/*
	 * Instance methods
	 */

	public void setLinkContractTemplate(LinkContractTemplate linkContractTemplate) {

		this.setPermissionTargetAddress(XDILinkContractConstants.XRI_S_SET, linkContractTemplate.getXdiEntity().getXri());
	}

	public XDI3Segment getRequestingParty() {

		return getRequestingParty(this.getContextNode().getXri());
	}

	public XDI3Segment getTemplateId() {

		return getTemplateId(this.getContextNode().getXri());
	}
}
