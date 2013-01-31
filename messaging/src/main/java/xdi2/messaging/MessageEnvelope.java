package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIConstants;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.features.multiplicity.Multiplicity.MappingContextNodeCollectionIterator;
import xdi2.core.features.multiplicity.XdiCollection;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
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
	public static MessageEnvelope fromOperationXriAndTargetXri(XDI3Segment operationXri, XDI3Segment targetXri) {

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
	public static MessageEnvelope fromOperationXriAndStatement(XDI3Segment operationXri, String statement) {

		if (statement == null) throw new NullPointerException();

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.getMessage(XDIMessagingConstants.XRI_S_ANONYMOUS, true);
		message.createOperation(operationXri, XDI3Segment.create("(" + statement + ")"));

		return messageEnvelope;
	}

	/**
	 * Factory method that creates an XDI message envelope bound to a given graph.
	 * @param operationXri The operation XRI to use for the new operation.
	 * @param targetXriOrStatement The target XRI or statement to which the operation applies.
	 * @return The XDI message envelope.
	 */
	public static final MessageEnvelope fromOperationXriAndTargetXriOrStatement(XDI3Segment operationXri, String targetXriOrStatement) {

		try {

			if (targetXriOrStatement == null) targetXriOrStatement = "()";

			XDI3Segment targetXri = XDI3Segment.create(targetXriOrStatement);
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
	 * Returns an existing XDI message collection in this XDI message envelope, or creates a new one.
	 * @param senderXri The sender.
	 * @param create Whether to create an XDI message collection if it does not exist.
	 * @return The existing or newly created XDI message collection.
	 */
	public MessageCollection getMessageCollection(XDI3Segment senderXri, boolean create) {

		XDI3Segment messageCollectionXri = XDI3Segment.create(senderXri.toString() + Multiplicity.collectionArcXri(XDIMessagingConstants.XRI_SS_MSG));
		ContextNode contextNode = this.getGraph().findContextNode(messageCollectionXri, create);

		if (contextNode == null) return null;

		XdiCollection xdiCollection = XdiCollection.fromContextNode(contextNode);

		return new MessageCollection(this, xdiCollection);
	}

	/**
	 * Returns all message collections in this message envelope.
	 * @return All message collections in the envelope.
	 */
	public ReadOnlyIterator<MessageCollection> getMessageCollections() {

		// get all context nodes that are valid XDI message collections

		Iterator<ContextNode> contextNodes = this.getGraph().getRootContextNode().getAllContextNodes();

		return new MappingCollectionMessageCollectionIterator(this, new MappingContextNodeCollectionIterator(contextNodes));
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
	 * @param senderXri The sender to look for.
	 * @return The messages.
	 */
	public ReadOnlyIterator<Message> getMessages(XDI3Segment senderXri) {

		MessageCollection messageCollection = this.getMessageCollection(senderXri, false);
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
	public int getMessageCollectionCount() {

		Iterator<MessageCollection> iterator = this.getMessageCollections();

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
	 * @param create Whether to create a message collection if it does not exist.
	 * @return The newly created XDI message collection.
	 */
	public Message getMessage(XDI3Segment senderXri, boolean create) {

		return this.getMessageCollection(senderXri, true).getMessage(create);
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

	public static class MappingCollectionMessageCollectionIterator extends NotNullIterator<MessageCollection> {

		public MappingCollectionMessageCollectionIterator(final MessageEnvelope messageEnvelope, Iterator<XdiCollection> xdiCollections) {

			super(new MappingIterator<XdiCollection, MessageCollection> (xdiCollections) {

				@Override
				public MessageCollection map(XdiCollection xdiCollection) {

					return MessageCollection.fromMessageEnvelopeAndXdiCollection(messageEnvelope, xdiCollection);
				}
			});
		}
	}
}
