package xdi2.core.features.multiplicity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.SelectingMappingIterator;

/**
 * An XDI collection according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public class XdiCollection extends XdiSubGraph {

	private static final long serialVersionUID = 1455719520426705802L;

	protected XdiCollection(ContextNode contextNode) {

		super(contextNode);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI collection.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI collection.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return Multiplicity.isCollectionArcXri(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI collection bound to a given context node.
	 * @param contextNode The context node that is an XDI collection.
	 * @return The XDI collection.
	 */
	public static XdiCollection fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new XdiCollection(contextNode);
	}

	/*
	 * Multiplicity methods
	 */

	/**
	 * Creates a new XDI entity member and adds it to this XDI collection.
	 * @return The newly created XDI entity member.
	 */
	public XdiEntityMember createEntityMember() {

		ContextNode contextNode = this.getContextNode().createContextNode(Multiplicity.entityMemberArcXri());

		return XdiEntityMember.fromContextNode(contextNode);
	}

	/**
	 * Creates a new XDI attribute member and adds it to this XDI collection.
	 * @return The newly created XDI attribute member.
	 */
	public XdiAttributeMember createAttributeMember() {

		ContextNode contextNode = this.getContextNode().createContextNode(Multiplicity.attributeMemberArcXri());

		return XdiAttributeMember.fromContextNode(contextNode);
	}

	/**
	 * Returns all XDI entity members in this XDI collection.
	 * @return An iterator over all XDI entity members.
	 */
	public Iterator<XdiEntityMember> entities() {

		return this.entities(false, true);
	}

	/**
	 * Returns all XDI attribute members in this XDI collection.
	 * @return An iterator over all XDI attribute members.
	 */
	public Iterator<XdiAttributeMember> attributes() {

		return this.attributes(false, true);
	}

	/**
	 * Returns all XDI entity members in this XDI collection.
	 * @return An iterator over all XDI entity members.
	 */
	public Iterator<XdiEntityMember> entities(boolean ordered, boolean unordered) {

		// ordered or unordered or both?

		Iterator<ContextNode> contextNodes;

		if (ordered && (! unordered)) {

			contextNodes = Ordering.getOrderedContextNodes(this.getContextNode());
		} else if ((! ordered) && unordered) {

			contextNodes = this.getContextNode().getContextNodes();
		} else if (ordered && unordered) {

			List<Iterator<ContextNode>> iterators = new ArrayList<Iterator<ContextNode>> ();
			iterators.add(Ordering.getOrderedContextNodes(this.getContextNode()));
			iterators.add(this.getContextNode().getContextNodes());

			contextNodes = new NoDuplicatesIterator<ContextNode> (new CompositeIterator<ContextNode> (iterators.iterator()));
		} else {

			return new EmptyIterator<XdiEntityMember> ();
		}

		// look for context nodes that are valid XDI entity members

		return new SelectingMappingIterator<ContextNode, XdiEntityMember> (contextNodes) {

			@Override
			public boolean select(ContextNode contextNode) {

				return Multiplicity.isEntityMemberArcXri(contextNode.getArcXri());
			}

			@Override
			public XdiEntityMember map(ContextNode contextNode) {

				return XdiEntityMember.fromContextNode(contextNode);
			}
		};
	}

	/**
	 * Returns all XDI attribute members in this XDI collection.
	 * @return An iterator over all XDI attribute members.
	 */
	public Iterator<XdiAttributeMember> attributes(boolean ordered, boolean unordered) {

		// ordered or unordered or both?

		Iterator<ContextNode> contextNodes;

		if (ordered && (! unordered)) {

			contextNodes = Ordering.getOrderedContextNodes(this.getContextNode());
		} else if ((! ordered) && unordered) {

			contextNodes = this.getContextNode().getContextNodes();
		} else if (ordered && unordered) {

			List<Iterator<ContextNode>> iterators = new ArrayList<Iterator<ContextNode>> ();
			iterators.add(Ordering.getOrderedContextNodes(this.getContextNode()));
			iterators.add(this.getContextNode().getContextNodes());

			contextNodes = new NoDuplicatesIterator<ContextNode> (new CompositeIterator<ContextNode> (iterators.iterator()));
		} else {

			return new EmptyIterator<XdiAttributeMember> ();
		}

		// look for context nodes that are valid XDI attribute members

		return new SelectingMappingIterator<ContextNode, XdiAttributeMember> (contextNodes) {

			@Override
			public boolean select(ContextNode contextNode) {

				return Multiplicity.isAttributeMemberArcXri(contextNode.getArcXri());
			}

			@Override
			public XdiAttributeMember map(ContextNode contextNode) {

				return XdiAttributeMember.fromContextNode(contextNode);
			}
		};
	}

	/**
	 * Returns the number of XDI entity members in this XDI collection.
	 */
	public int entitiesSize() {

		return new IteratorCounter(this.entities()).count();
	}

	/**
	 * Returns the number of XDI attribute members in this XDI collection.
	 */
	public int attributesSize() {

		return new IteratorCounter(this.attributes()).count();
	}
}
