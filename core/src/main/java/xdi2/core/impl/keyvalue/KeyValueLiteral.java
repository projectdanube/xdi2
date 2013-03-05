package xdi2.core.impl.keyvalue;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;

public class KeyValueLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 5391652119780088907L;

	private KeyValueStore keyValueStore;
	private String key;

	private String literalData;

	KeyValueLiteral(Graph graph, ContextNode contextNode, KeyValueStore keyValueStore, String key, String literalData) {

		super(graph, contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.literalData = literalData;
	}

	@Override
	public String getLiteralData() {

		if (this.literalData == null) {

			this.literalData = this.keyValueStore.getOne(this.key);
		}

		return this.literalData;
	}

	@Override
	public void setLiteralData(String literalData) {

		this.keyValueStore.replace(this.key, literalData);

		this.literalData = literalData;
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
