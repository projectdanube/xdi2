package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;

/**
 * An XDI attribute collection, represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeCollection extends XdiAbstractCollection<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeMemberUnordered, XdiAttributeMemberOrdered, XdiAttributeMember> implements XdiCollection<XdiAttributeCollection, XdiAttribute, XdiAttributeCollection, XdiAttributeMemberUnordered, XdiAttributeMemberOrdered, XdiAttributeMember> {

	private static final long serialVersionUID = -8518618921427437445L;

	protected XdiAttributeCollection(ContextNode contextNode) {

		super(contextNode, XdiAttributeCollection.class, XdiAttributeMemberUnordered.class, XdiAttributeMemberOrdered.class, XdiAttributeMember.class);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI attribute collection.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI attribute collection.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (contextNode.getXDIArc() == null || ! isValidXDIArc(contextNode.getXDIArc())) return false;
		if (contextNode.getContextNode() != null && XdiValue.isValid(contextNode.getContextNode())) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI attribute collection bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute collection.
	 * @return The XDI attribute collection.
	 */
	public static XdiAttributeCollection fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		if (! isValid(contextNode)) return null;

		return new XdiAttributeCollection(contextNode);
	}

	/*
	 * Methods for arcs
	 */

	public static XDIArc createAttributeCollectionXDIArc(XDIArc XDIarc) {

		return XDIArc.create("" + XDIConstants.XS_CLASS.charAt(0) + XDIConstants.XS_ATTRIBUTE.charAt(0) + XDIarc + XDIConstants.XS_ATTRIBUTE.charAt(1) + XDIConstants.XS_CLASS.charAt(1));
	}

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (! XDIarc.isClassXs()) return false;
		if (! XDIarc.isAttributeXs()) return false;

		if (XDIConstants.CS_CLASS_UNRESERVED.equals(XDIarc.getCs()) || XDIConstants.CS_CLASS_RESERVED.equals(XDIarc.getCs())) {

			if (! XDIarc.hasLiteral() && ! XDIarc.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiAttributeCollectionIterator extends NotNullIterator<XdiAttributeCollection> {

		public MappingContextNodeXdiAttributeCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAttributeCollection> (contextNodes) {

				@Override
				public XdiAttributeCollection map(ContextNode contextNode) {

					return XdiAttributeCollection.fromContextNode(contextNode);
				}
			});
		}
	}
}
