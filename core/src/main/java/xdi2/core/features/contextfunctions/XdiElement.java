package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI element (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiElement extends XdiSubGraph {

	private static final long serialVersionUID = -1075885367630005576L;

	protected XdiElement(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI element.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI element.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isElementArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI element bound to a given context node.
	 * @param contextNode The context node that is an XDI element.
	 * @return The XDI element.
	 */
	public static XdiElement fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiElement(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI member of this XDI element.
	 * @return The parent XDI member.
	 */
	public XdiMember getXdiMember() {

		return new XdiMember(this.getContextNode().getContextNode());
	}

	/*
	 * Methods for XDI element XRIs
	 */

	public static XDI3SubSegment createElementArcXri(XDI3Segment xri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_ELEMENT.charAt(0) + xri + XDI3Constants.CF_ELEMENT.charAt(1));
	}

	public static XDI3SubSegment createElementArcXri(XDI3SubSegment xri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_ELEMENT.charAt(0) + xri + XDI3Constants.CF_ELEMENT.charAt(1));
	}

	public static XDI3SubSegment createElementArcXri(String identifier) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_ELEMENT.charAt(0) + identifier + XDI3Constants.CF_ELEMENT.charAt(1));
	}

	/**
	 * Checks if a given XRI is an XDI element XRI.
	 * @param arcXri An XDI element XRI.
	 * @return True, if the XRI is an XDI element XRI.
	 */
	public static boolean isElementArcXri(XDI3SubSegment arcXri) {

		if (arcXri.hasCs()) return false;

		if (! arcXri.hasXRef()) return false;
		if (! XDI3Constants.CF_ELEMENT.equals(arcXri.getXRef().getCf())) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeElementIterator extends NotNullIterator<XdiElement> {

		public MappingContextNodeElementIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiElement> (contextNodes) {

				@Override
				public XdiElement map(ContextNode contextNode) {

					return XdiElement.fromContextNode(contextNode);
				}
			});
		}
	}
}
