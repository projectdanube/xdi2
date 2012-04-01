package xdi2.messaging.target.impl.graph;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIConstants;
import xdi2.core.variables.VariablesUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.impl.AbstractContextNodeHandler;

public class GraphContextNodeHandler extends AbstractContextNodeHandler {

	private Graph graph;

	GraphContextNodeHandler(Graph graph) {

		super();

		this.graph = graph;
	}

	/*
	 * Operations on context nodes
	 */

	@Override
	public boolean executeGetOnContextNode(ContextNode targetContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! targetContextNode.isEmpty()) return false;

		XRI3Segment targetContextNodeXri = targetContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(targetContextNodeXri, false);
		if (contextNode == null) return true;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);

		return true;
	}

	@Override
	public boolean executeDelOnContextNode(ContextNode targetContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! targetContextNode.isEmpty()) return false;

		XRI3Segment targetContextNodeXri = targetContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(targetContextNodeXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node " + targetContextNodeXri + " not found.");

		contextNode.delete();

		return true;
	}

	/*
	 * Operations on relations
	 */

	@Override
	public boolean executeGetOnRelation(ContextNode targetContextNode, Relation targetRelation, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment targetContextNodeXri = targetContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(targetContextNodeXri, false);
		if (contextNode == null) return true;

		boolean isRelationXriVariable = VariablesUtil.isVariable(targetRelation.getRelationXri());

		if (targetRelation.getArcXri().equals(XDIConstants.XRI_S_LITERAL)) {

			if (isRelationXriVariable) {

				Literal literal = contextNode.getLiteral();
				if (literal == null) return true;

				CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
			}
		} else {

			Iterator<Relation> relations = contextNode.getRelations(targetRelation.getArcXri());

			while (relations.hasNext()) {

				Relation relation = relations.next();

				if (isRelationXriVariable || targetRelation.getRelationXri().equals(relation.getRelationXri())) {

					CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
				}
			}
		}

		return true;
	}

	@Override
	public boolean executeDelOnRelation(ContextNode targetContextNode, Relation targetRelation, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment targetContextNodeXri = targetContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(targetContextNodeXri, false);
		if (contextNode == null) return true;

		boolean isRelationXriVariable = VariablesUtil.isVariable(targetRelation.getRelationXri());

		Iterator<Relation> relations = contextNode.getRelations(targetRelation.getArcXri());

		while (relations.hasNext()) {

			Relation relation = relations.next();

			if (isRelationXriVariable || targetRelation.getRelationXri().equals(relation.getRelationXri())) {

				relation.delete();
			}
		}

		return true;
	}

	/*
	 * Operations on literals
	 */

	@Override
	public boolean executeGetOnLiteral(ContextNode targetContextNode, Literal targetLiteral, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment targetContextNodeXri = targetContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(targetContextNodeXri, false);
		if (contextNode == null) return true;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return true;

		if (targetLiteral.getLiteralData().equals(literal.getLiteralData())) {

			CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
		}

		return true;
	}

	@Override
	public boolean executeModOnLiteral(ContextNode targetContextNode, Literal targetLiteral, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment targetContextNodeXri = targetContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(targetContextNodeXri, false);
		if (contextNode == null) return true;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return true;

		literal.setLiteralData(targetLiteral.getLiteralData());

		return true;
	}
}
