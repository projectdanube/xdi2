package xdi2.core.impl;


import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.impl.AbstractStatement.AbstractRelationStatement;
import xdi2.core.xri3.impl.XRI3Segment;

public abstract class AbstractRelation implements Relation {

	private static final long serialVersionUID = -9055773010138710261L;

	protected Graph graph;
	protected ContextNode contextNode;

	public AbstractRelation(Graph graph, ContextNode contextNode) {

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

		this.getContextNode().deleteRelation(this.getArcXri());
	}

	/*
	 * Methods for following the relation
	 */

	public ContextNode follow() {

		XRI3Segment relationXri = this.getRelationXri();

		return this.getGraph().findContextNode(relationXri, false);
	}

	/*
	 * Methods related to statements
	 */

	public RelationStatement getStatement() {

		return this.statement;
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getRelationXri().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof Relation)) return false;
		if (object == this) return true;

		Relation other = (Relation) object;

		// two relations are equal if their XRIs are equal

		return this.getArcXri().equals(other.getArcXri()) && this.getRelationXri().equals(other.getRelationXri());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + (this.getRelationXri() == null ? 0 : this.getRelationXri().hashCode());

		return hashCode;
	}

	public int compareTo(Relation other) {

		if (other == null || other == this) return 0;

		return this.getRelationXri().compareTo(other.getRelationXri());
	}

	/**
	 * A statement for this relation.
	 */

	private final RelationStatement statement = new AbstractRelationStatement() {

		private static final long serialVersionUID = 1937380243537401799L;

		public void delete() {

			AbstractRelation.this.delete();
		}

		public Graph getGraph() {

			return AbstractRelation.this.getGraph();
		}

		public ContextNode getSubject() {

			return AbstractRelation.this.getContextNode();
		}

		public XRI3Segment getPredicate() {

			return AbstractRelation.this.getArcXri();
		}

		public XRI3Segment getObject() {

			return AbstractRelation.this.getRelationXri();
		}

		public Relation getRelation() {

			return AbstractRelation.this;
		}
	};
}
