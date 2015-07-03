package xdi2.client.agent.target.impl;

import xdi2.client.XDIClient;
import xdi2.client.agent.target.AgentConnection;
import xdi2.client.impl.local.XDILocalClient;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

final class LocalAgentConnection extends AbstractAgentConnection implements AgentConnection {

	private Graph graph;
	private XDIArc ownerPeerRootXDIArc;

	public LocalAgentConnection(Graph graph, XDIArc ownerPeerRootXDIArc) {

		this.graph = graph;
		this.ownerPeerRootXDIArc = ownerPeerRootXDIArc;
	}

	@Override
	public XDIClient constructXDIClient() {

		return new XDILocalClient(this.graph);
	}

	@Override
	public Message constructMessage(MessageEnvelope messageEnvelope) {

		Message message = super.constructMessage(messageEnvelope);
		message.setToPeerRootXDIArc(this.ownerPeerRootXDIArc);

		return message;
	}
}
