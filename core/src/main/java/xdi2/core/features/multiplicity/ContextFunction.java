package xdi2.core.features.multiplicity;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.features.roots.XdiRoot;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI context function, represented as a context node.
 * 
 * @author markus
 */
public abstract class ContextFunction implements Serializable, Comparable<ContextFunction> {

	private static final long serialVersionUID = -8756059289169602694L;

	private ContextNode contextNode;

	protected ContextFunction(ContextNode contextNode) {

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

		return
				XdiRoot.isValid(contextNode) ||
				XdiMember.isValid(contextNode) ||
				XdiElement.isValid(contextNode) ||
				XdiValue.isValid(contextNode);
	}

	/**
	 * Factory method that creates a subgraph bound to a given context node.
	 * @param contextNode The context node that is a subgraph.
	 * @return The subgraph.
	 */
	public static ContextFunction fromContextNode(ContextNode contextNode) {

		ContextFunction contextFunction;
		
		if ((contextFunction = XdiRoot.fromContextNode(contextNode)) != null) return contextFunction;
		if ((contextFunction = XdiMember.fromContextNode(contextNode)) != null) return contextFunction;
		if ((contextFunction = XdiElement.fromContextNode(contextNode)) != null) return contextFunction;
		if ((contextFunction = XdiValue.fromContextNode(contextNode)) != null) return contextFunction;

		return null;
	}

	/*
	 * Instance methods
	 */

	/**
	 * @param Returns the "base" arc XRI of this XDI subgraph, without multiplicity syntax.
	 * @return The "base" arc XRI.
	 */
	public XDI3SubSegment getBaseArcXri() {
		
		return ContextFunctions.baseArcXri(this.getContextNode().getArcXri());
	}

	/**
	 * Gets or returns an XDI member under a context node.
	 * @param arcXri The "base" arc XRI of the XDI entity singleton, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI entity singleton.
	 */
	public XdiMember getMember(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment memberArcXri = XdiMember.createMemberArcXri(arcXri);
		ContextNode memberContextNode = this.getContextNode().getContextNode(memberArcXri);
		if (memberContextNode == null && create) memberContextNode = this.getContextNode().createContextNode(memberArcXri);
		if (memberContextNode == null) return null;

		return new XdiMember(memberContextNode);
	}

	/**
	 * Gets or returns an XDI attribute singleton under a context node.
	 * @param arcXri The "base" arc XRI of the XDI attribute singleton, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI attribute singleton.
	 */
	public XdiValue getAttributeSingleton(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment attributeSingletonArcXri = ContextFunctions.attributeSingletonArcXri(arcXri);
		ContextNode attributeSingletonContextNode = this.getContextNode().getContextNode(attributeSingletonArcXri);
		if (attributeSingletonContextNode == null && create) attributeSingletonContextNode = this.getContextNode().createContextNode(attributeSingletonArcXri);
		if (attributeSingletonContextNode == null) return null;

		return new XdiValue(attributeSingletonContextNode);
	}
	
	/**
	 * Gets or returns an XDI entity collection under a context node.
	 * @param arcXri The "base" arc XRI of the XDI entity collection, without multiplicity syntax.
	 * @param create Whether or not to create the context node if it doesn't exist.
	 * @return The XDI entity collection.
	 */
	public XdiEntityCollection getEntityCollection(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment entityCollectionArcXri = ContextFunctions.entityCollectionArcXri(arcXri);
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

		XDI3SubSegment attributeCollectionArcXri = ContextFunctions.attributeCollectionArcXri(arcXri);
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

		if (object == null || ! (object instanceof ContextFunction)) return false;
		if (object == this) return true;

		ContextFunction other = (ContextFunction) object;

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
	public int compareTo(ContextFunction other) {

		if (other == null || other == this) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
