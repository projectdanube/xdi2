package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI metaclass, represented as a context node.
 * 
 * @author markus
 */
public class XdiMetaClass extends XdiAbstractSubGraph<XdiMetaClass> {

	private static final long serialVersionUID = -96625188324691432L;

	protected XdiMetaClass(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI metaclass.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI metaclass.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI metaclass bound to a given context node.
	 * @param contextNode The context node that is an XDI metaclass.
	 * @return The XDI metaclass.
	 */
	public static XdiMetaClass fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return new XdiMetaClass(contextNode);
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createMetaClassXDIArc(XDIArc XDIarc) {

		return XDIArc.create("" + XDIConstants.XS_CLASS.charAt(0) + XDIarc + XDIConstants.XS_CLASS.charAt(1));
	}

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (! XDIarc.isClassXs()) return false;
		if (XDIarc.isAttributeXs()) return false;

		if (XDIConstants.CS_CLASS_UNRESERVED.equals(XDIarc.getCs()) || XDIConstants.CS_CLASS_RESERVED.equals(XDIarc.getCs())) {

			if (XDIarc.hasLiteral() || XDIarc.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiMetaClassIterator extends NotNullIterator<XdiMetaClass> {

		public MappingContextNodeXdiMetaClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMetaClass> (contextNodes) {

				@Override
				public XdiMetaClass map(ContextNode contextNode) {

					return XdiMetaClass.fromContextNode(contextNode);
				}
			});
		}
	}
}
