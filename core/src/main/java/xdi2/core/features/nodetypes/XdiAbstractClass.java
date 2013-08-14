package xdi2.core.features.nodetypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiAbstractInstanceUnordered.MappingContextNodeXdiInstanceUnorderedIterator;
import xdi2.core.util.iterators.CastingIterator;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.MappingEquivalenceXdiContextIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractClass<U extends XdiInstanceUnordered, O extends XdiInstanceOrdered, I extends XdiInstance> extends XdiAbstractSubGraph implements XdiClass<U, O, I> {

	private static final long serialVersionUID = -1976646316893343570L;

	private static final Logger log = LoggerFactory.getLogger(XdiAbstractClass.class);

	private Class<U> u;
	private Class<O> o;
	private Class<I> i;

	protected XdiAbstractClass(ContextNode contextNode, Class<U> u, Class<O> o, Class<I> i) {

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

		return XdiEntityClass.isValid(contextNode) || 
				XdiAttributeClass.isValid(contextNode);
	}

	/**
	 * Factory method that creates an XDI class bound to a given context node.
	 * @param contextNode The context node that is an XDI class.
	 * @return The XDI class.
	 */
	public static XdiAbstractClass<? extends XdiInstanceUnordered, ? extends XdiInstanceOrdered, ? extends XdiInstance> fromContextNode(ContextNode contextNode) {

		XdiAbstractClass<? extends XdiInstanceUnordered, ? extends XdiInstanceOrdered, ? extends XdiInstance> xdiClass;

		if ((xdiClass = XdiEntityClass.fromContextNode(contextNode)) != null) return xdiClass;
		if ((xdiClass = XdiAttributeClass.fromContextNode(contextNode)) != null) return xdiClass;

		return null;
	}

	/*
	 * Methods for XRIs
	 */

	public static boolean isValidArcXri(XDI3SubSegment arcXri) {

		return XdiEntityClass.isValidArcXri(arcXri) || 
				XdiAttributeClass.isValidArcXri(arcXri);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Sets an XDI instance under this XDI class.
	 * @return The XDI instance.
	 */
	@Override
	public U setXdiInstanceUnordered(XDI3SubSegment arcXri) {

		boolean attribute = this.attribute();

		if (arcXri == null) arcXri = XdiAbstractInstanceUnordered.createArcXriFromRandom(attribute);

		ContextNode instanceContextNode = this.getContextNode().getContextNode(arcXri);
		if (instanceContextNode == null) instanceContextNode = this.getContextNode().createContextNode(arcXri);

		return XdiAbstractContext.fromContextNode(instanceContextNode, this.getU());
	}

	/**
	 * gets an XDI instance under this XDI class.
	 * @return The XDI instance.
	 */
	@Override
	public U getXdiInstanceUnordered(XDI3SubSegment arcXri) {

		ContextNode instanceContextNode = this.getContextNode().getContextNode(arcXri);
		if (instanceContextNode == null) return null;

		return XdiAbstractContext.fromContextNode(instanceContextNode, this.getU());
	}

	/**
	 * Returns all XDI instances in this XDI class.
	 * @return An iterator over all XDI instances.
	 */
	@Override
	public ReadOnlyIterator<U> getXdiInstancesUnordered() {

		return new XdiInstancesUnorderedIterator();
	}

	/**
	 * Returns the number of XDI instances in this XDI class.
	 */
	@Override
	public long getXdiInstancesUnorderedCount() {

		return new IteratorCounter(this.getXdiInstancesUnordered()).count();
	}

	/**
	 * Sets an XDI element under this XDI class.
	 * @return The XDI element.
	 */
	@Override
	public O setXdiInstanceOrdered(long index) {

		boolean attribute = this.attribute();

		if (index < 0) index = this.getXdiInstancesOrderedCount();

		XDI3SubSegment arcXri = XdiAbstractInstanceOrdered.createArcXri(Long.toString(index), attribute);

		ContextNode contextNode = this.getContextNode().getContextNode(arcXri);
		if (contextNode == null) contextNode = this.getContextNode().createContextNode(arcXri);

		return XdiAbstractContext.fromContextNode(contextNode, this.getO());
	}

	/**
	 * Gets an XDI element under this XDI class.
	 * @return The XDI element.
	 */
	@Override
	public O getXdiInstanceOrdered(long index) {

		boolean attribute = this.attribute();

		XDI3SubSegment arcXri = XdiAbstractInstanceOrdered.createArcXri(Long.toString(index), attribute);

		ContextNode contextNode = this.getContextNode().getContextNode(arcXri);
		if (contextNode == null) return null;

		return XdiAbstractContext.fromContextNode(contextNode, this.getO());
	}

	/**
	 * Returns all XDI elements in this XDI class.
	 * @return An iterator over all XDI elements.
	 */
	@Override
	public ReadOnlyIterator<O> getXdiInstancesOrdered() {

		return new XdiInstancesOrderedIterator();
	}

	/**
	 * Returns the number of XDI elements in this XDI class.
	 */
	@Override
	public long getXdiInstancesOrderedCount() {

		return new IteratorCounter(this.getXdiInstancesOrdered()).count();
	}

	/**
	 * Returns all XDI instances and elements in this XDI class.
	 * @return An iterator over all XDI instances and elements.
	 */
	@Override
	public ReadOnlyIterator<I> getXdiInstances(boolean deref) {

		List<Iterator<? extends I>> list = new ArrayList<Iterator<? extends I>> ();
		list.add(new CastingIterator<O, I> (this.getXdiInstancesOrdered()));
		list.add(new CastingIterator<U, I> (this.getXdiInstancesUnordered()));

		Iterator<I> iterator = new CompositeIterator<I> (list.iterator());

		if (deref) {

			iterator = new CastingIterator<XdiContext, I> (new MappingEquivalenceXdiContextIterator(iterator));
			iterator = new NoDuplicatesIterator<I> (iterator);
		}

		return (ReadOnlyIterator<I>) iterator;
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

		if (this instanceof XdiAttributeClass)
			attribute = true;
		else if (this instanceof XdiEntityClass)
			attribute = false;
		else
			throw new IllegalStateException("Invalid XDI class: " + this.getClass().getSimpleName());

		return attribute;
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiClassIterator extends NotNullIterator<XdiClass<? extends XdiInstanceUnordered, ? extends XdiInstanceOrdered, ? extends XdiInstance>> {

		public MappingContextNodeXdiClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiClass<? extends XdiInstanceUnordered, ? extends XdiInstanceOrdered, ? extends XdiInstance>> (contextNodes) {

				@Override
				public XdiClass<? extends XdiInstanceUnordered, ? extends XdiInstanceOrdered, ? extends XdiInstance> map(ContextNode contextNode) {

					return XdiAbstractClass.fromContextNode(contextNode);
				}
			});
		}
	}

	public class XdiInstancesUnorderedIterator extends ReadOnlyIterator<U> {

		public XdiInstancesUnorderedIterator() {

			super(new CastingIterator<XdiInstanceUnordered, U> (new MappingContextNodeXdiInstanceUnorderedIterator(XdiAbstractClass.this.getContextNode().getContextNodes())));
		}
	}

	public class XdiInstancesOrderedIterator extends ReadOnlyIterator<O> {

		private int index = 0;
		private O nextXdiElement = null;
		private boolean triedNextXdiElement = false;

		public XdiInstancesOrderedIterator() {

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

			this.nextXdiElement = XdiAbstractClass.this.getXdiInstanceOrdered(this.index);

			if (log.isTraceEnabled()) log.trace("Next element at index " + this.index + ": " + this.nextXdiElement);

			this.triedNextXdiElement = true;
		}
	}
}
