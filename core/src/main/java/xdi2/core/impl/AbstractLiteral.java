package xdi2.core.impl;

import java.io.IOException;
import java.util.Comparator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.impl.AbstractStatement.AbstractLiteralStatement;
import xdi2.core.xri3.XDI3Segment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public abstract class AbstractLiteral implements Literal {

	private static final long serialVersionUID = -3376866498591508078L;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private ContextNode contextNode;

	public AbstractLiteral(ContextNode contextNode) {

		this.contextNode = contextNode;
	}

	@Override
	public Graph getGraph() {

		return this.getContextNode().getGraph();
	}

	@Override
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	@Override
	public void delete() {

		this.getContextNode().deleteLiteral();
	}

	@Override
	public String getLiteralDataString() {

		Object literalData = this.getLiteralData();
		if (! (literalData instanceof String)) return null;

		return (String) literalData;
	}

	@Override
	public Double getLiteralDataNumber() {

		Object literalData = this.getLiteralData();
		if (! (literalData instanceof Double)) return null;

		return (Double) literalData;
	}

	@Override
	public Boolean getLiteralDataBoolean() {

		Object literalData = this.getLiteralData();
		if (! (literalData instanceof Boolean)) return null;

		return (Boolean) literalData;
	}

	@Override
	public void setLiteralDataString(String literalData) {

		this.setLiteralData(literalData);
	}

	@Override
	public void setLiteralDataNumber(Double literalData) {

		this.setLiteralData(literalData);
	}

	@Override
	public void setLiteralDataBoolean(Boolean literalData) {

		this.setLiteralData(literalData);
	}

	/*
	 * Methods related to statements
	 */

	@Override
	public LiteralStatement getStatement() {

		return this.statement;
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getStatement().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Literal)) return false;
		if (object == this) return true;

		Literal other = (Literal) object;

		// two literals are equal if their context nodes and their data are equal

		return
				this.getContextNode().equals(other.getContextNode()) &&
				this.getLiteralData().equals(other.getLiteralData());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().getXri().hashCode();
		hashCode = (hashCode * 31) + this.getLiteralData().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Literal other) {

		if (other == null || other == this) return 0;

		int compare;

		if ((compare = this.getContextNode().compareTo(other.getContextNode())) != 0) return compare;
		if ((compare = LITERALDATACOMPARATOR.compare(this.getLiteralData(), other.getLiteralData())) != 0) return compare;

		return 0;
	}

	/*
	 * Helper classes
	 */

	public static final Comparator<Object> LITERALDATACOMPARATOR = new Comparator<Object> () {

		@Override
		public int compare(Object o1, Object o2) {

			if (! isValidLiteralData(o1)) throw new IllegalArgumentException("Invalid literal data: " + o1.getClass().getSimpleName());
			if (! isValidLiteralData(o2)) throw new IllegalArgumentException("Invalid literal data: " + o2.getClass().getSimpleName());

			if (o1 instanceof String && o2 instanceof String) {

				return ((String) o1).compareTo((String) o2);
			} else if (o1 instanceof Number && o2 instanceof Number) {

				return Double.compare(((Number) o1).doubleValue(), ((Number) o2).doubleValue());
			} else if (o1 instanceof Boolean && o2 instanceof Boolean) {

				return ((Boolean) o1).compareTo((Boolean) o2);
			}

			return 0;
		}
	};

	/*
	 * Helper methods
	 */

	public static boolean isValidLiteralData(Object literalData) {

		return literalData instanceof String || literalData instanceof Double || literalData instanceof Boolean || literalData instanceof JsonArray || literalData instanceof JsonObject || literalData == null;
	}

	public static String literalDataToString(Object literalData) {

		return gson.toJson(literalDataToJsonElement(literalData));

		/*		if (literalData instanceof String) {

			return "\"" + ((String) literalData).replace("\"", "\\\"");
		} else if (literalData instanceof Double) {

			return ((Double) literalData).toString();
		} else if (literalData instanceof Boolean) {

			return ((Boolean) literalData).toString();
		} else if (literalData instanceof List<?>) {

			return gson.toJson(literalData);
		} else if (literalData instanceof Map<?, ?>) {

			return gson.toJson(literalData);
		} else {

			throw new IllegalArgumentException("Invalid literal data: " + literalData.getClass().getSimpleName());
		}*/
	}

	public static Object stringToLiteralData(String string) {

		if (string == null) throw new NullPointerException();
		if (string.isEmpty()) throw new IllegalArgumentException("Invalid empty string.");

		try {

			JsonArray jsonArray = gson.getAdapter(JsonArray.class).fromJson("[" + string + "]");

			return jsonElementToLiteralData(jsonArray.get(0));
		} catch (IOException ex) {

			throw new Xdi2RuntimeException(ex.getMessage(), ex);
		}

		/*		if (string.startsWith("\"") && string.endsWith("\"")) {

			return string.substring(1, string.length() - 1).replace("\\\"", "\"");
		} else if (string.startsWith("[") && string.endsWith("]")) {

			return gson.fromJson(string.substring(1, string.length() - 1), List.class);
		} else if (string.startsWith("{") && string.endsWith("}")) {

			return gson.fromJson(string.substring(1, string.length() - 1), Map.class);
		} else if ("true".equals(string)) {

			return Boolean.TRUE;
		} else if ("false".equals(string)) {

			return Boolean.FALSE;
		} else {

			try {

				return Double.valueOf(string);
			} catch (Exception ex) {

				throw new IllegalArgumentException("Invalid string: " + string);
			}
		}*/
	}

	public static JsonElement literalDataToJsonElement(Object literalData) {

		if (literalData instanceof String) return new JsonPrimitive((String) literalData);
		if (literalData instanceof Double) return new JsonPrimitive((Double) literalData);
		if (literalData instanceof Boolean) return new JsonPrimitive((Boolean) literalData);
		if (literalData instanceof JsonArray) return (JsonArray) literalData;
		if (literalData instanceof JsonObject) return (JsonObject) literalData;
		if (literalData == null) return JsonNull.INSTANCE;

		throw new IllegalArgumentException("Invalid literal data: " + literalData.getClass().getSimpleName());
	}

	public static Object jsonElementToLiteralData(JsonElement jsonElement) {

		if (jsonElement == null) throw new NullPointerException();

		if (jsonElement instanceof JsonPrimitive) {

			if (((JsonPrimitive) jsonElement).isString()) return jsonElement.getAsString();
			if (((JsonPrimitive) jsonElement).isNumber()) return Double.valueOf(jsonElement.getAsDouble());
			if (((JsonPrimitive) jsonElement).isBoolean()) return Boolean.valueOf(jsonElement.getAsBoolean());
		} else if (jsonElement instanceof JsonArray) {

			return jsonElement;
		} else if (jsonElement instanceof JsonObject) {

			return jsonElement;
		} else if (jsonElement instanceof JsonNull) {

			return null;
		}

		throw new IllegalArgumentException("Invalid JSON element: " + jsonElement);
	}

	/**
	 * A statement for this literal.
	 */

	private final LiteralStatement statement = new AbstractLiteralStatement() {

		private static final long serialVersionUID = -8290065911553369697L;

		@Override
		public XDI3Segment getSubject() {

			return AbstractLiteral.this.getContextNode().getXri();
		}

		@Override
		public Object getObject() {

			return AbstractLiteral.this.getLiteralData();
		}

		@Override
		public Graph getGraph() {

			return AbstractLiteral.this.getGraph();
		}

		@Override
		public void delete() {

			AbstractLiteral.this.delete();
		}

		@Override
		public Literal getLiteral() {

			return AbstractLiteral.this;
		}
	};
}
