package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI attribute singleton, represented as a context node.
 * 
 * @author markus
 */
public class XdiAttributeSingleton extends XdiAbstractSingleton<XdiAttribute> implements XdiAttribute {

	private static final long serialVersionUID = -5769813522592588864L;

	protected XdiAttributeSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;
		if (contextNode.getContextNode() != null && XdiValue.isValid(contextNode.getContextNode())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI attribute singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute singleton.
	 * @return The XDI attribute singleton.
	 */
	public static XdiAttributeSingleton fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return contextNode.getXDIArc().isVariable() ? new Variable(contextNode) : new XdiAttributeSingleton(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Creates or returns an XDI value under this XDI attribute element.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI value.
	 */
	@Override
	public XdiValue getXdiValue(boolean create) {

		XDIArc valuearc = XdiValue.createXDIArc();

		ContextNode valueContextNode = create ? this.getContextNode().setContextNode(valuearc) : this.getContextNode().getContextNode(valuearc, false);
		if (valueContextNode == null) return null;

		return new XdiValue(valueContextNode);
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createAttributeSingletonXDIArc(XDIArc XDIarc, boolean variable) {

		StringBuffer buffer = new StringBuffer();
		if (variable) buffer.append(XDIConstants.XS_VARIABLE.charAt(0));
		buffer.append(XDIConstants.XS_ATTRIBUTE.charAt(0));
		buffer.append(XDIarc.toString());
		buffer.append(XDIConstants.XS_ATTRIBUTE.charAt(1));
		if (variable) buffer.append(XDIConstants.XS_VARIABLE.charAt(1));

		return XDIArc.create(buffer.toString());
	}

	public static XDIArc createAttributeSingletonXDIArc(XDIArc XDIarc) {

		return createAttributeSingletonXDIArc(XDIarc, false);
	}

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XDIarc.isCollection()) return false;
		if (! XDIarc.isAttribute()) return false;

		if (XDIConstants.CS_CLASS_UNRESERVED.equals(XDIarc.getCs()) || XDIConstants.CS_CLASS_RESERVED.equals(XDIarc.getCs())) {

			if (! XDIarc.hasLiteral() && ! XDIarc.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Variable class
	 */

	public static class Variable extends XdiAttributeSingleton implements XdiVariable<XdiAttribute> {

		private static final long serialVersionUID = 7411198118077832135L;

		private Variable(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return contextNode.getXDIArc().isVariable() && XdiAttributeSingleton.isValid(contextNode);
		}

		public static Variable fromContextNode(ContextNode contextNode) {

			if (contextNode == null) throw new NullPointerException();

			if (! isValid(contextNode)) return null;

			return new Variable(contextNode);
		}
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeSingletonIterator extends NotNullIterator<XdiAttributeSingleton> {

		public MappingContextNodeXdiAttributeSingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeSingleton> (contextNodes) {

				@Override
				public XdiAttributeSingleton map(ContextNode contextNode) {

					return XdiAttributeSingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
