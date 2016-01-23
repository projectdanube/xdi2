package xdi2.core.impl.wrapped;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.Node;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.memory.MemoryContextNode;
import xdi2.core.impl.memory.MemoryLiteralNode;
import xdi2.core.impl.memory.MemoryRelation;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

public class WrappedContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 4930852359817860369L;

	private MemoryContextNode memoryContextNode;

	WrappedContextNode(WrappedGraph graph, WrappedContextNode contextNode, MemoryContextNode memoryContextNode) {

		super(graph, contextNode);

		this.memoryContextNode = memoryContextNode;
	}

	@Override
	public XDIArc getXDIArc() {

		return this.memoryContextNode.getXDIArc();
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	@Override
	public synchronized ContextNode setContextNode(XDIArc XDIarc) {

		MemoryContextNode ret = (MemoryContextNode) this.memoryContextNode.setContextNode(XDIarc);

		return new WrappedContextNode((WrappedGraph) this.getGraph(), this, ret);
	}

	@Override
	public ContextNode getContextNode(XDIArc XDIarc, boolean subgraph) {

		MemoryContextNode ret = (MemoryContextNode) this.memoryContextNode.getContextNode(XDIarc, subgraph);

		return ret == null ? null : new WrappedContextNode((WrappedGraph) this.getGraph(), this, ret);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		ReadOnlyIterator<ContextNode> ret = this.memoryContextNode.getContextNodes();

		return new ReadOnlyIterator<ContextNode> (new WrappedContextNodeMappingIterator(ret));
	}

	@Override
	public boolean containsContextNode(XDIArc XDIarc) {

		return this.memoryContextNode.containsContextNode(XDIarc);
	}

	@Override
	public boolean containsContextNodes() {

		return this.memoryContextNode.containsContextNodes();
	}

	@Override
	public synchronized void delContextNode(XDIArc XDIarc) {

		this.memoryContextNode.delContextNode(XDIarc);
	}

	@Override
	public synchronized void delContextNodes() {

		this.memoryContextNode.delContextNodes();
	}

	/*
	 * Methods related to relations of this context node
	 */

	@Override
	public synchronized Relation setRelation(XDIAddress XDIaddress, Node targetNode) {

		MemoryRelation ret = (MemoryRelation) this.memoryContextNode.setRelation(XDIaddress, targetNode);

		return new WrappedRelation(this, ret);
	}

	@Override
	public Relation getRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		MemoryRelation ret = (MemoryRelation) this.memoryContextNode.getRelation(XDIaddress, targetXDIAddress);

		return ret == null ? null : new WrappedRelation(this, ret);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(XDIAddress XDIaddress) {

		ReadOnlyIterator<Relation> ret = this.memoryContextNode.getRelations(XDIaddress);

		return new ReadOnlyIterator<Relation> (new WrappedRelationMappingIterator(ret));
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		ReadOnlyIterator<Relation> ret = this.memoryContextNode.getRelations();

		return new ReadOnlyIterator<Relation> (new WrappedRelationMappingIterator(ret));
	}

	@Override
	public boolean containsRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		return this.memoryContextNode.containsRelation(XDIaddress, targetXDIAddress);
	}

	@Override
	public boolean containsRelations(XDIAddress XDIaddress) {

		return this.memoryContextNode.containsRelations(XDIaddress);
	}

	@Override
	public boolean containsRelations() {

		return this.memoryContextNode.containsRelations();
	}

	@Override
	public synchronized void delRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		this.memoryContextNode.delRelation(XDIaddress, targetXDIAddress);
	}

	@Override
	public synchronized void delRelations(XDIAddress XDIaddress) {

		this.memoryContextNode.delRelations(XDIaddress);
	}

	@Override
	public synchronized void delRelations() {

		this.memoryContextNode.delRelations();
	}

	/*
	 * Methods related to literals of this context node
	 */

	@Override
	public synchronized LiteralNode setLiteralNode(Object literalData) {

		MemoryLiteralNode ret = (MemoryLiteralNode) this.memoryContextNode.setLiteralNode(literalData);

		return ret == null ? null : new WrappedLiteralNode(this, ret);
	}

	@Override
	public LiteralNode getLiteralNode() {

		MemoryLiteralNode ret = (MemoryLiteralNode) this.memoryContextNode.getLiteralNode();

		return ret == null ? null : new WrappedLiteralNode(this, ret);
	}

	@Override
	public boolean containsLiteralNode() {

		return this.memoryContextNode.containsLiteralNode();
	}

	@Override
	public synchronized void delLiteralNode() {

		this.memoryContextNode.delLiteralNode();
	}

	private class WrappedContextNodeMappingIterator extends MappingIterator<ContextNode, ContextNode> {

		public WrappedContextNodeMappingIterator(Iterator<ContextNode> iterator) {

			super(iterator);
		}

		@Override
		public ContextNode map(ContextNode memoryContextNode) {

			return new WrappedContextNode((WrappedGraph) WrappedContextNode.this.getGraph(), WrappedContextNode.this, (MemoryContextNode) memoryContextNode);
		}
	}

	private class WrappedRelationMappingIterator extends MappingIterator<Relation, Relation> {

		public WrappedRelationMappingIterator(Iterator<Relation> iterator) {

			super(iterator);
		}

		@Override
		public Relation map(Relation memoryRelation) {

			return new WrappedRelation(WrappedContextNode.this, (MemoryRelation) memoryRelation);
		}
	}
}
