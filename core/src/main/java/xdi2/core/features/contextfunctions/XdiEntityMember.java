package xdi2.core.features.contextfunctions;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI entity member (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityMember extends XdiEntity {

	private static final long serialVersionUID = -1075885367630005576L;

	protected XdiEntityMember(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI entity member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI entity member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return
				isEntityMemberArcXri(contextNode.getArcXri()) &&
				XdiCollection.isValid(contextNode.getContextNode());
	}

	/**
	 * Factory method that creates an XDI entity member bound to a given context node.
	 * @param contextNode The context node that is an XDI entity member.
	 * @return The XDI entity member.
	 */
	public static XdiEntityMember fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiEntityMember(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Gets or returns the parent XDI collection of this XDI entity member.
	 * @return The parent XDI collection.
	 */
	public XdiCollection getParentXdiCollection() {

		return new XdiCollection(this.getContextNode().getContextNode());
	}

	/*
	 * Methods for XDI entity member XRIs
	 */

	public static XDI3SubSegment createEntityMemberArcXri(XDI3SubSegment arcXri) {

		return arcXri;
	}

	public static boolean isEntityMemberArcXri(XDI3SubSegment arcXri) {

		if (arcXri.hasXRef()) return false;

		if (! XDI3Constants.CS_BANG.equals(arcXri.getCs()) && ! XDI3Constants.CS_STAR.equals(arcXri.getCs())) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeEntityMemberIterator extends NotNullIterator<XdiEntityMember> {

		public MappingContextNodeEntityMemberIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiEntityMember> (contextNodes) {

				@Override
				public XdiEntityMember map(ContextNode contextNode) {

					return XdiEntityMember.fromContextNode(contextNode);
				}
			});
		}
	}
}
