package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.impl.AbstractStatement.AbstractLiteralStatement;
import xdi2.core.util.XDIConstants;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XRI3Segment;

public abstract class AbstractLiteral implements Literal {

	private static final long serialVersionUID = -3376866498591508078L;

	protected Graph graph;
	protected ContextNode contextNode;

	public AbstractLiteral(Graph graph, ContextNode contextNode) {

		this.graph = graph;
		this.contextNode = contextNode;
	}

	public Graph getGraph() {

		return this.graph;
	}

	public ContextNode getContextNode() {

		return this.contextNode;
	}

	public void delete() {

		this.getContextNode().deleteLiteral();
	}

	/*
	 * Methods related to statements
	 */

	public LiteralStatement getStatement() {

		return this.statement;
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getLiteralData();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Literal)) return false;
		if (object == this) return true;

		Literal other = (Literal) object;

		// two references are equal if their XRIs are equal

		if (this.getLiteralData() == null && other.getLiteralData() != null) return false;
		if (this.getLiteralData() != null && other.getLiteralData() == null) return false;
		if (this.getLiteralData() != null && other.getLiteralData() != null && ! this.getLiteralData().equals(other.getLiteralData())) return false;

		return true;
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getLiteralData().hashCode();

		return hashCode;
	}

	public int compareTo(Literal other) {

		if (other == null || other == this) return 0;

		return this.getLiteralData().compareTo(other.getLiteralData());
	}

	/**
	 * A statement for this literal.
	 */

	private final LiteralStatement statement = new AbstractLiteralStatement() {

		private static final long serialVersionUID = -8290065911553369697L;

		public XRI3Segment getSubject() {

			return AbstractLiteral.this.getContextNode().getXri();
		}

		public XRI3Segment getPredicate() {

			return XDIConstants.XRI_S_LITERAL;
		}

		public XRI3Segment getObject() {

			return XDIUtil.stringToDataXriSegment(AbstractLiteral.this.getLiteralData(), false);
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
