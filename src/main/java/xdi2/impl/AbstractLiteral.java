package xdi2.impl;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;

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

	public synchronized void delete() {

		this.getContextNode().deleteLiteral(this.getArcXri());
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
}
