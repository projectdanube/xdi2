package xdi2.messaging.target.impl.graph;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.constants.XDIConstants;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.VariableUtil;
import xdi2.core.util.XDI3Util;
import xdi2.core.xri3.XDI3Segment;
import xdi2.core.xri3.XDI3Statement;
import xdi2.core.xri3.XDI3SubSegment;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.SetOperation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractContextHandler;
import xdi2.messaging.target.ExecutionContext;

public class GraphContextHandler extends AbstractContextHandler {

	private Graph graph;

	public GraphContextHandler(Graph graph) {

		super();

		this.graph = graph;
	}

	public Graph getGraph() {

		return this.graph;
	}

	/*
	 * Operations on statements
	 */

	@Override
	public void executeSetOnStatement(XDI3Statement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().setStatement(targetStatement);
	}

	/*
	 * Operations on addresses
	 */

	@Override
	public void executeGetOnAddress(XDI3Segment targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().getDeepContextNode(targetAddress);
		if (contextNode == null) return;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
	}

	@Override
	public void executeSetOnAddress(XDI3Segment contextNodeXri, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().setDeepContextNode(contextNodeXri);
	}

	@Override
	public void executeDelOnAddress(XDI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (XDIConstants.XRI_S_ROOT.equals(contextNodeXri)) {

			this.getGraph().clear();
		} else if (contextNodeXri.getNumSubSegments() == 1) {

			this.getGraph().getRootContextNode().delContextNode(contextNodeXri.getFirstSubSegment());
		} else {

			XDI3Segment parentContextNodeXri = XDI3Util.parentXri(contextNodeXri, -1);
			XDI3SubSegment arcXri = XDI3Util.localXri(contextNodeXri, 1).getFirstSubSegment();

			ContextNode parentContextNode = this.getGraph().getDeepContextNode(parentContextNodeXri);
			if (parentContextNode == null) return;

			parentContextNode.delContextNode(arcXri);
		}
	}

	/*
	 * Operations on context node statements
	 */

	@Override
	public void executeGetOnContextNodeStatement(XDI3Statement contextNodeStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = contextNodeStatement.getContextNodeXri();
		XDI3SubSegment arcXri  = contextNodeStatement.getContextNodeArcXri();

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) return;

		ContextNode innerContextNode = contextNode.getContextNode(arcXri);
		if (innerContextNode == null) return;

		CopyUtil.copyStatement(innerContextNode.getStatement(), messageResult.getGraph(), null);
	}

	/*
	 * Operations on relation statements
	 */

	@Override
	public void executeGetOnRelationStatement(XDI3Statement relationStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		if (VariableUtil.isVariable(targetContextNodeXri)) {

			Iterator<Relation> relations;

			if (VariableUtil.isVariable(arcXri)) {

				relations = this.getGraph().getDeepRelations(contextNodeXri);
			} else {

				relations = this.getGraph().getDeepRelations(contextNodeXri, arcXri);
			}

			while (relations.hasNext()) CopyUtil.copyStatement(relations.next().getStatement(), messageResult.getGraph(), null);
		} else {

			Relation relation = this.getGraph().getDeepRelation(contextNodeXri, arcXri, targetContextNodeXri);
			if (relation == null) return;

			CopyUtil.copyStatement(relation.getStatement(), messageResult.getGraph(), null);
		}
	}

	@Override
	public void executeSetOnRelationStatement(XDI3Statement relationStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		this.getGraph().setDeepRelation(contextNodeXri, arcXri, targetContextNodeXri);
	}

	@Override
	public void executeDelOnRelationStatement(XDI3Statement relationStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = relationStatement.getContextNodeXri();
		XDI3Segment arcXri = relationStatement.getRelationArcXri();
		XDI3Segment targetContextNodeXri = relationStatement.getTargetContextNodeXri();

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) return;

		if (VariableUtil.isVariable(targetContextNodeXri)) {

			if (VariableUtil.isVariable(arcXri)) {

				contextNode.delRelations();
			} else {

				contextNode.delRelations(arcXri);
			}
		} else {

			contextNode.delRelation(arcXri, targetContextNodeXri);
		}
	}

	/*
	 * Operations on literal statements
	 */

	@Override
	public void executeGetOnLiteralStatement(XDI3Statement literalStatement, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		Object literalData = literalStatement.getLiteralData();

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) return;

		Literal literal = contextNode.getLiteral(literalData);
		if (literal == null) return;

		CopyUtil.copyStatement(literal.getStatement(), messageResult.getGraph(), null);
	}

	@Override
	public void executeSetOnLiteralStatement(XDI3Statement literalStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		Object literalData = literalStatement.getLiteralData();

		this.getGraph().setDeepLiteral(contextNodeXri, literalData);
	}

	@Override
	public void executeDelOnLiteralStatement(XDI3Statement literalStatement, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment contextNodeXri = literalStatement.getContextNodeXri();
		Object literalData = literalStatement.getLiteralData();

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) return;

		Literal literal = contextNode.getLiteral(literalData);
		if (literal == null) return;

		literal.delete();
	}
}
