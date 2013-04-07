package xdi2.core.features.contextfunctions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.features.contextfunctions.XdiAbstractInstance.MappingContextNodeXdiInstanceIterator;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.TerminatingOnNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiAbstractClass extends XdiAbstractSubGraph {

	private static final long serialVersionUID = -1976646316893343570L;

	protected XdiAbstractClass(ContextNode contextNode) {

		super(contextNode);
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
	public static XdiAbstractClass fromContextNode(ContextNode contextNode) {

		XdiAbstractClass xdiClass;

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
	 * Creates or returns an XDI instance under this XDI class.
	 * @return The XDI instance.
	 */
	public XdiAbstractInstance getXdiInstance(XDI3SubSegment arcXri, boolean create) {

		ContextNode instanceContextNode = this.getContextNode().getContextNode(arcXri);
		if (instanceContextNode == null && create) instanceContextNode = this.getContextNode().createContextNode(arcXri);
		if (instanceContextNode == null) return null;

		return XdiAbstractInstance.fromContextNode(instanceContextNode);
	}

	/**
	 * Creates a new XDI instance and adds it to this XDI class.
	 * @return The newly created XDI instance.
	 */
	public XdiAbstractInstance getXdiInstance() {

		return this.getXdiInstance(XDI3Util.randomUuidSubSegment(XDI3Constants.CS_BANG), true);
	}

	/**
	 * Returns all XDI instances in this XDI class.
	 * @return An iterator over all XDI instances.
	 */
	public Iterator<? extends XdiAbstractInstance> instances() {

		// get all context nodes that are valid XDI instances

		Iterator<ContextNode> contextNodes = this.getContextNode().getContextNodes();

		return new MappingContextNodeXdiInstanceIterator(contextNodes);
	}

	/**
	 * Returns the number of XDI instances in this XDI class.
	 */
	public int instancesSize() {

		return new IteratorCounter(this.instances()).count();
	}

	/**
	 * Creates or returns an XDI element under this XDI class.
	 * @return The XDI element.
	 */
	public XdiAbstractElement getXdiElement(int index, boolean create) {

		XDI3SubSegment arcXri = XDI3SubSegment.create("" + XDI3Constants.CF_ELEMENT.charAt(0) + index + XDI3Constants.CF_ELEMENT.charAt(1));

		ContextNode elementContextNode = this.getContextNode().getContextNode(arcXri);
		if (elementContextNode == null && create) elementContextNode = this.getContextNode().createContextNode(arcXri);
		if (elementContextNode == null) return null;

		return XdiAbstractElement.fromContextNode(elementContextNode);
	}

	/**
	 * Returns all XDI elements in this XDI class.
	 * @return An iterator over all XDI elements.
	 */
	public Iterator<? extends XdiAbstractElement> elements() {

		return new TerminatingOnNullIterator<XdiAbstractElement> (new ReadOnlyIterator<XdiAbstractElement> (null) {

			private int index = 0;
			private XdiAbstractElement nextXdiElement = null;
			private boolean triedNextXdiElement = false;

			@Override
			public boolean hasNext() {

				this.tryNextXdiElement();

				return this.nextXdiElement != null;
			}

			@Override
			public XdiAbstractElement next() {

				this.tryNextXdiElement();

				this.index++;
				this.triedNextXdiElement = false;

				return this.nextXdiElement;
			}

			private void tryNextXdiElement() {

				if (this.triedNextXdiElement) return;

				this.nextXdiElement = XdiAbstractClass.this.getXdiElement(this.index, false);

				this.triedNextXdiElement = true;
			}
		});
	}

	/**
	 * Returns all XDI instances and element in this XDI class.
	 * @return An iterator over all XDI instances and elements.
	 */
	public Iterator<? extends XdiSubGraph> instancesAndElements() {

		List<Iterator<? extends XdiSubGraph>> list = new ArrayList<Iterator<? extends XdiSubGraph>> ();
		list.add(this.elements());
		list.add(this.instances());

		CompositeIterator<XdiSubGraph> iterators = new CompositeIterator<XdiSubGraph> (list.iterator());

		return new NoDuplicatesIterator<XdiSubGraph> (iterators);
	}

	/*
	 * Helper classes
	 */

	public static class MappingContextNodeXdiClassIterator extends NotNullIterator<XdiAbstractClass> {

		public MappingContextNodeXdiClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiAbstractClass> (contextNodes) {

				@Override
				public XdiAbstractClass map(ContextNode contextNode) {

					return XdiAbstractClass.fromContextNode(contextNode);
				}
			});
		}
	}
}
