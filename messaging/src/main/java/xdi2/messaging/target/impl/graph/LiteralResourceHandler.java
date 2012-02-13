package xdi2.messaging.target.impl.graph;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.Literal;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.util.CopyUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

public class LiteralResourceHandler extends AbstractGraphResourceHandler {

	LiteralResourceHandler(Operation operation, Literal operationLiteral, Graph graph) {

		super(operation, operationLiteral, graph);
	}

	@Override
	public boolean executeAdd(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = this.operationLiteral.getContextNode().getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, true);

		contextNode.createLiteral(this.operationLiteral.getLiteralData());

		return true;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = this.operationLiteral.getContextNode().getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return true;

		if (this.operationLiteral.getLiteralData().equals(literal.getLiteralData())) {

			CopyUtil.copyLiteral(literal, messageResult.getGraph(), null);
		}

		return true;
	}

	@Override
	public boolean executeMod(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = this.operationLiteral.getContextNode().getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return true;

		literal.setLiteralData(this.operationLiteral.getLiteralData());

		return true;
	}
}
