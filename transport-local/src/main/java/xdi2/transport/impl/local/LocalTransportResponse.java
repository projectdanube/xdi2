package xdi2.transport.impl.local;

import xdi2.messaging.response.MessagingResponse;
import xdi2.transport.impl.AbstractTransportResponse;

public class LocalTransportResponse extends AbstractTransportResponse 	{

	private MessagingResponse messagingResponse;

	public LocalTransportResponse() {

		this.messagingResponse = null;
	}

	public LocalTransportResponse(MessagingResponse messagingResponse) {

		this.messagingResponse = messagingResponse;
	}

	public MessagingResponse getMessagingResponse() {

		return this.messagingResponse;
	}

	public void setMessagingResponse(MessagingResponse messagingResponse) {

		this.messagingResponse = messagingResponse;
	}
}
