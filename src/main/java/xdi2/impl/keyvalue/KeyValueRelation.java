package xdi2.impl.keyvalue;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Relation;
import xdi2.impl.AbstractRelation;
import xdi2.xri3.impl.XRI3Segment;
import xdi2.xri3.impl.XRI3SubSegment;

public class KeyValueRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2152877877561935106L;

	private KeyValueStore keyValueStore;
	private String key;

	private XRI3SubSegment arcXri;
	private XRI3Segment relationXri;

	KeyValueRelation(Graph graph, ContextNode contextNode, KeyValueStore keyValueStore, String key, XRI3SubSegment arcXri, XRI3Segment relationXri) {

		super(graph, contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.arcXri = arcXri;
		this.relationXri = relationXri;
	}

	@Override
	public XRI3SubSegment getArcXri() {

		return this.arcXri;
	}

	@Override
	public XRI3Segment getRelationXri() {

		if (this.relationXri == null) {

			this.relationXri = new XRI3Segment(this.keyValueStore.getOne(this.key));
		}

		return this.relationXri;
	}

	@Override
	public void setRelationXri(XRI3Segment relationXri) {

		if (relationXri == null) throw new NullPointerException();

		this.keyValueStore.replace(this.key, relationXri.toString());

		this.relationXri = relationXri;
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
