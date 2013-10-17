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
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class MemoryContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 4930852359817860369L;

	private XDI3SubSegment arcXri;

	private Map<XDI3SubSegment, MemoryContextNode> contextNodes;
	private Map<XDI3Segment, Map<XDI3Segment, MemoryRelation>> relations;
	private MemoryLiteral literal;

	MemoryContextNode(MemoryGraph graph, MemoryContextNode contextNode, XDI3SubSegment arcXri) {

		super(graph, contextNode);

		this.arcXri = arcXri;

		if (graph.getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

			this.contextNodes = new TreeMap<XDI3SubSegment, MemoryContextNode> ();
			this.relations = new TreeMap<XDI3Segment, Map<XDI3Segment, MemoryRelation>> ();
			this.literal = null;
		} else if (graph.getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

			this.contextNodes = new LinkedHashMap<XDI3SubSegment, MemoryContextNode> ();
			this.relations = new LinkedHashMap<XDI3Segment, Map<XDI3Segment, MemoryRelation>> ();
			this.literal = null;
		} else {

			this.contextNodes = new HashMap<XDI3SubSegment, MemoryContextNode> ();
			this.relations = new HashMap<XDI3Segment, Map<XDI3Segment, MemoryRelation>> ();
			this.literal = null;
		}
	}

	@Override
	public XDI3SubSegment getArcXri() {

		return this.arcXri;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	@Override
	public synchronized ContextNode setContextNode(XDI3SubSegment arcXri) {

		// check validity

		this.setContextNodeCheckValid(arcXri);

		// set the context node

		ContextNode contextNode = this.contextNodes.get(arcXri);

		if (contextNode != null) {
			
			return contextNode;
		}

		contextNode = new MemoryContextNode((MemoryGraph) this.getGraph(), this, arcXri);

		this.contextNodes.put(arcXri, (MemoryContextNode) contextNode);

		// set inner root

		this.setContextNodeSetInnerRoot(arcXri, contextNode);

		// done

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(XDI3SubSegment arcXri) {

		return this.contextNodes.get(arcXri);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		List<ContextNode> list = new ArrayList<ContextNode> (this.contextNodes.values());

		return new ReadOnlyIterator<ContextNode> (list.iterator());
	}

	@Override
	public boolean containsContextNode(XDI3SubSegment arcXri) {

		return this.contextNodes.containsKey(arcXri);
	}

	@Override
	public boolean containsContextNodes() {

		return ! this.contextNodes.isEmpty();
	}

	@Override
	public synchronized void delContextNode(XDI3SubSegment arcXri) {

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) return;

		// delete all inner roots and incoming relations

		((MemoryContextNode) contextNode).delContextNodeDelAllInnerRoots();
		((MemoryContextNode) contextNode).delContextNodeDelAllIncomingRelations();

		// delete this context node

		this.contextNodes.remove(arcXri);
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
	public synchronized Relation setRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		XDI3Segment targetContextNodeXri = targetContextNode.getXri();
		
		// check validity

		this.setRelationCheckValid(arcXri, targetContextNodeXri);

		// set the relation

		Relation relation = this.getRelation(arcXri, targetContextNodeXri);
		if (relation != null) return relation;

		Map<XDI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) {

			if (((MemoryGraph) this.getGraph()).getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

				relations = new TreeMap<XDI3Segment, MemoryRelation> ();
			} else if (((MemoryGraph) this.getGraph()).getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

				relations = new LinkedHashMap<XDI3Segment, MemoryRelation> ();
			} else {

				relations = new HashMap<XDI3Segment, MemoryRelation> ();
			}

			this.relations.put(arcXri, relations);
		}
		
		relation = new MemoryRelation(this, arcXri, targetContextNodeXri);
		
		relations.put(targetContextNodeXri, (MemoryRelation) relation);

		// done

		return relation;
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		Map<XDI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) return null;

		return relations.get(targetContextNodeXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(XDI3Segment arcXri) {

		Map<XDI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) return new EmptyIterator<Relation> ();

		List<Relation> list = new ArrayList<Relation> (relations.values());

		return new ReadOnlyIterator<Relation> (list.iterator());
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		List<Relation> list = new ArrayList<Relation> ();

		for (Entry<XDI3Segment, Map<XDI3Segment, MemoryRelation>> relations : this.relations.entrySet()) {

			list.addAll(relations.getValue().values());
		}

		return new ReadOnlyIterator<Relation> (list.iterator());
	}

	@Override
	public boolean containsRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		Map<XDI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) return false;

		return relations.containsKey(targetContextNodeXri);
	}

	@Override
	public boolean containsRelations(XDI3Segment arcXri) {

		return this.relations.containsKey(arcXri);
	}

	@Override
	public boolean containsRelations() {

		return ! this.relations.isEmpty();
	}

	@Override
	public synchronized void delRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		// delete the relation

		Map<XDI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) return;

		MemoryRelation relation = relations.remove(targetContextNodeXri);
		if (relation == null) return;

		if (relations.isEmpty()) {

			this.relations.remove(arcXri);
		}

		// delete inner root

		this.delRelationDelInnerRoot(arcXri, targetContextNodeXri);
	}

	@Override
	public synchronized void delRelations(XDI3Segment arcXri) {

		ReadOnlyIterator<Relation> relations = this.getRelations(arcXri);

		// delete relations

		this.relations.remove(arcXri);

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getArcXri(), relation.getTargetContextNodeXri());
		}
	}

	@Override
	public synchronized void delRelations() {

		ReadOnlyIterator<Relation> relations = this.getRelations();

		// delete relations

		this.relations.clear();

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getArcXri(), relation.getTargetContextNodeXri());
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
