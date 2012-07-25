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
 * An XDI entity collection according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public final class EntityCollection extends AbstractMultiplicityCollection<EntitySingleton> {

	private static final long serialVersionUID = 1455719520426705802L;

	protected EntityCollection(ContextNode contextNode) {

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
	public static EntityCollection fromContextNode(ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new EntityCollection(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Creates a new entity singleton and adds it to this XDI entity collection.
	 * @return The newly created entity singleton.
	 */
	public EntitySingleton createEntitySingleton() {

		ContextNode contextNode = this.getContextNode().createContextNode(Multiplicity.entityCollectionMemberArcXri());

		return EntitySingleton.fromContextNode(contextNode);
	}

	/**
	 * Returns all entity singletons in this XDI entity collection.
	 * @return An iterator over all entity singletons.
	 */
	@Override
	public Iterator<EntitySingleton> iterator() {

		return this.iterator(false, true);
	}

	/**
	 * Returns all entity singletons in this XDI entity collection.
	 * @return An iterator over all entity singletons.
	 */
	public Iterator<EntitySingleton> iterator(boolean ordered, boolean unordered) {

		// ordered or unordered?

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

			return new EmptyIterator<EntitySingleton> ();
		}

		// look for context nodes that are valid entity singletons

		return new SelectingMappingIterator<ContextNode, EntitySingleton> (contextNodes) {

			@Override
			public boolean select(ContextNode contextNode) {

				return Multiplicity.isEntityCollectionMemberArcXri(contextNode.getArcXri());
			}

			@Override
			public EntitySingleton map(ContextNode contextNode) {

				return EntitySingleton.fromContextNode(contextNode);
			}
		};
	}

	/**
	 * Returns the number of entity singletons in this XDI entity collection.
	 */
	public int size() {

		return new IteratorCounter(this.iterator()).count();
	}
}
