package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.impl.AbstractStatement.AbstractLiteralStatement;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.XDI3Segment;

public abstract class AbstractLiteral implements Literal {

	private static final long serialVersionUID = -3376866498591508078L;

	private Graph graph;
	private ContextNode contextNode;

	public AbstractLiteral(Graph graph, ContextNode contextNode) {

		this.graph = graph;
		this.contextNode = contextNode;
	}

	@Override
	public Graph getGraph() {

		return this.graph;
	}

	@Override
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	@Override
	public void delete() {

		this.getContextNode().deleteLiteral();
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
		if ((compare = this.getLiteralData().compareTo(other.getLiteralData())) != 0) return compare;

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
		public XDI3Segment getObject() {

			return XDIUtil.stringToLiteralSegment(AbstractLiteral.this.getLiteralData(), false);
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
