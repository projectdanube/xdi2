package xdi2.core.features.contextfunctions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.features.contextfunctions.XdiInstance.MappingContextNodeXdiInstanceIterator;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NoDuplicatesIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.TerminatingOnNullIterator;
import xdi2.core.xri3.XDI3Constants;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class XdiClass extends XdiAbstractSubGraph {

	private static final long serialVersionUID = -1976646316893343570L;

	protected XdiClass(ContextNode contextNode) {

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
	public static XdiClass fromContextNode(ContextNode contextNode) {

		XdiClass xdiClass;

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
	public XdiInstance getXdiInstance(XDI3SubSegment arcXri, boolean create) {

		ContextNode instanceContextNode = this.getContextNode().getContextNode(arcXri);
		if (instanceContextNode == null && create) instanceContextNode = this.getContextNode().createContextNode(arcXri);
		if (instanceContextNode == null) return null;

		return XdiInstance.fromContextNode(instanceContextNode);
	}

	/**
	 * Creates a new XDI instance and adds it to this XDI class.
	 * @return The newly created XDI instance.
	 */
	public XdiInstance getXdiInstance() {

		return this.getXdiInstance(XRIUtil.randomUuidSubSegment(XDI3Constants.CS_BANG), true);
	}

	/**
	 * Returns all XDI instances in this XDI class.
	 * @return An iterator over all XDI instances.
	 */
	public Iterator<? extends XdiInstance> instances() {

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
	public XdiElement getXdiElement(int index, boolean create) {

		XDI3SubSegment arcXri = XDI3SubSegment.create("" + XDI3Constants.CF_ELEMENT.charAt(0) + index + XDI3Constants.CF_ELEMENT.charAt(1));

		ContextNode elementContextNode = this.getContextNode().getContextNode(arcXri);
		if (elementContextNode == null && create) elementContextNode = this.getContextNode().createContextNode(arcXri);
		if (elementContextNode == null) return null;

		return XdiElement.fromContextNode(elementContextNode);
	}

	/**
	 * Returns all XDI elements in this XDI class.
	 * @return An iterator over all XDI elements.
	 */
	public Iterator<? extends XdiElement> elements() {

		return new TerminatingOnNullIterator<XdiElement> (new ReadOnlyIterator<XdiElement> (null) {

			private int index = 0;
			private XdiElement nextXdiElement = null;
			private boolean triedNextXdiElement = false;

			@Override
			public boolean hasNext() {

				this.tryNextXdiElement();

				return this.nextXdiElement != null;
			}

			@Override
			public XdiElement next() {

				this.tryNextXdiElement();

				this.index++;
				this.triedNextXdiElement = false;

				return this.nextXdiElement;
			}

			private void tryNextXdiElement() {

				if (this.triedNextXdiElement) return;

				this.nextXdiElement = XdiClass.this.getXdiElement(this.index, false);

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

	public static class MappingContextNodeXdiClassIterator extends NotNullIterator<XdiClass> {

		public MappingContextNodeXdiClassIterator(Iterator<ContextNode> contextNodes) {

			super(new MappingIterator<ContextNode, XdiClass> (contextNodes) {

				@Override
				public XdiClass map(ContextNode contextNode) {

					return XdiClass.fromContextNode(contextNode);
				}
			});
		}
	}
}
