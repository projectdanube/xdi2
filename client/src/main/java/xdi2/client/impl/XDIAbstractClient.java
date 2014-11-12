package xdi2.client.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.XDIClientListener;
import xdi2.client.events.XDIDiscoverEvent;
import xdi2.client.events.XDISendErrorEvent;
import xdi2.client.events.XDISendEvent;
import xdi2.client.events.XDISendSuccessEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.ErrorMessagingResponse;
import xdi2.messaging.response.MessagingResponse;

public abstract class XDIAbstractClient implements XDIClient {

	protected static final Logger log = LoggerFactory.getLogger(XDIAbstractClient.class);

	private final List<XDIClientListener> clientListeners;

	public XDIAbstractClient() {

		this.clientListeners = new ArrayList<XDIClientListener> ();
	}

	@Override
	public final MessagingResponse send(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// timestamp

		Date beginTimestamp = new Date();

		// send the messaging request and retrieve the messaging response

		if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope);
		MessagingResponse messagingResponse = this.sendInternal(messageEnvelope);
		if (log.isDebugEnabled()) log.debug("MessagingResponse: " + messagingResponse);

		// timestamp

		Date endTimestamp = new Date();

		// see if it is an error messaging response

		if (messagingResponse instanceof ErrorMessagingResponse) {

			ErrorMessagingResponse errorMessagingResponse = (ErrorMessagingResponse) messagingResponse;

			this.fireSendEvent(new XDISendErrorEvent(this, messageEnvelope, errorMessagingResponse, beginTimestamp, endTimestamp));

			throw new Xdi2ClientException("Error messaging response: " + errorMessagingResponse.getErrorString(), null, errorMessagingResponse);
		}

		// done

		this.fireSendEvent(new XDISendSuccessEvent(this, messageEnvelope, messagingResponse, beginTimestamp, endTimestamp));

		return messagingResponse;
	}

	protected abstract MessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException;

	/*
	 * Events
	 */

	@Override
	public void addClientListener(XDIClientListener clientListener) {

		if (this.clientListeners.contains(clientListener)) return;
		this.clientListeners.add(clientListener);
	}

	@Override
	public void removeClientListener(XDIClientListener clientListener) {

		this.clientListeners.remove(clientListener);
	}

	@Override
	public void fireSendEvent(XDISendEvent sendEvent) {

		for (XDIClientListener clientListener : this.clientListeners) clientListener.onSend(sendEvent);
	}

	@Override
	public void fireDiscoverEvent(XDIDiscoverEvent discoveryEvent) {

		for (XDIClientListener clientListener : this.clientListeners) clientListener.onDiscover(discoveryEvent);
	}
}
