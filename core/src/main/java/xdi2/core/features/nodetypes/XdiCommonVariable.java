package xdi2.core.features.nodetypes;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;

/**
 * An XDI common variable, represented as a context node.
 * 
 * @author markus
 */
public class XdiCommonVariable extends XdiAbstractContext<XdiCommonVariable> implements XdiVariable<XdiCommonVariable> {

	private static final long serialVersionUID = 4872125371237353090L;

	protected XdiCommonVariable(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI common variable.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI common variable.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		return XDIConstants.XDI_ADD_COMMON_VARIABLE.equals(contextNode.getXDIArc());
	}

	/**
	 * Factory method that creates an XDI common variable bound to a given context node.
	 * @param contextNode The context node that is an XDI common variable.
	 * @return The XDI common variable.
	 */
	public static XdiCommonVariable fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return new XdiCommonVariable(contextNode);
	}

	public static XdiCommonVariable fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}
}
