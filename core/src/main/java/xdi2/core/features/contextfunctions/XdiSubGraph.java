package xdi2.core.features.contextfunctions;

import java.io.Serializable;

import xdi2.core.ContextNode;
import xdi2.core.features.roots.XdiRoot;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI subgraph, represented as a context node.
 * 
 * @author markus
 */
public abstract class XdiSubGraph implements Serializable, Comparable<XdiSubGraph> {

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

		return
				XdiRoot.isValid(contextNode) ||
				XdiCollection.isValid(contextNode) ||
				XdiElement.isValid(contextNode) ||
				XdiValue.isValid(contextNode) ||
				XdiVariable.isValid(contextNode);
	}

	/**
	 * Factory method that creates a subgraph bound to a given context node.
	 * @param contextNode The context node that is a subgraph.
	 * @return The subgraph.
	 */
	public static XdiSubGraph fromContextNode(ContextNode contextNode) {

		XdiSubGraph xdiSubGraph;

		if ((xdiSubGraph = XdiRoot.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiCollection.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiElement.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiValue.fromContextNode(contextNode)) != null) return xdiSubGraph;
		if ((xdiSubGraph = XdiVariable.fromContextNode(contextNode)) != null) return xdiSubGraph;

		return null;
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
	 * Gets or returns an XDI member under a context node.
	 * @param arcXri The "base" arc XRI of the XDI member, without context function syntax.
	 * @param create Whether or not to create the XDI member if it doesn't exist.
	 * @return The XDI member.
	 */
	public XdiCollection getXdiMember(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment memberArcXri = XdiCollection.createMemberArcXri(arcXri);
		ContextNode memberContextNode = this.getContextNode().getContextNode(memberArcXri);
		if (memberContextNode == null && create) memberContextNode = this.getContextNode().createContextNode(memberArcXri);
		if (memberContextNode == null) return null;

		return new XdiCollection(memberContextNode);
	}

	/**
	 * Gets or returns an XDI value under a context node.
	 * @param arcXri The "base" arc XRI of the XDI value, without context function syntax.
	 * @param create Whether or not to create the XDI value if it doesn't exist.
	 * @return The XDI value.
	 */
	public XdiValue getXdiValue(XDI3SubSegment arcXri, boolean create) {

		XDI3SubSegment valueArcXri = XdiValue.createValueArcXri(arcXri);
		ContextNode valueContextNode = this.getContextNode().getContextNode(valueArcXri);
		if (valueContextNode == null && create) valueContextNode = this.getContextNode().createContextNode(valueArcXri);
		if (valueContextNode == null) return null;

		return new XdiValue(valueContextNode);
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
