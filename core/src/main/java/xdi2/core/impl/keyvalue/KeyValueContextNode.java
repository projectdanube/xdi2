package xdi2.core.impl.keyvalue;

import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.LiteralNode;
import xdi2.core.Node;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractContextNode;
import xdi2.core.impl.AbstractLiteralNode;
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

	private XDIArc XDIarc;

	public KeyValueContextNode(KeyValueGraph graph, KeyValueContextNode contextNode, KeyValueStore keyValueStore, String key, XDIArc XDIarc) {

		super(graph, contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.XDIarc = XDIarc;
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

		String contextNodesKey = this.getContextNodesKey();
		String contextNodeKey = this.getContextNodeKey(XDIarc);

		if (this.keyValueStore.contains(contextNodesKey, XDIarc.toString())) {

			KeyValueContextNode contextNode = new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, XDIarc);
			return contextNode;
		}

		this.keyValueStore.set(contextNodesKey, XDIarc.toString());
		this.keyValueStore.delete(contextNodeKey + "/--C");
		this.keyValueStore.delete(contextNodeKey + "/--R");
		this.keyValueStore.delete(contextNodeKey + "/--L");

		KeyValueContextNode contextNode = new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, XDIarc);

		// set inner root

		this.setContextNodeSetInnerRoot(XDIarc, contextNode);

		// done

		return contextNode;
	}

	@Override
	public ReadOnlyIterator<ContextNode> getContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		List<ContextNode> list = new IteratorListMaker<ContextNode> (new MappingIterator<String, ContextNode> (this.keyValueStore.getAll(contextNodesKey)) {

			@Override
			public ContextNode map(String item) {

				XDIArc XDIarc = XDIArc.create(item);
				String contextNodeKey = KeyValueContextNode.this.getContextNodeKey(XDIarc);

				return new KeyValueContextNode((KeyValueGraph) KeyValueContextNode.this.getGraph(), KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, contextNodeKey, XDIarc);
			}
		}).list();

		return new ReadOnlyIterator<ContextNode> (list.iterator());
	}

	@Override
	public ContextNode getContextNode(XDIArc XDIarc, boolean subgraph) {

		String contextNodesKey = this.getContextNodesKey();
		String contextNodeKey = this.getContextNodeKey(XDIarc);

		if (! this.keyValueStore.contains(contextNodesKey, XDIarc.toString())) return null;

		return new KeyValueContextNode((KeyValueGraph) this.getGraph(), this, this.keyValueStore, contextNodeKey, XDIarc);
	}

	@Override
	public boolean containsContextNode(XDIArc XDIarc) {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.contains(contextNodesKey, XDIarc.toString());
	}

	@Override
	public boolean containsContextNodes() {

		String contextNodesKey = this.getContextNodesKey();

		return this.keyValueStore.contains(contextNodesKey);
	}

	@Override
	public synchronized void delContextNode(XDIArc XDIarc) {

		ContextNode contextNode = this.getContextNode(XDIarc, true);
		if (contextNode == null) return;

		// delete all relations and incoming relations

		((KeyValueContextNode) contextNode).delContextNodeDelAllInnerRoots();
		((KeyValueContextNode) contextNode).delContextNodeDelAllIncomingRelations();

		// delete this context node

		String contextNodesKey = this.getContextNodesKey();

		this.keyValueStore.delete(contextNodesKey, XDIarc.toString());
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
	public synchronized Relation setRelation(XDIAddress XDIaddress, Node targetNode) {

		XDIAddress targetXDIAddress = targetNode.getXDIAddress();

		// check validity

		this.setRelationCheckValid(XDIaddress, targetXDIAddress);

		// set the relation

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(XDIaddress);

		this.keyValueStore.set(relationsKey, XDIaddress.toString());
		this.keyValueStore.set(relationKey, targetXDIAddress.toString());

		KeyValueRelation relation = new KeyValueRelation(this, this.keyValueStore, relationKey, XDIaddress, targetXDIAddress);

		// done

		return relation;
	}

	@Override
	public Relation getRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(XDIaddress);

		if (! this.keyValueStore.contains(relationsKey, XDIaddress.toString())) return null;
		if (! this.keyValueStore.contains(relationKey, targetXDIAddress.toString())) return null;

		return new KeyValueRelation(this, this.keyValueStore, relationKey, XDIaddress, targetXDIAddress);
	}

	@Override
	public Relation getRelation(XDIAddress XDIaddress) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(XDIaddress);

		if (! this.keyValueStore.contains(relationsKey, XDIaddress.toString())) return null;
		if (! this.keyValueStore.contains(relationKey)) return null;

		XDIAddress relationAddress = XDIAddress.create(this.keyValueStore.getOne(relationKey));

		return new KeyValueRelation(this, this.keyValueStore, relationKey, XDIaddress, relationAddress);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(final XDIAddress XDIaddress) {

		String relationsKey = this.getRelationsKey();
		final String relationKey = this.getRelationKey(XDIaddress);

		if (! this.keyValueStore.contains(relationsKey, XDIaddress.toString())) return new EmptyIterator<Relation> ();
		if (! this.keyValueStore.contains(relationKey)) return new EmptyIterator<Relation> ();

		List<Relation> list = new IteratorListMaker<Relation> (new MappingIterator<String, Relation> (this.keyValueStore.getAll(relationKey)) {

			@Override
			public Relation map(String relationAddressString) {

				XDIAddress relationAddress = XDIAddress.create(relationAddressString);

				return new KeyValueRelation(KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, XDIaddress, relationAddress);
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

				final XDIAddress XDIaddress = XDIAddress.create(item);
				final String relationKey = KeyValueContextNode.this.getRelationKey(XDIaddress);

				return new MappingIterator<String, Relation> (KeyValueContextNode.this.keyValueStore.getAll(relationKey)) {

					@Override
					public Relation map(String relationAddressString) {

						return new KeyValueRelation(KeyValueContextNode.this, KeyValueContextNode.this.keyValueStore, relationKey, XDIaddress, XDIAddress.create(relationAddressString));
					}
				};
			}
		}).list();

		return new ReadOnlyIterator<Relation> (list.iterator());
	}

	@Override
	public boolean containsRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(XDIaddress);

		return this.keyValueStore.contains(relationsKey, XDIaddress.toString()) && this.keyValueStore.contains(relationKey, targetXDIAddress.toString());
	}

	@Override
	public boolean containsRelations(XDIAddress XDIaddress) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(XDIaddress);

		return this.keyValueStore.contains(relationsKey, XDIaddress.toString()) && this.keyValueStore.contains(relationKey);
	}

	@Override
	public boolean containsRelations() {

		String relationsKey = this.getRelationsKey();

		return this.keyValueStore.contains(relationsKey);
	}

	@Override
	public synchronized void delRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		// delete the relation

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(XDIaddress);

		this.keyValueStore.delete(relationKey, targetXDIAddress.toString());

		if (! this.keyValueStore.contains(relationKey)) {

			this.keyValueStore.delete(relationsKey, XDIaddress.toString());
		}

		// delete inner root

		this.delRelationDelInnerRoot(XDIaddress, targetXDIAddress);
	}

	@Override
	public synchronized void delRelations(XDIAddress XDIaddress) {

		ReadOnlyIterator<Relation> relations = this.getRelations(XDIaddress);

		// delete relations

		String relationsKey = this.getRelationsKey();

		this.keyValueStore.delete(relationsKey, XDIaddress.toString());

		// delete inner roots

		for (Relation relation : relations) {

			this.delRelationDelInnerRoot(relation.getXDIAddress(), relation.getTargetXDIAddress());
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

			this.delRelationDelInnerRoot(relation.getXDIAddress(), relation.getTargetXDIAddress());
		}
	}

	@Override
	public long getRelationCount() {

		String relationsKey = this.getRelationsKey();

		Iterator<Long> mappingIterator = new MappingIterator<String, Long> (this.keyValueStore.getAll(relationsKey)) {

			@Override
			public Long map(String item) {

				final XDIAddress XDIaddress = XDIAddress.create(item);
				final String relationKey = KeyValueContextNode.this.getRelationKey(XDIaddress);

				return Long.valueOf(KeyValueContextNode.this.keyValueStore.count(relationKey));
			}
		};

		long sum = 0;
		while (mappingIterator.hasNext()) sum += mappingIterator.next().longValue();

		return sum;
	}

	@Override
	public long getRelationCount(XDIAddress XDIaddress) {

		String relationsKey = this.getRelationsKey();
		String relationKey = this.getRelationKey(XDIaddress);

		if (! this.keyValueStore.contains(relationsKey, XDIaddress.toString())) return 0;

		return this.keyValueStore.count(relationKey);
	}

	/*
	 * Methods related to literals of this context node
	 */

	@Override
	public synchronized LiteralNode setLiteralNode(Object literalData) {

		// check validity

		this.setLiteralCheckValid(literalData);

		// set the literal

		String literalKey = this.getLiteralKey();

		this.keyValueStore.replace(literalKey, AbstractLiteralNode.literalDataToString(literalData));

		KeyValueLiteralNode literalNode = new KeyValueLiteralNode(this, this.keyValueStore, literalKey, literalData);

		// done

		return literalNode;
	}

	@Override
	public LiteralNode getLiteralNode() {

		if (! this.containsLiteralNode()) return null;

		String literalKey = this.getLiteralKey();

		return new KeyValueLiteralNode(this, this.keyValueStore, literalKey, null);
	}

	@Override
	public boolean containsLiteralNode() {

		String literalKey = this.getLiteralKey();

		return this.keyValueStore.contains(literalKey);
	}

	@Override
	public synchronized void delLiteralNode() {

		String literalKey = this.getLiteralKey();

		this.keyValueStore.delete(literalKey);
	}

	/*
	 * Helper methods
	 */

	private String getContextNodesKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--C";
	}

	private String getContextNodeKey(XDIArc XDIarc) {

		return (this.isRootContextNode() ? "" : this.key) + XDIarc.toString();
	}

	private String getRelationsKey() {

		return (this.isRootContextNode() ? "" : this.key) + "/--R";
	}

	private String getRelationKey(XDIAddress XDIaddress) {

		return (this.isRootContextNode() ? "" : this.key) + "/" + XDIaddress.toString();
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
