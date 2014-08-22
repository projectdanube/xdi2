package xdi2.core.features.linkcontracts;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.AddressUtil;

/**
 * An XDI link contract template, represented as an XDI variable.
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

		if (! xdiVariable.getArc().equals(XDILinkContractConstants.XDI_ARC_DO_VARIABLE)) return false;

		if (getTemplateAuthorityAndId(xdiVariable.getAddress()) == null) return false;

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

	public static XDIAddress createLinkContractTemplateAddress(XDIAddress templateAuthorityAndId) {

		if (templateAuthorityAndId == null) throw new NullPointerException();
		
		List<XDIArc> linkContractTemplatearcs = new ArrayList<XDIArc> ();

		linkContractTemplatearcs.addAll(templateAuthorityAndId.getArcs());

		linkContractTemplatearcs.add(XDILinkContractConstants.XDI_ARC_DO_VARIABLE);

		return XDIAddress.fromComponents(linkContractTemplatearcs);
	}

	/**
	 * Factory method that finds or creates an XDI link contract template for a graph.
	 * @return The XDI link contract template.
	 */
	public static LinkContractTemplate findLinkContractTemplate(Graph graph, XDIAddress templateAuthorityAndId, boolean create) {

		XDIAddress linkContractTemplateAddress = createLinkContractTemplateAddress(templateAuthorityAndId);

		ContextNode linkContractTemplateContextNode = create ? graph.setDeepContextNode(linkContractTemplateAddress) : graph.getDeepContextNode(linkContractTemplateAddress, true);
		if (linkContractTemplateContextNode == null) return null;

		return new LinkContractTemplate(XdiVariable.fromContextNode(linkContractTemplateContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDIAddress getTemplateAuthorityAndId(XDIAddress xri) {

		int index = AddressUtil.indexOfAddress(xri, XDILinkContractConstants.XDI_ARC_DO_VARIABLE);
		if (index < 0) return null;

		return AddressUtil.subAddress(xri, 0, index);
	}

	/*
	 * Instance methods
	 */

	public GenericLinkContract instantiate() {

		throw new RuntimeException("Not implemented.");
	}

	public XDIAddress getTemplateAuthorityAndId() {

		return getTemplateAuthorityAndId(this.getContextNode().getAddress());
	}
}
