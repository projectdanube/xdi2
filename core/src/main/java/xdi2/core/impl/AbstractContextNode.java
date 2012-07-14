package xdi2.core.impl;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.AbstractStatement.AbstractContextNodeStatement;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorFirstItem;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;

public abstract class AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 7937255194345376190L;

	protected Graph graph;
	protected ContextNode contextNode;

	public AbstractContextNode(Graph graph, ContextNode contextNode) {

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
	public boolean isRootContextNode() {

		return this.getContextNode() == null;
	}

	@Override
	public boolean isLeafContextNode() {

		return ! this.containsContextNodes();
	}

	@Override
	public synchronized void delete() {

		if (this.isRootContextNode()) {

			this.clear();
		} else {

			this.getContextNode().deleteContextNode(this.getArcXri());
		}
	}

	@Override
	public synchronized void clear() {

		this.deleteContextNodes();
		this.deleteRelations();
		this.deleteLiteral();
	}

	@Override
	public boolean isEmpty() {

		return ! (this.containsContextNodes() || this.containsRelations() || this.containsLiteral());
	}

	@Override
	public XRI3Segment getXri() {

		if (this.isRootContextNode()) return XDIConstants.XRI_S_CONTEXT;

		StringBuilder xri = new StringBuilder();

		xri.append(this.getArcXri().toString());

		for (ContextNode contextNode = this.getContextNode(); 
				contextNode != null && ! contextNode.isRootContextNode(); 
				contextNode = contextNode.getContextNode()) {

			xri.insert(0, contextNode.getArcXri());
		}

		return new XRI3Segment(xri.toString());
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	@Override
	public ContextNode createContextNodes(XRI3Segment arcXri) {

		ContextNode contextNode = this;

		for (int i = 0; i < arcXri.getNumSubSegments(); i++) {

			contextNode = contextNode.createContextNode((XRI3SubSegment) arcXri.getSubSegment(i));
		}

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(final XRI3SubSegment arcXri) {

		Iterator<ContextNode> selectingIterator = new SelectingIterator<ContextNode> (this.getContextNodes()) {

			@Override
			public boolean select(ContextNode contextNode) {

				return contextNode.getArcXri().equals(arcXri);
			}
		};

		return new IteratorFirstItem<ContextNode> (selectingIterator).item();
	}

	@Override
	public Iterator<ContextNode> getAllContextNodes() {

		DescendingIterator<ContextNode, ContextNode> descendingIterator = new DescendingIterator<ContextNode, ContextNode> (this.getContextNodes()) {

			@Override
			public Iterator<ContextNode> descend(ContextNode contextNode) {

				return contextNode.getAllContextNodes();
			}
		};

		List<Iterator<ContextNode>> list = new ArrayList<Iterator<ContextNode>> ();
		list.add(this.getContextNodes());
		list.add(descendingIterator);

		return new CompositeIterator<ContextNode> (list.iterator());
	}

	@Override
	public Iterator<ContextNode> getAllLeafContextNodes() {

		return new SelectingIterator<ContextNode> (this.getAllContextNodes()) {

			@Override
			public boolean select(ContextNode contextNode) {

				return contextNode.isEmpty();
			}
		};
	}

	@Override
	public boolean containsContextNode(XRI3SubSegment arcXri) {

		return this.getContextNode(arcXri) != null;
	}

	@Override
	public boolean containsContextNodes() {

		return this.getContextNodeCount() > 0;
	}

	@Override
	public int getContextNodeCount() {

		return new IteratorCounter(this.getContextNodes()).count();
	}

	@Override
	public int getAllContextNodeCount() {

		return new IteratorCounter(this.getAllContextNodes()).count();
	}

	/*
	 * Methods related to relations of this context node
	 */

	@Override
	public Relation createRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		ContextNode contextNode = this.getGraph().findContextNode(relationXri, true);

		return this.createRelation(arcXri, contextNode);
	}

	@Override
	public Relation getRelation(XRI3Segment arcXri, ContextNode contextNode) {

		return this.getRelation(arcXri, contextNode.getXri());
	}

	@Override
	public Relation getRelation(XRI3Segment arcXri, final XRI3Segment relationXri) {

		Iterator<Relation> selectingIterator = new SelectingIterator<Relation> (this.getRelations(arcXri)) {

			@Override
			public boolean select(Relation relation) {

				return relation.getRelationXri().equals(relationXri);
			}
		};

		return new IteratorFirstItem<Relation> (selectingIterator).item();
	}

	@Override
	public Relation getRelation(XRI3Segment arcXri) {

		return new IteratorFirstItem<Relation> (this.getRelations(arcXri)).item();
	}

	@Override
	public Iterator<Relation> getRelations(final XRI3Segment arcXri) {

		return new SelectingIterator<Relation> (this.getRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getArcXri().equals(arcXri);
			}
		};
	}

	@Override
	public Iterator<Relation> getAllRelations() {

		DescendingIterator<ContextNode, Relation> descendingIterator = new DescendingIterator<ContextNode, Relation> (this.getContextNodes()) {

			@Override
			public Iterator<Relation> descend(ContextNode contextNode) {

				return contextNode.getAllRelations();
			}
		};

		List<Iterator<Relation>> list = new ArrayList<Iterator<Relation>> ();
		list.add(this.getRelations());
		list.add(descendingIterator);

		return new CompositeIterator<Relation> (list.iterator());
	}

	@Override
	public boolean containsRelation(XRI3Segment arcXri, ContextNode contextNode) {

		return this.getRelation(arcXri, contextNode) != null;
	}

	@Override
	public boolean containsRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		return this.getRelation(arcXri, relationXri) != null;
	}

	@Override
	public boolean containsRelations(XRI3Segment arcXri) {

		return this.getRelation(arcXri) != null;
	}

	@Override
	public boolean containsRelations() {

		return this.getRelationCount() > 0;
	}

	@Override
	public void deleteRelation(XRI3Segment arcXri, XRI3Segment relationXri) {

		ContextNode contextNode = this.getGraph().findContextNode(relationXri, false);
		if (contextNode == null) return;

		this.deleteRelation(arcXri, contextNode);
	}

	@Override
	public int getRelationCount(XRI3Segment arcXri) {

		return new IteratorCounter(this.getRelations(arcXri)).count();
	}

	@Override
	public int getRelationCount() {

		return new IteratorCounter(this.getRelations()).count();
	}

	@Override
	public int getAllRelationCount() {

		return new IteratorCounter(this.getAllRelations()).count();
	}

	/*
	 * Methods related to literals of this context node
	 */

	@Override
	public Literal createLiteralInContextNode(XRI3SubSegment arcXri, String literalData) {

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) contextNode = this.createContextNode(arcXri);

		return contextNode.createLiteral(literalData);
	}

	@Override
	public Literal getLiteralInContextNode(XRI3SubSegment arcXri) {

		ContextNode contextNode = this.getContextNode(arcXri);
		if (contextNode == null) return null;

		return contextNode.getLiteral();
	}

	@Override
	public Iterator<Literal> getAllLiterals() {

		DescendingIterator<ContextNode, Literal> descendingIterator = new DescendingIterator<ContextNode, Literal> (this.getContextNodes()) {

			@Override
			public Iterator<Literal> descend(ContextNode contextNode) {

				return contextNode.getAllLiterals();
			}
		};

		List<Iterator<Literal>> list = new ArrayList<Iterator<Literal>> ();
		list.add(new SingleItemIterator<Literal> (this.getLiteral()));
		list.add(descendingIterator);

		return new CompositeIterator<Literal> (list.iterator());
	}

	@Override
	public boolean containsLiteral() {

		return this.getLiteral() != null;
	}

	@Override
	public boolean containsLiteralInContextNode(XRI3SubSegment arcXri) {

		return this.getLiteralInContextNode(arcXri)  != null;
	}

	@Override
	public int getAllLiteralCount() {

		return new IteratorCounter(this.getAllLiterals()).count();
	}

	/*
	 * Methods related to statements
	 */

	@Override
	public Statement getStatement() {

		if (this.isRootContextNode()) {

			return null;
		} else {

			return this.statement;
		}
	}

	@Override
	public Iterator<Statement> getAllStatements() {

		Iterator<Statement> contextNodeStatement = null;
		Iterator<Statement> contextNodesStatements = null;
		Iterator<Statement> relationsStatements = null;
		Iterator<Statement> literalStatement = null;

		if (! this.isRootContextNode()) {

			contextNodeStatement = new SingleItemIterator<Statement> (this.getStatement());
		}

		if (this.containsContextNodes()) {

			contextNodesStatements = new DescendingIterator<ContextNode, Statement> (this.getContextNodes()) {

				@Override
				public Iterator<Statement> descend(ContextNode contextNode) {

					return contextNode.getAllStatements();
				}
			};
		}

		if (this.containsRelations()) {

			relationsStatements = new MappingIterator<Relation, Statement> (this.getRelations()) {

				@Override
				public Statement map(Relation relation) {

					return relation.getStatement();
				}
			};
		}

		if (this.containsLiteral()) {

			literalStatement = new SingleItemIterator<Statement> (this.getLiteral().getStatement());
		}

		List<Iterator<Statement>> list = new ArrayList<Iterator<Statement>> ();
		if (contextNodeStatement != null) list.add(contextNodeStatement);
		if (contextNodesStatements != null) list.add(contextNodesStatements);
		if (relationsStatements != null) list.add(relationsStatements);
		if (literalStatement != null) list.add(literalStatement);

		return new CompositeIterator<Statement> (list.iterator());
	}

	@Override
	public int getAllStatementCount() {

		return new IteratorCounter(this.getAllStatements()).count();
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getXri().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof ContextNode)) return false;
		if (object == this) return true;

		ContextNode other = (ContextNode) object;

		// two context nodes are equal if their XRIs are equal

		return this.getXri().equals(other.getXri());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getXri().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(ContextNode other) {

		if (other == null || other == this) return 0;

		return this.getXri().compareTo(other.getXri());
	}

	/**
	 * A statement for this context node.
	 */

	private final ContextNodeStatement statement = new AbstractContextNodeStatement() {

		private static final long serialVersionUID = 5008355182847367563L;

		@Override
		public XRI3Segment getSubject() {

			return AbstractContextNode.this.getContextNode().getXri();
		}

		@Override
		public XRI3Segment getPredicate() {

			return XDIConstants.XRI_S_CONTEXT;
		}

		@Override
		public XRI3Segment getObject() {

			return new XRI3Segment(AbstractContextNode.this.getArcXri().toString());
		}

		@Override
		public Graph getGraph() {

			return AbstractContextNode.this.getGraph();
		}

		@Override
		public void delete() {

			AbstractContextNode.this.delete();
		}

		@Override
		public ContextNode getContextNode() {

			return AbstractContextNode.this;
		}
	};
}
