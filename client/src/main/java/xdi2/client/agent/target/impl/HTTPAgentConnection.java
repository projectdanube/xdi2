package xdi2.client.agent.target.impl;

import java.net.URL;

import xdi2.client.XDIClient;
import xdi2.client.agent.target.AgentConnection;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.syntax.CloudNumber;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

final class HTTPAgentConnection extends AbstractAgentConnection implements AgentConnection {

	private CloudNumber cloudNumber;
	private URL xdiEndpointUrl;

	public HTTPAgentConnection(CloudNumber cloudNumber, URL xdiEndpointUrl) {

		this.cloudNumber = cloudNumber;
		this.xdiEndpointUrl = xdiEndpointUrl;
	}

	@Override
	public XDIClient constructXDIClient() {

		return new XDIHttpClient(this.xdiEndpointUrl);
	}

	@Override
	public Message constructMessage(MessageEnvelope messageEnvelope) {

		Message message = super.constructMessage(messageEnvelope);
		message.setToPeerRootXDIArc(this.cloudNumber.getPeerRootXDIArc());
		
		return message;
	}
}
