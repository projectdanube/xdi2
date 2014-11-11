package xdi2.messaging.request;

import java.util.Iterator;

import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.constants.XDIConstants;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.SingleItemIterator;
import xdi2.messaging.MessageEnvelope;

public class RequestMessageEnvelope extends MessageEnvelope<RequestMessageEnvelope, RequestMessageCollection, RequestMessage> implements MessagingRequest {

	private static final long serialVersionUID = 557515143508875296L;

	protected RequestMessageEnvelope(Graph graph) {

		super(graph, RequestMessageEnvelope.class, RequestMessageCollection.class, RequestMessage.class);
	}

	public RequestMessageEnvelope() {

		super(RequestMessageEnvelope.class, RequestMessageCollection.class, RequestMessage.class);
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a graph is a valid XDI message envelope.
	 * @param graph The graph to check.
	 * @return True if the graph is a valid XDI message envelope.
	 */
	public static boolean isValid(Graph graph) {

		return MessageEnvelope.isValid(graph);
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param graph The graph that is an XDI message envelope.
	 * @return The XDI message envelope.
	 */
	public static RequestMessageEnvelope fromGraph(Graph graph) {

		if (! isValid(graph)) return null;

		return new RequestMessageEnvelope(graph);
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXDIAddress The operation XRI to use for the new operation.
	 * @param targetXDIAddress The target address to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static RequestMessageEnvelope fromOperationXDIAddressAndTargetXDIAddress(XDIAddress operationXDIAddress, XDIAddress targetXDIAddress) {

		if (targetXDIAddress == null) targetXDIAddress = XDIConstants.XDI_ADD_CONTEXT;

		RequestMessageEnvelope messageEnvelope = new RequestMessageEnvelope();
		RequestMessage message = messageEnvelope.createMessage(XDIAuthenticationConstants.XDI_ADD_ANONYMOUS);
		message.createOperation(operationXDIAddress, targetXDIAddress);

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXDIAddress The operation XRI to use for the new operation.
	 * @param targetXDIStatements The target statements to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static RequestMessageEnvelope fromOperationXDIAddressAndTargetXDIStatements(XDIAddress operationXDIAddress, Iterator<XDIStatement> targetXDIStatements) {

		if (targetXDIStatements == null) throw new NullPointerException();

		RequestMessageEnvelope messageEnvelope = new RequestMessageEnvelope();
		RequestMessage message = messageEnvelope.createMessage(XDIAuthenticationConstants.XDI_ADD_ANONYMOUS);
		message.createOperation(operationXDIAddress, targetXDIStatements);

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXDIAddress The operation XRI to use for the new operation.
	 * @param targetXDIAddressOrTargetStatement The target address or target statement to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static final RequestMessageEnvelope fromOperationXDIAddressAndTargetXDIAddressOrTargetXDIStatement(XDIAddress operationXDIAddress, String targetXDIAddressOrTargetStatement) {

		try {

			if (targetXDIAddressOrTargetStatement == null) targetXDIAddressOrTargetStatement = "";

			XDIAddress targetXDIAddress = XDIAddress.create(targetXDIAddressOrTargetStatement);
			return RequestMessageEnvelope.fromOperationXDIAddressAndTargetXDIAddress(operationXDIAddress, targetXDIAddress);
		} catch (Exception ex) {

			XDIStatement targetXDIStatement = XDIStatement.create(targetXDIAddressOrTargetStatement);
			return RequestMessageEnvelope.fromOperationXDIAddressAndTargetXDIStatements(operationXDIAddress, new SingleItemIterator<XDIStatement> (targetXDIStatement));
		}
	}
}
