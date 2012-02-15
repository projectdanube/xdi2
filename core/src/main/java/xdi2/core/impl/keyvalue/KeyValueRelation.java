package xdi2.core.impl.keyvalue;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.xri3.impl.XRI3Segment;

public class KeyValueRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2152877877561935106L;

	private KeyValueStore keyValueStore;
	private String key;

	private XRI3Segment arcXri;
	private XRI3Segment relationXri;

	KeyValueRelation(Graph graph, ContextNode contextNode, KeyValueStore keyValueStore, String key, XRI3Segment arcXri, XRI3Segment relationXri) {

		super(graph, contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.arcXri = arcXri;
		this.relationXri = relationXri;
	}

	public XRI3Segment getArcXri() {

		return this.arcXri;
	}

	public XRI3Segment getRelationXri() {

		return this.relationXri;
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
