package xdi2.core.features.nodetypes;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI variable, represented as a context node.
 * 
 * @author markus
 */
public final class XdiVariableCollection extends XdiAbstractCollection<XdiVariableCollection, XdiVariable, XdiVariableCollection, XdiVariableMemberUnordered, XdiVariableMemberOrdered, XdiVariableMember> implements XdiCollection<XdiVariableCollection, XdiVariable, XdiVariableCollection, XdiVariableMemberUnordered, XdiVariableMemberOrdered, XdiVariableMember> {

	private static final long serialVersionUID = 577420131345150256L;

	protected XdiVariableCollection(ContextNode contextNode) {

		super(contextNode, XdiVariableCollection.class, XdiVariableMemberUnordered.class, XdiVariableMemberOrdered.class, XdiVariableMember.class);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI variable collection.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI variable collection.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return isValidArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI variable collection bound to a given context node.
	 * @param contextNode The context node that is an XDI variable collection.
	 * @return The XDI variable collection.
	 */
	public static XdiVariableCollection fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiVariableCollection(contextNode);
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

		if (XDIConstants.CS_CLASS_UNRESERVED.equals(arcXri.getCs()) || XDIConstants.CS_CLASS_RESERVED.equals(arcXri.getCs())) {

			if (! arcXri.hasLiteral() && ! arcXri.hasXRef()) return false;
		} else if (XDIConstants.CS_AUTHORITY_PERSONAL.equals(arcXri.getCs()) || XDIConstants.CS_AUTHORITY_LEGAL.equals(arcXri.getCs()) || XDIConstants.CS_AUTHORITY_GENERAL.equals(arcXri.getCs())) {

			if (arcXri.hasLiteral() || arcXri.hasXRef()) return false;
		} else {

			return false;
		}

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiVariableCollectionIterator extends NotNullIterator<XdiVariableCollection> {

		public MappingContextNodeXdiVariableCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiVariableCollection> (contextNodes) {

				@Override
				public XdiVariableCollection map(ContextNode contextNode) {

					return XdiVariableCollection.fromContextNode(contextNode);
				}
			});
		}
	}
}
