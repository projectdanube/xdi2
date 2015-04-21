package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.LiteralNode;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

public abstract class XdiAbstractAttribute extends XdiAbstractSubGraph<XdiAttribute> implements XdiAttribute {

	private static final long serialVersionUID = 7648046902369626744L;

	protected XdiAbstractAttribute(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (XdiAttributeSingleton.isValid(contextNode)) return true; 
		if (XdiAttributeMemberUnordered.isValid(contextNode)) return true;
		if (XdiAttributeMemberOrdered.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI attribute bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute.
	 * @return The XDI attribute.
	 */
	public static XdiAttribute fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiAttribute xdiAttribute = null;

		if ((xdiAttribute = XdiAttributeSingleton.fromContextNode(contextNode)) != null) return xdiAttribute;
		if ((xdiAttribute = XdiAttributeMemberUnordered.fromContextNode(contextNode)) != null) return xdiAttribute;
		if ((xdiAttribute = XdiAttributeMemberOrdered.fromContextNode(contextNode)) != null) return xdiAttribute;

		return null;
	}

	public static XdiAttribute fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Instance methods
	 */

	@Override
	public LiteralNode setLiteralNode(Object literalData) {

		return this.getContextNode().setLiteralNode(literalData);
	}

	@Override
	public LiteralNode setLiteralString(String literalData) {

		return this.getContextNode().setLiteralString(literalData);
	}

	@Override
	public LiteralNode setLiteralNumber(Double literalData) {

		return this.getContextNode().setLiteralNumber(literalData);
	}

	@Override
	public LiteralNode setLiteralBoolean(Boolean literalData) {

		return this.getContextNode().setLiteralBoolean(literalData);
	}	

	@Override
	public LiteralNode getLiteralNode() {

		return this.getContextNode().getLiteralNode();
	}

	@Override
	public LiteralNode getLiteralNode(Object literalData) {

		return this.getContextNode().getLiteralNode(literalData);
	}

	@Override
	public LiteralNode getLiteralString(String literalData) {

		return this.getContextNode().getLiteralString(literalData);
	}

	@Override
	public LiteralNode getLiteralNumber(Double literalData) {

		return this.getContextNode().getLiteralNumber(literalData);
	}

	@Override
	public LiteralNode getLiteralBoolean(Boolean literalData) {

		return this.getContextNode().getLiteralBoolean(literalData);
	}

	/*
	 * Methods for arcs
	 */

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiAttributeSingleton.isValidXDIArc(XDIarc)) return true; 
		if (XdiAttributeMemberUnordered.isValidXDIArc(XDIarc)) return true;
		if (XdiAttributeMemberOrdered.isValidXDIArc(XDIarc)) return true;

		return false;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeIterator extends NotNullIterator<XdiAttribute> {

		public MappingContextNodeXdiAttributeIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttribute> (contextNodes) {

				@Override
				public XdiAttribute map(ContextNode contextNode) {

					return XdiAbstractAttribute.fromContextNode(contextNode);
				}
			});
		}
	}
}
