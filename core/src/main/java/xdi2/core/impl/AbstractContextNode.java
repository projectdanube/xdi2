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
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.impl.AbstractStatement.AbstractContextNodeStatement;
import xdi2.core.util.XDI3Util;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorFirstItem;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3SubSegment;

public abstract class AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 7937255194345376190L;

	private Graph graph;
	private ContextNode contextNode;

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
	public synchronized void deleteWhileEmpty() {

		ContextNode currentContextNode = this;
		ContextNode parentContextNode;

		while (currentContextNode.isEmpty() && (! currentContextNode.isRootContextNode())) {

			parentContextNode = currentContextNode.getContextNode();
			currentContextNode.delete();
			currentContextNode = parentContextNode;
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
	/*
	 * TODO: This is inefficient for nodes deep down in the tree.
	 */
	public XDI3Segment getXri() {

		if (this.isRootContextNode()) return XDIConstants.XRI_S_CONTEXT;

		StringBuilder xri = new StringBuilder();

		xri.append(this.getArcXri().toString());

		for (ContextNode contextNode = this.getContextNode(); 
				contextNode != null && ! contextNode.isRootContextNode(); 
				contextNode = contextNode.getContextNode()) {

			xri.insert(0, contextNode.getArcXri());
		}

		return XDI3Segment.create(xri.toString());
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	//	public ContextNode createContextNode(XDI3SubSegment contextNodeArcXri);

	@Override
	public ContextNode createDeepContextNode(XDI3Segment contextNodeArcXris) {

		XDI3SubSegment contextNodeArcXri = XDI3Util.localXri(contextNodeArcXris, 1).getFirstSubSegment();
		contextNodeArcXris = XDI3Util.parentXri(contextNodeArcXris, -1);

		ContextNode contextNode = contextNodeArcXris == null ? this : this.setDeepContextNode(contextNodeArcXris);

		return contextNode.createContextNode(contextNodeArcXri);
	}

	@Override
	public ContextNode setContextNode(XDI3SubSegment contextNodeArcXri) {

		ContextNode contextNode = this.getContextNode(contextNodeArcXri);

		if (contextNode == null) {

			contextNode = this.createContextNode(contextNodeArcXri);
		}

		return contextNode;
	}

	@Override
	public ContextNode setDeepContextNode(XDI3Segment contextNodeArcXris) {

		if (contextNodeArcXris == null) return this;
		if (XDIConstants.XRI_S_ROOT.equals(contextNodeArcXris) && this.isRootContextNode()) return this;

		ContextNode contextNode = this;

		for (XDI3SubSegment contextNodeArcXri : contextNodeArcXris.getSubSegments()) {

			contextNode = contextNode.setContextNode(contextNodeArcXri);
		}

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(final XDI3SubSegment contextNodeArcXri) {

		Iterator<ContextNode> selectingIterator = new SelectingIterator<ContextNode> (this.getContextNodes()) {

			@Override
			public boolean select(ContextNode contextNode) {

				return contextNode.getArcXri().equals(contextNodeArcXri);
			}
		};

		return new IteratorFirstItem<ContextNode> (selectingIterator).item();
	}

	//	public ContextNode getContextNode(XDI3SubSegment contextNodeArcXri);

	@Override
	public ContextNode getDeepContextNode(XDI3Segment contextNodeArcXris) {

		if (XDIConstants.XRI_S_ROOT.equals(contextNodeArcXris) && this.isRootContextNode()) return this;

		ContextNode contextNode = this;

		for (XDI3SubSegment contextNodeArcXri : contextNodeArcXris.getSubSegments()) {

			contextNode = contextNode.getContextNode(contextNodeArcXri);
			if (contextNode == null) return null;
		}

		return contextNode;
	}

	//	public ReadOnlyIterator<ContextNode> getContextNodes();

	@Override
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDI3Segment contextNodeArcXris) {

		ContextNode contextNode = this.getDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return new EmptyIterator<ContextNode> ();

		return contextNode.getContextNodes();
	}

	@Override
	public ReadOnlyIterator<ContextNode> getAllContextNodes() {

		DescendingIterator<ContextNode, ContextNode> descendingIterator = new DescendingIterator<ContextNode, ContextNode> (this.getContextNodes()) {

			@Override
			public Iterator<ContextNode> descend(ContextNode contextNode) {

				return contextNode.getAllContextNodes();
			}
		};

		List<Iterator<? extends ContextNode>> list = new ArrayList<Iterator<? extends ContextNode>> ();
		list.add(this.getContextNodes());
		list.add(descendingIterator);

		return new CompositeIterator<ContextNode> (list.iterator());
	}

	@Override
	public ReadOnlyIterator<ContextNode> getAllLeafContextNodes() {

		return new SelectingIterator<ContextNode> (this.getAllContextNodes()) {

			@Override
			public boolean select(ContextNode contextNode) {

				return contextNode.isLeafContextNode();
			}
		};
	}

	@Override
	public boolean containsContextNode(XDI3SubSegment contextNodeArcXri) {

		return this.getContextNode(contextNodeArcXri) != null;
	}

	@Override
	public boolean containsContextNodes() {

		return this.getContextNodeCount() > 0;
	}

	@Override
	public void deleteContextNodes() {

		for (ContextNode contextNode : this.getContextNodes()) contextNode.delete();
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
	public Relation createRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		ContextNode targetContextNode = this.getGraph().setDeepContextNode(targetContextNodeXri);

		return this.createRelation(arcXri, targetContextNode);
	}

	@Override
	public Relation createDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		ContextNode contextNode = this.setDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.createRelation(arcXri, targetContextNodeXri);
	}

	//	public Relation createRelation(XDI3Segment arcXri, ContextNode targetContextNode);

	@Override
	public Relation createDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, ContextNode targetContextNode) {

		ContextNode contextNode = this.setDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.createRelation(arcXri, targetContextNode);
	}

	@Override
	public Relation setRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		ContextNode targetContextNode = this.getGraph().setDeepContextNode(targetContextNodeXri);

		return this.setRelation(arcXri, targetContextNode);
	}

	@Override
	public Relation setDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		ContextNode contextNode = this.setDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.setRelation(arcXri, targetContextNodeXri);
	}

	@Override
	public Relation setRelation(XDI3Segment arcXri, ContextNode targetContextNode) {

		Relation relation = this.getRelation(arcXri, targetContextNode.getXri());

		if (relation == null) {

			relation = this.createRelation(arcXri, targetContextNode.getXri());
		}

		return relation;
	}

	@Override
	public Relation setDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, ContextNode targetContextNode) {

		ContextNode contextNode = this.setDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.setRelation(arcXri, targetContextNode);
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri, final XDI3Segment targetContextNodeXri) {

		Iterator<Relation> selectingIterator = new SelectingIterator<Relation> (this.getRelations(arcXri)) {

			@Override
			public boolean select(Relation relation) {

				return relation.getTargetContextNodeXri().equals(targetContextNodeXri);
			}
		};

		return new IteratorFirstItem<Relation> (selectingIterator).item();
	}

	@Override
	public Relation getDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		ContextNode contextNode = this.getDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.getRelation(arcXri, targetContextNodeXri);
	}

	@Override
	public Relation getRelation(XDI3Segment arcXri) {

		return new IteratorFirstItem<Relation> (this.getRelations(arcXri)).item();
	}

	@Override
	public Relation getDeepRelation(XDI3Segment contextNodeArcXris, XDI3Segment arcXri) {

		ContextNode contextNode = this.getDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.getRelation(arcXri);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(final XDI3Segment arcXri) {

		return new SelectingIterator<Relation> (this.getRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getArcXri().equals(arcXri);
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeArcXris, XDI3Segment arcXri) {

		ContextNode contextNode = this.getDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return new EmptyIterator<Relation> ();

		return contextNode.getRelations(arcXri);
	}

	//	public ReadOnlyIterator<Relation> getRelations();

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDI3Segment contextNodeArcXris) {

		ContextNode contextNode = this.getDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.getRelations();
	}

	@Override
	public ReadOnlyIterator<Relation> getIncomingRelations(final XDI3Segment arcXri) {

		return new SelectingIterator<Relation> (this.getIncomingRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getArcXri().equals(arcXri);
			}
		};
	}

	@Override
	/*
	 * TODO: This is inefficient for a large number of context nodes in the graph.
	 */
	public ReadOnlyIterator<Relation> getIncomingRelations() {

		return new SelectingIterator<Relation> (this.getGraph().getRootContextNode().getAllRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.follow().equals(AbstractContextNode.this);
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getAllRelations() {

		DescendingIterator<ContextNode, Relation> descendingIterator = new DescendingIterator<ContextNode, Relation> (this.getContextNodes()) {

			@Override
			public Iterator<Relation> descend(ContextNode contextNode) {

				return contextNode.getAllRelations();
			}
		};

		List<Iterator<? extends Relation>> list = new ArrayList<Iterator<? extends Relation>> ();
		list.add(this.getRelations());
		list.add(descendingIterator);

		return new CompositeIterator<Relation> (list.iterator());
	}

	@Override
	public boolean containsRelation(XDI3Segment arcXri, XDI3Segment targetContextNodeXri) {

		return this.getRelation(arcXri, targetContextNodeXri) != null;
	}

	@Override
	public boolean containsRelations(XDI3Segment arcXri) {

		return this.getRelation(arcXri) != null;
	}

	@Override
	public boolean containsRelations() {

		return this.getRelationCount() > 0;
	}

	@Override
	public void deleteRelations(XDI3Segment arcXri) {

		for (Relation relation : this.getRelations(arcXri)) relation.delete();
	}

	@Override
	public void deleteRelations() {

		for (Relation relation : this.getRelations()) relation.delete();
	}

	@Override
	public void deleteIncomingRelations() {

		for (Relation relation : this.getIncomingRelations()) relation.delete();
	}

	@Override
	public int getRelationCount(XDI3Segment arcXri) {

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

	//	public Literal createLiteral(String literalData);

	@Override
	public Literal createDeepLiteral(XDI3Segment contextNodeArcXris, String literalData) {

		ContextNode contextNode = this.setDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.createLiteral(literalData);
	}

	@Override
	public Literal setLiteral(String literalData) {

		Literal literal = this.getLiteral();

		if (literal == null) {

			literal = this.createLiteral(literalData);
		} else {

			literal.setLiteralData(literalData);
		}

		return literal;
	}

	@Override
	public Literal setDeepLiteral(XDI3Segment contextNodeArcXris, String literalData) {

		ContextNode contextNode = this.setDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.setLiteral(literalData);
	}

	@Override
	public Literal getLiteral(String literalData) {

		Literal literal = this.getLiteral();
		if (literal == null) return null;

		if (! literal.getLiteralData().equals(literalData)) return null;

		return literal;
	}

	@Override
	public Literal getDeepLiteral(XDI3Segment contextNodeArcXris, String literalData) {

		ContextNode contextNode = this.getDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.getLiteral(literalData);
	}

	//	public Literal getLiteral();

	@Override
	public Literal getDeepLiteral(XDI3Segment contextNodeArcXris) {

		ContextNode contextNode = this.getDeepContextNode(contextNodeArcXris);
		if (contextNode == null) return null;

		return contextNode.getLiteral();
	}

	@Override
	public ReadOnlyIterator<Literal> getAllLiterals() {

		DescendingIterator<ContextNode, Literal> descendingIterator = new DescendingIterator<ContextNode, Literal> (this.getContextNodes()) {

			@Override
			public Iterator<Literal> descend(ContextNode contextNode) {

				return contextNode.getAllLiterals();
			}
		};

		Literal literal = this.getLiteral();

		List<Iterator<? extends Literal>> list = new ArrayList<Iterator<? extends Literal>> ();
		if (literal != null) list.add(new SingleItemIterator<Literal> (literal));
		list.add(descendingIterator);

		return new CompositeIterator<Literal> (list.iterator());
	}

	@Override
	public boolean containsLiteral(String literalData) {

		return this.getLiteral(literalData) != null;
	}

	@Override
	public boolean containsLiteral() {

		return this.getLiteral() != null;
	}

	@Override
	public int getAllLiteralCount() {

		return new IteratorCounter(this.getAllLiterals()).count();
	}

	/*
	 * Methods related to statements
	 */

	@Override
	public ContextNodeStatement getStatement() {

		if (this.isRootContextNode()) {

			return null;
		} else {

			return this.statement;
		}
	}

	@Override
	public ReadOnlyIterator<Statement> getAllStatements() {

		Iterator<Statement> contextNodesStatements = null;
		Iterator<Statement> relationsStatements = null;
		Iterator<Statement> literalStatement = null;

		if (this.containsContextNodes()) {

			contextNodesStatements = new DescendingIterator<ContextNode, Statement> (this.getContextNodes()) {

				@Override
				public Iterator<Statement> descend(ContextNode contextNode) {

					List<Iterator<? extends Statement>> list = new ArrayList<Iterator<? extends Statement>> ();
					list.add(new SingleItemIterator<Statement> (contextNode.getStatement()));
					list.add(contextNode.getAllStatements());

					return new CompositeIterator<Statement> (list.iterator());
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

		List<Iterator<? extends Statement>> list = new ArrayList<Iterator<? extends Statement>> ();
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
	 * Methods related to checking graph validity
	 */

	/**
	 * Checks if a context node can be created.
	 * Throws an exception, if the context node cannot be created.
	 */
	protected void checkContextNode(XDI3SubSegment arcXri, boolean checkExists) throws Xdi2GraphException {

		if (arcXri == null) throw new NullPointerException();

		if (XDIConstants.XRI_SS_CONTEXT.equals(arcXri)) throw new Xdi2GraphException("Invalid context node arc XRI: " + arcXri);

		if (checkExists && this.containsContextNode(arcXri)) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains the context node " + arcXri + ".");

		ContextNode tempContextNode = new BasicContextNode(this.getGraph(), this, arcXri, null, null, null);
		if (! XdiAbstractContext.isValid(tempContextNode)) throw new Xdi2GraphException("Invalid subgraph: " + arcXri);
	}

	/**
	 * Checks if a relation can be created.
	 * Throws an exception, if the relation cannot be created.
	 */
	protected void checkRelation(XDI3Segment arcXri, ContextNode targetContextNode, boolean checkExists) throws Xdi2GraphException {

		if (arcXri == null) throw new NullPointerException();
		if (targetContextNode == null) throw new NullPointerException();

		if (XDIConstants.XRI_SS_CONTEXT.equals(arcXri)) throw new Xdi2GraphException("Invalid relation arc XRI: " + arcXri);
		if (XDIConstants.XRI_SS_LITERAL.equals(arcXri)) throw new Xdi2GraphException("Invalid relation arc XRI: " + arcXri);

		if (XDIDictionaryConstants.XRI_S_REF.equals(arcXri) && ! this.isEmpty()) throw new Xdi2GraphException("Cannot add " + XDIDictionaryConstants.XRI_S_REF + " relational arc to non-empty context node.");
		if (XDIDictionaryConstants.XRI_S_REP.equals(arcXri) && ! this.isEmpty()) throw new Xdi2GraphException("Cannot add " + XDIDictionaryConstants.XRI_S_REP + " relational arc to non-empty context node.");

		if (checkExists && this.containsRelation(arcXri, targetContextNode.getXri())) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains the relation " + arcXri + "/" + targetContextNode + ".");
	}

	/**
	 * Checks if a literal can be created.
	 * Throws an exception, if the literal cannot be created.
	 */
	protected void checkLiteral(String literalData, boolean checkExists) throws Xdi2GraphException {

		if (literalData == null) throw new NullPointerException();

		if (checkExists && this.containsLiteral()) throw new Xdi2GraphException("Context node " + this.getXri() + " already contains a literal.");

		if (! XdiValue.isValid(this)) throw new Xdi2GraphException("Can only create a literal in a value context.");
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
		public XDI3Segment getSubject() {

			return AbstractContextNode.this.getContextNode().getXri();
		}

		@Override
		public XDI3Segment getObject() {

			return XDI3Segment.create(AbstractContextNode.this.getArcXri().toString());
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
