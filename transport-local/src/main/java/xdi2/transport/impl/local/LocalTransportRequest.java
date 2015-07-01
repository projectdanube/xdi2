package xdi2.transport.impl.local;

import xdi2.messaging.MessageEnvelope;
import xdi2.transport.impl.AbstractTransportRequest;

public class LocalTransportRequest extends AbstractTransportRequest {

	private MessageEnvelope messageEnvelope;

	public LocalTransportRequest() {

		this.messageEnvelope = null;
	}

	public LocalTransportRequest(MessageEnvelope messageEnvelope) {

		this.messageEnvelope = messageEnvelope;
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public void setMessageEnvelope(MessageEnvelope messageEnvelope) {

		this.messageEnvelope = messageEnvelope;
	}
}
