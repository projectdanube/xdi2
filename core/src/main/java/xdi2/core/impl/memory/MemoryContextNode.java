package xdi2.core.impl.memory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.util.iterators.CastingIterator;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public class MemoryContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 4930852359817860369L;

	XRI3SubSegment arcXri;

	private Map<XRI3SubSegment, MemoryContextNode> contextNodes;
	private Map<XRI3Segment, Map<XRI3Segment, MemoryRelation>> relations;
	private MemoryLiteral literal;

	MemoryContextNode(Graph graph, ContextNode contextNode) {

		super(graph, contextNode);

		if (((MemoryGraph) graph).getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

			this.contextNodes = new TreeMap<XRI3SubSegment, MemoryContextNode> ();
			this.relations = new TreeMap<XRI3Segment, Map<XRI3Segment, MemoryRelation>> ();
			this.literal = null;
		} else if (((MemoryGraph) graph).getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

			this.contextNodes = new LinkedHashMap<XRI3SubSegment, MemoryContextNode> ();
			this.relations = new LinkedHashMap<XRI3Segment, Map<XRI3Segment, MemoryRelation>> ();
			this.literal = null;
		} else {

			this.contextNodes = new HashMap<XRI3SubSegment, MemoryContextNode> ();
			this.relations = new HashMap<XRI3Segment, Map<XRI3Segment, MemoryRelation>> ();
			this.literal = null;
		}
	}

	@Override
	public XRI3SubSegment getArcXri() {

		return this.arcXri;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	public synchronized ContextNode createContextNode(XRI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();

		if (this.containsContextNode(arcXri)) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains the context node " + arcXri + ".");

		MemoryContextNode contextNode = new MemoryContextNode(this.getGraph(), this);
		contextNode.arcXri = arcXri;

		this.contextNodes.put(arcXri, contextNode);

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(XRI3SubSegment arcXri) {

		return this.contextNodes.get(arcXri);
	}

	public Iterator<ContextNode> getContextNodes() {

		return new CastingIterator<ContextNode> (this.contextNodes.values().iterator());
	}

	@Override
	public boolean containsContextNode(XRI3SubSegment arcXri) {

		return this.contextNodes.containsKey(arcXri);
	}

	@Override
	public boolean containsContextNodes() {

		return ! this.contextNodes.isEmpty();
	}

	public synchronized void deleteContextNode(XRI3SubSegment arcXri) {

		this.contextNodes.remove(arcXri);
	}

	public synchronized void deleteContextNodes() {

		this.contextNodes.clear();
	}

	/*
	 * Methods related to relations of this context node
	 */

	public synchronized Relation createRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		if (arcXri == null) throw new NullPointerException();
		if (relationXri == null) throw new NullPointerException();

		if (this.containsRelation(arcXri, relationXri)) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains the relation " + arcXri + "/" + relationXri + ".");

		Map<XRI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) {

			if (((MemoryGraph) this.graph).getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

				relations = new TreeMap<XRI3Segment, MemoryRelation> ();
			} else if (((MemoryGraph) this.graph).getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

				relations = new LinkedHashMap<XRI3Segment, MemoryRelation> ();
			} else {

				relations = new HashMap<XRI3Segment, MemoryRelation> ();
			}

			this.relations.put(arcXri, relations);
		}

		MemoryRelation relation = new MemoryRelation(this.getGraph(), this, arcXri, relationXri);
		relations.put(relationXri, relation);

		return relation;
	}

	public Relation getRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		Map<XRI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) return null;

		return relations.get(relationXri);
	}

	public Iterator<Relation> getRelations(XRI3Segment arcXri) {

		Map<XRI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) return new EmptyIterator<Relation> ();

		return new CastingIterator<Relation> (relations.values().iterator());
	}

	public Iterator<Relation> getRelations() {

		Iterator<MemoryRelation> descendingIterator = new DescendingIterator<Entry<XRI3Segment, Map<XRI3Segment, MemoryRelation>>, MemoryRelation> (this.relations.entrySet().iterator()) {

			@Override
			public Iterator<MemoryRelation> descend(Entry<XRI3Segment, Map<XRI3Segment, MemoryRelation>> item) {

				return item.getValue().values().iterator();
			}
		};

		return new CastingIterator<Relation> (descendingIterator);
	}

	public boolean containsRelation(XRI3Segment arcXri) {

		return this.relations.containsKey(arcXri);
	}

	@Override
	public boolean containsRelations() {

		return ! this.relations.isEmpty();
	}

	public synchronized void deleteRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		Map<XRI3Segment, MemoryRelation> relations = this.relations.get(arcXri);
		if (relations == null) return;

		relations.remove(relationXri);

		if (relations.isEmpty()) {

			this.relations.remove(arcXri);
		}
	}

	public synchronized void deleteRelations(XRI3Segment arcXri) {

		this.relations.remove(arcXri);
	}

	public synchronized void deleteRelations() {

		this.relations.clear();
	}

	/*
	 * Methods related to literals of this context node
	 */

	public synchronized Literal createLiteral(String literalData) {

		if (literalData == null) throw new NullPointerException();

		if (this.containsLiteral()) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains a literal.");

		MemoryLiteral literal = new MemoryLiteral(this.getGraph(), this, literalData);
		this.literal = literal;

		return literal;
	}

	public Literal getLiteral() {

		return this.literal;
	}

	@Override
	public boolean containsLiteral() {

		return this.literal != null;
	}

	public synchronized void deleteLiteral() {

		this.literal = null;
	}
}
