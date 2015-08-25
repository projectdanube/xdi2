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
 * An XDI unordered attribute instance, represented as a context node.
 * 
 * @author markus
 */
public class XdiAttributeInstanceUnordered extends XdiAbstractAttribute implements XdiInstanceUnordered<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance>, XdiAttributeInstance {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiAttributeInstanceUnordered(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI unordered attribute instance.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI unordered attribute instance.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI unordered attribute instance bound to a given context node.
	 * @param contextNode The context node that is an XDI unordered attribute instance.
	 * @return The XDI unordered attribute instance.
	 */
	public static XdiAttributeInstanceUnordered fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		if (contextNode.getXDIArc().isDefinition() && contextNode.getXDIArc().isVariable()) return new Definition.Variable(contextNode);
		if (contextNode.getXDIArc().isDefinition() && ! contextNode.getXDIArc().isVariable()) return new Definition(contextNode);
		if (! contextNode.getXDIArc().isDefinition() && contextNode.getXDIArc().isVariable()) return new Variable(contextNode);
		return new XdiAttributeInstanceUnordered(contextNode);
	}

	public static XdiAttributeInstanceUnordered fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(boolean immutable, boolean relative, String literal, XDIXRef xref) {

		return XdiAbstractInstanceUnordered.createXDIArc(true, immutable, relative, literal, xref);
	}

	public static XDIArc createXDIArc(boolean immutable, boolean relative, String literal) {

		return createXDIArc(immutable, relative, literal, null);
	}

	public static XDIArc createXDIArc(String literal) {

		return createXDIArc(true, false, literal);
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

		if (! XdiAbstractInstanceUnordered.isValidXDIArc(XDIarc, true)) return false;

		return true;
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the parent XDI collection of this XDI unordered attribute instance.
	 * @return The parent XDI collection.
	 */
	@Override
	public XdiAttributeCollection getXdiCollection() {

		return XdiAttributeCollection.fromContextNode(this.getContextNode().getContextNode());
	}

	/*
	 * Definition and Variable classes
	 */

	public static class Definition extends XdiAttributeInstanceUnordered implements XdiDefinition<XdiAttribute> {

		private static final long serialVersionUID = 4402788017621704585L;

		private Definition(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return XdiAttributeInstanceUnordered.isValid(contextNode) &&
					contextNode.getXDIArc().isDefinition() &&
					! contextNode.getXDIArc().isVariable();
		}

		public static Definition fromContextNode(ContextNode contextNode) {

			if (contextNode == null) throw new NullPointerException();

			if (! isValid(contextNode)) return null;

			return new Definition(contextNode);
		}

		public static class Variable extends Definition implements XdiVariable<XdiAttribute> {

			private static final long serialVersionUID = -1780709783037852933L;

			private Variable(ContextNode contextNode) {

				super(contextNode);
			}

			public static boolean isValid(ContextNode contextNode) {

				return XdiAttributeInstanceUnordered.isValid(contextNode) &&
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

	public static class Variable extends XdiAttributeInstanceUnordered implements XdiVariable<XdiAttribute> {

		private static final long serialVersionUID = 4215503983322026456L;

		private Variable(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return XdiAttributeInstanceUnordered.isValid(contextNode) &&
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

	public static class MappingContextNodeXdiAttributeInstanceUnorderedIterator extends NotNullIterator<XdiAttributeInstanceUnordered> {

		public MappingContextNodeXdiAttributeInstanceUnorderedIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeInstanceUnordered> (contextNodes) {

				@Override
				public XdiAttributeInstanceUnordered map(ContextNode contextNode) {

					return XdiAttributeInstanceUnordered.fromContextNode(contextNode);
				}
			});
		}
	}
}
