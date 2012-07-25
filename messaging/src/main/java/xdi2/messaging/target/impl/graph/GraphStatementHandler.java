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
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.variables.Variables;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIUtil;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.AbstractStatementHandler;

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
	public boolean executeAddOnStatement(Statement statement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		try {

			this.getGraph().addStatement(statement);
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot add statement: " + ex.getMessage(), ex, operation);
		}

		return true;
	}

	/*
	 * Operations on context node statements
	 */

	@Override
	public boolean executeDelOnContextNodeStatement(ContextNodeStatement contextNodeStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeStatement.getSubject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeStatement, null, operation);

		for (Object subSegment : contextNodeStatement.getObject().getSubSegments()) {

			contextNode = contextNode.getContextNode((XRI3SubSegment) subSegment);
		}

		contextNode.delete();

		return true;
	}

	/*
	 * Operations on relation statements
	 */

	@Override
	public boolean executeGetOnRelationStatement(RelationStatement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(relationStatement.getSubject(), false);
		if (contextNode == null) return true;

		boolean isRelationXriVariable = Variables.isVariable(relationStatement.getObject());

		if (relationStatement.getPredicate().equals(XDIConstants.XRI_S_LITERAL)) {

			if (isRelationXriVariable) {

				Literal literal = contextNode.getLiteral();
				if (literal == null) return true;

				CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
			}
		} else {

			Iterator<Relation> relations = contextNode.getRelations(relationStatement.getPredicate());

			while (relations.hasNext()) {

				Relation relation = relations.next();

				if (isRelationXriVariable || relationStatement.getObject().equals(relation.getRelationXri())) {

					CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
				}
			}
		}

		return true;
	}

	@Override
	public boolean executeDelOnRelationStatement(RelationStatement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(relationStatement.getSubject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + relationStatement, null, operation);

		if (Variables.isVariable(relationStatement.getObject())) {

			Iterator<Relation> relations = contextNode.getRelations(relationStatement.getPredicate());
			if (! relations.hasNext()) throw new Xdi2MessagingException("Relations not found: " + relationStatement, null, operation);

			while (relations.hasNext()) relations.next().delete();
		} else {

			Relation relation = contextNode.getRelation(relationStatement.getPredicate(), relationStatement.getObject());
			if (relation == null) throw new Xdi2MessagingException("Relation not found: " + relationStatement, null, operation);

			relation.delete();
		}

		return true;
	}

	/*
	 * Operations on literal statements
	 */

	@Override
	public boolean executeGetOnLiteralStatement(LiteralStatement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(literalStatement.getSubject(), false);
		if (contextNode == null) return true;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return true;

		if (XDIUtil.dataXriSegmentToString(literalStatement.getObject()).equals(literal.getLiteralData())) {

			CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
		}

		return true;
	}

	@Override
	public boolean executeModOnLiteralStatement(LiteralStatement literalStatement, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(literalStatement.getSubject(), false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + literalStatement, null, operation);

		Literal literal = contextNode.getLiteral();
		if (literal == null) throw new Xdi2MessagingException("Literal not found: " + literalStatement, null, operation);

		literal.setLiteralData(XDIUtil.dataXriSegmentToString(literalStatement.getObject()));

		return true;
	}
}
