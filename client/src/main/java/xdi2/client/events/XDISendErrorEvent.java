package xdi2.client.events;

import java.util.Date;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.ErrorMessagingResponse;

public class XDISendErrorEvent extends XDISendEvent {

	private static final long serialVersionUID = -547735780296539623L;

	public XDISendErrorEvent(Object source, MessageEnvelope messageEnvelope, ErrorMessagingResponse messageResult, Date beginTimestamp, Date endTimestamp) {

		super(source, messageEnvelope, messageResult, beginTimestamp, endTimestamp);
	}

	@Override
	public ErrorMessagingResponse getMessagingResponse() {

		return (ErrorMessagingResponse) super.getMessagingResponse();
	}
}
