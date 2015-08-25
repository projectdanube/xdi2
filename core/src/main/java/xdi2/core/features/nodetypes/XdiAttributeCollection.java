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
 * An XDI attribute collection, represented as a context node.
 * 
 * @author markus
 */
public class XdiAttributeCollection extends XdiAbstractCollection<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance> implements XdiCollection<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeInstanceUnordered, XdiAttributeInstanceOrdered, XdiAttributeInstance> {

	private static final long serialVersionUID = -8518618921427437445L;

	protected XdiAttributeCollection(ContextNode contextNode) {

		super(contextNode, XdiAttributeCollection.class, XdiAttributeInstanceUnordered.class, XdiAttributeInstanceOrdered.class, XdiAttributeInstance.class);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute collection.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute collection.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI attribute collection bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute collection.
	 * @return The XDI attribute collection.
	 */
	public static XdiAttributeCollection fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		if (contextNode.getXDIArc().isDefinition() && contextNode.getXDIArc().isVariable()) return new Definition.Variable(contextNode);
		if (contextNode.getXDIArc().isDefinition() && ! contextNode.getXDIArc().isVariable()) return new Definition(contextNode);
		if (! contextNode.getXDIArc().isDefinition() && contextNode.getXDIArc().isVariable()) return new Variable(contextNode);
		return new XdiAttributeCollection(contextNode);
	}

	public static XdiAttributeCollection fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Instance methods
	 */

	@Override
	public XdiAttributeInstanceUnordered setXdiInstanceUnordered() {

		return super.setXdiInstanceUnordered(true);
	}

	@Override
	public XdiAttributeInstanceUnordered setXdiInstanceUnordered(boolean immutable, boolean relative) {

		return super.setXdiInstanceUnordered(true, immutable, relative);
	}

	@Override
	public XdiAttributeInstanceUnordered setXdiInstanceUnordered(boolean immutable, boolean relative, String literal) {

		return super.setXdiInstanceUnordered(true, immutable, relative, literal);
	}

	@Override
	public XdiAttributeInstanceUnordered getXdiInstanceUnordered(boolean immutable, boolean relative, String literal) {

		return super.getXdiInstanceUnordered(true, immutable, relative, literal);
	}

	@Override
	public XdiAttributeInstanceOrdered setXdiInstanceOrdered() {

		return super.setXdiInstanceOrdered(true);
	}

	@Override
	public XdiAttributeInstanceOrdered setXdiInstanceOrdered(boolean immutable, boolean relative) {

		return super.setXdiInstanceOrdered(true, immutable, relative);
	}

	@Override
	public XdiAttributeInstanceOrdered setXdiInstanceOrdered(boolean immutable, boolean relative, long index) {

		return super.setXdiInstanceOrdered(true, immutable, relative, index);
	}

	@Override
	public XdiAttributeInstanceOrdered getXdiInstanceOrdered(boolean immutable, boolean relative, long index) {

		return super.getXdiInstanceOrdered(true, immutable, relative, index);
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(Character cs, boolean immutable, boolean relative, String literal, XDIXRef xref) {

		return XdiAbstractCollection.createXDIArc(cs, true, immutable, relative, literal, xref);
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

		if (! XDIarc.isCollection()) return false;
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

	public static class Definition extends XdiAttributeCollection implements XdiDefinition<XdiAttributeCollection> {

		private static final long serialVersionUID = 6740262141309967040L;

		private Definition(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return XdiAttributeCollection.isValid(contextNode) &&
					contextNode.getXDIArc().isDefinition() &&
					! contextNode.getXDIArc().isVariable();
		}

		public static Definition fromContextNode(ContextNode contextNode) {

			if (contextNode == null) throw new NullPointerException();

			if (! isValid(contextNode)) return null;

			return new Definition(contextNode);
		}

		public static class Variable extends Definition implements XdiVariable<XdiAttributeCollection> {

			private static final long serialVersionUID = 1599526969113350097L;

			private Variable(ContextNode contextNode) {

				super(contextNode);
			}

			public static boolean isValid(ContextNode contextNode) {

				return XdiAttributeCollection.isValid(contextNode) &&
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

	public static class Variable extends XdiAttributeCollection implements XdiVariable<XdiAttributeCollection> {

		private static final long serialVersionUID = 2330513109724196648L;

		private Variable(ContextNode contextNode) {

			super(contextNode);
		}

		public static boolean isValid(ContextNode contextNode) {

			return XdiAttributeCollection.isValid(contextNode) &&
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

	public static class MappingContextNodeXdiAttributeCollectionIterator extends NotNullIterator<XdiAttributeCollection> {

		public MappingContextNodeXdiAttributeCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeCollection> (contextNodes) {

				@Override
				public XdiAttributeCollection map(ContextNode contextNode) {

					return XdiAttributeCollection.fromContextNode(contextNode);
				}
			});
		}
	}
}
