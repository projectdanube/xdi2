package xdi2.server.impl.graph;

import xdi2.ContextNode;
import xdi2.Graph;
import xdi2.Literal;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.server.ExecutionContext;
import xdi2.xri3.impl.XRI3Authority;

public class LiteralResourceHandler extends AbstractGraphResourceHandler {

	LiteralResourceHandler(Operation operation, Literal operationLiteral, Graph graph) {

		super(operation, operationLiteral, graph);
	}

	@Override
	public boolean executeAdd(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Authority operationContextNodeXri = this.operationLiteral.getContextNode().getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, true);

		contextNode.createLiteral(this.operationLiteral.getLiteralData());

		return true;
	}

	@Override
	public boolean executeMod(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Authority operationContextNodeXri = this.operationLiteral.getContextNode().getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		Literal literal = contextNode.getLiteral();
		if (literal == null) return true;

		literal.setLiteralData(this.operationLiteral.getLiteralData());

		return true;
	}
}
