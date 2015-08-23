package xdi2.messaging.response;

import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;

/**
 * A message envelope that will be available in the future.
 * 
 * @author markus
 */
public class FutureMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = 6405364186568481142L;

	private MessageEnvelope messageEnvelope;
	private MessagingResponse messagingResponse;

	private Callback callback;

	private FutureMessagingResponse(MessageEnvelope messageEnvelope) {

		this.messageEnvelope = messageEnvelope;
		this.messagingResponse = null;

		this.callback = null;
	}

	/*
	 * Static methods
	 */

	public static FutureMessagingResponse create(MessageEnvelope messageEnvelope) {

		return new FutureMessagingResponse(messageEnvelope);
	}

	/*
	 * Overrides
	 */

	@Override
	public Graph getGraph() {

		if (this.getMessagingResponse() == null) return null;

		return this.getMessagingResponse().getGraph();
	}

	@Override
	public Graph getResultGraph() {

		if (this.getMessagingResponse() == null) return null;

		return this.getMessagingResponse().getGraph();
	}

	/*
	 * Instance methods
	 */

	public void onMessagingResponse(MessagingResponse messagingResponse) {

		this.messagingResponse = messagingResponse;

		this.getCallback().onMessagingResponse(this, messagingResponse);
	}

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	public MessagingResponse getMessagingResponse() {

		return this.messagingResponse;
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

		public void onMessagingResponse(FutureMessagingResponse futureMessagingResponse, MessagingResponse messagingResponse);
	}
}
