package xdi2.core.features.contextfunctions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.features.contextfunctions.XdiAttributeMember.MappingContextNodeAttributeMemberIterator;
import xdi2.core.features.contextfunctions.XdiEntityMember.MappingContextNodeEntityMemberIterator;
import xdi2.core.features.ordering.Ordering;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI collection according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public final class XdiCollection extends XdiSubGraph {

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

		return isCollectionArcXri(contextNode.getArcXri());
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
	 * Instance methods
	 */

	/**
	 * Creates a new XDI entity member and adds it to this XDI collection.
	 * @return The newly created XDI entity member.
	 */
	public XdiEntityMember createXdiEntityMember(XDI3SubSegment arcXri) {

		ContextNode contextNode = this.getContextNode().createContextNode(XdiEntityMember.createEntityMemberArcXri(XRIUtil.randomUuidSubSegment()));

		return XdiEntityMember.fromContextNode(contextNode);
	}

	/**
	 * Creates a new XDI entity member and adds it to this XDI collection.
	 * @return The newly created XDI entity member.
	 */
	public XdiEntityMember createXdiEntityMember() {

		return this.createXdiEntityMember(XRIUtil.randomUuidSubSegment());
	}

	/**
	 * Creates a new XDI attribute member and adds it to this XDI collection.
	 * @return The newly created XDI attribute member.
	 */
	public XdiAttributeMember createXdiAttributeMember(XDI3SubSegment arcXri) {

		ContextNode contextNode = this.getContextNode().createContextNode(XdiAttributeMember.createAttributeMemberArcXri(arcXri));

		return XdiAttributeMember.fromContextNode(contextNode);
	}

	/**
	 * Creates a new XDI attribute member and adds it to this XDI collection.
	 * @return The newly created XDI attribute member.
	 */
	public XdiAttributeMember createXdiAttributeMember() {

		return this.createXdiAttributeMember(XRIUtil.randomUuidSubSegment());
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
	public int entitiesSize() {

		return new IteratorCounter(this.entities()).count();
	}

	/**
	 * Returns the number of XDI attribute members in this XDI collection.
	 */
	public int attributesSize() {

		return new IteratorCounter(this.attributes()).count();
	}

	/*
	 * Methods for XDI collection XRIs
	 */

	public static XDI3SubSegment createCollectionArcXri(XDI3SubSegment xri) {

		return XDI3SubSegment.create("" + XDI3Constants.CF_COLLECTION.charAt(0) + xri + XDI3Constants.CF_COLLECTION.charAt(1));
	}

	public static boolean isCollectionArcXri(XDI3SubSegment arcXri) {

		if (arcXri.hasCs()) return false;

		if (! arcXri.hasXRef()) return false;
		if (! XDI3Constants.CF_COLLECTION.equals(arcXri.getXRef().getCf())) return false;

		return true;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeCollectionIterator extends NotNullIterator<XdiCollection> {

		public MappingContextNodeCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiCollection> (contextNodes) {

				@Override
				public XdiCollection map(ContextNode contextNode) {

					return XdiCollection.fromContextNode(contextNode);
				}
			});
		}
	}
}
