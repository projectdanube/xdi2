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
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiValue;
import xdi2.core.impl.AbstractStatement.AbstractContextNodeStatement;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.AddressUtil;
import xdi2.core.util.iterators.CompositeIterator;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorFirstItem;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingIterator;
import xdi2.core.util.iterators.SingleItemIterator;

public abstract class AbstractContextNode implements ContextNode {

	private static final long serialVersionUID = 7937255194345376190L;

	private Graph graph;
	private ContextNode contextNode;

	private XDIAddress xri;

	public AbstractContextNode(Graph graph, ContextNode contextNode) {

		if (graph == null) throw new NullPointerException();

		this.graph = graph;
		this.contextNode = contextNode;

		this.xri = null;
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
	public ContextNode getContextNode(int arcs) {

		ContextNode contextNode = this;

		for (int i=0; i<arcs; i++) {

			contextNode = contextNode.getContextNode();
			if (contextNode == null) break;
		}

		return contextNode;
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

			this.getContextNode().delContextNode(this.getArc());
		}
	}

	@Override
	public synchronized void clear() {

		this.delContextNodes();
		this.delRelations();
		this.delLiteral();
	}

	@Override
	public boolean isEmpty() {

		return ! (this.containsContextNodes() || this.containsRelations() || this.containsLiteral());
	}

	@Override
	public XDIAddress getAddress() {

		if (this.xri == null) {

			if (this.isRootContextNode()) {

				this.xri = XDIConstants.XDI_ADD_ROOT;
			} else {

				this.xri = AddressUtil.concatAddresses(this.getContextNode().getAddress(), this.getArc());
			}
		}

		return this.xri;
	}

	/*
	 * Methods related to context nodes of this context node
	 */

	//	public ContextNode setContextNode(XDIArc contextNodeArc);

	@Override
	public ContextNode setDeepContextNode(XDIAddress relativecontextNodeAddress) {

		if (relativecontextNodeAddress == null) return this;
		if (XDIConstants.XDI_ADD_ROOT.equals(relativecontextNodeAddress)) return this;

		ContextNode contextNode = this;

		for (XDIArc contextNodeArc : relativecontextNodeAddress.getArcs()) {

			contextNode = contextNode.setContextNode(contextNodeArc);
		}

		return contextNode;
	}

	@Override
	public ContextNode getContextNode(XDIArc relativecontextNodeAddress, boolean subgraph) {

		for (Iterator<ContextNode> contextNodes = this.getContextNodes(); contextNodes.hasNext(); ) {

			ContextNode contextNode = contextNodes.next();

			if (contextNode.getArc().equals(relativecontextNodeAddress)) return contextNode;
		}

		return null;
	}

	@Override
	public ContextNode getContextNode(XDIArc contextNodeArc) {

		return this.getContextNode(contextNodeArc, false);
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress relativecontextNodeAddress, boolean subgraph) {

		if (XDIConstants.XDI_ADD_ROOT.equals(relativecontextNodeAddress)) return this;

		ContextNode contextNode = this;

		for (XDIArc contextNodeArc : relativecontextNodeAddress.getArcs()) {

			contextNode = contextNode.getContextNode(contextNodeArc, subgraph);
			if (contextNode == null) return null;
		}

		return contextNode;
	}

	@Override
	public ContextNode getDeepContextNode(XDIAddress relativecontextNodeAddress) {

		return this.getDeepContextNode(relativecontextNodeAddress, false);
	}

	//	public ReadOnlyIterator<ContextNode> getContextNodes();

	@Override
	public ReadOnlyIterator<ContextNode> getDeepContextNodes(XDIAddress relativecontextNodeAddress) {

		ContextNode contextNode = this.getDeepContextNode(relativecontextNodeAddress, false);
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
	public boolean containsContextNode(XDIArc contextNodeArc) {

		return this.getContextNode(contextNodeArc, false) != null;
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
	public Relation setRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		// set the target context node

		ContextNode targetContextNode = this.setRelationSetTargetContextNode(targetContextNodeAddress);

		// set the relation

		Relation relation = this.setRelation(arc, targetContextNode);

		// done

		return relation;
	}

	@Override
	public Relation setDeepRelation(XDIAddress relativecontextNodeAddress, XDIAddress arc, XDIAddress targetContextNodeAddress) {

		ContextNode contextNode = this.setDeepContextNode(relativecontextNodeAddress);
		if (contextNode == null) return null;

		return contextNode.setRelation(arc, targetContextNodeAddress);
	}

	//	public Relation setRelation(XDIAddress arc, ContextNode targetContextNode) {

	@Override
	public Relation setDeepRelation(XDIAddress relativecontextNodeAddress, XDIAddress arc, ContextNode targetContextNode) {

		ContextNode contextNode = this.setDeepContextNode(relativecontextNodeAddress);
		if (contextNode == null) return null;

		return contextNode.setRelation(arc, targetContextNode);
	}

	@Override
	public Relation getRelation(XDIAddress arc, final XDIAddress targetContextNodeAddress) {

		Iterator<Relation> selectingIterator = new SelectingIterator<Relation> (this.getRelations(arc)) {

			@Override
			public boolean select(Relation relation) {

				return relation.getTargetContextNodeAddress().equals(targetContextNodeAddress);
			}
		};

		return new IteratorFirstItem<Relation> (selectingIterator).item();
	}

	@Override
	public Relation getDeepRelation(XDIAddress relativecontextNodeAddress, XDIAddress arc, XDIAddress targetContextNodeAddress) {

		ContextNode contextNode = this.getDeepContextNode(relativecontextNodeAddress, false);
		if (contextNode == null) return null;

		return contextNode.getRelation(arc, targetContextNodeAddress);
	}

	@Override
	public Relation getRelation(XDIAddress arc) {

		return new IteratorFirstItem<Relation> (this.getRelations(arc)).item();
	}

	@Override
	public Relation getDeepRelation(XDIAddress relativecontextNodeAddress, XDIAddress arc) {

		ContextNode contextNode = this.getDeepContextNode(relativecontextNodeAddress, false);
		if (contextNode == null) return null;

		return contextNode.getRelation(arc);
	}

	@Override
	public ReadOnlyIterator<Relation> getRelations(final XDIAddress arc) {

		return new SelectingIterator<Relation> (this.getRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getAddress().equals(arc);
			}
		};
	}

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress relativecontextNodeAddress, XDIAddress arc) {

		ContextNode contextNode = this.getDeepContextNode(relativecontextNodeAddress, false);
		if (contextNode == null) return new EmptyIterator<Relation> ();

		return contextNode.getRelations(arc);
	}

	//	public ReadOnlyIterator<Relation> getRelations();

	@Override
	public ReadOnlyIterator<Relation> getDeepRelations(XDIAddress relativecontextNodeAddress) {

		ContextNode contextNode = this.getDeepContextNode(relativecontextNodeAddress, false);
		if (contextNode == null) return null;

		return contextNode.getRelations();
	}

	@Override
	public ReadOnlyIterator<Relation> getIncomingRelations(final XDIAddress arc) {

		return new SelectingIterator<Relation> (this.getIncomingRelations()) {

			@Override
			public boolean select(Relation relation) {

				return relation.getAddress().equals(arc);
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
	public boolean containsRelation(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		return this.getRelation(arc, targetContextNodeAddress) != null;
	}

	@Override
	public boolean containsRelations(XDIAddress arc) {

		return this.getRelations(arc).hasNext();
	}

	@Override
	public boolean containsRelations() {

		return this.getRelations().hasNext();
	}

	@Override
	public boolean containsIncomingRelations(XDIAddress arc) {

		return this.getIncomingRelations(arc).hasNext();
	}

	@Override
	public boolean containsIncomingRelations() {

		return this.getIncomingRelations().hasNext();
	}

	@Override
	public void delRelations(XDIAddress arc) {

		for (Relation relation : this.getRelations(arc)) relation.delete();
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
	public long getRelationCount(XDIAddress arc) {

		return new IteratorCounter(this.getRelations(arc)).count();
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

	// public Literal setLiteral(Object literalData);

	@Override
	public Literal setLiteralString(String literalData) {

		return this.setLiteral(literalData);
	}

	@Override
	public Literal setLiteralNumber(Double literalData) {

		return this.setLiteral(literalData);
	}

	@Override
	public Literal setLiteralBoolean(Boolean literalData) {

		return this.setLiteral(literalData);
	}

	@Override
	public Literal setDeepLiteral(XDIAddress relativecontextNodeAddress, Object literalData) {

		ContextNode contextNode = this.setDeepContextNode(relativecontextNodeAddress);
		if (contextNode == null) return null;

		return contextNode.setLiteral(literalData);
	}

	@Override
	public Literal setDeepLiteralString(XDIAddress relativecontextNodeAddress, String literalData) {

		return this.setDeepLiteral(relativecontextNodeAddress, literalData);
	}

	@Override
	public Literal setDeepLiteralNumber(XDIAddress relativecontextNodeAddress, Double literalData) {

		return this.setDeepLiteral(relativecontextNodeAddress, literalData);
	}

	@Override
	public Literal setDeepLiteralBoolean(XDIAddress relativecontextNodeAddress, Boolean literalData) {

		return this.setDeepLiteral(relativecontextNodeAddress, literalData);
	}

	@Override
	public Literal getLiteral(Object literalData) {

		Literal literal = this.getLiteral();
		if (literal == null) return null;

		if (! AbstractLiteral.isLiteralDataEqual(literal.getLiteralData(), literalData)) return null;

		return literal;
	}

	@Override
	public Literal getLiteralString(String literalData) {

		return this.getLiteral(literalData);
	}

	@Override
	public Literal getLiteralNumber(Double literalData) {

		return this.getLiteral(literalData);
	}

	@Override
	public Literal getLiteralBoolean(Boolean literalData) {

		return this.getLiteral(literalData);
	}

	@Override
	public Literal getDeepLiteral(XDIAddress relativecontextNodeAddress, Object literalData) {

		ContextNode contextNode = this.getDeepContextNode(relativecontextNodeAddress, false);
		if (contextNode == null) return null;

		return contextNode.getLiteral(literalData);
	}

	@Override
	public Literal getDeepLiteralString(XDIAddress relativecontextNodeAddress, String literalData) {

		return this.getDeepLiteral(relativecontextNodeAddress, literalData);
	}

	@Override
	public Literal getDeepLiteralNumber(XDIAddress relativecontextNodeAddress, Double literalData) {

		return this.getDeepLiteral(relativecontextNodeAddress, literalData);
	}

	@Override
	public Literal getDeepLiteralBoolean(XDIAddress relativecontextNodeAddress, Boolean literalData) {

		return this.getDeepLiteral(relativecontextNodeAddress, literalData);
	}

	//	public Literal getLiteral();

	@Override
	public Literal getDeepLiteral(XDIAddress relativecontextNodeAddress) {

		ContextNode contextNode = this.getDeepContextNode(relativecontextNodeAddress, false);
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
	public boolean containsLiteral(Object literalData) {

		return this.getLiteral(literalData) != null;
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
	public boolean containsLiteral() {

		return this.getLiteral() != null;
	}

	@Override
	public long getAllLiteralCount() {

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
	public Statement setStatement(XDIStatement statementAddress) {

		// set the statement

		if (statementAddress.isContextNodeStatement()) {

			ContextNode contextNode = this.setDeepContextNode(statementAddress.getTargetContextNodeAddress());

			return contextNode.getStatement();
		} else if (statementAddress.isRelationStatement()) {

			Relation relation = this.setDeepRelation(statementAddress.getContextNodeAddress(), statementAddress.getRelationAddress(), statementAddress.getTargetContextNodeAddress());

			return relation.getStatement();
		} else if (statementAddress.isLiteralStatement()) {

			Literal literal = this.setDeepLiteral(statementAddress.getContextNodeAddress(), statementAddress.getLiteralData());

			return literal.getStatement();
		} else {

			throw new Xdi2GraphException("Invalid statement XRI: " + statementAddress);
		}
	}

	@Override
	public Statement getStatement(XDIStatement statementAddress) {

		ContextNode baseContextNode = this.getDeepContextNode(statementAddress.getSubject(), false);
		if (baseContextNode == null) return null;

		if (statementAddress.isContextNodeStatement()) {

			ContextNode contextNode = baseContextNode.getContextNode(statementAddress.getContextNodeArc(), false);

			return contextNode == null ? null : contextNode.getStatement();
		} else if (statementAddress.isRelationStatement()) {

			Relation relation = baseContextNode.getRelation(statementAddress.getRelationAddress(), statementAddress.getTargetContextNodeAddress());

			return relation == null ? null : relation.getStatement();
		} else if (statementAddress.isLiteralStatement()) {

			Literal literal = baseContextNode.getLiteral(statementAddress.getLiteralData());

			return literal == null ? null : literal.getStatement();
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
	public boolean containsStatement(XDIStatement statementAddress) {

		return this.getStatement(statementAddress) != null;
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
	protected void setContextNodeCheckValid(XDIArc arc) throws Xdi2GraphException {

		if (arc == null) throw new NullPointerException();

		if (XDIConstants.XDI_ADD_CONTEXT.equals(arc)) throw new Xdi2GraphException("Invalid context node arc: " + arc);

		if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REF)) throw new Xdi2GraphException("Cannot add " + arc + " context node to context node " + this.getAddress() + " containing a " + XDIDictionaryConstants.XDI_ADD_REF + " relation.");
		if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REP)) throw new Xdi2GraphException("Cannot add " + arc + " context node to context node " + this.getAddress() + " containing a " + XDIDictionaryConstants.XDI_ADD_REP + " relation.");

		ContextNode tempContextNode = new BasicContextNode(this.getGraph(), this, arc, null, null, null);
		if (! XdiAbstractContext.isValid(tempContextNode)) throw new Xdi2GraphException("Invalid subgraph: " + arc + " under subgraph " + this.getAddress());
	}

	/**
	 * Checks if a relation can be created.
	 * Throws an exception, if the relation cannot be created.
	 */
	protected void setRelationCheckValid(XDIAddress arc, XDIAddress targetContextNodeAddress) throws Xdi2GraphException {

		if (arc == null) throw new NullPointerException();
		if (targetContextNodeAddress == null) throw new NullPointerException();

		if (XDIConstants.XDI_ADD_CONTEXT.equals(arc)) throw new Xdi2GraphException("Invalid relation arc: " + arc);
		if (XDIConstants.XDI_ADD_LITERAL.equals(arc)) throw new Xdi2GraphException("Invalid relation arc: " + arc);

		if (! this.isEmpty()) {

			if (XDIDictionaryConstants.XDI_ADD_REF.equals(arc)) {

				if (! this.containsRelation(XDIDictionaryConstants.XDI_ADD_REF, targetContextNodeAddress)) throw new Xdi2GraphException("Cannot add " + arc + "/" + targetContextNodeAddress + " relation to non-empty context node " + this.getAddress() + ".");
			} else {

				if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REF)) throw new Xdi2GraphException("Cannot add " + arc + "/" + targetContextNodeAddress + " relation to context node " + this.getAddress() + ", which already contains a $ref.");
			}

			if (XDIDictionaryConstants.XDI_ADD_REP.equals(arc)) {

				if (! this.containsRelation(XDIDictionaryConstants.XDI_ADD_REP, targetContextNodeAddress)) throw new Xdi2GraphException("Cannot add " + arc + "/" + targetContextNodeAddress + " relation to non-empty context node " + this.getAddress() + ".");
			} else {

				if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REP)) throw new Xdi2GraphException("Cannot add " + arc + "/" + targetContextNodeAddress + " relation to context node " + this.getAddress() + ", which already contains a $rep.");
			}
		}
	}

	/**
	 * Checks if a literal can be created.
	 * Throws an exception, if the literal cannot be created.
	 */
	protected void setLiteralCheckValid(Object literalData) throws Xdi2GraphException {

		if (! XdiValue.isValid(this)) throw new Xdi2GraphException("Can only create a literal in a value context.");

		if (! AbstractLiteral.isValidLiteralData(literalData)) throw new IllegalArgumentException("Invalid literal data: " + literalData.getClass().getSimpleName());

		if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REF)) throw new Xdi2GraphException("Cannot add literal to context node " + this.getAddress() + " containing a " + XDIDictionaryConstants.XDI_ADD_REF + " relation.");
		if (this.containsRelations(XDIDictionaryConstants.XDI_ADD_REP)) throw new Xdi2GraphException("Cannot add literal to context node " + this.getAddress() + " containing a " + XDIDictionaryConstants.XDI_ADD_REP + " relation.");
	}

	/**
	 * When a context node is created, check if the inner root subject and predicate have to be created too.
	 */
	protected void setContextNodeSetInnerRoot(XDIArc arc, ContextNode contextNode) {

		if (XdiInnerRoot.isInnerRootarc(arc)) {

			this.setDeepContextNode(XdiInnerRoot.getSubjectOfInnerRootAddress(arc)).setRelation(XdiInnerRoot.getPredicateOfInnerRootAddress(arc), contextNode);
		}
	}

	/**
	 * When a relation is created, check if the target context node has to be created too.
	 */
	protected ContextNode setRelationSetTargetContextNode(XDIAddress targetContextNodeAddress) {

		return this.getGraph().setDeepContextNode(targetContextNodeAddress);
	}

	/**
	 * When a context node is deleted, all inner roots have to be deleted too.
	 */
	protected void delContextNodeDelAllInnerRoots() {

		for (Relation relation : this.getAllRelations()) {

			AbstractContextNode contextNode = ((AbstractContextNode) relation.getContextNode());

			contextNode.delRelationDelInnerRoot(relation.getAddress(), relation.getTargetContextNodeAddress());
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
	protected void delRelationDelInnerRoot(XDIAddress arc, XDIAddress targetContextNodeAddress) {

		if (XdiInnerRoot.createInnerRootarc(this.getAddress(), arc).equals(targetContextNodeAddress)) {

			this.getGraph().getDeepContextNode(targetContextNodeAddress, false).delete();
		}
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getAddress().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof ContextNode)) return false;
		if (object == this) return true;

		ContextNode other = (ContextNode) object;

		// two context nodes are equal if their XRIs are equal

		return this.getAddress().equals(other.getAddress());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getAddress().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(ContextNode other) {

		if (other == null || other == this) return 0;

		return this.getAddress().compareTo(other.getAddress());
	}

	/**
	 * A statement for this context node.
	 */

	private final ContextNodeStatement statement = new AbstractContextNodeStatement() {

		private static final long serialVersionUID = 5008355182847367563L;

		@Override
		public XDIAddress getSubject() {

			return AbstractContextNode.this.getContextNode().getAddress();
		}

		@Override
		public XDIArc getObject() {

			return AbstractContextNode.this.getArc();
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
