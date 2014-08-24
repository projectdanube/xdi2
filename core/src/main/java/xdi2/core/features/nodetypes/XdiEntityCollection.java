package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI entity collection, represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityCollection extends XdiAbstractCollection<XdiEntityCollection, XdiEntity, XdiEntityCollection, XdiEntityMemberUnordered, XdiEntityMemberOrdered, XdiEntityMember> implements XdiCollection<XdiEntityCollection, XdiEntity, XdiEntityCollection, XdiEntityMemberUnordered, XdiEntityMemberOrdered, XdiEntityMember> {

	private static final long serialVersionUID = -8518618921427437445L;

	protected XdiEntityCollection(ContextNode contextNode) {

		super(contextNode, XdiEntityCollection.class, XdiEntityMemberUnordered.class, XdiEntityMemberOrdered.class, XdiEntityMember.class);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity collection.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity collection.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return 
				isValidXDIArc(contextNode.getXDIArc()) &&
				( ! XdiAttributeCollection.isValid(contextNode.getContextNode()) && ! XdiAbstractAttribute.isValid(contextNode.getContextNode()) );
	}

	/**
	 * Factory method that creates an XDI entity collection bound to a given context node.
	 * @param contextNode The context node that is an XDI entity collection.
	 * @return The XDI entity collection.
	 */
	public static XdiEntityCollection fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityCollection(contextNode);
	}

	/*
	 * Methods for XRIs
	 */

	public static XDIArc createXDIArc(XDIArc arc) {

		return XDIArc.create("" + XDIConstants.XS_CLASS.charAt(0) + arc + XDIConstants.XS_CLASS.charAt(1));
	}

	public static boolean isValidXDIArc(XDIArc arc) {

		if (arc == null) return false;

		if (! arc.isClassXs()) return false;
		if (arc.isAttributeXs()) return false;

		if (XDIConstants.CS_CLASS_UNRESERVED.equals(arc.getCs()) || XDIConstants.CS_CLASS_RESERVED.equals(arc.getCs())) {

			if (! arc.hasLiteral() && ! arc.hasXRef()) return false;
		} else if (XDIConstants.CS_AUTHORITY_PERSONAL.equals(arc.getCs()) || XDIConstants.CS_AUTHORITY_LEGAL.equals(arc.getCs()) || XDIConstants.CS_AUTHORITY_GENERAL.equals(arc.getCs())) {

			if (arc.hasLiteral() || arc.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiEntityCollectionIterator extends NotNullIterator<XdiEntityCollection> {

		public MappingContextNodeXdiEntityCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityCollection> (contextNodes) {

				@Override
				public XdiEntityCollection map(ContextNode contextNode) {

					return XdiEntityCollection.fromContextNode(contextNode);
				}
			});
		}
	}
}
