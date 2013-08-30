package xdi2.core.impl.memory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.iterators.CastingIterator;
import xdi2.core.util.iterators.DescendingIterator;
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

		this.checkContextNode(arcXri);

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode != null) return contextNode;

		contextNode = new MemoryContextNode((MemoryGraph) this.getGraph(), this, arcXri);

		this.contextNodes.put(arcXri, (MemoryContextNode) contextNode);

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(XDI3SubSegment arcXri) {

		return this.contextNodes.get(arcXri);
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		return new ReadOnlyIterator<ContextNode> (new CastingIterator<MemoryContextNode, ContextNode> (this.contextNodes.values().iterator()));
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

		// delete incoming relations

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) return;

		for (Iterator<Relation> relations = contextNode.getIncomingRelations(); relations.hasNext(); ) relations.next().delete();

		// delete this context node

		this.contextNodes.remove(arcXri);
	}

	@Override
	public synchronized void delContextNodes() {

		// delete incoming relations

		for (Iterator<ContextNode> contextNodes = this.getContextNodes(); contextNodes.hasNext(); )
			for (Iterator<Relation> relations = contextNodes.next().getIncomingRelations(); relations.hasNext(); ) 
				relations.next().delete();

		// delete context nodes

		this.contextNodes.clear();
	}

	/*
	 * Methods related to relations of this context node
	 */

	@Override
	public synchronized Relation setRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		this.checkRelation(arcXri, targetContextNode);

		XDI3Segment targetContextNodeXri = targetContextNode.getXri();

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

		return new ReadOnlyIterator<Relation> (new CastingIterator<MemoryRelation, Relation> (relations.values().iterator()));
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		Iterator<MemoryRelation> descendingIterator = new DescendingIterator<Entry<XDI3Segment, Map<XDI3Segment, MemoryRelation>>, MemoryRelation> (this.relations.entrySet().iterator()) {

			@Override
			public Iterator<MemoryRelation> descend(Entry<XDI3Segment, Map<XDI3Segment, MemoryRelation>> item) {

				return item.getValue().values().iterator();
			}
		};

		return new ReadOnlyIterator<Relation> (new CastingIterator<MemoryRelation, Relation> (descendingIterator));
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

		Map<XDI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) return;

		relations.remove(targetContextNodeXri);

		if (relations.isEmpty()) {

			this.relations.remove(arcXri);
		}
	}

	@Override
	public synchronized void delRelations(XDI3Segment arcXri) {

		this.relations.remove(arcXri);
	}

	@Override
	public synchronized void delRelations() {

		this.relations.clear();
	}

	/*
	 * Methods related to literals of this context node
	 */

	@Override
	public synchronized Literal setLiteral(Object literalData) {

		this.checkLiteral(literalData);

		Literal literal = this.getLiteral(literalData);
		if (literal != null) return literal;

		literal = new MemoryLiteral(this, literalData);
	
		this.literal = (MemoryLiteral) literal;
		
		return literal;
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
