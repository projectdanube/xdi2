package xdi2.core.features.multiplicity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.features.multiplicity.Multiplicity.MappingContextNodeEntityMemberIterator;
import xdi2.core.features.ordering.Ordering;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.NoDuplicatesIterator;

/**
 * An XDI entity collection (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiEntityCollection extends XdiCollection {

	private static final long serialVersionUID = 1455719520426705802L;

	protected XdiEntityCollection(ContextNode contextNode) {

		super(contextNode);
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

		return Multiplicity.isEntityCollectionArcXri(contextNode.getArcXri());
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
	 * Multiplicity methods
	 */

	/**
	 * Creates a new XDI entity member and adds it to this XDI collection.
	 * @return The newly created XDI entity member.
	 */
	@Override
	public XdiEntityMember createMember() {

		ContextNode contextNode = this.getContextNode().createContextNode(Multiplicity.memberArcXri(XRIUtil.randomUuidSubSegment()));

		return (XdiEntityMember) XdiMember.fromContextNode(contextNode);
	}

	/**
	 * Returns all XDI entity members in this XDI collection.
	 * @return An iterator over all XDI entity members.
	 */
	@Override
	public Iterator<XdiEntityMember> members() {

		return this.members(false, true);
	}

	/**
	 * Returns all XDI entity members in this XDI entity collection.
	 * @return An iterator over all XDI entity members.
	 */
	public Iterator<XdiEntityMember> members(boolean ordered, boolean unordered) {

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

			return new EmptyIterator<XdiEntityMember> ();
		}

		// look for context nodes that are valid XDI entity members

		return new MappingContextNodeEntityMemberIterator(contextNodes);
	}

	/**
	 * Returns the number of XDI entity members in this XDI collection.
	 */
	@Override
	public int membersSize() {

		return new IteratorCounter(this.members()).count();
	}
}
