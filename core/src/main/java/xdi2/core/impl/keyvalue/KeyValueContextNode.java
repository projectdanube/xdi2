package xdi2.core.impl.keyvalue;

import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public class KeyValueContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = -4967051993820678931L;

	private KeyValueStore keyValueStore;
	private String key;

	private XDI3SubSegment arcXri;

	public KeyValueContextNode(KeyValueGraph graph, KeyValueContextNode contextNode, KeyValueStore keyValueStore, String key, XDI3SubSegment arcXri) {

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

		String contextNodesKey = this.getContextNodesKey();
		String contextNodeKey = this.getContextNodeKey(arcXri);

		if (this.keyValueStore.contains(contextNodesKey, arcXri.toString())) {

			KeyValueContextNode contextNode = new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, arcXri);
			return contextNode;
		}

		this.keyValueStore.set(contextNodesKey, arcXri.toString());
		this.keyValueStore.delete(contextNodeKey + "/--C");
		this.keyValueStore.delete(contextNodeKey + "/--R");
		this.keyValueStore.delete(contextNodeKey + "/--L");

		KeyValueContextNode contextNode = new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, arcXri);

		// set inner root

		this.setContextNodeSetInnerRoot(arcXri, contextNode);

		// done

		return contextNode;
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		List<ContextNode> list = new IteratorListMaker<ContextNode> (new MappingIterator<String, ContextNode> (this.keyValueStore.getAll(contextNodesKey)) {

			@Override
			public ContextNode map(String item) {

				XDI3SubSegment arcXri = XDI3SubSegment.create(item);
				String contextNodeKey = KeyValueContextNode.this.getContextNodeKey(arcXri);

				return new KeyValueContextNode((KeyValueGraph) KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, contextNodeKey, arcXri);
			}
		}).list();

		return new ReadOnlyIterator<ContextNode> (list.iterator());
	}

	@Override
	public ContextNode getContextNode(XDI3SubSegment arcXri) {

		String contextNodesKey = this.getContextNodesKey();
		String contextNodeKey = this.getContextNodeKey(arcXri);

		if (! this.keyValueStore.contains(contextNodesKey, arcXri.toString())) return null;

		return new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, arcXri);
	}

	@Override
	public boolean containsContextNode(XDI3SubSegment arcXri) {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.contains(contextNodesKey, arcXri.toString());
	}

	@Override
	public boolean containsContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.contains(contextNodesKey);
	}

	@Override
	public synchronized void delContextNode(XDI3SubSegment arcXri) {

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) return;

		// delete all relations and incoming relations

		((KeyValueContextNode) contextNode).delContextNodeDelAllInnerRoots();
		((KeyValueContextNode) contextNode).delContextNodeDelAllIncomingRelations();

		// delete this context node

		String contextNodesKey = this.getContextNodesKey();

		this.keyValueStore.delete(contextNodesKey, arcXri.toString());
	}

	@Override
	public synchronized void delContextNodes() {

		// delete all relations and incoming relations

		for (ContextNode contextNode : this.getContextNodes()) {

			for (Relation relation : contextNode.getAllRelations()) relation.delete();
			for (Relation relation : contextNode.getAllIncomingRelations()) relation.delete();
		}

		// delete context nodes

		String contextNodesKey = this.getContextNodesKey();

		this.keyValueStore.delete(contextNodesKey);
	}

	@Override
	public synchronized long getContextNodeCount() {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.count(contextNodesKey);
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

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		this.keyValueStore.set(relationsKey, arcXri.toString());
		this.keyValueStore.set(relationKey, targetContextNodeXri.toString());

		KeyValueRelation relation = new KeyValueRelation(this, this.keyValueStore, relationKey, arcXri, targetContextNodeXri);

		// done

		return relation;
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		if (! this.keyValueStore.contains(relationsKey, arcXri.toString())) return null;
		if (! this.keyValueStore.contains(relationKey, targetContextNodeXri.toString())) return null;

		return new KeyValueRelation(this, this.keyValueStore, relationKey, arcXri, targetContextNodeXri);
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		if (! this.keyValueStore.contains(relationsKey, arcXri.toString())) return null;
		if (! this.keyValueStore.contains(relationKey)) return null;

		XDI3Segment relationXri = XDI3Segment.create(this.keyValueStore.getOne(relationKey));

		return new KeyValueRelation(this, this.keyValueStore, relationKey, arcXri, relationXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(final XDI3Segment arcXri) {

		String relationsKey = this.getRelationsKey();
		final String relationKey = this.getRelationKey(arcXri);

		if (! this.keyValueStore.contains(relationsKey, arcXri.toString())) return new EmptyIterator<Relation> ();
		if (! this.keyValueStore.contains(relationKey)) return new EmptyIterator<Relation> ();

		List<Relation> list = new IteratorListMaker<Relation> (new MappingIterator<String, Relation> (this.keyValueStore.getAll(relationKey)) {

			@Override
			public Relation map(String relationXriString) {

				XDI3Segment relationXri = XDI3Segment.create(relationXriString);

				return new KeyValueRelation(KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, arcXri, relationXri);
			}
		}).list();

		return new ReadOnlyIterator<Relation> (list.iterator());
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations() {

		String relationsKey = this.getRelationsKey();

		List<Relation> list = new IteratorListMaker<Relation> (new DescendingIterator<String, Relation> (this.keyValueStore.getAll(relationsKey)) {

			@Override
			public Iterator<Relation> descend(String item) {

				final XDI3Segment arcXri = XDI3Segment.create(item);
				final String relationKey = KeyValueContextNode.this.getRelationKey(arcXri);

				return new MappingIterator<String, Relation> (KeyValueContextNode.this.keyValueStore.getAll(relationKey)) {

					@Override
					public Relation map(String relationXriString) {

						return new KeyValueRelation(KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, arcXri, XDI3Segment.create(relationXriString));
					}
				};
			}
		}).list();

		return new ReadOnlyIterator<Relation> (list.iterator());
	}

	@Override
	public boolean containsRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		return this.keyValueStore.contains(relationsKey, arcXri.toString()) && this.keyValueStore.contains(relationKey, targetContextNodeXri.toString());
	}

	@Override
	public boolean containsRelations(XDI3Segment arcXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		return this.keyValueStore.contains(relationsKey, arcXri.toString()) && this.keyValueStore.contains(relationKey);
	}

	@Override
	public boolean containsRelations() {

		String relationsKey = this.getRelationsKey();

		return this.keyValueStore.contains(relationsKey);
	}

	@Override
	public synchronized void delRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		// delete the relation

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		this.keyValueStore.delete(relationKey, targetContextNodeXri.toString());

		if (! this.keyValueStore.contains(relationKey)) {

			this.keyValueStore.delete(relationsKey, arcXri.toString());
		}

		// delete inner root

		this.delRelationDelInnerRoot(arcXri, targetContextNodeXri);
	}

	@Override
	public synchronized void delRelations(XDI3Segment arcXri) {

		ReadOnlyIterator<Relation> relations = this.getRelations(arcXri);

		// delete relations

		String relationsKey = this.getRelationsKey();

		this.keyValueStore.delete(relationsKey, arcXri.toString());

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getArcXri(), relation.getTargetContextNodeXri());
		}
	}

	@Override
	public synchronized void delRelations() {

		ReadOnlyIterator<Relation> relations = this.getRelations();

		// delete relations

		String relationsKey = this.getRelationsKey();

		this.keyValueStore.delete(relationsKey);

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getArcXri(), relation.getTargetContextNodeXri());
		}
	}

	@Override
	public long getRelationCount() {

		String relationsKey = this.getRelationsKey();

		Iterator<Long> mappingIterator = new MappingIterator<String, Long> (this.keyValueStore.getAll(relationsKey)) {

			@Override
			public Long map(String item) {

				final XDI3Segment arcXri = XDI3Segment.create(item);
				final String relationKey = KeyValueContextNode.this.getRelationKey(arcXri);

				return Long.valueOf(KeyValueContextNode.this.keyValueStore.count(relationKey));
			}
		};

		long sum = 0;
		while (mappingIterator.hasNext()) sum += mappingIterator.next().longValue();

		return sum;
	}

	@Override
	public long getRelationCount(XDI3Segment arcXri) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arcXri);

		if (! this.keyValueStore.contains(relationsKey, arcXri.toString())) return 0;

		return this.keyValueStore.count(relationKey);
	}

	/*
	 * Methods related to literals of this context node
	 */

	@Override
	public synchronized Literal setLiteral(Object literalData) {

		// check validity

		this.setLiteralCheckValid(literalData);

		// set the literal

		String literalKey = this.getLiteralKey();

		this.keyValueStore.replace(literalKey, AbstractLiteral.literalDataToString(literalData));

		KeyValueLiteral literal = new KeyValueLiteral(this, this.keyValueStore, literalKey, literalData);

		// done

		return literal;
	}

	@Override
	public Literal getLiteral() {

		if (! this.containsLiteral()) return null;

		String literalKey = this.getLiteralKey();

		return new KeyValueLiteral(this, this.keyValueStore, literalKey, null);
	}

	@Override
	public boolean containsLiteral() {

		String literalKey = this.getLiteralKey();

		return this.keyValueStore.contains(literalKey);
	}

	@Override
	public synchronized void delLiteral() {

		String literalKey = this.getLiteralKey();

		this.keyValueStore.delete(literalKey);
	}

	/*
	 * Helper methods
	 */

	private String getContextNodesKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--C";
	}

	private String getContextNodeKey(XDI3SubSegment arcXri) {

		return (this.isRootContextNode() ? "" : this.key) + arcXri.toString();
	}

	private String getRelationsKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--R";
	}

	private String getRelationKey(XDI3Segment arcXri) {

		return (this.isRootContextNode() ? "" : this.key) + "/" + arcXri.toString();
	}

	private String getLiteralKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--L";
	}

	KeyValueStore getKeyValueStore() {

		return this.keyValueStore;
	}

	String getKey() {

		return this.key;
	}
}
