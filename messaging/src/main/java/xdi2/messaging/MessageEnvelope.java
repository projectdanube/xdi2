package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * An XDI message envelope, represented as a graph.
 * 
 * @author markus
 */
public class MessageEnvelope implements Serializable, Comparable<MessageEnvelope> {

	private static final long serialVersionUID = -7335038610687761197L;

	protected static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	protected Graph graph;

	protected MessageEnvelope(Graph graph) {

		this.graph = graph;
	}

	public MessageEnvelope() {

		this(graphFactory.openGraph());
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

		return true;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param graph The graph that is an XDI message envelope.
	 * @return The XDI message envelope.
	 */
	public static MessageEnvelope fromGraph(Graph graph) {

		if (! isValid(graph)) return null;

		return new MessageEnvelope(graph);
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXri The operation XRI to use for the new operation.
	 * @param targetXri The target XRI to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static MessageEnvelope fromOperationXriAndTargetXri(XRI3Segment operationXri, XRI3Segment targetXri) {

		if (targetXri == null) targetXri = XDIConstants.XRI_S_CONTEXT;

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		message.createOperation(operationXri, targetXri);

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXri The operation XRI to use for the new operation.
	 * @param statement The statement to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static MessageEnvelope fromOperationXriAndStatement(XRI3Segment operationXri, String statement) {

		if (statement == null) throw new NullPointerException();

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		message.createOperation(operationXri, new XRI3Segment("(" + statement + ")"));

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXri The operation XRI to use for the new operation.
	 * @param targetXriOrStatement The target XRI or statement to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static final MessageEnvelope fromOperationXriAndTargetXriOrStatement(XRI3Segment operationXri, String targetXriOrStatement) {

		try {

			if (targetXriOrStatement == null) targetXriOrStatement = "()";

			XRI3Segment targetXri = new XRI3Segment(targetXriOrStatement);
			return MessageEnvelope.fromOperationXriAndTargetXri(operationXri, targetXri);
		} catch (Exception ex) {

			return MessageEnvelope.fromOperationXriAndStatement(operationXri, targetXriOrStatement);
		}
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying graph to which this XDI message envelope is bound.
	 * @return A graph that represents the XDI message envelope.
	 */
	public Graph getGraph() {

		return this.graph;
	}

	/**
	 * Returns an existing XDI message container in this XDI message envelope, or creates a new one.
	 * @param senderXri The sender.
	 * @param create Whether to create an XDI message container if it does not exist.
	 * @return The existing or newly created XDI message container.
	 */
	public MessageContainer getMessageContainer(XRI3Segment senderXri, boolean create) {

		XRI3Segment messageContainerXri = new XRI3Segment(senderXri.toString() + XDIMessagingConstants.XRI_SS_MSG.toString());
		ContextNode contextNode = this.getGraph().findContextNode(messageContainerXri, create);

		if (contextNode == null) return null;

		return new MessageContainer(this, contextNode);
	}

	/**
	 * Returns all message containers in this message envelope.
	 * @return All message containers in the envelope.
	 */
	public Iterator<MessageContainer> getMessageContainers() {

		// get all context nodes that are valid XDI message containers

		Iterator<ContextNode> contextNodes = this.getGraph().getRootContextNode().getAllContextNodes();

		return new SelectingMappingIterator<ContextNode, MessageContainer> (contextNodes) {

			@Override
			public boolean select(ContextNode contextNode) {

				return MessageContainer.isValid(contextNode);
			}

			@Override
			public MessageContainer map(ContextNode contextNode) {

				return MessageContainer.fromMessageEnvelopeAndContextNode(MessageEnvelope.this, contextNode);
			}
		};
	}

	/**
	 * Returns all messages in this message envelope.
	 * @return All messages contained in the envelope.
	 */
	public Iterator<Message> getMessages() {

		return new DescendingIterator<MessageContainer, Message> (this.getMessageContainers()) {

			@Override
			public Iterator<Message> descend(MessageContainer messageContainer) {

				return messageContainer.getMessages();
			}
		};
	}

	/**
	 * Finds messages with a given sender in this message envelope.
	 * @param senderXri The sender to look for.
	 * @return The messages.
	 */
	public Iterator<Message> getMessages(XRI3Segment senderXri) {

		MessageContainer messageContainer = this.getMessageContainer(senderXri, false);
		if (messageContainer == null) return new EmptyIterator<Message> ();

		return messageContainer.getMessages();
	}

	/**
	 * Returns all operations in this message envelope.
	 * @return All messages contained in the envelope.
	 */
	public Iterator<Operation> getOperations() {

		Iterator<Operation> descendingOperator = new DescendingIterator<Message, Operation>(this.getMessages()) {

			@Override
			public Iterator<Operation> descend(Message item) {

				return item.getOperations();
			}
		};

		return descendingOperator;
	}

	/**
	 * Returns the number of message containers in the message envelope.
	 */
	public int getMessageContainerCount() {

		Iterator<MessageContainer> iterator = this.getMessageContainers();

		return new IteratorCounter(iterator).count();
	}

	/**
	 * Returns the number of messages in the message envelope.
	 */
	public int getMessageCount() {

		Iterator<Message> iterator = this.getMessages();

		return new IteratorCounter(iterator).count();
	}

	/**
	 * Returns the number of operations in all messages of the message envelope.
	 */
	public int getOperationCount() {

		Iterator<Operation> iterator = this.getOperations();

		return new IteratorCounter(iterator).count();
	}

	/*
	 * Convenience instance methods for messages
	 */

	/**
	 * Returns or creates a new XDI message in this XDI message envelope for a given sender.
	 * @param senderXri The sender.
	 * @param create Whether to create a message container if it does not exist.
	 * @return The newly created XDI message container.
	 */
	public Message getMessage(XRI3Segment senderXri, boolean create) {

		return this.getMessageContainer(senderXri, true).getMessage(create);
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getGraph().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof MessageEnvelope)) return false;
		if (object == this) return true;

		MessageEnvelope other = (MessageEnvelope) object;

		return this.getGraph().equals(other.getGraph());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getGraph().hashCode();

		return hashCode;
	}

	public int compareTo(MessageEnvelope other) {

		if (other == this || other == null) return 0;

		return this.getGraph().compareTo(other.getGraph());
	}
}
