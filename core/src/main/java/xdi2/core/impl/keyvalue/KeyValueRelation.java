package xdi2.core.impl.keyvalue;

import xdi2.core.Relation;
import xdi2.core.impl.AbstractRelation;
import xdi2.core.syntax.XDIAddress;

public class KeyValueRelation extends AbstractRelation implements Relation {

	private static final long serialVersionUID = -2152877877561935106L;

	private KeyValueStore keyValueStore;
	private String key;

	private XDIAddress XDIaddress;
	private XDIAddress targetXDIAddress;

	KeyValueRelation(KeyValueContextNode contextNode, KeyValueStore keyValueStore, String key, XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		super(contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.XDIaddress = XDIaddress;
		this.targetXDIAddress = targetXDIAddress;
	}

	@Override
	public XDIAddress getXDIAddress() {

		return this.XDIaddress;
	}

	@Override
	public XDIAddress getTargetXDIAddress() {

		return this.targetXDIAddress;
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
