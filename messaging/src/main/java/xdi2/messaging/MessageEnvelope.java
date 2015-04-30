package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityCollection.MappingContextNodeXdiEntityCollectionIterator;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SingleItemIterator;
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
	 * @param operationXDIAddress The operation XRI to use for the new operation.
	 * @param targetXDIAddress The target address to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static MessageEnvelope fromOperationXDIAddressAndTargetXDIAddress(XDIAddress operationXDIAddress, XDIAddress targetXDIAddress) {

		if (targetXDIAddress == null) targetXDIAddress = XDIConstants.XDI_ADD_ROOT;

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(XDIMessagingConstants.XDI_ADD_ANONYMOUS);
		message.createOperation(operationXDIAddress, targetXDIAddress);

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXDIAddress The operation XRI to use for the new operation.
	 * @param targetXDIStatements The target statements to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static MessageEnvelope fromOperationXDIAddressAndTargetXDIStatements(XDIAddress operationXDIAddress, Iterator<XDIStatement> targetXDIStatements) {

		if (targetXDIStatements == null) throw new NullPointerException();

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(XDIMessagingConstants.XDI_ADD_ANONYMOUS);
		message.createOperation(operationXDIAddress, targetXDIStatements);

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXDIAddress The operation XRI to use for the new operation.
	 * @param targetXDIAddressOrTargetStatement The target address or target statement to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static final MessageEnvelope fromOperationXDIAddressAndTargetXDIAddressOrTargetXDIStatement(XDIAddress operationXDIAddress, String targetXDIAddressOrTargetStatement) {

		try {

			if (targetXDIAddressOrTargetStatement == null) targetXDIAddressOrTargetStatement = "";

			XDIAddress targetXDIAddress = XDIAddress.create(targetXDIAddressOrTargetStatement);
			return MessageEnvelope.fromOperationXDIAddressAndTargetXDIAddress(operationXDIAddress, targetXDIAddress);
		} catch (Exception ex) {

			XDIStatement targetXDIStatement = XDIStatement.create(targetXDIAddressOrTargetStatement);
			return MessageEnvelope.fromOperationXDIAddressAndTargetXDIStatements(operationXDIAddress, new SingleItemIterator<XDIStatement> (targetXDIStatement));
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
	 * Returns an existing XDI message collection in this XDI message envelope, or creates a new one.
	 * @param senderXDIAddress The sender.
	 * @param create Whether to create an XDI message collection if it does not exist.
	 * @return The existing or newly created XDI message collection.
	 */
	public MessageCollection getMessageCollection(XDIAddress senderXDIAddress, boolean create) {

		if (senderXDIAddress == null) senderXDIAddress = XDIMessagingConstants.XDI_ADD_ANONYMOUS;

		XDIAddress messageCollectionXDIAddress = XDIAddress.create(senderXDIAddress.toString() + XdiEntityCollection.createXDIArc(XDIMessagingConstants.XDI_ARC_MSG));
		ContextNode contextNode = create ? this.getGraph().setDeepContextNode(messageCollectionXDIAddress) : this.getGraph().getDeepContextNode(messageCollectionXDIAddress, true);

		if (contextNode == null) return null;

		XdiEntityCollection xdiEntityCollection = XdiEntityCollection.fromContextNode(contextNode);

		return new MessageCollection(this, xdiEntityCollection);
	}

	/**
	 * Returns all message collections in this message envelope.
	 * @return All message collections in the envelope.
	 */
	public ReadOnlyIterator<MessageCollection> getMessageCollections() {

		// get all context nodes that are valid XDI message collections

		Iterator<ContextNode> contextNodes = this.getGraph().getRootContextNode(true).getAllContextNodes();

		return new MappingXdiEntityCollectionMessageCollectionIterator(this, new MappingContextNodeXdiEntityCollectionIterator(contextNodes));
	}

	/**
	 * Deletes all message collections from this message envelope.
	 */
	public void deleteMessageCollections() {

		for (MessageCollection messageCollection : new IteratorListMaker<MessageCollection> (this.getMessageCollections()).list()) {

			messageCollection.getContextNode().delete();
		}
	}

	/**
	 * Returns all messages in this message envelope.
	 * @return All messages contained in the envelope.
	 */
	public ReadOnlyIterator<Message> getMessages() {

		return new DescendingIterator<MessageCollection, Message> (this.getMessageCollections()) {

			@Override
			public Iterator<Message> descend(MessageCollection messageCollection) {

				return messageCollection.getMessages();
			}
		};
	}

	/**
	 * Finds messages with a given sender in this message envelope.
	 * @param senderXDIAddress The sender to look for.
	 * @return The messages.
	 */
	public ReadOnlyIterator<Message> getMessages(XDIAddress senderXDIAddress) {

		MessageCollection messageCollection = this.getMessageCollection(senderXDIAddress, false);
		if (messageCollection == null) return new EmptyIterator<Message> ();

		return messageCollection.getMessages();
	}

	/**
	 * Returns all operations in this message envelope.
	 * @return All messages contained in the envelope.
	 */
	public ReadOnlyIterator<Operation> getOperations() {

		return new DescendingIterator<Message, Operation>(this.getMessages()) {

			@Override
			public Iterator<Operation> descend(Message item) {

				return item.getOperations();
			}
		};
	}

	/**
	 * Returns the number of message collections in the message envelope.
	 */
	public long getMessageCollectionCount() {

		Iterator<MessageCollection> iterator = this.getMessageCollections();

		return new IteratorCounter(iterator).count();
	}

	/**
	 * Returns the number of messages in the message envelope.
	 */
	public long getMessageCount() {

		Iterator<Message> iterator = this.getMessages();

		return new IteratorCounter(iterator).count();
	}

	/**
	 * Returns the number of operations in all messages of the message envelope.
	 */
	public long getOperationCount() {

		Iterator<Operation> iterator = this.getOperations();

		return new IteratorCounter(iterator).count();
	}

	/*
	 * Convenience instance methods for messages
	 */

	/**
	 * Creates a new XDI message in this XDI message envelope for a given sender.
	 * @param senderXDIAddress The sender.
	 * @return The newly created XDI message.
	 */
	public Message createMessage(XDIAddress senderXDIAddress) {

		return this.getMessageCollection(senderXDIAddress, true).createMessage();
	}

	/**
	 * Creates a new XDI message in this XDI message envelope for a given sender.
	 * @param senderXDIAddress The sender.
	 * @param index Index in an ordered collection.
	 * @return The newly created XDI message.
	 */
	public Message createMessage(XDIAddress senderXDIAddress, long index) {

		return this.getMessageCollection(senderXDIAddress, true).createMessage(index);
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

	@Override
	public int compareTo(MessageEnvelope other) {

		if (other == this || other == null) return 0;

		return this.getGraph().compareTo(other.getGraph());
	}

	/*
	 * Helper classes
	 */

	public static class MappingXdiEntityCollectionMessageCollectionIterator extends NotNullIterator<MessageCollection> {

		public MappingXdiEntityCollectionMessageCollectionIterator(final MessageEnvelope messageEnvelope, Iterator<XdiEntityCollection> xdiEntityCollections) {

			super(new MappingIterator<XdiEntityCollection, MessageCollection> (xdiEntityCollections) {

				@Override
				public MessageCollection map(XdiEntityCollection xdiEntityCollection) {

					return MessageCollection.fromMessageEnvelopeAndXdiEntityClass(messageEnvelope, xdiEntityCollection);
				}
			});
		}
	}
}
