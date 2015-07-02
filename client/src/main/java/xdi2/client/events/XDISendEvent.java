package xdi2.client.events;

import java.util.Date;

import xdi2.client.XDIClient;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;

public class XDISendEvent extends XDIEvent {

	private static final long serialVersionUID = 5301716219045375638L;

	private MessageEnvelope messageEnvelope;
	private MessagingResponse messagingResponse;
	private Date beginTimestamp;
	private Date endTimestamp;

	public XDISendEvent(Object source, MessageEnvelope messageEnvelope, MessagingResponse messagingResponse, Date beginTimestamp, Date endTimestamp) {

		super(source);

		this.messageEnvelope = messageEnvelope;
		this.messagingResponse = messagingResponse;
		this.beginTimestamp = beginTimestamp;
		this.endTimestamp = endTimestamp;
	}

	@Override
	public XDIClient getSource() {

		return (XDIClient) super.getSource();
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public MessagingResponse getMessagingResponse() {

		return this.messagingResponse;
	}

	public Date getBeginTimestamp() {

		return this.beginTimestamp;
	}

	public Date getEndTimestamp() {

		return this.endTimestamp;
	}
}
