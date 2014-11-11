package xdi2.client.events;

import java.util.Date;

import xdi2.messaging.request.MessagingRequest;
import xdi2.messaging.response.MessagingResponse;

public class XDISendSuccessEvent extends XDISendEvent {

	private static final long serialVersionUID = -547735780296539623L;

	public XDISendSuccessEvent(Object source, MessagingRequest messagingRequest, MessagingResponse messagingResponse, Date beginTimestamp, Date endTimestamp) {

		super(source, messagingRequest, messagingResponse, beginTimestamp, endTimestamp);
	}
}
