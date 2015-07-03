package xdi2.client.agent.target.impl;

import xdi2.client.agent.target.AgentConnection;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;

public abstract class AbstractAgentConnection implements AgentConnection {

	@Override
	public MessageEnvelope constructMessageEnvelope() {

		MessageEnvelope messageEnvelope = new MessageEnvelope();

		return messageEnvelope;
	}

	@Override
	public Message constructMessage(MessageEnvelope messageEnvelope) {

		Message message = messageEnvelope.createMessage(XDIMessagingConstants.XDI_ADD_ANONYMOUS);

		return message;
	}
}
