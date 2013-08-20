package xdi2.core.impl.keyvalue;

import xdi2.core.Literal;
import xdi2.core.impl.AbstractLiteral;

public class KeyValueLiteral extends AbstractLiteral implements Literal {

	private static final long serialVersionUID = 5391652119780088907L;

	private KeyValueStore keyValueStore;
	private String key;

	private Object literalData;

	KeyValueLiteral(KeyValueContextNode contextNode, KeyValueStore keyValueStore, String key, Object literalData) {

		super(contextNode);

		this.keyValueStore = keyValueStore;
		this.key = key;

		this.literalData = literalData;
	}

	@Override
	public Object getLiteralData() {

		if (this.literalData == null) {

			this.literalData = AbstractLiteral.stringToLiteralData(this.keyValueStore.getOne(this.key));
		}

		return this.literalData;
	}

	@Override
	public void setLiteralData(Object literalData) {

		this.keyValueStore.replace(this.key, literalDataToString(literalData));

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
