package xdi2.transport.impl.local;

import xdi2.messaging.response.TransportMessagingResponse;
import xdi2.transport.impl.AbstractTransportResponse;

public class LocalTransportResponse extends AbstractTransportResponse 	{

	private TransportMessagingResponse messagingResponse;

	public LocalTransportResponse() {

		this.messagingResponse = null;
	}

	public LocalTransportResponse(TransportMessagingResponse messagingResponse) {

		this.messagingResponse = messagingResponse;
	}

	public TransportMessagingResponse getMessagingResponse() {

		return this.messagingResponse;
	}

	public void setMessagingResponse(TransportMessagingResponse messagingResponse) {

		this.messagingResponse = messagingResponse;
	}
}
