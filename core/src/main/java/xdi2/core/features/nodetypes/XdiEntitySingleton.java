package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI entity singleton, represented as a context node.
 * 
 * @author markus
 */
public class XdiEntitySingleton extends XdiAbstractSingleton<XdiEntity> implements XdiEntity {

	private static final long serialVersionUID = 7600443284706530972L;

	private XdiEntitySingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;
		if (contextNode.getContextNode() != null && XdiAttributeCollection.isValid(contextNode.getContextNode())) return false;
		if (contextNode.getContextNode() != null && XdiAbstractAttribute.isValid(contextNode.getContextNode())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI entity singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI entity singleton.
	 * @return The XDI entity singleton.
	 */
	public static XdiEntitySingleton fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return contextNode.getXDIArc().isVariable() ? new Variable(contextNode) : new XdiEntitySingleton(contextNode);
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createEntitySingletonXDIArc(XDIArc XDIarc, boolean variable) {

		StringBuffer buffer = new StringBuffer();
		if (variable) buffer.append(XDIConstants.XS_VARIABLE.charAt(0));
		buffer.append(XDIarc.toString());
		if (variable) buffer.append(XDIConstants.XS_VARIABLE.charAt(1));

		return XDIArc.create(buffer.toString());
	}

	public static XDIArc createEntitySingletonXDIArc(XDIArc XDIarc) {

		return createEntitySingletonXDIArc(XDIarc, false);
	}

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XDIarc.isAttribute()) return false;
		if (XDIarc.isCollection()) return false;

		if (! XDIarc.hasLiteral() && ! XDIarc.hasXRef()) return false;

		if (XDIConstants.CS_CLASS_UNRESERVED.equals(XDIarc.getCs()) || XDIConstants.CS_CLASS_RESERVED.equals(XDIarc.getCs())) {

		} else if (XDIConstants.CS_AUTHORITY_PERSONAL.equals(XDIarc.getCs()) || XDIConstants.CS_AUTHORITY_LEGAL.equals(XDIarc.getCs()) || XDIConstants.CS_AUTHORITY_GENERAL.equals(XDIarc.getCs())) {

		} else {

			return false;
		}

		return true;
	}

	/*
	 * Variable class
	 */

	public static class Variable extends XdiEntitySingleton implements XdiVariable<XdiEntity> {

		private static final long serialVersionUID = -8329704361890032696L;

		private Variable(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return contextNode.getXDIArc().isVariable() && XdiEntitySingleton.isValid(contextNode);
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

	public static class MappingContextNodeXdiEntitySingletonIterator extends NotNullIterator<XdiEntitySingleton> {

		public MappingContextNodeXdiEntitySingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntitySingleton> (contextNodes) {

				@Override
				public XdiEntitySingleton map(ContextNode contextNode) {

					return XdiEntitySingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
