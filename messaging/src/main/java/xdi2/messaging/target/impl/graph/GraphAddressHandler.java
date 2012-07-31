package xdi2.messaging.target.impl.graph;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.util.CopyUtil;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.DelOperation;
import xdi2.messaging.GetOperation;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractAddressHandler;
import xdi2.messaging.target.ExecutionContext;

public class GraphAddressHandler extends AbstractAddressHandler {

	private Graph graph;

	GraphAddressHandler(Graph graph) {

		super();

		this.graph = graph;
	}

	public Graph getGraph() {
		
		return this.graph;
	}
	
	@Override
	public boolean executeGetOnAddress(XRI3Segment targetAddress, GetOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(targetAddress, false);

		if (contextNode != null) {

			CopyUtil.copyContextNode(contextNode, messageResult.getGraph(), null);
		}

		return false;
	}

	@Override
	public boolean executeDelOnAddress(XRI3Segment targetAddress, DelOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		ContextNode contextNode = this.getGraph().findContextNode(targetAddress, false);
		if (contextNode == null) throw new Xdi2MessagingException("Context node not found: " + targetAddress, null, operation);

		contextNode.delete();

		return false;
	}
}
