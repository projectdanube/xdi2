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
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
import xdi2.messaging.SetOperation;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractContextHandler;
import xdi2.messaging.target.ExecutionContext;

public class GraphContextHandler extends AbstractContextHandler {

	private Graph graph;

	GraphContextHandler(Graph graph) {

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
	public void executeAddOnStatement(XDI3Statement targetStatement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().createStatement(targetStatement);
	}

	@Override
	public void executeSetOnStatement(XDI3Statement targetStatement, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().setStatement(targetStatement);
	}

	/*
	 * Operations on context nodes
	 */

	@Override
	public void getContext(XDI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) return;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
	}

	@Override
	public void addContext(XDI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().createDeepContextNode(contextNodeXri);
	}

	@Override
	public void setContext(XDI3Segment contextNodeXri, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().setDeepContextNode(contextNodeXri);
	}

	@Override
	public void delContext(XDI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (XDIConstants.XRI_S_ROOT.equals(contextNodeXri)) {

			this.getGraph().clear();
		} else if (contextNodeXri.getNumSubSegments() == 1) {

			this.getGraph().getRootContextNode().deleteContextNode(contextNodeXri.getFirstSubSegment());
		} else {

			XDI3Segment parentContextNodeXri = XDI3Util.parentXri(contextNodeXri, -1);
			XDI3SubSegment localContextNodeArcXri = XDI3Util.localXri(contextNodeXri, 1).getFirstSubSegment();

			ContextNode parentContextNode = this.getGraph().getDeepContextNode(parentContextNodeXri);
			if (parentContextNode == null) return;

			parentContextNode.deleteContextNode(localContextNodeArcXri);
		}
	}

	/*
	 * Operations on relations
	 */

	@Override
	public void getRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (VariableUtil.isVariable(targetContextNodeXri)) {

			Iterator<Relation> relations;

			if (VariableUtil.isVariable(arcXri)) {

				relations = this.getGraph().getDeepRelations(contextNodeXri);
			} else {

				relations = this.getGraph().getDeepRelations(contextNodeXri, arcXri);
			}

			while (relations.hasNext()) CopyUtil.copyRelation(relations.next(), messageResult.getGraph(), null);
		} else {

			Relation relation = this.getGraph().getDeepRelation(contextNodeXri, arcXri, targetContextNodeXri);
			if (relation == null) return;

			CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
		}
	}

	@Override
	public void delRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) return;

		if (VariableUtil.isVariable(targetContextNodeXri)) {

			if (VariableUtil.isVariable(arcXri)) {

				contextNode.deleteRelations();
			} else {

				contextNode.deleteRelations(arcXri);
			}
		} else {

			contextNode.deleteRelation(arcXri, targetContextNodeXri);
		}
	}

	/*
	 * Operations on literals
	 */

	@Override
	public void getLiteral(XDI3Segment contextNodeXri, Object literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) return;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return;

		if (literalData.equals(literal.getLiteralData())) {

			CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
		}
	}

	@Override
	public void modLiteral(XDI3Segment contextNodeXri, Object literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeXri, null, executionContext);

		Literal literal = contextNode.getLiteral();
		if (literal == null) throw new Xdi2MessagingException("Literal not found: " + contextNodeXri, null, executionContext);

		literal.setLiteralData(literalData);
	}

	@Override
	public void delLiteral(XDI3Segment contextNodeXri, Object literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().getDeepContextNode(contextNodeXri);
		if (contextNode == null) return;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return;

		if (literalData.equals(literal.getLiteralData())) {

			literal.delete();
		}
	}
}
