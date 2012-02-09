package xdi2.impl;


import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Relation;
import xdi2.Statement;
import xdi2.xri3.impl.XRI3Segment;

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

		return this.getGraph().findContextNode(relationXri);
	}

	/*
	 * Methods related to statements
	 */

	public Statement getStatement() {
		
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

		if (this.getRelationXri() == null && other.getRelationXri() != null) return false;
		if (this.getRelationXri() != null && other.getRelationXri() == null) return false;
		if (this.getRelationXri() != null && other.getRelationXri() != null && ! this.getRelationXri().equals(other.getRelationXri())) return false;

		return true;
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
	 * A class representing a statement for this relation.
	 */
	private final Statement statement = new AbstractStatement() {

		private static final long serialVersionUID = 1937380243537401799L;

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
	};
}
