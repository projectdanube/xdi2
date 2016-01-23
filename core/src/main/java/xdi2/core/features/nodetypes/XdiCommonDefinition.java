package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * An XDI common definition, represented as a context node.
 * 
 * @author markus
 */
public class XdiCommonDefinition extends XdiAbstractContext<XdiCommonDefinition> implements XdiDefinition<XdiCommonDefinition> {

	private static final long serialVersionUID = 4872125371237353090L;

	protected XdiCommonDefinition(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI common definition.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI common definition.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		return XDIConstants.XDI_ADD_COMMON_DEFINITION.equals(contextNode.getXDIArc());
	}

	/**
	 * Factory method that creates an XDI common definition bound to a given context node.
	 * @param contextNode The context node that is an XDI common definition.
	 * @return The XDI common definition.
	 */
	public static XdiCommonDefinition fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return new XdiCommonDefinition(contextNode);
	}

	public static XdiCommonDefinition fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}
}
