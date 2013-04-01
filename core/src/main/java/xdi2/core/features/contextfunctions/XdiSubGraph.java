package xdi2.core.features.contextfunctions;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI subgraph according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public class XdiSubGraph implements Serializable, Comparable<XdiSubGraph> {

	private static final long serialVersionUID = -8756059289169602694L;

	private ContextNode contextNode;

	protected XdiSubGraph(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		this.contextNode = contextNode;
	}

	public ContextNode getContextNode() {

		return this.contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid subgraph.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid subgraph.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return true;
	}

	/**
	 * Factory method that creates a subgraph bound to a given context node.
	 * @param contextNode The context node that is a subgraph.
	 * @return The subgraph.
	 */
	public static XdiSubGraph fromContextNode(ContextNode contextNode) {

		if (XdiCollection.isValid(contextNode)) return XdiCollection.fromContextNode(contextNode);
		if (XdiEntitySingleton.isValid(contextNode)) return XdiEntitySingleton.fromContextNode(contextNode);
		if (XdiAttributeSingleton.isValid(contextNode)) return XdiAttributeSingleton.fromContextNode(contextNode);
		if (XdiEntityMember.isValid(contextNode)) return XdiEntityMember.fromContextNode(contextNode);
		if (XdiAttributeMember.isValid(contextNode)) return XdiAttributeMember.fromContextNode(contextNode);

		return new XdiSubGraph(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * @param Returns the "base" arc XRI, without context function syntax.
	 * @return The "base" arc XRI.
	 */
	public XDI3SubSegment getBaseArcXri() {

		XDI3SubSegment arcXri = this.getContextNode().getArcXri();

		if (arcXri.hasXRef() && arcXri.getXRef().hasSegment()) {

			return arcXri.getXRef().getSegment().getFirstSubSegment();
		} else {

			return arcXri;
		}
	}

	/**
	 * Creates or returns an XDI collection under this XDI subgraph.
	 * @param arcXri The "base" arc XRI of the XDI collection, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI collection.
	 */
	public XdiCollection getXdiCollection(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment collectionArcXri = XdiCollection.createCollectionArcXri(arcXri);
		ContextNode collectionContextNode = this.getContextNode().getContextNode(collectionArcXri);
		if (collectionContextNode == null && create) collectionContextNode = this.getContextNode().createContextNode(collectionArcXri);
		if (collectionContextNode == null) return null;

		return new XdiCollection(collectionContextNode);
	}

	/**
	 * Creates or returns an XDI attribute singleton under this XDI subgraph.
	 * @param arcXri The "base" arc XRI of the XDI attribute singleton, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI attribute singleton.
	 */
	public XdiAttributeSingleton getXdiAttributeSingleton(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment attributeSingletonArcXri = XdiAttributeSingleton.createAttributeSingletonArcXri(arcXri);
		ContextNode attributeSingletonContextNode = this.getContextNode().getContextNode(attributeSingletonArcXri);
		if (attributeSingletonContextNode == null && create) attributeSingletonContextNode = this.getContextNode().createContextNode(attributeSingletonArcXri);
		if (attributeSingletonContextNode == null) return null;

		return new XdiAttributeSingleton(attributeSingletonContextNode);
	}

	/**
	 * Creates or returns an XDI entity singleton under this XDI subgraph.
	 * @param arcXri The "base" arc XRI of the XDI entity singleton, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI entity singleton.
	 */
	public XdiEntitySingleton getXdiEntitySingleton(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment entitySingletonArcXri = XdiEntitySingleton.createEntitySingletonArcXri(arcXri);
		ContextNode entitySingletonContextNode = this.getContextNode().getContextNode(entitySingletonArcXri);
		if (entitySingletonContextNode == null && create) entitySingletonContextNode = this.getContextNode().createContextNode(entitySingletonArcXri);
		if (entitySingletonContextNode == null) return null;

		return new XdiEntitySingleton(entitySingletonContextNode);
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof XdiSubGraph)) return false;
		if (object == this) return true;

		XdiSubGraph other = (XdiSubGraph) object;

		// two subgraphs are equal if their context nodes are equal

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(XdiSubGraph other) {

		if (other == null || other == this) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
