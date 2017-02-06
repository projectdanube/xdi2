package xdi2.core.features.nodetypes;

import java.lang.reflect.Method;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.GraphUtil;
import xdi2.core.util.iterators.MappingIterator;

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

		if (contextNode == null) throw new NullPointerException();

		if (XdiCommonVariable.isValid(contextNode)) return true; 
		if (XdiCommonDefinition.isValid(contextNode)) return true; 
		if (XdiAbstractRoot.isValid(contextNode)) return true; 
		if (XdiAbstractSubGraph.isValid(contextNode)) return true;

		return false;
	}

	/**
	 * Factory method that creates a XDI context bound to a given context node.
	 * @param contextNode The context node that is an XDI context.
	 * @return The XDI context.
	 */
	public static XdiContext<?> fromContextNode(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();

		XdiContext<?> xdiContext = null;

		if ((xdiContext = XdiCommonVariable.fromContextNode(contextNode)) != null) return xdiContext;
		if ((xdiContext = XdiCommonDefinition.fromContextNode(contextNode)) != null) return xdiContext;
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

	public static XdiContext<?> fromXDIAddress(XDIAddress XDIaddress) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress));
	}

	public static <T extends XdiContext<?>> T fromXDIAddress(XDIAddress XDIaddress, Class<T> t) {

		return fromContextNode(GraphUtil.contextNodeFromComponents(XDIaddress), t);
	}

	/**
	 * Returns the "base" arc, without context node type syntax.
	 * @param arc The arc of a context node.
	 * @return The "base" arc.
	 */
	public static XDIArc getBaseXDIArc(XDIArc XDIarc) {

		StringBuilder buffer = new StringBuilder();

		if (XDIarc.hasCs()) buffer.append(XDIarc.getCs());
		if (XDIarc.hasLiteral()) buffer.append(XDIarc.getLiteral());
		if (XDIarc.hasXRef()) buffer.append(XDIarc.getXRef());

		return XDIArc.create(buffer.toString());
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
	public XDIAddress getXDIAddress() {

		return this.getContextNode().getXDIAddress();
	}

	@Override
	public XDIArc getXDIArc() {

		return this.getContextNode().getXDIArc();
	}

	/**
	 * Returns the "base" arc, without context node type syntax.
	 * @return The "base" arc.
	 */
	@Override
	public XDIArc getBaseXDIArc() {

		return getBaseXDIArc(this.getXDIArc());
	}

	@Override
	public XdiContext<?> getXdiContext() {

		ContextNode contextNode = this.getContextNode().getContextNode();
		if (contextNode == null) return null;

		return XdiAbstractContext.fromContextNode(contextNode);
	}

	@Override
	@SuppressWarnings("unchecked")
	public EQ dereference(boolean reference, boolean replacement, boolean identity) {

		EQ xdiContext = (EQ) this;

		while (true) {

			if (reference) {

				EQ referenceXdiContext = this.getReferenceXdiContext();
				if (referenceXdiContext != null) { xdiContext = referenceXdiContext; continue; }
			}

			if (replacement) {

				EQ replacementXdiContext = this.getReplacementXdiContext();
				if (replacementXdiContext != null) { xdiContext = replacementXdiContext; continue; }
			}

			if (identity) {

				EQ identityXdiContext = this.getIdentityXdiContext();
				if (identityXdiContext != null) { xdiContext = identityXdiContext; continue; }
			}

			break;
		}

		return xdiContext;
	}

	@Override
	public EQ dereference() {

		return this.dereference(true, true, false);
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
	@SuppressWarnings("unchecked")
	public EQ getIdentityXdiContext() {

		ContextNode identityContextNode = Equivalence.getIdentityContextNode(this.getContextNode());
		EQ xdiContext = identityContextNode == null ? null : (EQ) XdiAbstractContext.fromContextNode(identityContextNode);

		return xdiContext;
	}

	@Override
	public XdiRoot findRoot() {

		return XdiCommonRoot.findCommonRoot(this.getGraph()).getRoot(this.getContextNode().getXDIAddress(), false);
	}

	@Override
	public XdiCommonRoot findLocalRoot() {

		return new XdiCommonRoot(this.getContextNode().getGraph().getRootContextNode(false));
	}

	@Override
	public XdiInnerRoot getXdiInnerRoot(XDIAddress innerRootPredicateAddress, boolean create) {

		XdiRoot xdiRoot = this.findRoot();
		if (xdiRoot == null) return null;

		XDIAddress innerRootSubjectAddress = xdiRoot.absoluteToRelativeXDIAddress(this.getContextNode().getXDIAddress());

		return xdiRoot.getInnerRoot(innerRootSubjectAddress, innerRootPredicateAddress, create);
	}

	@Override
	public XdiEntityCollection getXdiEntityCollection(XDIArc XDIarc, boolean create) {

		ContextNode entityCollectionContextNode = create ? this.getContextNode().setContextNode(XDIarc) : this.getContextNode().getContextNode(XDIarc, false);
		if (entityCollectionContextNode == null) return null;

		return XdiEntityCollection.fromContextNode(entityCollectionContextNode);
	}

	@Override
	public XdiEntityCollection getXdiEntityCollection(XDIAddress XDIaddress, boolean create) {

		if (XDIaddress.isLiteralNodeXDIAddress()) return null;

		ContextNode entityCollectionContextNode = create ? (ContextNode) this.getContextNode().setDeepNode(XDIaddress) : (ContextNode) this.getContextNode().getDeepNode(XDIaddress, false);
		if (entityCollectionContextNode == null) return null;

		return XdiEntityCollection.fromContextNode(entityCollectionContextNode);
	}

	@Override
	public XdiAttributeCollection getXdiAttributeCollection(XDIArc XDIarc, boolean create) {

		ContextNode attributeCollectionContextNode = create ? this.getContextNode().setContextNode(XDIarc) : this.getContextNode().getContextNode(XDIarc, false);
		if (attributeCollectionContextNode == null) return null;

		return XdiAttributeCollection.fromContextNode(attributeCollectionContextNode);
	}

	@Override
	public XdiAttributeCollection getXdiAttributeCollection(XDIAddress XDIaddress, boolean create) {

		if (XDIaddress.isLiteralNodeXDIAddress()) return null;

		ContextNode attributeCollectionContextNode = create ? (ContextNode) this.getContextNode().setDeepNode(XDIaddress) : (ContextNode) this.getContextNode().getDeepNode(XDIaddress, false);
		if (attributeCollectionContextNode == null) return null;

		return XdiAttributeCollection.fromContextNode(attributeCollectionContextNode);
	}

	@Override
	public XdiEntitySingleton getXdiEntitySingleton(XDIArc XDIarc, boolean create) {

		ContextNode entitySingletonContextNode = create ? this.getContextNode().setContextNode(XDIarc) : this.getContextNode().getContextNode(XDIarc, false);
		if (entitySingletonContextNode == null) return null;

		return XdiEntitySingleton.fromContextNode(entitySingletonContextNode);
	}

	@Override
	public XdiEntitySingleton getXdiEntitySingleton(XDIAddress XDIaddress, boolean create) {

		if (XDIaddress.isLiteralNodeXDIAddress()) return null;

		ContextNode entitySingletonContextNode = create ? (ContextNode) this.getContextNode().setDeepNode(XDIaddress) : (ContextNode) this.getContextNode().getDeepNode(XDIaddress, false);
		if (entitySingletonContextNode == null) return null;

		return XdiEntitySingleton.fromContextNode(entitySingletonContextNode);
	}

	@Override
	public XdiAttributeSingleton getXdiAttributeSingleton(XDIArc XDIarc, boolean create) {

		ContextNode attributeSingletonContextNode = create ? this.getContextNode().setContextNode(XDIarc) : this.getContextNode().getContextNode(XDIarc, false);
		if (attributeSingletonContextNode == null) return null;

		return XdiAttributeSingleton.fromContextNode(attributeSingletonContextNode);
	}

	@Override
	public XdiAttributeSingleton getXdiAttributeSingleton(XDIAddress XDIaddress, boolean create) {

		if (XDIaddress.isLiteralNodeXDIAddress()) return null;

		ContextNode attributeSingletonContextNode = create ? (ContextNode) this.getContextNode().setDeepNode(XDIaddress) : (ContextNode) this.getContextNode().getDeepNode(XDIaddress, false);
		if (attributeSingletonContextNode == null) return null;

		return XdiAttributeSingleton.fromContextNode(attributeSingletonContextNode);
	}

	@Override
	public XdiEntity getXdiEntity(XDIArc XDIarc, boolean create) {

		ContextNode entityContextNode = create ? this.getContextNode().setContextNode(XDIarc) : this.getContextNode().getContextNode(XDIarc, false);
		if (entityContextNode == null) return null;

		return XdiAbstractEntity.fromContextNode(entityContextNode);
	}

	@Override
	public XdiEntity getXdiEntity(XDIAddress XDIaddress, boolean create) {

		if (XDIaddress.isLiteralNodeXDIAddress()) return null;

		ContextNode entityContextNode = create ? (ContextNode) this.getContextNode().setDeepNode(XDIaddress) : (ContextNode) this.getContextNode().getDeepNode(XDIaddress, false);
		if (entityContextNode == null) return null;

		return XdiAbstractEntity.fromContextNode(entityContextNode);
	}

	@Override
	public XdiAttribute getXdiAttribute(XDIArc XDIarc, boolean create) {

		ContextNode attributeContextNode = create ? this.getContextNode().setContextNode(XDIarc) : this.getContextNode().getContextNode(XDIarc, false);
		if (attributeContextNode == null) return null;

		return XdiAbstractAttribute.fromContextNode(attributeContextNode);
	}

	@Override
	public XdiAttribute getXdiAttribute(XDIAddress XDIaddress, boolean create) {

		if (XDIaddress.isLiteralNodeXDIAddress()) return null;

		ContextNode attributeContextNode = create ? (ContextNode) this.getContextNode().setDeepNode(XDIaddress) : (ContextNode) this.getContextNode().getDeepNode(XDIaddress, false);
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
