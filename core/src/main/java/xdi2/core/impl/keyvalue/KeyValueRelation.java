package xdi2.core.impl.keyvalue;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.syntax.XDIAddress;

public class KeyValueRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2152877877561935106L;

	private KeyValueStore keyValueStore;
	private String key;

	private XDIAddress XDIaddress;
	private XDIAddress targetContextNodeXDIAddress;

	KeyValueRelation(KeyValueContextNode contextNode, KeyValueStore keyValueStore, String key, XDIAddress XDIaddress, XDIAddress targetContextNodeXDIAddress) {

		super(contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.XDIaddress = XDIaddress;
		this.targetContextNodeXDIAddress = targetContextNodeXDIAddress;
	}

	@Override
	public XDIAddress getXDIAddress() {

		return this.XDIaddress;
	}

	@Override
	public XDIAddress getTargetContextNodeXDIAddress() {

		return this.targetContextNodeXDIAddress;
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
