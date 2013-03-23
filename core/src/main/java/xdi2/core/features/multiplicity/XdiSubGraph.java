package xdi2.core.features.multiplicity;

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

		if (XdiEntitySingleton.isValid(contextNode)) return new XdiEntitySingleton(contextNode);
		if (XdiAttributeSingleton.isValid(contextNode)) return new XdiAttributeSingleton(contextNode);
		if (XdiEntityCollection.isValid(contextNode)) return new XdiEntityCollection(contextNode);
		if (XdiAttributeCollection.isValid(contextNode)) return new XdiEntityCollection(contextNode);
		if (XdiEntityMember.isValid(contextNode)) return new XdiEntityMember(contextNode);
		if (XdiAttributeMember.isValid(contextNode)) return new XdiAttributeMember(contextNode);

		return new XdiSubGraph(contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * @param Returns the "base" arc XRI of this XDI subgraph, without multiplicity syntax.
	 * @return The "base" arc XRI.
	 */
	public XDI3SubSegment getBaseArcXri() {
		
		return Multiplicity.baseArcXri(this.getContextNode().getArcXri());
	}

	/**
	 * Gets or returns an XDI entity singleton under a context node.
	 * @param arcXri The "base" arc XRI of the XDI entity singleton, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI entity singleton.
	 */
	public XdiEntitySingleton getEntitySingleton(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment entitySingletonArcXri = Multiplicity.entitySingletonArcXri(arcXri);
		ContextNode entitySingletonContextNode = this.getContextNode().getContextNode(entitySingletonArcXri);
		if (entitySingletonContextNode == null && create) entitySingletonContextNode = this.getContextNode().createContextNode(entitySingletonArcXri);
		if (entitySingletonContextNode == null) return null;

		return new XdiEntitySingleton(entitySingletonContextNode);
	}

	/**
	 * Gets or returns an XDI attribute singleton under a context node.
	 * @param arcXri The "base" arc XRI of the XDI attribute singleton, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI attribute singleton.
	 */
	public XdiAttributeSingleton getAttributeSingleton(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment attributeSingletonArcXri = Multiplicity.attributeSingletonArcXri(arcXri);
		ContextNode attributeSingletonContextNode = this.getContextNode().getContextNode(attributeSingletonArcXri);
		if (attributeSingletonContextNode == null && create) attributeSingletonContextNode = this.getContextNode().createContextNode(attributeSingletonArcXri);
		if (attributeSingletonContextNode == null) return null;

		return new XdiAttributeSingleton(attributeSingletonContextNode);
	}
	
	/**
	 * Gets or returns an XDI entity collection under a context node.
	 * @param arcXri The "base" arc XRI of the XDI entity collection, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI entity collection.
	 */
	public XdiEntityCollection getEntityCollection(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment entityCollectionArcXri = Multiplicity.entityCollectionArcXri(arcXri);
		ContextNode entityCollectionContextNode = this.getContextNode().getContextNode(entityCollectionArcXri);
		if (entityCollectionContextNode == null && create) entityCollectionContextNode = this.getContextNode().createContextNode(entityCollectionArcXri);
		if (entityCollectionContextNode == null) return null;

		return new XdiEntityCollection(entityCollectionContextNode);
	}
	
	/**
	 * Gets or returns an XDI attribute collection under a context node.
	 * @param arcXri The "base" arc XRI of the XDI attribute collection, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI attribute collection.
	 */
	public XdiAttributeCollection getAttributeCollection(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment attributeCollectionArcXri = Multiplicity.attributeCollectionArcXri(arcXri);
		ContextNode attributeCollectionContextNode = this.getContextNode().getContextNode(attributeCollectionArcXri);
		if (attributeCollectionContextNode == null && create) attributeCollectionContextNode = this.getContextNode().createContextNode(attributeCollectionArcXri);
		if (attributeCollectionContextNode == null) return null;

		return new XdiAttributeCollection(attributeCollectionContextNode);
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

		// two multiplicity objects are equal if their context nodes are equal

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
