package xdi2.core.impl.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.Node;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

public class MemoryContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 4930852359817860369L;

	private XDIArc XDIarc;

	private Map<XDIArc, MemoryContextNode> contextNodes;
	private Map<XDIAddress, Map<XDIAddress, MemoryRelation>> relations;
	private MemoryLiteralNode literalNode;

	MemoryContextNode(MemoryGraph graph, MemoryContextNode contextNode, XDIArc XDIarc) {

		super(graph, contextNode);

		this.XDIarc = XDIarc;

		if (graph.getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

			this.contextNodes = new TreeMap<XDIArc, MemoryContextNode> ();
			this.relations = new TreeMap<XDIAddress, Map<XDIAddress, MemoryRelation>> ();
			this.literalNode = null;
		} else if (graph.getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

			this.contextNodes = new LinkedHashMap<XDIArc, MemoryContextNode> ();
			this.relations = new LinkedHashMap<XDIAddress, Map<XDIAddress, MemoryRelation>> ();
			this.literalNode = null;
		} else {

			this.contextNodes = new HashMap<XDIArc, MemoryContextNode> ();
			this.relations = new HashMap<XDIAddress, Map<XDIAddress, MemoryRelation>> ();
			this.literalNode = null;
		}
	}

	@Override
	public XDIArc getXDIArc() {

		return this.XDIarc;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	@Override
	public synchronized ContextNode setContextNode(XDIArc XDIarc) {

		// check validity

		this.setContextNodeCheckValid(XDIarc);

		// set the context node

		ContextNode contextNode = this.contextNodes.get(XDIarc);

		if (contextNode != null) {

			return contextNode;
		}

		contextNode = new MemoryContextNode((MemoryGraph) this.getGraph(), this, XDIarc);

		this.contextNodes.put(XDIarc, (MemoryContextNode) contextNode);

		// set inner root

		this.setContextNodeSetInnerRoot(XDIarc, contextNode);

		// done

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(XDIArc XDIarc, boolean subgraph) {

		return this.contextNodes.get(XDIarc);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		List<ContextNode> list = new ArrayList<ContextNode> (this.contextNodes.values());

		return new ReadOnlyIterator<ContextNode> (list.iterator());
	}

	@Override
	public boolean containsContextNode(XDIArc XDIarc) {

		return this.contextNodes.containsKey(XDIarc);
	}

	@Override
	public boolean containsContextNodes() {

		return ! this.contextNodes.isEmpty();
	}

	@Override
	public synchronized void delContextNode(XDIArc XDIarc) {

		ContextNode contextNode = this.getContextNode(XDIarc, true);
		if (contextNode == null) return;

		// delete all inner roots and incoming relations

		((MemoryContextNode) contextNode).delContextNodeDelAllInnerRoots();
		((MemoryContextNode) contextNode).delContextNodeDelAllIncomingRelations();

		// delete this context node

		this.contextNodes.remove(XDIarc);
	}

	@Override
	public synchronized void delContextNodes() {

		// delete all relations and incoming relations

		for (ContextNode contextNode : this.getContextNodes()) {

			for (Relation relation : contextNode.getAllRelations()) relation.delete();
			for (Relation relation : contextNode.getAllIncomingRelations()) relation.delete();
		}

		// delete context nodes

		this.contextNodes.clear();
	}

	/*
	 * Methods related to relations of this context node
	 */

	@Override
	public synchronized Relation setRelation(XDIAddress XDIaddress, Node targetNode) {

		XDIAddress targetXDIAddress = targetNode.getXDIAddress();

		// check validity

		this.setRelationCheckValid(XDIaddress, targetXDIAddress);

		// set the relation

		Relation relation = this.getRelation(XDIaddress, targetXDIAddress);
		if (relation != null) return relation;

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(XDIaddress);
		if (relations == null) {

			if (((MemoryGraph) this.getGraph()).getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

				relations = new TreeMap<XDIAddress, MemoryRelation> ();
			} else if (((MemoryGraph) this.getGraph()).getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

				relations = new LinkedHashMap<XDIAddress, MemoryRelation> ();
			} else {

				relations = new HashMap<XDIAddress, MemoryRelation> ();
			}

			this.relations.put(XDIaddress, relations);
		}

		relation = new MemoryRelation(this, XDIaddress, targetXDIAddress);

		relations.put(targetXDIAddress, (MemoryRelation) relation);

		// done

		return relation;
	}

	@Override
	public Relation getRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(XDIaddress);
		if (relations == null) return null;

		return relations.get(targetXDIAddress);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(XDIAddress XDIaddress) {

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(XDIaddress);
		if (relations == null) return new EmptyIterator<Relation> ();

		List<Relation> list = new ArrayList<Relation> (relations.values());

		return new ReadOnlyIterator<Relation> (list.iterator());
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		List<Relation> list = new ArrayList<Relation> ();

		for (Entry<XDIAddress, Map<XDIAddress, MemoryRelation>> relations : this.relations.entrySet()) {

			list.addAll(relations.getValue().values());
		}

		return new ReadOnlyIterator<Relation> (list.iterator());
	}

	@Override
	public boolean containsRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(XDIaddress);
		if (relations == null) return false;

		return relations.containsKey(targetXDIAddress);
	}

	@Override
	public boolean containsRelations(XDIAddress XDIaddress) {

		return this.relations.containsKey(XDIaddress);
	}

	@Override
	public boolean containsRelations() {

		return ! this.relations.isEmpty();
	}

	@Override
	public synchronized void delRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		// delete the relation

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(XDIaddress);
		if (relations == null) return;

		MemoryRelation relation = relations.remove(targetXDIAddress);
		if (relation == null) return;

		if (relations.isEmpty()) {

			this.relations.remove(XDIaddress);
		}

		// delete inner root

		this.delRelationDelInnerRoot(XDIaddress, targetXDIAddress);
	}

	@Override
	public synchronized void delRelations(XDIAddress XDIaddress) {

		ReadOnlyIterator<Relation> relations = this.getRelations(XDIaddress);

		// delete relations

		this.relations.remove(XDIaddress);

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getXDIAddress(), relation.getTargetXDIAddress());
		}
	}

	@Override
	public synchronized void delRelations() {

		ReadOnlyIterator<Relation> relations = this.getRelations();

		// delete relations

		this.relations.clear();

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getXDIAddress(), relation.getTargetXDIAddress());
		}
	}

	/*
	 * Methods related to literals of this context node
	 */

	@Override
	public synchronized LiteralNode setLiteralNode(Object literalData) {

		// check validity

		this.setLiteralCheckValid(literalData);

		// set the literal

		this.literalNode = new MemoryLiteralNode(this, literalData);

		// done

		return this.literalNode;
	}

	@Override
	public LiteralNode getLiteralNode() {

		return this.literalNode;
	}

	@Override
	public boolean containsLiteralNode() {

		return this.literalNode != null;
	}

	@Override
	public synchronized void delLiteralNode() {

		this.literalNode = null;
	}
}
