package xdi2.core.features.nodetypes;

import java.lang.reflect.Method;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.equivalence.Equivalence;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.XDIStatementUtil;
import xdi2.core.util.iterators.MappingIterator;

public abstract class XdiAbstractContext<EQ extends XdiContext<EQ>> implements XdiContext<EQ> {

	private static final long serialVersionUID = -8756059289169602694L;

	private static final Logger log = LoggerFactory.getLogger(XdiAbstractContext.class);

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
	 * General methods
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
	public Graph toGraph() {

		Graph graph = MemoryGraphFactory.getInstance().openGraph();

		return graph;
	}

	@Override
	public XDIAddress getXDIAddress() {

		return this.getContextNode().getXDIAddress();
	}

	@Override
	public XDIArc getXDIArc() {

		return this.getContextNode().getXDIArc();
	}

	@Override
	public XDIArc getBaseXDIArc() {

		return getBaseXDIArc(this.getXDIArc());
	}

	/*
	 * Equivalence relations
	 */

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

	/*
	 * Finding roots
	 */

	@Override
	public XdiRoot findRoot() {

		return XdiCommonRoot.findCommonRoot(this.getGraph()).getRoot(this.getContextNode().getXDIAddress(), false);
	}

	@Override
	public XdiCommonRoot findCommonRoot() {

		return new XdiCommonRoot(this.getContextNode().getGraph().getRootContextNode(false));
	}

	/*
	 * Getting contexts under this context
	 */

	@Override
	public XdiInnerRoot getXdiInnerRoot(XDIAddress innerRootPredicateAddress, boolean create) {

		XdiRoot xdiRoot = this.findRoot();

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

		ContextNode entityCollectionContextNode = create ? this.getContextNode().setDeepContextNode(XDIaddress) : this.getContextNode().getDeepContextNode(XDIaddress, false);
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

		ContextNode attributeCollectionContextNode = create ? this.getContextNode().setDeepContextNode(XDIaddress) : this.getContextNode().getDeepContextNode(XDIaddress, false);
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

		ContextNode entitySingletonContextNode = create ? this.getContextNode().setDeepContextNode(XDIaddress) : this.getContextNode().getDeepContextNode(XDIaddress, false);
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

		ContextNode attributeSingletonContextNode = create ? this.getContextNode().setDeepContextNode(XDIaddress) : this.getContextNode().getDeepContextNode(XDIaddress, false);
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

		ContextNode entityContextNode = create ? this.getContextNode().setDeepContextNode(XDIaddress) : this.getContextNode().getDeepContextNode(XDIaddress, false);
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

		ContextNode attributeContextNode = create ? this.getContextNode().setDeepContextNode(XDIaddress) : this.getContextNode().getDeepContextNode(XDIaddress, false);
		if (attributeContextNode == null) return null;

		return XdiAbstractAttribute.fromContextNode(attributeContextNode);
	}

	/*
	 * Addresses and statements relative to this context
	 */

	@Override
	public XDIAddress absoluteToRelativeXDIAddress(XDIAddress absoluteAddress) {

		XDIAddress relativeAddress = XDIAddressUtil.removeStartXDIAddress(absoluteAddress, this.getContextNode().getXDIAddress());

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeAddress(" + absoluteAddress + " --> " + relativeAddress + ")");

		return relativeAddress;
	}

	@Override
	public XDIAddress relativeToAbsoluteXDIAddress(XDIAddress relativeAddress) {

		XDIAddress absoluteAddress = XDIAddressUtil.concatXDIAddresses(this.getContextNode().getXDIAddress(), relativeAddress);

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteAddress(" + relativeAddress + " --> " + absoluteAddress + ")");

		return absoluteAddress;
	}

	@Override
	public XDIStatement absoluteToRelativeXDIStatement(XDIStatement absoluteStatementAddress) {

		XDIStatement relativeStatementAddress = XDIStatementUtil.removeStartXDIStatement(absoluteStatementAddress, this.getContextNode().getXDIAddress());

		if (log.isTraceEnabled()) log.trace("absoluteToRelativeStatementAddress(" + absoluteStatementAddress + " --> " + relativeStatementAddress + ")");

		return relativeStatementAddress;
	}

	@Override
	public XDIStatement relativeToAbsoluteXDIStatement(XDIStatement relativeStatementAddress) {

		XDIStatement absoluteStatementAddress = XDIStatementUtil.concatXDIStatement(this.getContextNode().getXDIAddress(), relativeStatementAddress);

		if (log.isTraceEnabled()) log.trace("relativeToAbsoluteStatementAddress(" + relativeStatementAddress + " --> " + absoluteStatementAddress + ")");

		return absoluteStatementAddress;
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
