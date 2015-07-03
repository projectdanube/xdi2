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
import xdi2.core.features.error.XdiError;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;

public abstract class XDIAbstractClient implements XDIClient {

	protected static final Logger log = LoggerFactory.getLogger(XDIAbstractClient.class);

	private ManipulatorList manipulators;

	private final List<XDIClientListener> clientListeners;

	public XDIAbstractClient() {

		this.manipulators = new ManipulatorList();

		this.clientListeners = new ArrayList<XDIClientListener> ();
	}

	@Override
	public final MessagingResponse send(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// timestamp

		Date beginTimestamp = new Date();

		// manipulate

		ManipulatorExecutor.executeMessageEnvelopeManipulators(this.getManipulators(), messageEnvelope);

		for (Message message : messageEnvelope.getMessages()) {

			ManipulatorExecutor.executeMessageManipulators(this.getManipulators(), message);
		}

		// send the messaging request and retrieve the messaging response

		if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope);
		MessagingResponse messagingResponse = this.sendInternal(messageEnvelope);
		if (log.isDebugEnabled()) log.debug("MessagingResponse: " + messagingResponse);

		// timestamp

		Date endTimestamp = new Date();

		// see if the messaging response has an associated error

		if (messagingResponse.hasXdiError()) {

			XdiError xdiError = messagingResponse.getXdiError();

			this.fireSendEvent(new XDISendErrorEvent(this, messageEnvelope, messagingResponse, beginTimestamp, endTimestamp));

			throw new Xdi2ClientException("Error messaging response: " + xdiError.getErrorString(), messagingResponse);
		}

		// done

		this.fireSendEvent(new XDISendSuccessEvent(this, messageEnvelope, messagingResponse, beginTimestamp, endTimestamp));

		return messagingResponse;
	}

	protected abstract MessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException;

	/*
	 * Getters and setters
	 */

	public ManipulatorList getManipulators() {

		return this.manipulators;
	}

	public void setInterceptors(ManipulatorList interceptors) {

		this.manipulators = interceptors;
	}

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
