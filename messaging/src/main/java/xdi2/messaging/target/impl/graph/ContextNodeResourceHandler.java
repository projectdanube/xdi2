package xdi2.messaging.target.impl.graph;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.util.CopyUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

public class ContextNodeResourceHandler extends AbstractGraphResourceHandler {

	ContextNodeResourceHandler(Operation operation, ContextNode operationContextNode, Graph graph) {

		super(operation, operationContextNode, graph);
	}

	@Override
	public boolean executeAdd(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = this.operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, true);

		CopyUtil.copyContextNodeContents(this.operationContextNode, contextNode, null);
		
		return true;
	}

	@Override
	public boolean executeGet(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = this.operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) return true;

		CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);

		return true;
	}

	@Override
	public boolean executeDel(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XRI3Segment operationContextNodeXri = this.operationContextNode.getXri();
		ContextNode contextNode = this.graph.findContextNode(operationContextNodeXri, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node " + operationContextNodeXri + " not found.");

		contextNode.delete();
		
		return true;
	}
}
