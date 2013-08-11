package xdi2.core.impl;

import java.util.Comparator;

import org.apache.commons.lang.math.NumberUtils;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.impl.AbstractStatement.AbstractLiteralStatement;
import xdi2.core.xri3.XDI3Segment;

import com.google.gson.JsonPrimitive;

public abstract class AbstractLiteral implements Literal {

	private static final long serialVersionUID = -3376866498591508078L;

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
	public Number getLiteralDataNumber() {

		Object literalData = this.getLiteralData();
		if (! (literalData instanceof Number)) return null;

		return (Number) literalData;
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
	public void setLiteralDataNumber(Number literalData) {

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

		return literalData instanceof String || literalData instanceof Number || literalData instanceof Boolean;
	}

	public static String literalDataToString(Object literalData) {

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

	public static Object stringToLiteralData(String string) {

		if (string == null) throw new NullPointerException();
		if (string.isEmpty()) throw new IllegalArgumentException("Invalid empty string.");

		if (string.startsWith("\"") && string.endsWith("\"")) {

			return string.substring(1, string.length() - 1).replace("\\\"", "\"");
		} else {

			try {

				return NumberUtils.createNumber(string);
			} catch (Exception ex) {

				if ("true".equals(string)) return Boolean.TRUE;
				if ("false".equals(string)) return Boolean.FALSE;

				throw new IllegalArgumentException("Invalid string: " + string);
			}
		}
	}

	public static JsonPrimitive literalDataToJsonPrimitive(Object literalData) {

		if (literalData == null) throw new NullPointerException();

		if (literalData instanceof String) return new JsonPrimitive((String) literalData);
		if (literalData instanceof Number) return new JsonPrimitive((Number) literalData);
		if (literalData instanceof Boolean) return new JsonPrimitive((Boolean) literalData);

		throw new IllegalArgumentException("Invalid literal data: " + literalData.getClass().getSimpleName());
	}

	public static Object jsonPrimitiveToLiteralData(JsonPrimitive jsonPrimitive) {

		if (jsonPrimitive == null) throw new NullPointerException();

		if (jsonPrimitive.isString()) return jsonPrimitive.getAsString();
		if (jsonPrimitive.isNumber()) return jsonPrimitive.getAsNumber();
		if (jsonPrimitive.isBoolean()) return Boolean.valueOf(jsonPrimitive.getAsBoolean());

		throw new IllegalArgumentException("Invalid JSON primitive: " + jsonPrimitive);
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
