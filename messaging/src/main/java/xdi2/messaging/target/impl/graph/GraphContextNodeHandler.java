package xdi2.messaging.target.impl.graph;

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
	public boolean executeGetOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! operationContextNode.isEmpty()) return false;

		XRI3Segment operationContextNodeXri = operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);

		return true;
	}

	@Override
	public boolean executeDelOnContextNode(ContextNode operationContextNode, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (! operationContextNode.isEmpty()) return false;

		XRI3Segment operationContextNodeXri = operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node " + operationContextNodeXri + " not found.");

		contextNode.delete();

		return true;
	}

	/*
	 * Operations on relations
	 */

	@Override
	public boolean executeGetOnRelation(ContextNode operationContextNode, Relation operationRelation, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		boolean isRelationXriVariable = VariablesUtil.isVariable(operationRelation.getRelationXri());

		if (operationRelation.getArcXri().equals(XDIConstants.XRI_S_LITERAL)) {

			if (isRelationXriVariable) {

				Literal literal = contextNode.getLiteral();
				if (literal == null) return true;

				CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
			}
		} else {

			Relation relation = contextNode.getRelation(operationRelation.getArcXri());
			if (relation == null) return true;

			if (isRelationXriVariable || operationRelation.getRelationXri().equals(relation.getRelationXri())) {

				CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
			}
		}

		return true;
	}

	@Override
	public boolean executeDelOnRelation(ContextNode operationContextNode, Relation operationRelation, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		Relation relation = contextNode.getRelation(operationRelation.getArcXri());
		if (relation == null) return true;

		if (VariablesUtil.isVariable(operationRelation.getRelationXri()) ||
				operationRelation.getRelationXri().equals(relation.getRelationXri())) {

			relation.delete();
		}

		return true;
	}

	/*
	 * Operations on literals
	 */

	@Override
	public boolean executeGetOnLiteral(ContextNode operationContextNode, Literal operationLiteral, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return true;

		if (operationLiteral.getLiteralData().equals(literal.getLiteralData())) {

			CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
		}

		return true;
	}

	@Override
	public boolean executeModOnLiteral(ContextNode operationContextNode, Literal operationLiteral, Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return true;

		literal.setLiteralData(operationLiteral.getLiteralData());

		return true;
	}
}
