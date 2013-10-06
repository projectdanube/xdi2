package xdi2.core.features.nodetypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiAbstractMemberUnordered.MappingContextNodeXdiMemberUnorderedIterator;
import xdi2.core.util.iterators.CastingIterator;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.MappingEquivalenceXdiContextIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractCollection<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiMemberUnordered<EQC, EQI, C, U, O, I>, O extends XdiMemberOrdered<EQC, EQI, C, U, O, I>, I extends XdiMember<EQC, EQI, C, U, O, I>> extends XdiAbstractSubGraph<EQC> implements XdiCollection<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = -1976646316893343570L;

	private static final Logger log = LoggerFactory.getLogger(XdiAbstractCollection.class);

	private Class<U> u;
	private Class<O> o;
	private Class<I> i;

	protected XdiAbstractCollection(ContextNode contextNode, Class<U> u, Class<O> o, Class<I> i) {

		super(contextNode);

		this.u = u;
		this.o = o;
		this.i = i;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI class.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI class.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return XdiEntityCollection.isValid(contextNode) || 
				XdiAttributeCollection.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI class bound to a given context node.
	 * @param contextNode The context node that is an XDI class.
	 * @return The XDI class.
	 */
	public static XdiCollection<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		XdiCollection<?, ?, ?, ?, ?, ?> xdiCollection;

		if ((xdiCollection = XdiEntityCollection.fromContextNode(contextNode)) != null) return xdiCollection;
		if ((xdiCollection = XdiAttributeCollection.fromContextNode(contextNode)) != null) return xdiCollection;

		return xdiCollection;
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiEntityCollection.isValidArcXri(arcXri) || 
				XdiAttributeCollection.isValidArcXri(arcXri);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Sets an XDI instance under this XDI class.
	 * @return The XDI instance.
	 */
	@Override
	public U setXdiMemberUnordered(XDI3SubSegment arcXri) {

		boolean attribute = this.attribute();

		if (arcXri == null) arcXri = XdiAbstractMemberUnordered.createArcXriFromRandom(attribute);

		ContextNode memberContextNode = this.getContextNode().setContextNode(arcXri);

		return XdiAbstractContext.fromContextNode(memberContextNode, this.getU());
	}

	/**
	 * gets an XDI instance under this XDI class.
	 * @return The XDI instance.
	 */
	@Override
	public U getXdiMemberUnordered(XDI3SubSegment arcXri) {
		
		ContextNode memberContextNode = this.getContextNode().getContextNode(arcXri);
		if (memberContextNode == null) return null;

		return XdiAbstractContext.fromContextNode(memberContextNode, this.getU());
	}

	/**
	 * Returns all XDI instances in this XDI class.
	 * @return An iterator over all XDI instances.
	 */
	@Override
	public ReadOnlyIterator<U> getXdiMembersUnordered() {

		return new XdiMembersUnorderedIterator();
	}

	/**
	 * Returns the number of XDI instances in this XDI class.
	 */
	@Override
	public long getXdiMembersUnorderedCount() {

		return new IteratorCounter(this.getXdiMembersUnordered()).count();
	}

	/**
	 * Sets an XDI element under this XDI class.
	 * @return The XDI element.
	 */
	@Override
	public O setXdiMemberOrdered(long index) {

		boolean attribute = this.attribute();

		if (index < 0) index = this.getXdiMembersOrderedCount();

		XDI3SubSegment arcXri = XdiAbstractMemberOrdered.createArcXri(Long.toString(index), attribute);

		ContextNode contextNode = this.getContextNode().setContextNode(arcXri);

		return XdiAbstractContext.fromContextNode(contextNode, this.getO());
	}

	/**
	 * Gets an XDI element under this XDI class.
	 * @return The XDI element.
	 */
	@Override
	public O getXdiMemberOrdered(long index) {

		boolean attribute = this.attribute();

		XDI3SubSegment arcXri = XdiAbstractMemberOrdered.createArcXri(Long.toString(index), attribute);

		ContextNode contextNode = this.getContextNode().getContextNode(arcXri);
		if (contextNode == null) return null;

		return XdiAbstractContext.fromContextNode(contextNode, this.getO());
	}

	/**
	 * Returns all XDI elements in this XDI class.
	 * @return An iterator over all XDI elements.
	 */
	@Override
	public ReadOnlyIterator<O> getXdiMembersOrdered() {

		return new XdiMembersOrderedIterator();
	}

	/**
	 * Returns the number of XDI elements in this XDI class.
	 */
	@Override
	public long getXdiMembersOrderedCount() {

		return new IteratorCounter(this.getXdiMembersOrdered()).count();
	}

	/**
	 * Returns all XDI instances and elements in this XDI class.
	 * @return An iterator over all XDI instances and elements.
	 */
	@Override
	public ReadOnlyIterator<I> getXdiMembers() {

		List<Iterator<? extends I>> list = new ArrayList<Iterator<? extends I>> ();
		list.add(new CastingIterator<O, I> (this.getXdiMembersOrdered()));
		list.add(new CastingIterator<U, I> (this.getXdiMembersUnordered()));

		Iterator<I> iterator = new CompositeIterator<I> (list.iterator());

		return (ReadOnlyIterator<I>) iterator;
	}

	/**
	 * Returns all XDI instances and elements in this XDI class.
	 * @return An iterator over all XDI instances and elements.
	 */
	@Override
	public ReadOnlyIterator<EQI> getXdiMembersDeref() {

		Iterator<EQI> iterator = new CastingIterator<I, EQI> (this.getXdiMembers());

		iterator = new MappingEquivalenceXdiContextIterator<EQI> (iterator);
		iterator = new NoDuplicatesIterator<EQI> (iterator);

		return (ReadOnlyIterator<EQI>) iterator;
	}

	public Class<U> getU() {

		return this.u;
	}

	public Class<O> getO() {

		return this.o;
	}

	public Class<I> getI() {

		return this.i;
	}

	private boolean attribute() {

		boolean attribute;

		if (XdiAttributeCollection.class.isAssignableFrom(this.getClass()))
			attribute = true;
		else if (XdiEntityCollection.class.isAssignableFrom(this.getClass()))
			attribute = false;
		else
			throw new IllegalStateException("Invalid XDI class: " + this.getClass().getSimpleName());

		return attribute;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiCollectionIterator extends NotNullIterator<XdiCollection<?, ?, ?, ?, ?, ?>> {

		public MappingContextNodeXdiCollectionIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiCollection<?, ?, ?, ?, ?, ?>> (contextNodes) {

				@Override
				public XdiCollection<?, ?, ?, ?, ?, ?> map(ContextNode contextNode) {

					return XdiAbstractCollection.fromContextNode(contextNode);
				}
			});
		}
	}

	public class XdiMembersUnorderedIterator extends ReadOnlyIterator<U> {

		public XdiMembersUnorderedIterator() {

			super(new CastingIterator<XdiMemberUnordered<?, ?, ?, ?, ?, ?>, U> (new MappingContextNodeXdiMemberUnorderedIterator(XdiAbstractCollection.this.getContextNode().getContextNodes())));
		}
	}

	public class XdiMembersOrderedIterator extends ReadOnlyIterator<O> {

		private int index = 0;
		private O nextXdiElement = null;
		private boolean triedNextXdiElement = false;

		public XdiMembersOrderedIterator() {

			super(null);
		}

		@Override
		public boolean hasNext() {

			this.tryNextXdiElement();

			return this.nextXdiElement != null;
		}

		@Override
		public O next() {

			this.tryNextXdiElement();

			this.index++;
			this.triedNextXdiElement = false;

			return this.nextXdiElement;
		}

		private void tryNextXdiElement() {

			if (this.triedNextXdiElement) return;

			this.nextXdiElement = XdiAbstractCollection.this.getXdiMemberOrdered(this.index);

			if (log.isTraceEnabled()) log.trace("Next element at index " + this.index + ": " + this.nextXdiElement);

			this.triedNextXdiElement = true;
		}
	}
}
