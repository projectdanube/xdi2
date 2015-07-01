package xdi2.messaging.response;

import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;

/**
 * A message envelope as an XDI messaging response.
 * 
 * @author markus
 */
public class FullMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -150908814464607155L;

	private MessageEnvelope messageEnvelope;

	private FullMessagingResponse(MessageEnvelope messageEnvelope) {

		this.messageEnvelope = messageEnvelope;
	}

	/*
	 * Static methods
	 */

	public static FullMessagingResponse create(MessageEnvelope messageEnvelope) {

		FullMessagingResponse messageEnvelopeMessagingResponse = new FullMessagingResponse(messageEnvelope);

		return messageEnvelopeMessagingResponse;
	}

	public static boolean isValid(Graph graph) {

		return MessageEnvelope.isValid(graph);
	}

	public static FullMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new FullMessagingResponse(MessageEnvelope.fromGraph(graph));
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

		// TODO this is not really the result graph

		return this.getMessageEnvelope().getGraph();
	}

	/*
	 * Instance methods
	 */

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}
}
