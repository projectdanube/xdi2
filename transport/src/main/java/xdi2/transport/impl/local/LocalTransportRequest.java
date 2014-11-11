package xdi2.transport.impl.local;

import xdi2.messaging.request.MessagingRequest;
import xdi2.transport.impl.AbstractTransportRequest;

public class LocalTransportRequest extends AbstractTransportRequest {

	private MessagingRequest messagingRequest;

	public LocalTransportRequest() {

		this.messagingRequest = null;
	}

	public LocalTransportRequest(MessagingRequest messagingRequest) {

		this.messagingRequest = messagingRequest;
	}

	public MessagingRequest getMessagingRequest() {

		return this.messagingRequest;
	}

	public void setMessagingRequest(MessagingRequest messagingRequest) {

		this.messagingRequest = messagingRequest;
	}
}
