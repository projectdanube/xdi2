package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI attribute singleton, represented as a context node.
 * 
 * @author markus
 */
public class XdiAttributeSingleton extends XdiAbstractAttribute implements XdiSingleton<XdiAttribute>, XdiAttribute {

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

		if (contextNode.getXDIArc().isDefinition() && contextNode.getXDIArc().isVariable()) return new Definition.Variable(contextNode);
		if (contextNode.getXDIArc().isDefinition() && ! contextNode.getXDIArc().isVariable()) return new Definition(contextNode);
		if (! contextNode.getXDIArc().isDefinition() && contextNode.getXDIArc().isVariable()) return new Variable(contextNode);
		return new XdiAttributeSingleton(contextNode);
	}

	public static XdiAttributeSingleton fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Instance methods
	 */

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(Character cs, boolean immutable, boolean relative, String literal, XDIXRef xref) {

		return XDIArc.fromComponents(
				cs, 
				false, 
				false, 
				false, 
				true, 
				immutable, 
				relative, 
				literal, 
				xref);
	}

	public static XDIArc createXDIArc(XDIArc XDIarc) {

		return createXDIArc(
				XDIarc.getCs(), 
				XDIarc.isImmutable(), 
				XDIarc.isRelative(), 
				XDIarc.getLiteral(), 
				XDIarc.getXRef());
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
	 * Definition and Variable classes
	 */

	public static class Definition extends XdiAttributeSingleton implements XdiDefinition<XdiAttribute> {

		private static final long serialVersionUID = 7411198118077832135L;

		private Definition(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return XdiAttributeSingleton.isValid(contextNode) &&
					contextNode.getXDIArc().isDefinition() &&
					! contextNode.getXDIArc().isVariable();
		}

		public static Definition fromContextNode(ContextNode contextNode) {

			if (contextNode == null) throw new NullPointerException();

			if (! isValid(contextNode)) return null;

			return new Definition(contextNode);
		}

		public static class Variable extends Definition implements XdiVariable<XdiAttribute> {

			private static final long serialVersionUID = 8934101084784717952L;

			private Variable(ContextNode contextNode) {

				super(contextNode);
			}

			public static boolean isValid(ContextNode contextNode) {

				return XdiAttributeSingleton.isValid(contextNode) &&
						contextNode.getXDIArc().isDefinition() &&
						contextNode.getXDIArc().isVariable();
			}

			public static Variable fromContextNode(ContextNode contextNode) {

				if (contextNode == null) throw new NullPointerException();

				if (! isValid(contextNode)) return null;

				return new Variable(contextNode);
			}
		}
	}

	public static class Variable extends XdiAttributeSingleton implements XdiVariable<XdiAttribute> {

		private static final long serialVersionUID = -5508680702470597923L;

		private Variable(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return XdiAttributeSingleton.isValid(contextNode) &&
					! contextNode.getXDIArc().isDefinition() &&
					contextNode.getXDIArc().isVariable();
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
