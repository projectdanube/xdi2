package xdi2.core.impl.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

public class MemoryContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 4930852359817860369L;

	private XDIArc arc;

	private Map<XDIArc, MemoryContextNode> contextNodes;
	private Map<XDIAddress, Map<XDIAddress, MemoryRelation>> relations;
	private MemoryLiteral literal;

	MemoryContextNode(MemoryGraph graph, MemoryContextNode contextNode, XDIArc arc) {

		super(graph, contextNode);

		this.arc = arc;

		if (graph.getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

			this.contextNodes = new TreeMap<XDIArc, MemoryContextNode> ();
			this.relations = new TreeMap<XDIAddress, Map<XDIAddress, MemoryRelation>> ();
			this.literal = null;
		} else if (graph.getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

			this.contextNodes = new LinkedHashMap<XDIArc, MemoryContextNode> ();
			this.relations = new LinkedHashMap<XDIAddress, Map<XDIAddress, MemoryRelation>> ();
			this.literal = null;
		} else {

			this.contextNodes = new HashMap<XDIArc, MemoryContextNode> ();
			this.relations = new HashMap<XDIAddress, Map<XDIAddress, MemoryRelation>> ();
			this.literal = null;
		}
	}

	@Override
	public XDIArc getArc() {

		return this.arc;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	@Override
	public synchronized ContextNode setContextNode(XDIArc arc) {

		// check validity

		this.setContextNodeCheckValid(arc);

		// set the context node

		ContextNode contextNode = this.contextNodes.get(arc);

		if (contextNode != null) {
			
			return contextNode;
		}

		contextNode = new MemoryContextNode((MemoryGraph) this.getGraph(), this, arc);

		this.contextNodes.put(arc, (MemoryContextNode) contextNode);

		// set inner root

		this.setContextNodeSetInnerRoot(arc, contextNode);

		// done

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(XDIArc arc, boolean subgraph) {

		return this.contextNodes.get(arc);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		List<ContextNode> list = new ArrayList<ContextNode> (this.contextNodes.values());

		return new ReadOnlyIterator<ContextNode> (list.iterator());
	}

	@Override
	public boolean containsContextNode(XDIArc arc) {

		return this.contextNodes.containsKey(arc);
	}

	@Override
	public boolean containsContextNodes() {

		return ! this.contextNodes.isEmpty();
	}

	@Override
	public synchronized void delContextNode(XDIArc arc) {

		ContextNode contextNode = this.getContextNode(arc, true);
		if (contextNode == null) return;

		// delete all inner roots and incoming relations

		((MemoryContextNode) contextNode).delContextNodeDelAllInnerRoots();
		((MemoryContextNode) contextNode).delContextNodeDelAllIncomingRelations();

		// delete this context node

		this.contextNodes.remove(arc);
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
	public synchronized Relation setRelation(XDIAddress arc, ContextNode targetContextNode) {

		XDIAddress targetContextNodeAddress = targetContextNode.getAddress();
		
		// check validity

		this.setRelationCheckValid(arc, targetContextNodeAddress);

		// set the relation

		Relation relation = this.getRelation(arc, targetContextNodeAddress);
		if (relation != null) return relation;

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(arc);
		if (relations == null) {

			if (((MemoryGraph) this.getGraph()).getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

				relations = new TreeMap<XDIAddress, MemoryRelation> ();
			} else if (((MemoryGraph) this.getGraph()).getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

				relations = new LinkedHashMap<XDIAddress, MemoryRelation> ();
			} else {

				relations = new HashMap<XDIAddress, MemoryRelation> ();
			}

			this.relations.put(arc, relations);
		}
		
		relation = new MemoryRelation(this, arc, targetContextNodeAddress);
		
		relations.put(targetContextNodeAddress, (MemoryRelation) relation);

		// done

		return relation;
	}

	@Override
	public Relation getRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(arc);
		if (relations == null) return null;

		return relations.get(targetContextNodeAddress);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(XDIAddress arc) {

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(arc);
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
	public boolean containsRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(arc);
		if (relations == null) return false;

		return relations.containsKey(targetContextNodeAddress);
	}

	@Override
	public boolean containsRelations(XDIAddress arc) {

		return this.relations.containsKey(arc);
	}

	@Override
	public boolean containsRelations() {

		return ! this.relations.isEmpty();
	}

	@Override
	public synchronized void delRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		// delete the relation

		Map<XDIAddress, MemoryRelation> relations = this.relations.get(arc);
		if (relations == null) return;

		MemoryRelation relation = relations.remove(targetContextNodeAddress);
		if (relation == null) return;

		if (relations.isEmpty()) {

			this.relations.remove(arc);
		}

		// delete inner root

		this.delRelationDelInnerRoot(arc, targetContextNodeAddress);
	}

	@Override
	public synchronized void delRelations(XDIAddress arc) {

		ReadOnlyIterator<Relation> relations = this.getRelations(arc);

		// delete relations

		this.relations.remove(arc);

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getAddress(), relation.getTargetContextNodeAddress());
		}
	}

	@Override
	public synchronized void delRelations() {

		ReadOnlyIterator<Relation> relations = this.getRelations();

		// delete relations

		this.relations.clear();

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getAddress(), relation.getTargetContextNodeAddress());
		}
	}

	/*
	 * Methods related to literals of this context node
	 */

	@Override
	public synchronized Literal setLiteral(Object literalData) {

		// check validity

		this.setLiteralCheckValid(literalData);

		// set the literal

		this.literal = new MemoryLiteral(this, literalData);

		// done

		return this.literal;
	}

	@Override
	public Literal getLiteral() {

		return this.literal;
	}

	@Override
	public boolean containsLiteral() {

		return this.literal != null;
	}

	@Override
	public synchronized void delLiteral() {

		this.literal = null;
	}
}
