package xdi2.messaging.response;

import xdi2.core.Graph;
import xdi2.core.features.error.XdiError;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.MessageEnvelope;

/**
 * A messaging response that will be available in the future.
 * 
 * @author markus
 */
public class FutureMessagingResponse implements MessagingResponse {

	private MessageEnvelope messageEnvelope;

	private Callback callback;

	private FutureMessagingResponse(MessageEnvelope messageEnvelope) {

		this.messageEnvelope = messageEnvelope;

		this.callback = null;
	}

	/*
	 * Static methods
	 */

	public static FutureMessagingResponse fromMessageEnvelope(MessageEnvelope messageEnvelope) {

		return new FutureMessagingResponse(messageEnvelope);
	}

	/*
	 * Instance methods
	 */

	@Override
	public Graph getGraph() {

		throw new IllegalStateException("This messaging response is not yet available.");
	}

	@Override
	public Graph getResultGraph() {

		throw new IllegalStateException("This messaging response is not yet available.");
	}

	@Override
	public boolean hasXdiError() {

		throw new IllegalStateException("This messaging response is not yet available.");
	}

	@Override
	public XdiError getXdiError() {

		throw new IllegalStateException("This messaging response is not yet available.");
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public void onMessagingResponse(XDIAddress messageXDIaddress, TransportMessagingResponse messagingResponse) {

		if (this.getCallback() == null) return;

		this.getCallback().onMessagingResponse(this, messageXDIaddress, messagingResponse);
	}

	/*
	 * Getters and setters
	 */

	public Callback getCallback() {

		return this.callback;
	}

	public void setCallback(Callback callback) {

		this.callback = callback;
	}

	/*
	 * Helper classes
	 */

	public interface Callback {

		public void onMessagingResponse(FutureMessagingResponse futureMessagingResponse, XDIAddress messageXDIAddress, TransportMessagingResponse messagingResponse);
	}
}
