package xdi2.messaging.response;

import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;

/**
 * A message envelope as an XDI messaging response.
 * 
 * @author markus
 */
public class MessageEnvelopeMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -150908814464607155L;

	private MessageEnvelope messageEnvelope;

	private MessageEnvelopeMessagingResponse(MessageEnvelope messageEnvelope) {

		this.messageEnvelope = messageEnvelope;
	}

	/*
	 * Static methods
	 */

	public static MessageEnvelopeMessagingResponse create(MessageEnvelope messageEnvelope) {

		MessageEnvelopeMessagingResponse messageEnvelopeMessagingResponse = new MessageEnvelopeMessagingResponse(messageEnvelope);

		return messageEnvelopeMessagingResponse;
	}

	public static boolean isValid(Graph graph) {

		return MessageEnvelope.isValid(graph);
	}

	public static MessageEnvelopeMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new MessageEnvelopeMessagingResponse(MessageEnvelope.fromGraph(graph));
	}

	/*
	 * Overrides
	 */

	@Override
	public Graph getGraph() {

		return this.getMessageEnvelope().getGraph();
	}

	@Override
	public Graph getResultGraph() {

		return this.getMessageEnvelope().getGraph();
	}

	/*
	 * Instance methods
	 */

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}
}
