package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI ordered attribute instance, represented as a context node.
 * 
 * @author markus
 */
public class XdiAttributeInstanceOrdered extends XdiAbstractAttribute implements XdiInstanceOrdered<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance>, XdiAttributeInstance {

	private static final long serialVersionUID = 3562576098019686485L;

	protected XdiAttributeInstanceOrdered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI ordered attribute instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI ordered attribute instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI ordered attribute instance bound to a given context node.
	 * @param contextNode The context node that is an XDI ordered attribute instance.
	 * @return The XDI ordered attribute instance.
	 */
	public static XdiAttributeInstanceOrdered fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		if (contextNode.getXDIArc().isDefinition() && contextNode.getXDIArc().isVariable()) return new Definition.Variable(contextNode);
		if (contextNode.getXDIArc().isDefinition() && ! contextNode.getXDIArc().isVariable()) return new Definition(contextNode);
		if (! contextNode.getXDIArc().isDefinition() && contextNode.getXDIArc().isVariable()) return new Variable(contextNode);
		return new XdiAttributeInstanceOrdered(contextNode);
	}

	public static XdiAttributeInstanceOrdered fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(boolean immutable, boolean relative, String literal, XDIXRef xref) {

		return XdiAbstractInstanceOrdered.createXDIArc(true, immutable, relative, literal, xref);
	}

	public static XDIArc createXDIArc(boolean immutable, boolean relative, String literal) {

		return createXDIArc(immutable, relative, literal, null);
	}

	public static XDIArc createXDIArc(String literal) {

		return createXDIArc(false, false, literal);
	}

	public static XDIArc createXDIArc() {

		return createXDIArc((String) null);
	}

	public static XDIArc createXDIArc(XDIArc XDIarc) {

		return createXDIArc(
				XDIarc.isImmutable(), 
				XDIarc.isRelative(), 
				XDIarc.getLiteral(), 
				XDIarc.getXRef());
	}

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (! XdiAbstractInstanceOrdered.isValidXDIArc(XDIarc, true)) return false;

		return true;
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the parent XDI collection of this XDI ordered attribute instance.
	 * @return The parent XDI collection.
	 */
	@Override
	public XdiAttributeCollection getXdiCollection() {

		return XdiAttributeCollection.fromContextNode(this.getContextNode().getContextNode());
	}

	/*
	 * Definition and Variable classes
	 */

	public static class Definition extends XdiAttributeInstanceOrdered implements XdiDefinition<XdiAttribute> {

		private static final long serialVersionUID = 9076766650130455708L;

		private Definition(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return XdiAttributeInstanceOrdered.isValid(contextNode) &&
					contextNode.getXDIArc().isDefinition() &&
					! contextNode.getXDIArc().isVariable();
		}

		public static Definition fromContextNode(ContextNode contextNode) {

			if (contextNode == null) throw new NullPointerException();

			if (! isValid(contextNode)) return null;

			return new Definition(contextNode);
		}

		public static class Variable extends Definition implements XdiVariable<XdiAttribute> {

			private static final long serialVersionUID = 8232065600537713537L;

			private Variable(ContextNode contextNode) {

				super(contextNode);
			}

			public static boolean isValid(ContextNode contextNode) {

				return XdiAttributeInstanceOrdered.isValid(contextNode) &&
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

	public static class Variable extends XdiAttributeInstanceOrdered implements XdiVariable<XdiAttribute> {

		private static final long serialVersionUID = -3150796131750366380L;

		private Variable(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return XdiAttributeInstanceOrdered.isValid(contextNode) &&
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

	public static class MappingContextNodeXdiAttributeInstanceOrderedIterator extends NotNullIterator<XdiAttributeInstanceOrdered> {

		public MappingContextNodeXdiAttributeInstanceOrderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeInstanceOrdered> (contextNodes) {

				@Override
				public XdiAttributeInstanceOrdered map(ContextNode contextNode) {

					return XdiAttributeInstanceOrdered.fromContextNode(contextNode);
				}
			});
		}
	}
}
