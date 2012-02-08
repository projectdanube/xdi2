package xdi2.impl.keyvalue;

import java.util.Iterator;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.Relation;
import xdi2.exceptions.Xdi2GraphException;
import xdi2.impl.AbstractContextNode;
import xdi2.util.iterators.MappingIterator;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public class KeyValueContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = -4967051993820678931L;

	private KeyValueStore keyValueStore;
	private String key;

	private XRI3SubSegment arcXri;

	public KeyValueContextNode(Graph graph, ContextNode contextNode, KeyValueStore keyValueStore, String key, XRI3SubSegment arcXri) {

		super(graph, contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.arcXri = arcXri;
	}

	@Override
	public synchronized void clear() {

		if (this.isRootContextNode()) {

			this.keyValueStore.clear();
		} else {

			super.clear();
		}
	}

	public XRI3SubSegment getArcXri() {

		return this.arcXri;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	public synchronized ContextNode createContextNode(XRI3SubSegment arcXri) {

		if (arcXri == null) throw new NullPointerException();

		if (this.containsContextNode(arcXri)) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains the context node " + arcXri + ".");

		String contextNodeKey = (this.isRootContextNode() ? "" : this.key) + arcXri.toString();

		if (((KeyValueGraph) this.getGraph()).isSupportGetContextNodes()) this.keyValueStore.put(this.key + "/" + "--C", arcXri.toString());
		this.keyValueStore.put(contextNodeKey, ".");

		KeyValueContextNode contextNode = new KeyValueContextNode(this.getGraph(), this, this.keyValueStore, contextNodeKey, arcXri);

		return contextNode;
	}

	public Iterator<ContextNode> getContextNodes() {

		return new MappingIterator<String, ContextNode> (this.keyValueStore.getAll(this.key + "/--C")) {

			@Override
			public ContextNode map(String item) {

				XRI3SubSegment arcXri = new XRI3SubSegment(item);
				String contextNodeKey = (KeyValueContextNode.this.isRootContextNode() ? "" : KeyValueContextNode.this.key) + arcXri.toString();

				return new KeyValueContextNode(KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, contextNodeKey, arcXri);
			}
		};
	}

	@Override
	public ContextNode getContextNode(XRI3SubSegment arcXri) {

		if (! this.containsContextNode(arcXri)) return(null);

		String contextNodeKey = (this.isRootContextNode() ? "" : this.key) + arcXri.toString();

		return new KeyValueContextNode(this.getGraph(), this, this.keyValueStore, contextNodeKey, arcXri);
	}

	@Override
	public boolean containsContextNode(XRI3SubSegment arcXri) {

		String contextNodeKey = (this.isRootContextNode() ? "" : this.key) + arcXri.toString();

		return this.keyValueStore.contains(contextNodeKey);
	}

	@Override
	public boolean containsContextNodes() {

		return this.keyValueStore.contains(this.key + "/--C");
	}

	public synchronized void deleteContextNode(XRI3SubSegment arcXri) {

		String contextNodeKey = (this.isRootContextNode() ? "" : this.key) + arcXri.toString();

		if (((KeyValueGraph) this.getGraph()).isSupportGetContextNodes()) this.keyValueStore.delete(this.key + "/--C", arcXri.toString());
		this.keyValueStore.delete(contextNodeKey);
	}

	public synchronized void deleteContextNodes() {

		if (((KeyValueGraph) this.getGraph()).isSupportGetContextNodes()) this.keyValueStore.delete(this.key + "/--C");
	}

	@Override
	public synchronized int getContextNodeCount() {

		return this.keyValueStore.count(this.key + "/--C");
	}

	/*
	 * Methods related to relations of this context node
	 */

	public synchronized Relation createRelation(XRI3SubSegment arcXri, XRI3Segment relationXri) {

		if (arcXri == null) throw new NullPointerException();
		if (relationXri == null) throw new NullPointerException();

		if (this.containsLiteral(arcXri)) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains the literal " + arcXri + ".");
		if (this.containsRelation(arcXri)) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains the relation " + arcXri + ".");

		String relationKey = (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();

		if (((KeyValueGraph) this.getGraph()).isSupportGetRelations()) this.keyValueStore.put(this.key + "/" + "--R", arcXri.toString());
		this.keyValueStore.put(relationKey, relationXri.toString());

		KeyValueRelation relation = new KeyValueRelation(this.getGraph(), this, this.keyValueStore, relationKey, arcXri, relationXri);

		return relation;
	}

	public Iterator<Relation> getRelations() {

		return new MappingIterator<String, Relation> (this.keyValueStore.getAll(this.key + "/--R")) {

			@Override
			public Relation map(String item) {

				XRI3SubSegment arcXri = new XRI3SubSegment(item);
				String relationKey = (KeyValueContextNode.this.isRootContextNode() ? "" : KeyValueContextNode.this.key) + "/" + arcXri.toString();

				return new KeyValueRelation(KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, arcXri, null);
			}
		};
	}

	@Override
	public Relation getRelation(XRI3SubSegment arcXri) {

		if (! this.containsRelation(arcXri)) return(null);

		String relationKey = (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();

		return new KeyValueRelation(this.getGraph(), this, this.keyValueStore, relationKey, arcXri, null);
	}

	@Override
	public boolean containsRelations() {

		return this.keyValueStore.contains(this.key + "/--R");
	}

	@Override
	public boolean containsRelation(XRI3SubSegment arcXri) {

		String relationKey = (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();

		return this.keyValueStore.contains(relationKey);
	}

	public synchronized void deleteRelation(XRI3SubSegment arcXri) {

		String relationKey = (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();

		if (((KeyValueGraph) this.getGraph()).isSupportGetRelations()) this.keyValueStore.delete(this.key + "/--R", arcXri.toString());
		this.keyValueStore.delete(relationKey);
	}

	public synchronized void deleteRelations() {

		if (((KeyValueGraph) this.getGraph()).isSupportGetRelations()) this.keyValueStore.delete(this.key + "/--R");
	}

	@Override
	public synchronized int getRelationCount() {

		return this.keyValueStore.count(this.key + "/--R");
	}

	/*
	 * Methods related to literals of this context node
	 */

	public synchronized Literal createLiteral(XRI3SubSegment arcXri, String literalData) {

		if (arcXri == null) throw new NullPointerException();
		if (literalData == null) throw new NullPointerException();

		if (this.containsLiteral(arcXri)) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains the literal " + arcXri + ".");
		if (this.containsLiteral(arcXri)) throw new Xdi2GraphException("Context node " + this.getArcXri() + " already contains the relation " + arcXri + ".");

		String literalKey = (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();

		if (((KeyValueGraph) this.getGraph()).isSupportGetLiterals()) this.keyValueStore.put(this.key + "/" + "--L", arcXri.toString());
		this.keyValueStore.put(literalKey, literalData.toString());

		KeyValueLiteral relation = new KeyValueLiteral(this.getGraph(), this, this.keyValueStore, literalKey, arcXri, literalData);

		return relation;
	}

	public Iterator<Literal> getLiterals() {

		return new MappingIterator<String, Literal> (this.keyValueStore.getAll(this.key + "/--L")) {

			@Override
			public Literal map(String item) {

				XRI3SubSegment arcXri = new XRI3SubSegment(item);
				String literalKey = (KeyValueContextNode.this.isRootContextNode() ? "" : KeyValueContextNode.this.key) + "/" + arcXri.toString();

				return new KeyValueLiteral(KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, literalKey, arcXri, null);
			}
		};
	}

	@Override
	public Literal getLiteral(XRI3SubSegment arcXri) {

		if (! this.containsLiteral(arcXri)) return(null);

		String literalKey = (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();

		return new KeyValueLiteral(this.getGraph(), this, this.keyValueStore, literalKey, arcXri, null);
	}

	@Override
	public boolean containsLiterals() {

		return this.keyValueStore.contains(this.key + "/--L");
	}

	@Override
	public boolean containsLiteral(XRI3SubSegment arcXri) {

		String literalKey = (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();

		return this.keyValueStore.contains(literalKey);
	}

	public synchronized void deleteLiteral(XRI3SubSegment arcXri) {

		String literalKey = (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();

		if (((KeyValueGraph) this.getGraph()).isSupportGetLiterals()) this.keyValueStore.delete(this.key + "/--L", arcXri.toString());
		this.keyValueStore.delete(literalKey);
	}

	public synchronized void deleteLiterals() {

		if (((KeyValueGraph) this.getGraph()).isSupportGetLiterals()) this.keyValueStore.delete(this.key + "/--L");
	}

	@Override
	public synchronized int getLiteralCount() {

		return this.keyValueStore.count(this.key + "/--L");
	}

	/*
	 * Misc methods
	 */

	KeyValueStore getKeyValueStore() {

		return this.keyValueStore;
	}

	String getKey() {

		return this.key;
	}
}
