package xdi2.core.features.contextfunctions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.features.contextfunctions.XdiElement.MappingContextNodeXdiElementIterator;
import xdi2.core.features.ordering.Ordering;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI member (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiMember extends XdiSubGraph {

	private static final long serialVersionUID = -1075885367630005576L;

	protected XdiMember(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI member.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI member.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return isMemberArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI member bound to a given context node.
	 * @param contextNode The context node that is an XDI member.
	 * @return The XDI member.
	 */
	public static XdiMember fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiMember(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Creates a new XDI element and adds it to this XDI member.
	 * @return The newly created XDI element.
	 */
	public XdiElement createXdiElement() {

		ContextNode contextNode = this.getContextNode().createContextNode(XdiElement.createElementArcXri(XRIUtil.randomUuidSubSegment()));

		return new XdiElement(contextNode);
	}

	/**
	 * Returns all XDI elements in this XDI member.
	 * @return An iterator over all XDI elements.
	 */
	public Iterator<XdiElement> elements() {

		return this.elements(false, true);
	}

	/**
	 * Returns all XDI elements in this XDI member.
	 * @return An iterator over all XDI elements.
	 */
	public Iterator<XdiElement> elements(boolean ordered, boolean unordered) {

		// ordered or unordered or both?

		Iterator<ContextNode> contextNodes;

		if (ordered && (! unordered)) {

			contextNodes = Ordering.getOrderedContextNodes(this.getContextNode());
		} else if ((! ordered) && unordered) {

			contextNodes = this.getContextNode().getContextNodes();
		} else if (ordered && unordered) {

			List<Iterator<? extends ContextNode>> iterators = new ArrayList<Iterator<? extends ContextNode>> ();
			iterators.add(Ordering.getOrderedContextNodes(this.getContextNode()));
			iterators.add(this.getContextNode().getContextNodes());

			contextNodes = new NoDuplicatesIterator<ContextNode> (new CompositeIterator<ContextNode> (iterators.iterator()));
		} else {

			return new EmptyIterator<XdiElement> ();
		}

		// look for context nodes that are valid XDI elements

		return new MappingContextNodeXdiElementIterator(contextNodes);
	}

	/**
	 * Returns the number of XDI elements in this XDI member.
	 */
	public int elementsSize() {

		return new IteratorCounter(this.elements()).count();
	}

	/*
	 * Methods for XDI member XRIs
	 */

	public static XDI3SubSegment createMemberArcXri(XDI3SubSegment arcXri) {

		return arcXri;
	}

	/**
	 * Checks if a given XRI is an XDI member XRI.
	 * @param xri An XDI member XRI.
	 * @return True, if the XRI is an XDI member XRI.
	 */
	public static boolean isMemberArcXri(XDI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();
		if (arcXri.hasXRef()) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiMemberIterator extends NotNullIterator<XdiMember> {

		public MappingContextNodeXdiMemberIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiMember> (contextNodes) {

				@Override
				public XdiMember map(ContextNode contextNode) {

					return XdiMember.fromContextNode(contextNode);
				}
			});
		}
	}
}
