package xdi2.messaging.target.impl.graph;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.Statement.ContextNodeStatement;
import xdi2.core.Statement.LiteralStatement;
import xdi2.core.Statement.RelationStatement;
import xdi2.core.features.variables.Variables;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIUtil;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractStatementHandler;
import xdi2.messaging.target.ExecutionContext;

@Deprecated
public class GraphStatementHandler extends AbstractStatementHandler {

	private Graph graph;

	GraphStatementHandler(Graph graph) {

		super();

		this.graph = graph;
	}

	public Graph getGraph() {

		return this.graph;
	}

	/*
	 * Operations on all types of statements
	 */

	@Override
	public void executeAddOnStatement(Statement statement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		try {

			this.getGraph().addStatement(statement);
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot add statement: " + ex.getMessage(), ex, executionContext);
		}

		return;
	}

	/*
	 * Operations on context node statements
	 */

	@Override
	public void executeGetOnContextNodeStatement(ContextNodeStatement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeStatement.getSubject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeStatement, null, executionContext);

		contextNode = contextNode.findContextNode(contextNodeStatement.getObject(), false);
		if (contextNode == null) return;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);

		return;
	}

	@Override
	public void executeDelOnContextNodeStatement(ContextNodeStatement contextNodeStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeStatement.getSubject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeStatement, null, executionContext);

		contextNode = contextNode.findContextNode(contextNodeStatement.getObject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeStatement, null, executionContext);

		contextNode.delete();

		return;
	}

	/*
	 * Operations on relation statements
	 */

	@Override
	public void executeGetOnRelationStatement(RelationStatement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(relationStatement.getSubject(), false);
		if (contextNode == null) return;

		if (Variables.isVariableSingle(relationStatement.getObject())) {

			Iterator<Relation> relations;

			if (Variables.isVariableSingle(relationStatement.getPredicate())) {

				relations = contextNode.getRelations();
			} else {

				relations = contextNode.getRelations(relationStatement.getPredicate());
			}

			while (relations.hasNext()) CopyUtil.copyRelation(relations.next(), messageResult.getGraph(), null);
		} else {

			Relation relation = contextNode.getRelation(relationStatement.getPredicate(), relationStatement.getObject());
			if (relation == null) return;

			CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
		}
	}

	@Override
	public void executeDelOnRelationStatement(RelationStatement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(relationStatement.getSubject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + relationStatement, null, executionContext);

		if (Variables.isVariableSingle(relationStatement.getObject())) {

			if (Variables.isVariableSingle(relationStatement.getPredicate())) {

				contextNode.deleteRelations();
			} else {

				contextNode.deleteRelations(relationStatement.getPredicate());
			}
		} else {

			contextNode.deleteRelation(relationStatement.getPredicate(), relationStatement.getObject());
		}
	}

	/*
	 * Operations on literal statements
	 */

	@Override
	public void executeGetOnLiteralStatement(LiteralStatement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(literalStatement.getSubject(), false);
		if (contextNode == null) return;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return;

		String literalStatementData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());
		
		if (literalStatementData.isEmpty() || literalStatementData.equals(literal.getLiteralData())) {

			CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
		}
	}

	@Override
	public void executeModOnLiteralStatement(LiteralStatement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(literalStatement.getSubject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + literalStatement, null, executionContext);

		Literal literal = contextNode.getLiteral();
		if (literal == null) throw new Xdi2MessagingException("Literal not found: " + literalStatement, null, executionContext);

		literal.setLiteralData(XDIUtil.dataXriSegmentToString(literalStatement.getObject()));
	}

	@Override
	public void executeDelOnLiteralStatement(LiteralStatement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(literalStatement.getSubject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + literalStatement, null, executionContext);

		Literal literal = contextNode.getLiteral();
		if (literal == null) throw new Xdi2MessagingException("Literal not found: " + literalStatement, null, executionContext);

		String literalStatementData = XDIUtil.dataXriSegmentToString(literalStatement.getObject());

		if (literalStatementData.isEmpty() || literalStatementData.equals(literal.getLiteralData())) {

			literal.delete();
		}
	}
}
