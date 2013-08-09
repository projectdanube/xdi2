package xdi2.core.impl.keyvalue;

import org.apache.commons.lang.math.NumberUtils;

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

			this.literalData = stringToLiteralData(this.keyValueStore.getOne(this.key));
		}

		return this.literalData;
	}

	@Override
	public void setLiteralData(Object literalData) {

		this.keyValueStore.replace(this.key, literalDataToString(literalData));

		this.literalData = literalData;
	}

	/*
	 * Helper methods
	 */

	private static String literalDataToString(Object literalData) {

		if (literalData == null) throw new NullPointerException();

		if (literalData instanceof String) {

			return "\"" + ((String) literalData).replace("\"", "\\\"") + "\"";
		} else if (literalData instanceof Number) {

			return literalData.toString();
		} else if (literalData instanceof Boolean) {

			return literalData.toString();
		} else {

			throw new IllegalArgumentException("Invalid literal data: " + literalData.getClass().getSimpleName());
		}
	}

	private static Object stringToLiteralData(String string) {

		if (string == null) throw new NullPointerException();
		if (string.isEmpty()) throw new IllegalArgumentException("Invalid emtpy string.");

		if (string.startsWith("\"") && string.endsWith("\"")) {

			return string.substring(1, string.length() - 2).replace("\\\"", "\"");
		} else {

			try {

				return NumberUtils.createNumber(string);
			} catch (Exception ex) {

				try {

					return Boolean.parseBoolean(string);
				} catch (Exception ex2) {

					throw new IllegalArgumentException("Invalid string: " + string);
				}
			}
		}
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
