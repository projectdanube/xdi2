package xdi2.messaging.response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import xdi2.core.Graph;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;

/**
 * A message envelope as an XDI messaging response.
 * 
 * @author markus
 */
public class MessageEnvelopeMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = -150908814464607155L;

	private MessageEnvelope messageEnvelope;

	protected MessageEnvelopeMessagingResponse(MessageEnvelope messageEnvelope) {

		this.messageEnvelope = messageEnvelope;
	}

	/*
	 * Static methods
	 */

	public static boolean isValid(Graph graph) {

		return MessageEnvelope.isValid(graph);
	}

	public static MessageEnvelopeMessagingResponse fromGraph(Graph graph) {

		if (! isValid(graph)) return(null);

		return new MessageEnvelopeMessagingResponse(MessageEnvelope.fromGraph(graph));
	}

	public static MessageEnvelopeMessagingResponse fromMessageEnvelope(MessageEnvelope responseMessageEnvelope) {

		// new messaging response

		MessageEnvelopeMessagingResponse messageEnvelopeMessagingResponse = new MessageEnvelopeMessagingResponse(responseMessageEnvelope);

		// done

		return messageEnvelopeMessagingResponse;
	}
	
	/*
	 * Instance methods
	 */

	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	@Override
	public Graph getGraph() {

		return this.getGraph();
	}

	@Override
	public Iterator<Graph> getResultGraphs() {

		List<Graph> resultGraphs = new ArrayList<Graph> ();

		for (Operation operation : this.getMessageEnvelope().getOperations()) {

			resultGraphs.add(operation.getTargetInnerRoot().toGraph());
		}

		return resultGraphs.iterator();
	}

	@Override
	public Graph getResultGraph() {

		return null;
	}
}
