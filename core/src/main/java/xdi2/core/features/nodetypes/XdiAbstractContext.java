package xdi2.core.features.nodetypes;

import java.lang.reflect.Method;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

/**
 * An XDI subgraph according to the multiplicity pattern, represented as a context node.
 * 
 * @author markus
 */
public abstract class XdiAbstractContext<EQ extends XdiContext<EQ>> implements XdiContext<EQ> {

	private static final long serialVersionUID = -8756059289169602694L;

	private ContextNode contextNode;

	protected XdiAbstractContext(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI subgraph.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI subgraph.
	 */
	public static boolean isValid(ContextNode contextNode) {

		if (contextNode == null) return false;

		return XdiAbstractRoot.isValid(contextNode) || 
				XdiAbstractSubGraph.isValid(contextNode);
	}

	/**
	 * Factory method that creates a XDI context bound to a given context node.
	 * @param contextNode The context node that is an XDI context.
	 * @return The XDI context.
	 */
	public static XdiContext<?> fromContextNode(ContextNode contextNode) {

		XdiContext<?> xdiContext = null;

		if ((xdiContext = XdiAbstractRoot.fromContextNode(contextNode)) != null) return xdiContext;
		if ((xdiContext = XdiAbstractSubGraph.fromContextNode(contextNode)) != null) return xdiContext;

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends XdiContext<?>> T fromContextNode(ContextNode contextNode, Class<T> t) {

		try {

			Method fromContextNode = t.getMethod("fromContextNode", ContextNode.class);

			return (T) fromContextNode.invoke(null, contextNode);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/**
	 * Returns the "base" arc XRI, without context node type syntax.
	 * @param arcXri The arc XRI of a context node.
	 * @return The "base" arc XRI.
	 */
	public static XDI3SubSegment getBaseArcXri(XDI3SubSegment arcXri) {

		StringBuilder buffer = new StringBuilder();

		if (arcXri.hasCs()) buffer.append(arcXri.getCs());
		if (arcXri.hasLiteral()) buffer.append(arcXri.getLiteral());
		if (arcXri.hasXRef()) buffer.append(arcXri.getXRef());

		return XDI3SubSegment.create(buffer.toString());
	}

	/*
	 * Instance methods
	 */

	@Override
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	@Override
	public Graph getGraph() {

		return this.contextNode.getGraph();
	}

	@Override
	public XDI3Segment getXri() {

		return this.getContextNode().getXri();
	}

	@Override
	public XDI3SubSegment getArcXri() {

		return this.getContextNode().getArcXri();
	}

	/**
	 * Returns the "base" arc XRI, without context node type syntax.
	 * @return The "base" arc XRI.
	 */
	@Override
	public XDI3SubSegment getBaseArcXri() {

		return getBaseArcXri(this.getArcXri());
	}

	@Override
	@SuppressWarnings("unchecked")
	public EQ dereference() {

		EQ xdiContext;

		if ((xdiContext = this.getReferenceXdiContext()) != null) return xdiContext;
		if ((xdiContext = this.getReplacementXdiContext()) != null) return xdiContext;

		return (EQ) this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public EQ getReferenceXdiContext() {

		ContextNode referenceContextNode = Equivalence.getReferenceContextNode(this.getContextNode());
		EQ xdiContext = referenceContextNode == null ? null : (EQ) XdiAbstractContext.fromContextNode(referenceContextNode);

		return xdiContext;
	}

	@Override
	@SuppressWarnings("unchecked")
	public EQ getReplacementXdiContext() {

		ContextNode replacementContextNode = Equivalence.getReplacementContextNode(this.getContextNode());
		EQ xdiContext = replacementContextNode == null ? null : (EQ) XdiAbstractContext.fromContextNode(replacementContextNode);

		return xdiContext;
	}

	@Override
	public Iterator<EQ> getIdentityXdiContexts() {

		Iterator<ContextNode> identityContextNodes = Equivalence.getIdentityContextNodes(this.getContextNode());

		return new MappingIterator<ContextNode, EQ> (identityContextNodes) {

			@Override
			@SuppressWarnings("unchecked")
			public EQ map(ContextNode identityContextNode) {

				EQ xdiContext = identityContextNode == null ? null : (EQ) XdiAbstractContext.fromContextNode(identityContextNode);

				return xdiContext;
			}
		};
	}

	@Override
	public XdiRoot findRoot() {

		return XdiLocalRoot.findLocalRoot(this.getGraph()).findRoot(this.getContextNode().getXri(), false);
	}

	@Override
	public XdiEntityCollection getXdiEntityCollection(XDI3SubSegment arcXri, boolean create) {

		ContextNode entityCollectionContextNode = create ? this.getContextNode().setContextNode(arcXri) : this.getContextNode().getContextNode(arcXri, false);
		if (entityCollectionContextNode == null) return null;

		return XdiEntityCollection.fromContextNode(entityCollectionContextNode);
	}

	@Override
	public XdiEntityCollection getXdiEntityCollection(XDI3Segment xri, boolean create) {

		ContextNode entityCollectionContextNode = create ? this.getContextNode().setDeepContextNode(xri) : this.getContextNode().getDeepContextNode(xri, false);
		if (entityCollectionContextNode == null) return null;

		return XdiEntityCollection.fromContextNode(entityCollectionContextNode);
	}

	@Override
	public XdiAttributeCollection getXdiAttributeCollection(XDI3SubSegment arcXri, boolean create) {

		ContextNode attributeCollectionContextNode = create ? this.getContextNode().setContextNode(arcXri) : this.getContextNode().getContextNode(arcXri, false);
		if (attributeCollectionContextNode == null) return null;

		return XdiAttributeCollection.fromContextNode(attributeCollectionContextNode);
	}

	@Override
	public XdiAttributeCollection getXdiAttributeCollection(XDI3Segment xri, boolean create) {

		ContextNode attributeCollectionContextNode = create ? this.getContextNode().setDeepContextNode(xri) : this.getContextNode().getDeepContextNode(xri, false);
		if (attributeCollectionContextNode == null) return null;

		return XdiAttributeCollection.fromContextNode(attributeCollectionContextNode);
	}

	@Override
	public XdiEntitySingleton getXdiEntitySingleton(XDI3SubSegment arcXri, boolean create) {

		ContextNode entitySingletonContextNode = create ? this.getContextNode().setContextNode(arcXri) : this.getContextNode().getContextNode(arcXri, false);
		if (entitySingletonContextNode == null) return null;

		return XdiEntitySingleton.fromContextNode(entitySingletonContextNode);
	}

	@Override
	public XdiEntitySingleton getXdiEntitySingleton(XDI3Segment xri, boolean create) {

		ContextNode entitySingletonContextNode = create ? this.getContextNode().setDeepContextNode(xri) : this.getContextNode().getDeepContextNode(xri, false);
		if (entitySingletonContextNode == null) return null;

		return XdiEntitySingleton.fromContextNode(entitySingletonContextNode);
	}

	@Override
	public XdiAttributeSingleton getXdiAttributeSingleton(XDI3SubSegment arcXri, boolean create) {

		ContextNode attributeSingletonContextNode = create ? this.getContextNode().setContextNode(arcXri) : this.getContextNode().getContextNode(arcXri, false);
		if (attributeSingletonContextNode == null) return null;

		return XdiAttributeSingleton.fromContextNode(attributeSingletonContextNode);
	}

	@Override
	public XdiAttributeSingleton getXdiAttributeSingleton(XDI3Segment xri, boolean create) {

		ContextNode attributeSingletonContextNode = create ? this.getContextNode().setDeepContextNode(xri) : this.getContextNode().getDeepContextNode(xri, false);
		if (attributeSingletonContextNode == null) return null;

		return XdiAttributeSingleton.fromContextNode(attributeSingletonContextNode);
	}

	@Override
	public XdiEntity getXdiEntity(XDI3SubSegment arcXri, boolean create) {

		ContextNode entityContextNode = create ? this.getContextNode().setContextNode(arcXri) : this.getContextNode().getContextNode(arcXri, false);
		if (entityContextNode == null) return null;

		return XdiAbstractEntity.fromContextNode(entityContextNode);
	}

	@Override
	public XdiEntity getXdiEntity(XDI3Segment xri, boolean create) {

		ContextNode entityContextNode = create ? this.getContextNode().setDeepContextNode(xri) : this.getContextNode().getDeepContextNode(xri, false);
		if (entityContextNode == null) return null;

		return XdiAbstractEntity.fromContextNode(entityContextNode);
	}

	@Override
	public XdiAttribute getXdiAttribute(XDI3SubSegment arcXri, boolean create) {

		ContextNode attributeContextNode = create ? this.getContextNode().setContextNode(arcXri) : this.getContextNode().getContextNode(arcXri, false);
		if (attributeContextNode == null) return null;

		return XdiAbstractAttribute.fromContextNode(attributeContextNode);
	}

	@Override
	public XdiAttribute getXdiAttribute(XDI3Segment xri, boolean create) {

		ContextNode attributeContextNode = create ? this.getContextNode().setDeepContextNode(xri) : this.getContextNode().getDeepContextNode(xri, false);
		if (attributeContextNode == null) return null;

		return XdiAbstractAttribute.fromContextNode(attributeContextNode);
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

		if (object == null || ! (object instanceof XdiContext)) return false;
		if (object == this) return true;

		XdiContext<?> other = (XdiContext<?>) object;

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
	public int compareTo(XdiContext<?> other) {

		if (other == null || other == this) return 0;

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
