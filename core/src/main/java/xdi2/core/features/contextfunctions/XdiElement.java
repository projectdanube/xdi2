package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiElement extends XdiSubGraph {

	private static final long serialVersionUID = 8283064321616435273L;

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

		return XdiEntityElement.isValid(contextNode) || 
				XdiAttributeElement.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI element bound to a given context node.
	 * @param contextNode The context node that is an XDI element.
	 * @return The XDI element.
	 */
	public static XdiElement fromContextNode(ContextNode contextNode) {

		XdiElement xdiElement;

		if ((xdiElement = XdiEntityElement.fromContextNode(contextNode)) != null) return xdiElement;
		if ((xdiElement = XdiAttributeElement.fromContextNode(contextNode)) != null) return xdiElement;

		return null;
	}

	/*
	 * Instance methods
	 */

	public abstract XdiClass getXdiClass();

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiEntityElement.isValidArcXri(arcXri) || 
				XdiAttributeElement.isValidArcXri(arcXri);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiElementClassIterator extends NotNullIterator<XdiElement> {

		public MappingContextNodeXdiElementClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiElement> (contextNodes) {

				@Override
				public XdiElement map(ContextNode contextNode) {

					return XdiElement.fromContextNode(contextNode);
				}
			});
		}
	}
}
