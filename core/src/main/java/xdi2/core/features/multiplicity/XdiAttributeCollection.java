package xdi2.core.features.multiplicity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.features.multiplicity.Multiplicity.MappingContextNodeAttributeMemberIterator;
import xdi2.core.features.ordering.Ordering;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.NoDuplicatesIterator;

/**
 * An XDI attribute collection (context function), represented as a context node.
 * 
 * @author markus
 */
public final class XdiAttributeCollection extends XdiCollection {

	private static final long serialVersionUID = 1455719520426705802L;

	protected XdiAttributeCollection(ContextNode contextNode) {

		super(contextNode);
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

		return Multiplicity.isAttributeCollectionArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI attribute collection bound to a given context node.
	 * @param contextNode The context node that is an XDI attribute collection.
	 * @return The XDI attribute collection.
	 */
	public static XdiAttributeCollection fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiAttributeCollection(contextNode);
	}

	/*
	 * Multiplicity methods
	 */

	/**
	 * Creates a new XDI attribute member and adds it to this XDI collection.
	 * @return The newly created XDI attribute member.
	 */
	@Override
	public XdiAttributeMember createMember() {

		ContextNode contextNode = this.getContextNode().createContextNode(Multiplicity.memberArcXri(XRIUtil.randomUuidSubSegment()));

		return (XdiAttributeMember) XdiMember.fromContextNode(contextNode);
	}

	/**
	 * Returns all XDI attribute members in this XDI collection.
	 * @return An iterator over all XDI attribute members.
	 */
	@Override
	public Iterator<XdiAttributeMember> members() {

		return this.members(false, true);
	}

	/**
	 * Returns all XDI attribute members in this XDI attribute collection.
	 * @return An iterator over all XDI attribute members.
	 */
	public Iterator<XdiAttributeMember> members(boolean ordered, boolean unordered) {

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

			return new EmptyIterator<XdiAttributeMember> ();
		}

		// look for context nodes that are valid XDI attribute members

		return new MappingContextNodeAttributeMemberIterator(contextNodes);
	}

	/**
	 * Returns the number of XDI entity members in this XDI collection.
	 */
	@Override
	public int membersSize() {

		return new IteratorCounter(this.members()).count();
	}
}
