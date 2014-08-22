package xdi2.core.features.linkcontracts.template;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContractBase;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI link contract template, represented as an XDI variable.
 * 
 * Example addresses:
 * [+]!:uuid:1234#registration$template
 * 
 * @author markus
 */
public class LinkContractTemplate extends LinkContractBase {

	private static final long serialVersionUID = 1373222090414868359L;

	protected LinkContractTemplate(XdiVariable xdiVariable) {

		super(xdiVariable);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI variable is a valid XDI link contract template.
	 * @param xdiVariable The XDI variable to check.
	 * @return True if the XDI variable is a valid XDI link contract template.
	 */
	public static boolean isValid(XdiVariable xdiVariable) {

		if (! xdiVariable.getArcXri().equals(XDILinkContractConstants.XRI_SS_TEMPLATE)) return false;

		if (getTemplateAuthorityAndId(xdiVariable.getXri()) == null) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI link contract template bound to a given XDI variable.
	 * @param xdiVariable The XDI variable that is an XDI link contract template.
	 * @return The XDI link contract template.
	 */
	public static LinkContractTemplate fromXdiVariable(XdiVariable xdiVariable) {

		if (! isValid(xdiVariable)) return null;

		return new LinkContractTemplate(xdiVariable);
	}

	public static XDI3Segment createLinkContractTemplateXri(XDI3Segment templateAuthorityAndId) {

		if (templateAuthorityAndId == null) throw new NullPointerException();
		
		List<XDI3SubSegment> linkContractTemplateArcXris = new ArrayList<XDI3SubSegment> ();

		linkContractTemplateArcXris.addAll(templateAuthorityAndId.getSubSegments());

		linkContractTemplateArcXris.add(XDILinkContractConstants.XRI_SS_TEMPLATE);

		return XDI3Segment.fromComponents(linkContractTemplateArcXris);
	}

	/**
	 * Factory method that finds or creates an XDI link contract template for a graph.
	 * @return The XDI link contract template.
	 */
	public static LinkContractTemplate findLinkContractTemplate(Graph graph, XDI3Segment templateAuthorityAndId, boolean create) {

		XDI3Segment linkContractTemplateXri = createLinkContractTemplateXri(templateAuthorityAndId);

		ContextNode linkContractTemplateContextNode = create ? graph.setDeepContextNode(linkContractTemplateXri) : graph.getDeepContextNode(linkContractTemplateXri, true);
		if (linkContractTemplateContextNode == null) return null;

		return new LinkContractTemplate(XdiVariable.fromContextNode(linkContractTemplateContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDI3Segment getTemplateAuthorityAndId(XDI3Segment xri) {

		int index = XDI3Util.indexOfXri(xri, XDILinkContractConstants.XRI_SS_TEMPLATE);
		if (index < 0) return null;

		return XDI3Util.subXri(xri, 0, index);
	}

	/*
	 * Instance methods
	 */

	public GenericLinkContract instantiate() {

		throw new RuntimeException("Not implemented.");
	}

	public XDI3Segment getTemplateAuthorityAndId() {

		return getTemplateAuthorityAndId(this.getContextNode().getXri());
	}
}
