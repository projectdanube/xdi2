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
import xdi2.messaging.response.TransportMessagingResponse;

public abstract class XDIAbstractClient <MESSAGINGRESPONSE extends MessagingResponse> implements XDIClient<MESSAGINGRESPONSE> {

	private static final Logger log = LoggerFactory.getLogger(XDIAbstractClient.class);

	private ManipulatorList manipulators;
	private final List<XDIClientListener> clientListeners;

	public XDIAbstractClient() {

		this.manipulators = new ManipulatorList();
		this.clientListeners = new ArrayList<XDIClientListener> ();
	}

	@Override
	public final MESSAGINGRESPONSE send(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Preparing to send " + messageEnvelope.getMessageCount() + " message(s).");

		// timestamp

		Date beginTimestamp = new Date();

		// create manipulation context

		ManipulationContext manipulationContext = this.createManipulationContext();

		// manipulate

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Applying " + this.getManipulators().size() + " manipulator(s).");

		ManipulatorExecutor.executeMessageEnvelopeManipulators(this.getManipulators(), messageEnvelope, manipulationContext);

		for (Message message : messageEnvelope.getMessages()) {

			ManipulatorExecutor.executeMessageManipulators(this.getManipulators(), message, manipulationContext);
		}

		// send the messaging request and retrieve the messaging response

		if (log.isDebugEnabled()) log.debug("" + this.getClass().getSimpleName() + ": MessageEnvelope: " + messageEnvelope);
		MESSAGINGRESPONSE messagingResponse = this.sendInternal(messageEnvelope);
		if (log.isDebugEnabled()) log.debug("" + this.getClass().getSimpleName() + ": MessagingResponse: " + messagingResponse);

		// timestamp

		Date endTimestamp = new Date();

		// see if the messaging response has an associated error

		if (messagingResponse instanceof TransportMessagingResponse) {

			if (((TransportMessagingResponse) messagingResponse).hasXdiError()) {

				XdiError xdiError = messagingResponse.getXdiError();

				this.fireSendEvent(new XDISendErrorEvent(this, messageEnvelope, messagingResponse, beginTimestamp, endTimestamp));

				throw new Xdi2ClientException("Error messaging response: " + xdiError.getErrorString(), messagingResponse);
			}
		}

		// done

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Sent successfully and got " + messagingResponse.getClass().getSimpleName());

		this.fireSendEvent(new XDISendSuccessEvent(this, messageEnvelope, messagingResponse, beginTimestamp, endTimestamp));

		return messagingResponse;
	}

	protected abstract MESSAGINGRESPONSE sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException;

	/*
	 * Helper methods
	 */

	public ManipulationContext createManipulationContext() {

		ManipulationContext manipulationContext = ManipulationContext.createManipulationContext();

		putXDIAbstractClient(manipulationContext, this);

		return manipulationContext;
	}

	/*
	 * Getters and setters
	 */

	public ManipulatorList getManipulators() {

		return this.manipulators;
	}

	public void setManipulators(ManipulatorList manipulators) {

		this.manipulators = manipulators;
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

	/*
	 * ManipulationContext helper methods
	 */

	private static final String MANIPULATIONCONTEXT_KEY_XDIABSTRACTCLIENT = XDIAbstractClient.class.getCanonicalName() + "#xdiabstractclient";

	public static XDIAbstractClient<?> getXDIAbstractClient(ManipulationContext manipulationContext) {

		return (XDIAbstractClient<?>) manipulationContext.getManipulationContextAttribute(MANIPULATIONCONTEXT_KEY_XDIABSTRACTCLIENT);
	}

	public static void putXDIAbstractClient(ManipulationContext manipulationContext, XDIAbstractClient<?> xdiAbstractClient) {

		manipulationContext.putManipulationContextAttribute(MANIPULATIONCONTEXT_KEY_XDIABSTRACTCLIENT, xdiAbstractClient);
	}
}
