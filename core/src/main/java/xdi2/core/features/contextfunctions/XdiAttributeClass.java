package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.CastingIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI attribute class (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeClass extends XdiAbstractClass {

	private static final long serialVersionUID = -8518618921427437445L;

	protected XdiAttributeClass(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute class.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute class.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI attribute class bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute class.
	 * @return The XDI attribute class.
	 */
	public static XdiAttributeClass fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeClass(contextNode);
	}

	/*
	 * Instance methods
	 */

	@Override
	public XdiAttributeInstance getXdiInstance(XDI3SubSegment arcXri, boolean create) {

		return (XdiAttributeInstance) super.getXdiInstance(arcXri, create);
	}

	@Override
	public XdiAttributeInstance getXdiInstance() {

		return (XdiAttributeInstance) super.getXdiInstance();
	}

	@Override
	public Iterator<XdiAttributeInstance> instances() {

		return new CastingIterator<XdiAbstractInstance, XdiAttributeInstance> (super.instances());
	}

	@Override
	public XdiAttributeElement getXdiElement(int index, boolean create) {

		return (XdiAttributeElement) super.getXdiElement(index, create);
	}

	@Override
	public Iterator<XdiAttributeElement> elements() {

		return new CastingIterator<XdiAbstractInstance, XdiAttributeElement> (super.instances());
	}

	@Override
	public Iterator<XdiAttribute> instancesAndElements() {

		return new CastingIterator<XdiSubGraph, XdiAttribute> (super.instances());
	}

	/*
	 * Methods for XRIs
	 */

	public static XDI3SubSegment createArcXri(XDI3SubSegment arcXri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_ATTRIBUTE_CLASS.charAt(0) + arcXri + XDI3Constants.CF_ATTRIBUTE_CLASS.charAt(1));
	}

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) return false;

		if (arcXri.hasCs()) return false;

		if (! arcXri.hasXRef()) return false;
		if (! XDI3Constants.CF_ATTRIBUTE_CLASS.equals(arcXri.getXRef().getCf())) return false;

		if (! arcXri.getXRef().hasSegment()) return false;

		if (XDI3Constants.CS_DOLLAR.equals(arcXri.getXRef().getSegment().getFirstSubSegment().getCs())) {

		} else if (XDI3Constants.CS_PLUS.equals(arcXri.getXRef().getSegment().getFirstSubSegment().getCs())) {

		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeClassIterator extends NotNullIterator<XdiAttributeClass> {

		public MappingContextNodeXdiAttributeClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeClass> (contextNodes) {

				@Override
				public XdiAttributeClass map(ContextNode contextNode) {

					return XdiAttributeClass.fromContextNode(contextNode);
				}
			});
		}
	}
}
