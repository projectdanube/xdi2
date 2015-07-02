package xdi2.client.events;

import java.util.Date;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.error.MessagingError;
import xdi2.messaging.response.MessagingResponse;

public class XDISendErrorEvent extends XDISendEvent {

	private static final long serialVersionUID = -547735780296539623L;

	public XDISendErrorEvent(Object source, MessageEnvelope messageEnvelope, MessagingResponse messagingResponse, Date beginTimestamp, Date endTimestamp) {

		super(source, messageEnvelope, messagingResponse, beginTimestamp, endTimestamp);
	}

	public MessagingError getMessagingError() {

		return this.getMessagingResponse().getMessagingError();
	}
}
