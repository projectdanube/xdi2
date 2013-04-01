package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI attribute member (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeMember extends XdiAttribute {

	private static final long serialVersionUID = 1027868266675630350L;

	protected XdiAttributeMember(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				isAttributeMemberArcXri(contextNode.getArcXri()) &&
				XdiCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI attribute member bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute member.
	 * @return The XDI attribute member.
	 */
	public static XdiAttributeMember fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeMember(contextNode);
	}

	/*
	 * Methods for XDI attribute member XRIs
	 */

	public static XDI3SubSegment createAttributeMemberArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_ATTRIBUTE_VALUE.charAt(0) + arcXri + XDI3Constants.CF_ATTRIBUTE_VALUE.charAt(1));
	}

	public static boolean isAttributeMemberArcXri(XDI3SubSegment arcXri) {

		if (arcXri.hasCs()) return false;

		if (! arcXri.hasXRef()) return false;
		if (! XDI3Constants.CF_ATTRIBUTE_VALUE.equals(arcXri.getXRef().getCf())) return false;
		if (! arcXri.getXRef().hasSegment()) return false;
		if (! XDI3Constants.CS_BANG.equals(arcXri.getXRef().getSegment().getFirstSubSegment().getCs()) && ! XDI3Constants.CS_STAR.equals(arcXri.getXRef().getSegment().getFirstSubSegment().getCs())) return false;

		return true;
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI collection of this XDI attribute member.
	 * @return The parent XDI collection.
	 */
	public XdiCollection getParentCollection() {

		return new XdiCollection(this.getContextNode().getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeMemberIterator extends NotNullIterator<XdiAttributeMember> {

		public MappingContextNodeXdiAttributeMemberIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeMember> (contextNodes) {

				@Override
				public XdiAttributeMember map(ContextNode contextNode) {

					return XdiAttributeMember.fromContextNode(contextNode);
				}
			});
		}
	}
}
