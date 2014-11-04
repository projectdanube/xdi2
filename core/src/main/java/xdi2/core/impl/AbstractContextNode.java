package xdi2.core.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.LiteralNode;
import xdi2.core.Node;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.exceptions.Xdi2GraphException;
import xdi2.core.features.nodetypes.XdiAbstractAttribute;
import xdi2.core.features.nodetypes.XdiAbstractContext;
import xdi2.core.features.nodetypes.XdiContext;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.impl.AbstractStatement.AbstractContextNodeStatement;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorFirstItem;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.util.iterators.SingleItemIterator;

public abstract class AbstractContextNode extends AbstractNode implements ContextNode {

	private static final long serialVersionUID = 7937255194345376190L;

	private Graph graph;

	public AbstractContextNode(Graph graph, ContextNode contextNode) {

		super(contextNode);

		if (graph == null) throw new NullPointerException();

		this.graph = graph;
	}

	@Override
	public Graph getGraph() {

		return this.graph;
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

			this.getContextNode().delContextNode(this.getXDIArc());
		}
	}

	@Override
	public synchronized void clear() {

		this.delContextNodes();
		this.delRelations();
		this.delLiteralNode();
	}

	@Override
	public boolean isEmpty() {

		return ! (this.containsContextNodes() || this.containsRelations() || this.containsLiteralNode());
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	//	public ContextNode setContextNode(XDIArc contextNodeXDIArc);

	@Override
	public ContextNode getContextNode(XDIArc relativeContextNodeXDIAddress, boolean subgraph) {

		for (Iterator<ContextNode> contextNodes = this.getContextNodes(); contextNodes.hasNext(); ) {

			ContextNode contextNode = contextNodes.next();

			if (contextNode.getXDIArc().equals(relativeContextNodeXDIAddress)) return contextNode;
		}

		return null;
	}

	@Override
	public ContextNode getContextNode(XDIArc contextNodeXDIArc) {

		return this.getContextNode(contextNodeXDIArc, false);
	}

	//	public ReadOnlyIterator<ContextNode> getContextNodes();

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
	public boolean containsContextNode(XDIArc contextNodeXDIArc) {

		return this.getContextNode(contextNodeXDIArc, false) != null;
	}

	@Override
	public boolean containsContextNodes() {

		return this.getContextNodeCount() > 0;
	}

	@Override
	public void delContextNodes() {

		for (ContextNode contextNode : this.getContextNodes()) contextNode.delete();
	}

	@Override
	public long getContextNodeCount() {

		return new IteratorCounter(this.getContextNodes()).count();
	}

	@Override
	public long getAllContextNodeCount() {

		return new IteratorCounter(this.getAllContextNodes()).count();
	}

	/*
	 * Methods related to relations of this context node
	 */

	@Override
	public Relation setRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		// set the target node

		Node targetNode = this.setRelationSetTargetNode(targetXDIAddress);

		// set the relation

		Relation relation = this.setRelation(XDIaddress, targetNode);

		// done

		return relation;
	}

	//	public Relation setRelation(XDIAddress XDIaddress, Node targetNode) {

	@Override
	public Relation getRelation(XDIAddress XDIaddress, final XDIAddress targetXDIAddress) {

		Iterator<Relation> selectingIterator = new SelectingIterator<Relation> (this.getRelations(XDIaddress)) {

			@Override
			public boolean select(Relation relation) {

				return relation.getTargetXDIAddress().equals(targetXDIAddress);
			}
		};

		return new IteratorFirstItem<Relation> (selectingIterator).item();
	}

	@Override
	public Relation getRelation(XDIAddress XDIaddress) {

		return new IteratorFirstItem<Relation> (this.getRelations(XDIaddress)).item();
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(final XDIAddress XDIaddress) {

		return new SelectingIterator<Relation> (this.getRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getXDIAddress().equals(XDIaddress);
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getIncomingRelations(final XDIAddress XDIaddress) {

		return new SelectingIterator<Relation> (this.getIncomingRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getXDIAddress().equals(XDIaddress);
			}
		};
	}

	@Override
	/*
	 * TODO: This is inefficient for a large number of context nodes in the graph.
	 */
	public ReadOnlyIterator<Relation> getIncomingRelations() {

		return new SelectingIterator<Relation> (this.getGraph().getRootContextNode(true).getAllRelations()) {

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
	public ReadOnlyIterator<Relation> getAllIncomingRelations() {

		DescendingIterator<ContextNode, Relation> descendingIterator = new DescendingIterator<ContextNode, Relation> (this.getContextNodes()) {

			@Override
			public Iterator<Relation> descend(ContextNode contextNode) {

				return contextNode.getAllIncomingRelations();
			}
		};

		List<Iterator<? extends Relation>> list = new ArrayList<Iterator<? extends Relation>> ();
		list.add(this.getIncomingRelations());
		list.add(descendingIterator);

		return new CompositeIterator<Relation> (list.iterator());
	}

	@Override
	public boolean containsRelation(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		return this.getRelation(XDIaddress, targetXDIAddress) != null;
	}

	@Override
	public boolean containsRelations(XDIAddress XDIaddress) {

		return this.getRelations(XDIaddress).hasNext();
	}

	@Override
	public boolean containsRelations() {

		return this.getRelations().hasNext();
	}

	@Override
	public boolean containsIncomingRelations(XDIAddress XDIaddress) {

		return this.getIncomingRelations(XDIaddress).hasNext();
	}

	@Override
	public boolean containsIncomingRelations() {

		return this.getIncomingRelations().hasNext();
	}

	@Override
	public void delRelations(XDIAddress XDIaddress) {

		for (Relation relation : this.getRelations(XDIaddress)) relation.delete();
	}

	@Override
	public void delRelations() {

		for (Relation relation : this.getRelations()) relation.delete();
	}

	@Override
	public void delIncomingRelations() {

		for (Relation relation : this.getIncomingRelations()) relation.delete();
	}

	@Override
	public long getRelationCount(XDIAddress XDIaddress) {

		return new IteratorCounter(this.getRelations(XDIaddress)).count();
	}

	@Override
	public long getRelationCount() {

		return new IteratorCounter(this.getRelations()).count();
	}

	@Override
	public long getAllRelationCount() {

		return new IteratorCounter(this.getAllRelations()).count();
	}

	/*
	 * Methods related to literals of this context node
	 */

	// public Literal setLiteralNode(Object literalData);

	@Override
	public LiteralNode setLiteralString(String literalData) {

		return this.setLiteralNode(literalData);
	}

	@Override
	public LiteralNode setLiteralNumber(Double literalData) {

		return this.setLiteralNode(literalData);
	}

	@Override
	public LiteralNode setLiteralBoolean(Boolean literalData) {

		return this.setLiteralNode(literalData);
	}

	@Override
	public LiteralNode getLiteralNode(Object literalData) {

		LiteralNode literalNode = this.getLiteralNode();
		if (literalNode == null) return null;

		if (! AbstractLiteralNode.isLiteralDataEqual(literalNode.getLiteralData(), literalData)) return null;

		return literalNode;
	}

	@Override
	public LiteralNode getLiteralString(String literalData) {

		return this.getLiteralNode(literalData);
	}

	@Override
	public LiteralNode getLiteralNumber(Double literalData) {

		return this.getLiteralNode(literalData);
	}

	@Override
	public LiteralNode getLiteralBoolean(Boolean literalData) {

		return this.getLiteralNode(literalData);
	}

	//	public Literal getLiteralNode();

	@Override
	public ReadOnlyIterator<LiteralNode> getAllLiterals() {

		DescendingIterator<ContextNode, LiteralNode> descendingIterator = new DescendingIterator<ContextNode, LiteralNode> (this.getContextNodes()) {

			@Override
			public Iterator<LiteralNode> descend(ContextNode contextNode) {

				return contextNode.getAllLiterals();
			}
		};

		LiteralNode literalNode = this.getLiteralNode();

		List<Iterator<? extends LiteralNode>> list = new ArrayList<Iterator<? extends LiteralNode>> ();
		if (literalNode != null) list.add(new SingleItemIterator<LiteralNode> (literalNode));
		list.add(descendingIterator);

		return new CompositeIterator<LiteralNode> (list.iterator());
	}

	@Override
	public boolean containsLiteralNode(Object literalData) {

		return this.getLiteralNode(literalData) != null;
	}

	@Override
	public boolean containsLiteralString(String literalData) {

		return this.getLiteralString(literalData) != null;
	}

	@Override
	public boolean containsLiteralNumber(Double literalData) {

		return this.getLiteralNumber(literalData) != null;
	}

	@Override
	public boolean containsLiteralBoolean(Boolean literalData) {

		return this.getLiteralBoolean(literalData) != null;
	}

	@Override
	public boolean containsLiteralNode() {

		return this.getLiteralNode() != null;
	}

	@Override
	public long getAllLiteralCount() {

		return new IteratorCounter(this.getAllLiterals()).count();
	}

	/*
	 * Deep methods
	 */

	@Override
	public Node setDeepNode(XDIAddress relativeNodeXDIAddress) {

		if (relativeNodeXDIAddress == null) return this;
		if (XDIConstants.XDI_ADD_ROOT.equals(relativeNodeXDIAddress)) return this;

		ContextNode contextNode = this;

		for (XDIArc contextNodeXDIArc : relativeNodeXDIAddress.getContextNodeXDIAddress().getXDIArcs()) {

			contextNode = contextNode.setContextNode(contextNodeXDIArc);
		}

		if (relativeNodeXDIAddress.isLiteralNodeXDIAddress()) {

			return contextNode.setLiteralNode(null);
		} else {

			return contextNode;
		}
	}

	@Override
	public ContextNode setDeepContextNode(XDIAddress relativeContextNodeXDIAddress) {

		if (relativeContextNodeXDIAddress.isLiteralNodeXDIAddress()) throw new Xdi2GraphException("Not a context node address: " + relativeContextNodeXDIAddress);

		return (ContextNode) this.setDeepNode(relativeContextNodeXDIAddress);
	}

	@Override
	public LiteralNode setDeepLiteralNode(XDIAddress relativeLiteralNodeXDIAddress) {

		if (! relativeLiteralNodeXDIAddress.isLiteralNodeXDIAddress()) throw new Xdi2GraphException("Not a literal node address: " + relativeLiteralNodeXDIAddress);

		return (LiteralNode) this.setDeepNode(relativeLiteralNodeXDIAddress);
	}

	@Override
	public Node getDeepNode(XDIAddress relativeNodeXDIAddress, boolean subgraph) {

		if (XDIConstants.XDI_ADD_ROOT.equals(relativeNodeXDIAddress)) return this;

		ContextNode contextNode = this;

		for (XDIArc contextNodeXDIArc : relativeNodeXDIAddress.getContextNodeXDIAddress().getXDIArcs()) {

			contextNode = contextNode.getContextNode(contextNodeXDIArc, subgraph);
			if (contextNode == null) return null;
		}

		if (relativeNodeXDIAddress.isLiteralNodeXDIAddress()) {

			return contextNode.getLiteralNode();
		} else {

			return contextNode;
		}
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress relativeContextNodeXDIAddress, boolean subgraph) {

		if (relativeContextNodeXDIAddress.isLiteralNodeXDIAddress()) throw new Xdi2GraphException("Not a context node address: " + relativeContextNodeXDIAddress);

		return (ContextNode) this.getDeepNode(relativeContextNodeXDIAddress, subgraph);
	}

	@Override
	public LiteralNode getDeepLiteralNode(XDIAddress relativeLiteralNodeXDIAddress, boolean subgraph) {

		if (! relativeLiteralNodeXDIAddress.isLiteralNodeXDIAddress()) throw new Xdi2GraphException("Not a literal node address: " + relativeLiteralNodeXDIAddress);

		return (LiteralNode) this.getDeepNode(relativeLiteralNodeXDIAddress, subgraph);
	}

	@Override
	public Node getDeepNode(XDIAddress relativeNodeXDIAddress) {

		return this.getDeepNode(relativeNodeXDIAddress, false);
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress relativeContextNodeXDIAddress) {

		if (relativeContextNodeXDIAddress.isLiteralNodeXDIAddress()) throw new Xdi2GraphException("Not a context node address: " + relativeContextNodeXDIAddress);

		return (ContextNode) this.getDeepNode(relativeContextNodeXDIAddress);
	}

	@Override
	public LiteralNode getDeepLiteralNode(XDIAddress relativeLiteralNodeXDIAddress) {

		if (! relativeLiteralNodeXDIAddress.isLiteralNodeXDIAddress()) throw new Xdi2GraphException("Not a literal node address: " + relativeLiteralNodeXDIAddress);

		return (LiteralNode) this.getDeepNode(relativeLiteralNodeXDIAddress);
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
	public Statement setStatement(XDIStatement statement) {

		// set the statement

		if (statement.isContextNodeStatement()) {

			ContextNode contextNode = (ContextNode) this.setDeepNode(statement.getTargetXDIAddress());

			return contextNode.getStatement();
		} else if (statement.isRelationStatement()) {

			ContextNode contextNode = (ContextNode) this.setDeepNode(statement.getContextNodeXDIAddress());
			Relation relation = contextNode.setRelation(statement.getRelationXDIAddress(), statement.getTargetXDIAddress());

			return relation.getStatement();
		} else if (statement.isLiteralStatement()) {

			ContextNode contextNode = (ContextNode) this.setDeepNode(statement.getContextNodeXDIAddress());
			LiteralNode literalNode = contextNode.setLiteralNode(statement.getLiteralData());

			return literalNode.getStatement();
		} else {

			throw new Xdi2GraphException("Invalid statement: " + statement);
		}
	}

	@Override
	public Statement getStatement(XDIStatement statement) {

		ContextNode baseContextNode = this.getDeepContextNode(statement.getSubject(), false);
		if (baseContextNode == null) return null;

		if (statement.isContextNodeStatement()) {

			ContextNode contextNode = baseContextNode.getContextNode(statement.getContextNodeXDIArc(), false);

			return contextNode == null ? null : contextNode.getStatement();
		} else if (statement.isRelationStatement()) {

			Relation relation = baseContextNode.getRelation(statement.getRelationXDIAddress(), statement.getTargetXDIAddress());

			return relation == null ? null : relation.getStatement();
		} else if (statement.isLiteralStatement()) {

			LiteralNode literalNode = baseContextNode.getLiteralNode(statement.getLiteralData());

			return literalNode == null ? null : literalNode.getStatement();
		}

		return null;
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

		if (this.containsLiteralNode()) {

			literalStatement = new SingleItemIterator<Statement> (this.getLiteralNode().getStatement());
		}

		List<Iterator<? extends Statement>> list = new ArrayList<Iterator<? extends Statement>> ();
		if (contextNodesStatements != null) list.add(contextNodesStatements);
		if (relationsStatements != null) list.add(relationsStatements);
		if (literalStatement != null) list.add(literalStatement);

		return new CompositeIterator<Statement> (list.iterator());
	}

	@Override
	public boolean containsStatement(XDIStatement XDIstatement) {

		return this.getStatement(XDIstatement) != null;
	}

	@Override
	public long getAllStatementCount() {

		return new IteratorCounter(this.getAllStatements()).count();
	}

	/*
	 * Helper methods for subclasses
	 */

	/**
	 * Checks if a context node can be created.
	 * Throws an exception, if the context node cannot be created.
	 */
	protected void setContextNodeCheckValid(XDIArc XDIarc) throws Xdi2GraphException {

		if (XDIarc == null) throw new NullPointerException();

		if (XDIConstants.XDI_ADD_CONTEXT.equals(XDIarc)) throw new Xdi2GraphException("Invalid context node arc: " + XDIarc);

		if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REF)) throw new Xdi2GraphException("Cannot add " + XDIarc + " context node to context node " + this.getXDIAddress() + " containing a " + XDIDictionaryConstants.XDI_ADD_REF + " relation.");
		if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REP)) throw new Xdi2GraphException("Cannot add " + XDIarc + " context node to context node " + this.getXDIAddress() + " containing a " + XDIDictionaryConstants.XDI_ADD_REP + " relation.");

		ContextNode tempContextNode = new BasicContextNode(this.getGraph(), this, XDIarc, null, null, null);
		if (! XdiAbstractContext.isValid(tempContextNode)) throw new Xdi2GraphException("Invalid subgraph: " + XDIarc + " under subgraph " + this.getXDIAddress());
	}

	/**
	 * Checks if a relation can be created.
	 * Throws an exception, if the relation cannot be created.
	 */
	protected void setRelationCheckValid(XDIAddress XDIaddress, XDIAddress targetXDIAddress) throws Xdi2GraphException {

		if (XDIaddress == null) throw new NullPointerException();
		if (targetXDIAddress == null) throw new NullPointerException();

		if (XDIConstants.XDI_ADD_CONTEXT.equals(XDIaddress)) throw new Xdi2GraphException("Invalid relation arc: " + XDIaddress);
		if (XDIConstants.XDI_ADD_LITERAL.equals(XDIaddress)) throw new Xdi2GraphException("Invalid relation arc: " + XDIaddress);

		if (! this.isEmpty()) {

			if (XDIDictionaryConstants.XDI_ADD_REF.equals(XDIaddress)) {

				if (! this.containsRelation(XDIDictionaryConstants.XDI_ADD_REF, targetXDIAddress)) throw new Xdi2GraphException("Cannot add " + XDIaddress + "/" + targetXDIAddress + " relation to non-empty context node " + this.getXDIAddress() + ".");
			} else {

				if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REF)) throw new Xdi2GraphException("Cannot add " + XDIaddress + "/" + targetXDIAddress + " relation to context node " + this.getXDIAddress() + ", which already contains a $ref.");
			}

			if (XDIDictionaryConstants.XDI_ADD_REP.equals(XDIaddress)) {

				if (! this.containsRelation(XDIDictionaryConstants.XDI_ADD_REP, targetXDIAddress)) throw new Xdi2GraphException("Cannot add " + XDIaddress + "/" + targetXDIAddress + " relation to non-empty context node " + this.getXDIAddress() + ".");
			} else {

				if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REP)) throw new Xdi2GraphException("Cannot add " + XDIaddress + "/" + targetXDIAddress + " relation to context node " + this.getXDIAddress() + ", which already contains a $rep.");
			}
		}
	}

	/**
	 * Checks if a literal can be created.
	 * Throws an exception, if the literal cannot be created.
	 */
	protected void setLiteralCheckValid(Object literalData) throws Xdi2GraphException {

		if (! XdiAbstractAttribute.isValid(this)) throw new Xdi2GraphException("Can only create a literal in an attribute context.");

		if (! AbstractLiteralNode.isValidLiteralData(literalData)) throw new IllegalArgumentException("Invalid literal data: " + literalData.getClass().getSimpleName());

		if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REF)) throw new Xdi2GraphException("Cannot add literal to context node " + this.getXDIAddress() + " containing a " + XDIDictionaryConstants.XDI_ADD_REF + " relation.");
		if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REP)) throw new Xdi2GraphException("Cannot add literal to context node " + this.getXDIAddress() + " containing a " + XDIDictionaryConstants.XDI_ADD_REP + " relation.");
	}

	/**
	 * When a context node is created, check if the inner root subject and predicate have to be created too.
	 */
	protected void setContextNodeSetInnerRoot(XDIArc XDIarc, ContextNode contextNode) {

		if (XdiInnerRoot.isValidXDIArc(XDIarc)) {

			ContextNode subjectContextNode = (ContextNode) this.setDeepNode(XdiInnerRoot.getSubjectOfInnerRootXDIArc(XDIarc));

			subjectContextNode.setRelation(XdiInnerRoot.getPredicateOfInnerRootXDIArc(XDIarc), contextNode);
		}
	}

	/**
	 * When a relation is created, check if the target node has to be created too.
	 */
	protected Node setRelationSetTargetNode(XDIAddress targetXDIAddress) {

		return this.getGraph().setDeepNode(targetXDIAddress);
	}

	/**
	 * When a context node is deleted, all inner roots have to be deleted too.
	 */
	protected void delContextNodeDelAllInnerRoots() {

		for (Relation relation : this.getAllRelations()) {

			AbstractContextNode contextNode = ((AbstractContextNode) relation.getContextNode());

			contextNode.delRelationDelInnerRoot(relation.getXDIAddress(), relation.getTargetXDIAddress());
		}
	}

	/**
	 * When a context node is deleted, all relations have to be deleted too.
	 */
	protected void delContextNodeDelAllRelations() {

		for (Relation relation : this.getAllRelations()) relation.delete();
	}

	/**
	 * When a context node is deleted, all incoming relations have to be deleted too.
	 */
	protected void delContextNodeDelAllIncomingRelations() {

		for (Relation relation : this.getAllIncomingRelations()) relation.delete();
	}

	/**
	 * When a relation is deleted, its inner root has to be deleted too.
	 */
	protected void delRelationDelInnerRoot(XDIAddress XDIaddress, XDIAddress targetXDIAddress) {

		XdiContext<?> xdiContext = XdiAbstractContext.fromContextNode(this);
		XdiInnerRoot xdiInnerRoot = xdiContext.getXdiInnerRoot(XDIaddress, false);
		if (xdiInnerRoot == null) return;

		if (xdiInnerRoot.getXDIAddress().equals(targetXDIAddress)) {

			this.getGraph().getDeepNode(targetXDIAddress, false).delete();
		}
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getXDIAddress().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof ContextNode)) return false;
		if (object == this) return true;

		ContextNode other = (ContextNode) object;

		// two context nodes are equal if their addresses are equal

		return this.getXDIAddress().equals(other.getXDIAddress());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getXDIAddress().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(ContextNode other) {

		if (other == null || other == this) return 0;

		return this.getXDIAddress().compareTo(other.getXDIAddress());
	}

	/**
	 * A statement for this context node.
	 */

	private final ContextNodeStatement statement = new AbstractContextNodeStatement() {

		private static final long serialVersionUID = 5008355182847367563L;

		@Override
		public XDIAddress getSubject() {

			return AbstractContextNode.this.getContextNode().getXDIAddress();
		}

		@Override
		public XDIArc getObject() {

			return AbstractContextNode.this.getXDIArc();
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
