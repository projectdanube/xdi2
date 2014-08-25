package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI value, represented as a context node.
 * 
 * @author markus
 */
public final class XdiValue extends XdiAbstractSubGraph<XdiValue> {

	private static final long serialVersionUID = 3710989824639753381L;

	protected XdiValue(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI value.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI value.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;
		if (! XdiAbstractAttribute.isValid(contextNode.getContextNode())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI value bound to a given context node.
	 * @param contextNode The context node that is an XDI value.
	 * @return The XDI value.
	 */
	public static XdiValue fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return new XdiValue(contextNode);
	}

	/*
	 * Instance methods
	 */

	public Literal setLiteral(Object literalData) {

		return this.getContextNode().setLiteral(literalData);
	}

	public Literal setLiteralString(String literalData) {

		return this.getContextNode().setLiteralString(literalData);
	}

	public Literal setLiteralNumber(Double literalData) {

		return this.getContextNode().setLiteralNumber(literalData);
	}

	public Literal setLiteralBoolean(Boolean literalData) {

		return this.getContextNode().setLiteralBoolean(literalData);
	}	
	public Literal getLiteral() {

		return this.getContextNode().getLiteral();
	}

	public Literal getLiteral(Object literalData) {

		return this.getContextNode().getLiteral(literalData);
	}

	public Literal getLiteralString(String literalData) {

		return this.getContextNode().getLiteralString(literalData);
	}

	public Literal getLiteralNumber(Double literalData) {

		return this.getContextNode().getLiteralNumber(literalData);
	}

	public Literal getLiteralBoolean(Boolean literalData) {

		return this.getContextNode().getLiteralBoolean(literalData);
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc() {

		return XDIConstants.XDI_ARC_LITERAL;
	}

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) return false;

		if (XDIarc.isClassXs()) return false;
		if (XDIarc.isAttributeXs()) return false;
		if (XDIarc.hasLiteral()) return false;
		if (XDIarc.hasXRef()) return false;

		if (! XDIConstants.CS_VALUE.equals(XDIarc.getCs())) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiValueIterator extends NotNullIterator<XdiValue> {

		public MappingContextNodeXdiValueIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiValue> (contextNodes) {

				@Override
				public XdiValue map(ContextNode contextNode) {

					return XdiValue.fromContextNode(contextNode);
				}
			});
		}
	}
}
