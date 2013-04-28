package xdi2.client.events;

import java.util.Date;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

public class XDISendSuccessEvent extends XDISendEvent {

	private static final long serialVersionUID = -547735780296539623L;

	public XDISendSuccessEvent(Object source, MessageEnvelope messageEnvelope, MessageResult messageResult, Date beginTimestamp, Date endTimestamp) {

		super(source, messageEnvelope, messageResult, beginTimestamp, endTimestamp);
	}
}
