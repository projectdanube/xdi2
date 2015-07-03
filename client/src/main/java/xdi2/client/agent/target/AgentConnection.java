package xdi2.client.agent.target;

import xdi2.client.XDIClient;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public interface AgentConnection {

	public XDIClient constructXDIClient();
	public MessageEnvelope constructMessageEnvelope();
	public Message constructMessage(MessageEnvelope messageEnvelope);
}
