package xdi2.core.impl;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Relation;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.impl.AbstractStatement.AbstractRelationStatement;
import xdi2.core.syntax.XDIAddress;

public abstract class AbstractRelation implements Relation {

	private static final long serialVersionUID = -9055773010138710261L;

	private ContextNode contextNode;

	public AbstractRelation(ContextNode contextNode) {

		if (contextNode == null) throw new NullPointerException();
		
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
	public synchronized void delete() {

		this.getContextNode().delRelation(this.getArc(), this.getTargetContextNodeAddress());
	}

	@Override
	public ContextNode follow() {

		return this.getGraph().getDeepContextNode(this.getTargetContextNodeAddress(), false);
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

		// two relations are equal if their context nodes, arcs, and target context node XRIs are equal

		return 
				this.getContextNode().equals(other.getContextNode()) &&
				this.getArc().equals(other.getArc()) && 
				this.getTargetContextNodeAddress().equals(other.getTargetContextNodeAddress());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();
		hashCode = (hashCode * 31) + this.getArc().hashCode();
		hashCode = (hashCode * 31) + this.getTargetContextNodeAddress().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(Relation other) {

		if (other == null || other == this) return 0;

		int compare;

		if ((compare = this.getContextNode().compareTo(other.getContextNode())) != 0) return compare;
		if ((compare = this.getArc().compareTo(other.getArc())) != 0) return compare;
		if ((compare = this.getTargetContextNodeAddress().compareTo(other.getTargetContextNodeAddress())) != 0) return compare;

		return 0;
	}

	/**
	 * A statement for this relation.
	 */

	private final RelationStatement statement = new AbstractRelationStatement() {

		private static final long serialVersionUID = 1937380243537401799L;

		@Override
		public XDIAddress getSubject() {

			return AbstractRelation.this.getContextNode().getAddress();
		}

		@Override
		public XDIAddress getPredicate() {

			return AbstractRelation.this.getArc();
		}

		@Override
		public XDIAddress getObject() {

			return AbstractRelation.this.getTargetContextNodeAddress();
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
