package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIConstants;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * An XDI message envelope, represented as a graph.
 * 
 * @author markus
 */
public class MessageEnvelope implements Serializable, Comparable<MessageEnvelope> {

	private static final long serialVersionUID = -7335038610687761197L;

	protected static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	protected Graph graph;

	private MessageEnvelope(Graph graph) {

		this.graph = graph;
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

		MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
		MessageContainer messageContainer = messageEnvelope.getMessageContainer(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		Message message = messageContainer.createMessage();
		message.createOperation(operationXri, targetXri);

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXri The operation XRI to use for the new operation.
	 * @param statement The statement to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static MessageEnvelope fromOperationXriAndStatement(XRI3Segment operationXri, String statement) throws Xdi2ParseException {

		if (statement == null) throw new NullPointerException();

		MessageEnvelope messageEnvelope = MessageEnvelope.newInstance();
		ContextNode statementContextNode = messageEnvelope.getGraph().addStatement(statement).getSubject();
		MessageContainer messageContainer = messageEnvelope.getMessageContainer(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		Message message = messageContainer.createMessage();
		message.createOperation(operationXri, statementContextNode.getXri());

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a new in-memory graph.
	 * @return The XDI message envelope.
	 */
	public static MessageEnvelope newInstance() {

		return new MessageEnvelope(graphFactory.openGraph());
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
	 * Returns the underlying "target" graph without $msg context nodes, i.e. clean of XDI messaging constructs.
	 */
	public Graph getTargetGraph() {

		// create a copy of the operation graph without message containers

		Graph targetGraph = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyGraph(this.getGraph(), targetGraph, null);

		boolean done;

		do {

			done = true;

			for (Iterator<ContextNode> contextNodes = targetGraph.getRootContextNode().getAllContextNodes(); contextNodes.hasNext(); ) {

				ContextNode contextNode = contextNodes.next();

				if (contextNode.getArcXri().equals(XDIMessagingConstants.XRI_S_MSG)) {

					ContextNode parentContextNode = contextNode.getContextNode();

					do {

						contextNode.delete();
						contextNode = parentContextNode;
						parentContextNode = contextNode.getContextNode();
					} while (parentContextNode != null && contextNode.isEmpty());

					done = false;
					break;
				}
			}
		} while (! done);

		return targetGraph;
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

				return MessageContainer.fromContextNode(contextNode);
			}
		};
	}

	/**
	 * Creates a new XDI message container in this XDI message envelope.
	 * @param senderXri The sender of the message container.
	 * @return The newly created XDI message container.
	 */
	public MessageContainer getMessageContainer(XRI3Segment senderXri, boolean create) {

		XRI3Segment messageXri = new XRI3Segment(senderXri.toString() + XDIMessagingConstants.XRI_SS_MSG.toString());
		ContextNode contextNode = this.getGraph().findContextNode(messageXri, true);

		return new MessageContainer(this, contextNode);
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

		XRI3Segment messageContextXri = new XRI3Segment(senderXri.toString() + XDIMessagingConstants.XRI_SS_MSG.toString());
		ContextNode contextNode = this.getGraph().findContextNode(messageContextXri, true);

		Iterator<ContextNode> messageInstanceContextNodes = contextNode.getContextNodes();

		return new SelectingMappingIterator<ContextNode, Message> (messageInstanceContextNodes) {

			@Override
			public boolean select(ContextNode messageInstanceContextNode) {

				return Message.isValid(messageInstanceContextNode);
			}

			@Override
			public Message map(ContextNode messageInstanceContextNode) {

				return Message.fromContextNode(messageInstanceContextNode);
			}
		};
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
