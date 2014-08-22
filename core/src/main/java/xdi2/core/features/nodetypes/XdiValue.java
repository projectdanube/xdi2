package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI value (context function), represented as a context node.
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

		if (contextNode == null) return false;

		return isValidarc(contextNode.getArc()) &&
				XdiAbstractAttribute.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI value bound to a given context node.
	 * @param contextNode The context node that is an XDI value.
	 * @return The XDI value.
	 */
	public static XdiValue fromContextNode(ContextNode contextNode) {

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
	 * Methods for XRIs
	 */

	public static XDIArc createarc() {

		return XDIConstants.XDI_ARC_LITERAL;
	}

	public static boolean isValidarc(XDIArc arc) {

		if (arc == null) return false;

		if (arc.isClassXs()) return false;
		if (arc.isAttributeXs()) return false;
		if (arc.hasLiteral()) return false;
		if (arc.hasXRef()) return false;

		if (! XDIConstants.CS_VALUE.equals(arc.getCs())) return false;

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
