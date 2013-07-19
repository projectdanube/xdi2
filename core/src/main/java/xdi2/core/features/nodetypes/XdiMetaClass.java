package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI metaclass (context node type).
 * 
 * @author markus
 */
public class XdiMetaClass extends XdiAbstractSubGraph {

	private static final long serialVersionUID = -96625188324691432L;

	protected XdiMetaClass(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI metaclass.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI metaclass.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI metaclass bound to a given context node.
	 * @param contextNode The context node that is an XDI metaclass.
	 * @return The XDI metaclass.
	 */
	public static XdiMetaClass fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiMetaClass(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDIConstants.XS_CLASS.charAt(0) + arcXri + XDIConstants.XS_CLASS.charAt(1));
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (! arcXri.isClassXs()) return false;
		if (arcXri.isAttributeXs()) return false;

		if (XDIConstants.CS_PLUS.equals(arcXri.getCs()) || XDIConstants.CS_DOLLAR.equals(arcXri.getCs())) {

			if (arcXri.hasLiteral() || arcXri.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiMetaClassIterator extends NotNullIterator<XdiMetaClass> {

		public MappingContextNodeXdiMetaClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMetaClass> (contextNodes) {

				@Override
				public XdiMetaClass map(ContextNode contextNode) {

					return XdiMetaClass.fromContextNode(contextNode);
				}
			});
		}
	}
}
