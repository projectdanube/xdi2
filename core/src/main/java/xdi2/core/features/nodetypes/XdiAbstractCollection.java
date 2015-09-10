package xdi2.core.features.nodetypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiAbstractInstanceUnordered.MappingContextNodeXdiInstanceUnorderedIterator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIXRef;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.CastingIterator;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.MappingEquivalenceXdiContextIterator;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

public abstract class XdiAbstractCollection<EQC extends XdiCollection<EQC, EQI, C, U, O, I>, EQI extends XdiSubGraph<EQI>, C extends XdiCollection<EQC, EQI, C, U, O, I>, U extends XdiInstanceUnordered<EQC, EQI, C, U, O, I>, O extends XdiInstanceOrdered<EQC, EQI, C, U, O, I>, I extends XdiInstance<EQC, EQI, C, U, O, I>> extends XdiAbstractSubGraph<EQC> implements XdiCollection<EQC, EQI, C, U, O, I> {

	private static final long serialVersionUID = -1976646316893343570L;

	private static final Logger log = LoggerFactory.getLogger(XdiAbstractCollection.class);

	private Class<C> c;
	private Class<U> u;
	private Class<O> o;
	private Class<I> i;

	protected XdiAbstractCollection(ContextNode contextNode, Class<C> c, Class<U> u, Class<O> o, Class<I> i) {

		super(contextNode);

		this.c = c;
		this.u = u;
		this.o = o;
		this.i = i;
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

		if (contextNode == null) throw new NullPointerException();

		if (XdiEntityCollection.isValid(contextNode)) return true; 
		if (XdiAttributeCollection.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates an XDI collection bound to a given context node.
	 * @param contextNode The context node that is an XDI collection.
	 * @return The XDI collection.
	 */
	public static XdiCollection<?, ?, ?, ?, ?, ?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiCollection<?, ?, ?, ?, ?, ?> xdiCollection;

		if ((xdiCollection = XdiEntityCollection.fromContextNode(contextNode)) != null) return xdiCollection;
		if ((xdiCollection = XdiAttributeCollection.fromContextNode(contextNode)) != null) return xdiCollection;

		return xdiCollection;
	}

	public static XdiCollection<?, ?, ?, ?, ?, ?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	/*
	 * Instance methods
	 */

	/**
	 * Sets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public U setXdiInstanceUnordered(boolean attribute) {

		return this.setXdiInstanceUnordered(attribute, null);
	}

	/**
	 * Sets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public U setXdiInstanceUnordered(boolean attribute, String literal) {

		return this.setXdiInstanceUnordered(attribute, true, false, literal);
	}

	/**
	 * Sets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public U setXdiInstanceUnordered(boolean attribute, boolean immutable, boolean relative) {

		return this.setXdiInstanceUnordered(attribute, immutable, relative, null);
	}

	/**
	 * Sets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public U setXdiInstanceUnordered(boolean attribute, boolean immutable, boolean relative, String literal) {

		XDIArc XDIarc = XdiAbstractInstanceUnordered.createXDIArc(attribute, immutable, relative, literal, null);

		ContextNode instanceContextNode = this.getContextNode().setContextNode(XDIarc);

		return XdiAbstractContext.fromContextNode(instanceContextNode, this.getU());
	}

	/**
	 * gets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public U getXdiInstanceUnordered(boolean attribute, boolean immutable, boolean relative, String literal) {

		if (literal == null) throw new NullPointerException();

		XDIArc XDIarc = XdiAbstractInstanceUnordered.createXDIArc(attribute, immutable, relative, literal, null);

		ContextNode instanceContextNode = this.getContextNode().getContextNode(XDIarc, false);
		if (instanceContextNode == null) return null;

		return XdiAbstractContext.fromContextNode(instanceContextNode, this.getU());
	}

	/**
	 * Returns all XDI instances in this XDI collection.
	 * @return An iterator over all XDI instances.
	 */
	@Override
	public ReadOnlyIterator<U> getXdiInstancesUnordered() {

		return new XdiInstancesUnorderedIterator();
	}

	/**
	 * Returns the number of XDI instances in this XDI collection.
	 */
	@Override
	public long getXdiInstancesUnorderedCount() {

		return new IteratorCounter(this.getXdiInstancesUnordered()).count();
	}

	/**
	 * Sets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public O setXdiInstanceOrdered(boolean attribute) {

		return this.setXdiInstanceOrdered(attribute, -1);
	}

	/**
	 * Sets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public O setXdiInstanceOrdered(boolean attribute, long index) {

		return this.setXdiInstanceOrdered(attribute, false, true, index);
	}

	/**
	 * Sets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public O setXdiInstanceOrdered(boolean attribute, boolean immutable, boolean relative) {

		return this.setXdiInstanceOrdered(attribute, immutable, relative, -1);
	}

	/**
	 * Sets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public O setXdiInstanceOrdered(boolean attribute, boolean immutable, boolean relative, long index) {

		String literal = index >= 0 ? Long.toString(index) : Long.toString(this.getXdiInstancesOrderedCount());

		XDIArc XDIarc = XdiAbstractInstanceOrdered.createXDIArc(attribute, immutable, relative, literal, null);

		ContextNode contextNode = this.getContextNode().setContextNode(XDIarc);

		return XdiAbstractContext.fromContextNode(contextNode, this.getO());
	}

	/**
	 * Gets an XDI instance under this XDI collection.
	 * @return The XDI instance.
	 */
	public O getXdiInstanceOrdered(boolean attribute, boolean immutable, boolean relative, long index) {

		if (index < 0) throw new IllegalArgumentException();

		String literal = Long.toString(index);

		XDIArc XDIarc = XdiAbstractInstanceOrdered.createXDIArc(attribute, immutable, relative, literal, null);

		ContextNode contextNode = this.getContextNode().getContextNode(XDIarc, false);
		if (contextNode == null) return null;

		return XdiAbstractContext.fromContextNode(contextNode, this.getO());
	}

	/**
	 * Returns all XDI instances in this XDI collection.
	 * @return An iterator over all XDI instances.
	 */
	@Override
	public ReadOnlyIterator<O> getXdiInstancesOrdered() {

		return new XdiInstancesOrderedIterator();
	}

	/**
	 * Returns the number of XDI instances in this XDI collection.
	 */
	@Override
	public long getXdiInstancesOrderedCount() {

		return new IteratorCounter(this.getXdiInstancesOrdered()).count();
	}

	/**
	 * Returns all XDI instances and instances in this XDI collection.
	 * @return An iterator over all XDI instances and instances.
	 */
	@Override
	public ReadOnlyIterator<I> getXdiInstances() {

		List<Iterator<? extends I>> list = new ArrayList<Iterator<? extends I>> ();
		list.add(new CastingIterator<O, I> (this.getXdiInstancesOrdered()));
		list.add(new CastingIterator<U, I> (this.getXdiInstancesUnordered()));

		Iterator<I> iterator = new CompositeIterator<I> (list.iterator());

		return (ReadOnlyIterator<I>) iterator;
	}

	/**
	 * Returns all XDI instances and instances in this XDI collection.
	 * @return An iterator over all XDI instances and instances.
	 */
	@Override
	public ReadOnlyIterator<EQI> getXdiInstancesDeref() {

		Iterator<EQI> iterator = new CastingIterator<I, EQI> (this.getXdiInstances());

		iterator = new MappingEquivalenceXdiContextIterator<EQI> (iterator);
		iterator = new NoDuplicatesIterator<EQI> (iterator);

		return (ReadOnlyIterator<EQI>) iterator;
	}

	public Class<C> getC() {

		return this.c;
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

	/*
	 * Methods for arcs
	 */

	public static XDIArc createXDIArc(Character cs, boolean attribute, boolean immutable, boolean relative, String literal, XDIXRef xref) {

		return XDIArc.fromComponents(
				cs, 
				false, 
				false, 
				true, 
				attribute, 
				immutable, 
				relative, 
				literal, 
				xref);
	}

	public static boolean isValidXDIArc(XDIArc XDIarc) {

		if (XDIarc == null) throw new NullPointerException();

		if (XdiEntityCollection.isValidXDIArc(XDIarc)) return true; 
		if (XdiAttributeCollection.isValidXDIArc(XDIarc)) return true;

		return false;
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

	public class XdiInstancesUnorderedIterator extends ReadOnlyIterator<U> {

		public XdiInstancesUnorderedIterator() {

			super(new CastingIterator<XdiInstanceUnordered<?, ?, ?, ?, ?, ?>, U> (new MappingContextNodeXdiInstanceUnorderedIterator(XdiAbstractCollection.this.getContextNode().getContextNodes())));
		}
	}

	public class XdiInstancesOrderedIterator extends ReadOnlyIterator<O> {

		private int index = 0;
		private O nextXdiInstance = null;
		private boolean triedNextXdiInstance = false;

		public XdiInstancesOrderedIterator() {

			super(null);
		}

		@Override
		public boolean hasNext() {

			this.tryNextXdiInstance();

			return this.nextXdiInstance != null;
		}

		@Override
		public O next() {

			this.tryNextXdiInstance();

			this.index++;
			this.triedNextXdiInstance = false;

			return this.nextXdiInstance;
		}

		private void tryNextXdiInstance() {

			if (this.triedNextXdiInstance) return;

			this.nextXdiInstance = XdiAbstractCollection.this.getXdiInstanceOrdered(false, false, this.index);

			if (log.isTraceEnabled()) log.trace("Next instance at index " + this.index + ": " + this.nextXdiInstance);

			this.triedNextXdiInstance = true;
		}
	}
}
