package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.impl.AbstractStatement.AbstractLiteralStatement;
import xdi2.core.xri3.XDI3Segment;

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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public int compareTo(Literal other) {

		if (other == null || other == this) return 0;

		int compare;

		if ((compare = this.getContextNode().compareTo(other.getContextNode())) != 0) return compare;
		if ((compare = ((Comparable) this.getLiteralData()).compareTo((Comparable) other.getLiteralData())) != 0) return compare;

		return 0;
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
