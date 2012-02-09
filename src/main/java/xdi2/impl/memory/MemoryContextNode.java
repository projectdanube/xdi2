package xdi2.impl.memory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.exceptions.Xdi2GraphException;
import xdi2.impl.AbstractContextNode;
import xdi2.util.iterators.CastingIterator;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public class MemoryContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 4930852359817860369L;

	XRI3SubSegment arcXri;

	private Map<XRI3SubSegment, MemoryContextNode> contextNodes;
	private Map<XRI3SubSegment, MemoryRelation> relations;
	private MemoryLiteral literal;

	MemoryContextNode(Graph graph, ContextNode contextNode) {

		super(graph, contextNode);

		if (((MemoryGraph) graph).getSortMode() == MemoryGraphFactory.SORTMODE_ALPHA) {

			this.contextNodes = new TreeMap<XRI3SubSegment, MemoryContextNode> ();
			this.relations = new TreeMap<XRI3SubSegment, MemoryRelation> ();
			this.literal = null;
		} else if (((MemoryGraph) graph).getSortMode() == MemoryGraphFactory.SORTMODE_ORDER) {

			this.contextNodes = new LinkedHashMap<XRI3SubSegment, MemoryContextNode> ();
			this.relations = new LinkedHashMap<XRI3SubSegment, MemoryRelation> ();
			this.literal = null;
		} else {

			this.contextNodes = new HashMap<XRI3SubSegment, MemoryContextNode> ();
			this.relations = new HashMap<XRI3SubSegment, MemoryRelation> ();
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

	public Iterator<ContextNode> getContextNodes() {

		return new CastingIterator<ContextNode> (this.contextNodes.values().iterator());
	}

	@Override
	public ContextNode getContextNode(XRI3SubSegment arcXri) {

		return this.contextNodes.get(arcXri);
	}

	@Override
	public boolean containsContextNodes() {

		return ! this.contextNodes.isEmpty();
	}

	@Override
	public boolean containsContextNode(XRI3SubSegment arcXri) {

		return this.contextNodes.containsKey(arcXri);
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

	public synchronized Relation createRelation(XRI3SubSegment arcXri, XRI3Segment relationXri) {

		if (arcXri == null) throw new NullPointerException();
		if (relationXri == null) throw new NullPointerException();

		if (this.containsRelation(arcXri)) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains the relation " + arcXri + ".");

		MemoryRelation relation = new MemoryRelation(this.getGraph(), this, arcXri, relationXri);
		this.relations.put(arcXri, relation);

		return relation;
	}

	public Iterator<Relation> getRelations() {

		return new CastingIterator<Relation> (this.relations.values().iterator());
	}

	@Override
	public Relation getRelation(XRI3SubSegment arcXri) {

		return this.relations.get(arcXri);
	}

	@Override
	public boolean containsRelations() {

		return ! this.relations.isEmpty();
	}

	@Override
	public boolean containsRelation(XRI3SubSegment arcXri) {

		return this.relations.containsKey(arcXri);
	}

	public synchronized void deleteRelation(XRI3SubSegment arcXri) {

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
