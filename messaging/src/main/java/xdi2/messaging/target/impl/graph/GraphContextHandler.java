package xdi2.messaging.target.impl.graph;

import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.Relation;
import xdi2.core.Statement;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.variables.Variables;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XRIUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.XRI3SubSegment;
import xdi2.messaging.AddOperation;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.ModOperation;
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
	public void executeAddOnStatement(Statement statement, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		try {

			this.getGraph().addStatement(statement);
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot add statement: " + ex.getMessage(), ex, executionContext);
		}
	}

	/*
	 * Operations on context nodes
	 */

	@Override
	public void addContext(XRI3Segment contextNodeXri, AddOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment parentXri = XRIUtil.parentXri(contextNodeXri);
		if (parentXri == null) parentXri = XDIConstants.XRI_S_CONTEXT;

		XRI3SubSegment localXri = XRIUtil.localXri(contextNodeXri);

		ContextNode contextNode = this.getGraph().findContextNode(parentXri, true);
		contextNode.createContextNode(localXri);
	}

	@Override
	public void getContext(XRI3Segment contextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) return;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
	}

	@Override
	public void delContext(XRI3Segment contextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeXri, null, executionContext);

		contextNode.delete();
	}

	/*
	 * Operations on relations
	 */

	@Override
	public void getRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) return;

		boolean isObjectVariable = Variables.isVariable(targetContextNodeXri);

		if (arcXri.equals(XDIConstants.XRI_S_LITERAL)) {

			if (isObjectVariable) {

				Literal literal = contextNode.getLiteral();
				if (literal == null) return;

				CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
			}
		} else {

			if (isObjectVariable) {

				Iterator<Relation> relations = contextNode.getRelations(arcXri);

				while (relations.hasNext()) CopyUtil.copyRelation(relations.next(), messageResult.getGraph(), null);
			} else {

				Relation relation = contextNode.getRelation(arcXri, targetContextNodeXri);
				if (relation == null) return;

				CopyUtil.copyRelation(relation, messageResult.getGraph(), null);
			}
		}
	}

	@Override
	public void delRelation(XRI3Segment contextNodeXri, XRI3Segment arcXri, XRI3Segment targetContextNodeXri, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeXri, null, executionContext);

		if (Variables.isVariable(targetContextNodeXri)) {

			if (contextNode.containsRelations()) {

				contextNode.deleteRelations(arcXri);
			} else if (contextNode.containsLiteral()) {

				contextNode.deleteLiteral();
			}
		} else {

			contextNode.deleteRelation(arcXri, targetContextNodeXri);
		}
	}

	/*
	 * Operations on literals
	 */

	@Override
	public void getLiteral(XRI3Segment contextNodeXri, String literalData, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) return;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return;

		if (literalData.equals(literal.getLiteralData())) {

			CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
		}
	}

	@Override
	public void modLiteral(XRI3Segment contextNodeXri, String literalData, ModOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeXri, null, executionContext);

		Literal literal = contextNode.getLiteral();
		if (literal == null) throw new Xdi2MessagingException("Literal not found: " + contextNodeXri, null, executionContext);

		literal.setLiteralData(literalData);
	}

	@Override
	public void delLiteral(XRI3Segment contextNodeXri, String literalData, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(contextNodeXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + contextNodeXri, null, executionContext);

		Literal literal = contextNode.getLiteral();
		if (literal == null) throw new Xdi2MessagingException("Literal not found: " + contextNodeXri, null, executionContext);

		if (literalData.equals(literal.getLiteralData())) {

			literal.delete();
		}
	}
}
