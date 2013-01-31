package xdi2.core.impl.keyvalue;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.xri3.XDI3Segment;

public class KeyValueRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2152877877561935106L;

	private KeyValueStore keyValueStore;
	private String key;

	private XDI3Segment arcXri;
	private XDI3Segment targetContextNodeXri;

	KeyValueRelation(Graph graph, ContextNode contextNode, KeyValueStore keyValueStore, String key, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		super(graph, contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.arcXri = arcXri;
		this.targetContextNodeXri = targetContextNodeXri;
	}

	@Override
	public XDI3Segment getArcXri() {

		return this.arcXri;
	}

	@Override
	public XDI3Segment getTargetContextNodeXri() {

		return this.targetContextNodeXri;
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
