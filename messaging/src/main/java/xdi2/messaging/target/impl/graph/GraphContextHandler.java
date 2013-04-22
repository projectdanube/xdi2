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

		try {

			this.getGraph().createStatement(targetStatement);
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot add statement: " + ex.getMessage(), ex, executionContext);
		}
	}

	/*
	 * Operations on context nodes
	 */

	@Override
	public void addContext(XDI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDI3Segment parentXri = XDI3Util.parentXri(contextNodeXri, -1);
		if (parentXri == null) parentXri = XDIConstants.XRI_S_CONTEXT;

		XDI3SubSegment localXri = XDI3Util.localXri(contextNodeXri, 1).getFirstSubSegment();

		ContextNode contextNode = this.getGraph().findContextNode(parentXri, true);
		contextNode.createContextNode(localXri);
	}

	@Override
	public void getContext(XDI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) return;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
	}

	@Override
	public void setContext(XDI3Segment contextNodeXri, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		this.getGraph().findContextNode(contextNodeXri, true);
	}

	@Override
	public void delContext(XDI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) return;

		contextNode.delete();
	}

	/*
	 * Operations on relations
	 */

	@Override
	public void getRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) return;

		if (VariableUtil.isVariable(targetContextNodeXri)) {

			Iterator<Relation> relations = contextNode.getRelations(arcXri);

			if (VariableUtil.isVariable(arcXri)) {

				relations = contextNode.getRelations();
			} else {

				relations = contextNode.getRelations(arcXri);
			}

			while (relations.hasNext()) CopyUtil.copyRelation(relations.next(), messageResult.getGraph(), null);
		} else {

			Relation relation = contextNode.getRelation(arcXri, targetContextNodeXri);
			if (relation == null) return;

			CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
		}
	}

	@Override
	public void setRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, true);

		boolean contains = contextNode.containsRelation(arcXri, targetContextNodeXri);
		if (! contains) contextNode.createRelation(arcXri, targetContextNodeXri);

		return;
	}

	@Override
	public void delRelation(XDI3Segment contextNodeXri, XDI3Segment arcXri, XDI3Segment targetContextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
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
	public void getLiteral(XDI3Segment contextNodeXri, String literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) return;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return;

		if (literalData.isEmpty() || literalData.equals(literal.getLiteralData())) {

			CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
		}
	}

	@Override
	public void modLiteral(XDI3Segment contextNodeXri, String literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeXri, null, executionContext);

		Literal literal = contextNode.getLiteral();
		if (literal == null) throw new Xdi2MessagingException("Literal not found: " + contextNodeXri, null, executionContext);

		literal.setLiteralData(literalData);
	}

	@Override
	public void setLiteral(XDI3Segment contextNodeXri, String literalData, SetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, true);

		Literal literal = contextNode.getLiteral();

		if (literal == null) 
			contextNode.createLiteral(literalData);
		else
			literal.setLiteralData(literalData);
	}

	@Override
	public void delLiteral(XDI3Segment contextNodeXri, String literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) return;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return;

		if (literalData.isEmpty() || literalData.equals(literal.getLiteralData())) {

			literal.delete();
		}
	}
}
