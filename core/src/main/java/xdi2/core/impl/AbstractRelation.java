package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.impl.AbstractStatement.AbstractRelationStatement;
import xdi2.core.xri3.XDI3Segment;

public abstract class AbstractRelation implements Relation {

	private static final long serialVersionUID = -9055773010138710261L;

	private Graph graph;
	private ContextNode contextNode;

	public AbstractRelation(Graph graph, ContextNode contextNode) {

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
	public synchronized void delete() {

		this.getContextNode().deleteRelation(this.getArcXri(), this.getTargetContextNodeXri());
	}

	@Override
	public ContextNode follow() {

		return this.getGraph().findContextNode(this.getTargetContextNodeXri(), false);
	}

	/*
	 * Methods related to statements
	 */

	@Override
	public RelationStatement getStatement() {

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

		if (object == null || ! (object instanceof Relation)) return false;
		if (object == this) return true;

		Relation other = (Relation) object;

		// two relations are equal if their context nodes, arc XRIs, and target context node XRIs are equal

		return 
				this.getContextNode().equals(other.getContextNode()) &&
				this.getArcXri().equals(other.getArcXri()) && 
				this.getTargetContextNodeXri().equals(other.getTargetContextNodeXri());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();
		hashCode = (hashCode * 31) + this.getArcXri().hashCode();
		hashCode = (hashCode * 31) + this.getTargetContextNodeXri().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Relation other) {

		if (other == null || other == this) return 0;

		int compare;

		if ((compare = this.getContextNode().compareTo(other.getContextNode())) != 0) return compare;
		if ((compare = this.getArcXri().compareTo(other.getArcXri())) != 0) return compare;
		if ((compare = this.getTargetContextNodeXri().compareTo(other.getTargetContextNodeXri())) != 0) return compare;

		return 0;
	}

	/**
	 * A statement for this relation.
	 */

	private final RelationStatement statement = new AbstractRelationStatement() {

		private static final long serialVersionUID = 1937380243537401799L;

		@Override
		public XDI3Segment getSubject() {

			return AbstractRelation.this.getContextNode().getXri();
		}

		@Override
		public XDI3Segment getPredicate() {

			return AbstractRelation.this.getArcXri();
		}

		@Override
		public XDI3Segment getObject() {

			return AbstractRelation.this.getTargetContextNodeXri();
		}

		@Override
		public Graph getGraph() {

			return AbstractRelation.this.getGraph();
		}

		@Override
		public void delete() {

			AbstractRelation.this.delete();
		}

		@Override
		public Relation getRelation() {

			return AbstractRelation.this;
		}
	};
}
