package xdi2.core.impl.keyvalue;

import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.AbstractLiteral;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;

public class KeyValueContextNode extends AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = -4967051993820678931L;

	private KeyValueStore keyValueStore;
	private String key;

	private XDIArc arc;

	public KeyValueContextNode(KeyValueGraph graph, KeyValueContextNode contextNode, KeyValueStore keyValueStore, String key, XDIArc arc) {

		super(graph, contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.arc = arc;
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

		String contextNodesKey = this.getContextNodesKey();
		String contextNodeKey = this.getContextNodeKey(arc);

		if (this.keyValueStore.contains(contextNodesKey, arc.toString())) {

			KeyValueContextNode contextNode = new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, arc);
			return contextNode;
		}

		this.keyValueStore.set(contextNodesKey, arc.toString());
		this.keyValueStore.delete(contextNodeKey + "/--C");
		this.keyValueStore.delete(contextNodeKey + "/--R");
		this.keyValueStore.delete(contextNodeKey + "/--L");

		KeyValueContextNode contextNode = new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, arc);

		// set inner root

		this.setContextNodeSetInnerRoot(arc, contextNode);

		// done

		return contextNode;
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		List<ContextNode> list = new IteratorListMaker<ContextNode> (new MappingIterator<String, ContextNode> (this.keyValueStore.getAll(contextNodesKey)) {

			@Override
			public ContextNode map(String item) {

				XDIArc arc = XDIArc.create(item);
				String contextNodeKey = KeyValueContextNode.this.getContextNodeKey(arc);

				return new KeyValueContextNode((KeyValueGraph) KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, contextNodeKey, arc);
			}
		}).list();

		return new ReadOnlyIterator<ContextNode> (list.iterator());
	}

	@Override
	public ContextNode getContextNode(XDIArc arc, boolean subgraph) {

		String contextNodesKey = this.getContextNodesKey();
		String contextNodeKey = this.getContextNodeKey(arc);

		if (! this.keyValueStore.contains(contextNodesKey, arc.toString())) return null;

		return new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, arc);
	}

	@Override
	public boolean containsContextNode(XDIArc arc) {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.contains(contextNodesKey, arc.toString());
	}

	@Override
	public boolean containsContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.contains(contextNodesKey);
	}

	@Override
	public synchronized void delContextNode(XDIArc arc) {

		ContextNode contextNode = this.getContextNode(arc, true);
		if (contextNode == null) return;

		// delete all relations and incoming relations

		((KeyValueContextNode) contextNode).delContextNodeDelAllInnerRoots();
		((KeyValueContextNode) contextNode).delContextNodeDelAllIncomingRelations();

		// delete this context node

		String contextNodesKey = this.getContextNodesKey();

		this.keyValueStore.delete(contextNodesKey, arc.toString());
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
	public synchronized Relation setRelation(XDIAddress arc, ContextNode targetContextNode) {

		XDIAddress targetContextNodeAddress = targetContextNode.getAddress();

		// check validity

		this.setRelationCheckValid(arc, targetContextNodeAddress);

		// set the relation

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arc);

		this.keyValueStore.set(relationsKey, arc.toString());
		this.keyValueStore.set(relationKey, targetContextNodeAddress.toString());

		KeyValueRelation relation = new KeyValueRelation(this, this.keyValueStore, relationKey, arc, targetContextNodeAddress);

		// done

		return relation;
	}

	@Override
	public Relation getRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arc);

		if (! this.keyValueStore.contains(relationsKey, arc.toString())) return null;
		if (! this.keyValueStore.contains(relationKey, targetContextNodeAddress.toString())) return null;

		return new KeyValueRelation(this, this.keyValueStore, relationKey, arc, targetContextNodeAddress);
	}

	@Override
	public Relation getRelation(XDIAddress arc) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arc);

		if (! this.keyValueStore.contains(relationsKey, arc.toString())) return null;
		if (! this.keyValueStore.contains(relationKey)) return null;

		XDIAddress relationXri = XDIAddress.create(this.keyValueStore.getOne(relationKey));

		return new KeyValueRelation(this, this.keyValueStore, relationKey, arc, relationXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(final XDIAddress arc) {

		String relationsKey = this.getRelationsKey();
		final String relationKey = this.getRelationKey(arc);

		if (! this.keyValueStore.contains(relationsKey, arc.toString())) return new EmptyIterator<Relation> ();
		if (! this.keyValueStore.contains(relationKey)) return new EmptyIterator<Relation> ();

		List<Relation> list = new IteratorListMaker<Relation> (new MappingIterator<String, Relation> (this.keyValueStore.getAll(relationKey)) {

			@Override
			public Relation map(String relationXriString) {

				XDIAddress relationXri = XDIAddress.create(relationXriString);

				return new KeyValueRelation(KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, arc, relationXri);
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

				final XDIAddress arc = XDIAddress.create(item);
				final String relationKey = KeyValueContextNode.this.getRelationKey(arc);

				return new MappingIterator<String, Relation> (KeyValueContextNode.this.keyValueStore.getAll(relationKey)) {

					@Override
					public Relation map(String relationXriString) {

						return new KeyValueRelation(KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, arc, XDIAddress.create(relationXriString));
					}
				};
			}
		}).list();

		return new ReadOnlyIterator<Relation> (list.iterator());
	}

	@Override
	public boolean containsRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arc);

		return this.keyValueStore.contains(relationsKey, arc.toString()) && this.keyValueStore.contains(relationKey, targetContextNodeAddress.toString());
	}

	@Override
	public boolean containsRelations(XDIAddress arc) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arc);

		return this.keyValueStore.contains(relationsKey, arc.toString()) && this.keyValueStore.contains(relationKey);
	}

	@Override
	public boolean containsRelations() {

		String relationsKey = this.getRelationsKey();

		return this.keyValueStore.contains(relationsKey);
	}

	@Override
	public synchronized void delRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		// delete the relation

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arc);

		this.keyValueStore.delete(relationKey, targetContextNodeAddress.toString());

		if (! this.keyValueStore.contains(relationKey)) {

			this.keyValueStore.delete(relationsKey, arc.toString());
		}

		// delete inner root

		this.delRelationDelInnerRoot(arc, targetContextNodeAddress);
	}

	@Override
	public synchronized void delRelations(XDIAddress arc) {

		ReadOnlyIterator<Relation> relations = this.getRelations(arc);

		// delete relations

		String relationsKey = this.getRelationsKey();

		this.keyValueStore.delete(relationsKey, arc.toString());

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getArc(), relation.getTargetContextNodeAddress());
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

			this.delRelationDelInnerRoot(relation.getArc(), relation.getTargetContextNodeAddress());
		}
	}

	@Override
	public long getRelationCount() {

		String relationsKey = this.getRelationsKey();

		Iterator<Long> mappingIterator = new MappingIterator<String, Long> (this.keyValueStore.getAll(relationsKey)) {

			@Override
			public Long map(String item) {

				final XDIAddress arc = XDIAddress.create(item);
				final String relationKey = KeyValueContextNode.this.getRelationKey(arc);

				return Long.valueOf(KeyValueContextNode.this.keyValueStore.count(relationKey));
			}
		};

		long sum = 0;
		while (mappingIterator.hasNext()) sum += mappingIterator.next().longValue();

		return sum;
	}

	@Override
	public long getRelationCount(XDIAddress arc) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(arc);

		if (! this.keyValueStore.contains(relationsKey, arc.toString())) return 0;

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

	private String getContextNodeKey(XDIArc arc) {

		return (this.isRootContextNode() ? "" : this.key) + arc.toString();
	}

	private String getRelationsKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--R";
	}

	private String getRelationKey(XDIAddress arc) {

		return (this.isRootContextNode() ? "" : this.key) + "/" + arc.toString();
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
