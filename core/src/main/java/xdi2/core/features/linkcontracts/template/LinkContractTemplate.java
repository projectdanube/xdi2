package xdi2.core.features.linkcontracts.template;

import java.util.ArrayList;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDILinkContractConstants;
import xdi2.core.features.linkcontracts.LinkContractBase;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;

/**
 * An XDI link contract template, represented as an XDI variable.
 * 
 * Example addresses:
 * +!:uuid:1234#registration{$do}
 * 
 * @author markus
 */
public class LinkContractTemplate extends LinkContractBase<XdiEntitySingleton.Variable> {

	private static final long serialVersionUID = 1373222090414868359L;

	public static final LinkContractTemplate LINK_CONTRACT_TEMPLATE_ROOT;
	public static final LinkContractTemplate LINK_CONTRACT_TEMPLATE_SET;
	public static final LinkContractTemplate LINK_CONTRACT_TEMPLATE_PUSH;

	private XdiEntitySingleton.Variable xdiEntitySingletonVariable;

	static {

		LINK_CONTRACT_TEMPLATE_ROOT = null;
		LINK_CONTRACT_TEMPLATE_SET = null;
		LINK_CONTRACT_TEMPLATE_PUSH = null;
	}

	protected LinkContractTemplate(XdiEntitySingleton.Variable xdiEntitySingletonVariable) {

		this.xdiEntitySingletonVariable = xdiEntitySingletonVariable;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI variable is a valid XDI link contract template.
	 * @param xdiVariable The XDI variable to check.
	 * @return True if the XDI variable is a valid XDI link contract template.
	 */
	public static boolean isValid(XdiEntitySingleton.Variable xdiVariable) {

		if (! xdiVariable.getXDIArc().equals(XDILinkContractConstants.XDI_ARC_V_DO)) return false;

		if (getTemplateAuthorityAndId(xdiVariable.getXDIAddress()) == null) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI link contract template bound to a given XDI variable.
	 * @param xdiVariable The XDI variable that is an XDI link contract template.
	 * @return The XDI link contract template.
	 */
	public static LinkContractTemplate fromXdiEntitySingletonVariable(XdiEntitySingleton.Variable xdiVariable) {

		if (! isValid(xdiVariable)) return null;

		return new LinkContractTemplate(xdiVariable);
	}

	public static XDIAddress createLinkContractTemplateXDIAddress(XDIAddress templateAuthorityAndId) {

		if (templateAuthorityAndId == null) throw new NullPointerException();

		if (templateAuthorityAndId.isLiteralNodeXDIAddress()) throw new IllegalArgumentException("Cannot use literal address of template authority and ID " + templateAuthorityAndId);

		List<XDIArc> linkContractTemplateArcXDIAddresses = new ArrayList<XDIArc> ();

		linkContractTemplateArcXDIAddresses.addAll(templateAuthorityAndId.getXDIArcs());
		linkContractTemplateArcXDIAddresses.add(XDILinkContractConstants.XDI_ARC_V_DO);

		return XDIAddress.fromComponents(linkContractTemplateArcXDIAddresses);
	}

	/**
	 * Factory method that finds or creates an XDI link contract template for a graph.
	 * @return The XDI link contract template.
	 */
	public static LinkContractTemplate findLinkContractTemplate(Graph graph, XDIAddress templateAuthorityAndId, boolean create) {

		XDIAddress linkContractTemplateXDIAddress = createLinkContractTemplateXDIAddress(templateAuthorityAndId);

		ContextNode linkContractTemplateContextNode = create ? (ContextNode) graph.setDeepNode(linkContractTemplateXDIAddress) : (ContextNode) graph.getDeepNode(linkContractTemplateXDIAddress, true);
		if (linkContractTemplateContextNode == null) return null;

		return new LinkContractTemplate(XdiEntitySingleton.Variable.fromContextNode(linkContractTemplateContextNode));
	}

	/*
	 * Static methods
	 */

	public static XDIAddress getTemplateAuthorityAndId(XDIAddress XDIaddress) {

		int index = XDIAddressUtil.indexOfXDIArc(XDIaddress, XDILinkContractConstants.XDI_ARC_V_DO);
		if (index < 0) return null;

		return XDIAddressUtil.subXDIAddress(XDIaddress, 0, index);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI variable to which this XDI link contract template is bound.
	 * @return An XDI variable that represents the XDI link contract template.
	 */
	public XdiEntitySingleton.Variable getXdiVariable() {

		return this.xdiEntitySingletonVariable;
	}

	@Override
	public XdiEntitySingleton.Variable getXdiSubGraph() {

		return this.xdiEntitySingletonVariable;
	}

	public GenericLinkContract instantiate() {

		throw new RuntimeException("Not implemented.");
	}

	public XDIAddress getTemplateAuthorityAndId() {

		return getTemplateAuthorityAndId(this.getContextNode().getXDIAddress());
	}
}
