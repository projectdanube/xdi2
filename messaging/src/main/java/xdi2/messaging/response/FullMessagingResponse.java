package xdi2.messaging.response;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;

/**
 * A message envelope as an XDI messaging response.
 * 
 * @author markus
 */
public class FullMessagingResponse extends TransportMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -150908814464607155L;

	private MessageEnvelope messageEnvelope;
	private Graph resultGraph;

	private FullMessagingResponse(MessageEnvelope messageEnvelope, Graph resultGraph) {

		this.messageEnvelope = messageEnvelope;
		this.resultGraph = resultGraph;
	}

	/*
	 * Static methods
	 */

	public static boolean isValid(Graph graph) {

		MessageEnvelope messageEnvelope = MessageEnvelope.fromGraph(graph);
		if (messageEnvelope == null) return false;

		if (! messageEnvelope.getMessages().hasNext()) return false;

		Message message = messageEnvelope.getMessages().next();

		if (message.getOperationsContextNode() == null) return false;
		if (message.getFromPeerRootXDIArc() == null) return false;
		if (message.getToPeerRootXDIArc() == null) return false;

		return true;
	}

	public static FullMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return fromMessageEnvelope(MessageEnvelope.fromGraph(graph));
	}

	public static FullMessagingResponse fromMessageEnvelope(MessageEnvelope messageEnvelope) {

		Graph resultGraph = MemoryGraphFactory.getInstance().openGraph();

		for (Operation operation : messageEnvelope.getOperations()) {

			XdiInnerRoot xdiInnerRoot = operation.getTargetXdiInnerRoot();
			if (xdiInnerRoot == null) continue;

			CopyUtil.copyContextNodeContents(xdiInnerRoot.getContextNode(), resultGraph, null);
		}

		FullMessagingResponse messageEnvelopeMessagingResponse = new FullMessagingResponse(messageEnvelope, resultGraph);

		return messageEnvelopeMessagingResponse;
	}

	/*
	 * Overrides
	 */

	@Override
	public Graph getGraph() {

		return this.getMessageEnvelope().getGraph();
	}

	@Override
	public Graph getResultGraph() {

		return this.resultGraph;
	}

	/*
	 * Instance methods
	 */

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}
}
