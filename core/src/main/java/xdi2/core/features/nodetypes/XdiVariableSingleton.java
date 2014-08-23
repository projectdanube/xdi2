package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI variable singleton, represented as a context node.
 * 
 * @author markus
 */
public final class XdiVariableSingleton extends XdiAbstractSingleton<XdiVariable> implements XdiVariable {

	private static final long serialVersionUID = 3095667439821943614L;

	protected XdiVariableSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI variable singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI variable singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return 
				isValidArcXri(contextNode.getArcXri()) &&
				( ! XdiAttributeCollection.isValid(contextNode.getContextNode()) && ! XdiAbstractAttribute.isValid(contextNode.getContextNode()) );
	}

	/**
	 * Factory method that creates an XDI entity singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI entity singleton.
	 * @return The XDI entity singleton.
	 */
	public static XdiVariableSingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiVariableSingleton(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(XDI3SubSegment arcXri) {

		return arcXri;
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (arcXri.isAttributeXs()) return false;
		if (arcXri.isClassXs()) return false;

		if (! arcXri.hasLiteral() && ! arcXri.hasXRef()) return false;

		if (XDIConstants.CS_CLASS_UNRESERVED.equals(arcXri.getCs()) || XDIConstants.CS_CLASS_RESERVED.equals(arcXri.getCs())) {

		} else if (XDIConstants.CS_AUTHORITY_PERSONAL.equals(arcXri.getCs()) || XDIConstants.CS_AUTHORITY_LEGAL.equals(arcXri.getCs()) || XDIConstants.CS_AUTHORITY_GENERAL.equals(arcXri.getCs())) {

		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntitySingletonIterator extends NotNullIterator<XdiVariableSingleton> {

		public MappingContextNodeXdiEntitySingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiVariableSingleton> (contextNodes) {

				@Override
				public XdiVariableSingleton map(ContextNode contextNode) {

					return XdiVariableSingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
