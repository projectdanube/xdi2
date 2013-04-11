package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI attribute singleton (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeSingleton extends XdiAbstractSingleton implements XdiAttribute {

	private static final long serialVersionUID = -5769813522592588864L;

	protected XdiAttributeSingleton(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute singleton.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute singleton.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI attribute singleton bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute singleton.
	 * @return The XDI attribute singleton.
	 */
	public static XdiAttributeSingleton fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeSingleton(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Creates or returns an XDI value under this XDI attribute element.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI value.
	 */
	@Override
	public XdiValue getXdiValue(boolean create) {

		XDI3SubSegment valueArcXri = XdiValue.createArcXri();

		ContextNode valueContextNode = this.getContextNode().getContextNode(valueArcXri);
		if (valueContextNode == null && create) valueContextNode = this.getContextNode().createContextNode(valueArcXri);
		if (valueContextNode == null) return null;

		return new XdiValue(valueContextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(XDI3SubSegment arcXri) {

		StringBuilder buffer = new StringBuilder();

		if (arcXri.hasCs()) buffer.append(arcXri.getCs());
		buffer.append(XDI3Constants.C_SINGLETON);
		buffer.append(XDI3Constants.C_ATTRIBUTE);
		if (arcXri.hasLiteral()) buffer.append(arcXri.getLiteral());
		if (arcXri.hasXRef()) buffer.append(arcXri.getXRef());

		return XDI3SubSegment.create(buffer.toString());
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (! arcXri.isSingleton()) return false;
		if (! arcXri.isAttribute()) return false;

		if (XDI3Constants.CS_PLUS.equals(arcXri.getCs()) || XDI3Constants.CS_DOLLAR.equals(arcXri.getCs())) {

			if (! arcXri.hasLiteral() && ! arcXri.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeSingletonIterator extends NotNullIterator<XdiAttributeSingleton> {

		public MappingContextNodeXdiAttributeSingletonIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeSingleton> (contextNodes) {

				@Override
				public XdiAttributeSingleton map(ContextNode contextNode) {

					return XdiAttributeSingleton.fromContextNode(contextNode);
				}
			});
		}
	}
}
