package xdi2.messaging;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIAuthenticationConstants;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityCollection.MappingContextNodeXdiEntityCollectionIterator;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.EmptyIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.operations.Operation;

/**
 * An XDI message envelope, represented as a graph.
 * 
 * @author markus
 */
public abstract class MessageEnvelope <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> implements Serializable, Comparable<MessageEnvelope<?, ?, ?>> {

	private static final long serialVersionUID = -7335038610687761197L;

	protected static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private Class<ME> me;
	private Class<MC> mc;
	private Class<M> m;

	protected Graph graph;

	protected MessageEnvelope(Graph graph, Class<ME> me, Class<MC> mc, Class<M> m) {

		this.graph = graph;

		this.me = me;
		this.mc = mc;
		this.m = m;
	}

	public MessageEnvelope(Class<ME> me, Class<MC> mc, Class<M> m) {

		this(graphFactory.openGraph(), me, mc, m);
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
	@SuppressWarnings("unchecked")
	public static <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> ME fromGraph(Graph graph, Class<ME> me, Class<MC> mc, Class<M> m) {

		try {

			Method method = me.getMethod("fromGraph", Graph.class, Class.class, Class.class, Class.class);
			return (ME) method.invoke(null, graph, me, mc, m);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
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
	@SuppressWarnings("unchecked")
	public MC getMessageCollection(XDIAddress senderXDIAddress, boolean create) {

		if (senderXDIAddress == null) senderXDIAddress = XDIAuthenticationConstants.XDI_ADD_ANONYMOUS;

		XDIAddress messageCollectionXDIAddress = XDIAddress.create(senderXDIAddress.toString() + XdiEntityCollection.createEntityCollectionXDIArc(XDIMessagingConstants.XDI_ARC_MSG));
		ContextNode contextNode = create ? this.getGraph().setDeepContextNode(messageCollectionXDIAddress) : this.getGraph().getDeepContextNode(messageCollectionXDIAddress, true);

		if (contextNode == null) return null;

		XdiEntityCollection xdiEntityCollection = XdiEntityCollection.fromContextNode(contextNode);

		try {

			Method method = this.getMC().getMethod("fromMessageEnvelopeAndXdiEntityCollection", this.getME(), XdiEntityCollection.class);
			return (MC) method.invoke(null, this, xdiEntityCollection);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/**
	 * Returns all message collections in this message envelope.
	 * @return All message collections in the envelope.
	 */
	public ReadOnlyIterator<MC> getMessageCollections() {

		// get all context nodes that are valid XDI message collections

		Iterator<ContextNode> contextNodes = this.getGraph().getRootContextNode(true).getAllContextNodes();

		return new MappingXdiEntityCollectionMessageCollectionIterator<ME, MC, M> (this, new MappingContextNodeXdiEntityCollectionIterator(contextNodes));
	}

	/**
	 * Deletes all message collections from this message envelope.
	 */
	public void deleteMessageCollections() {

		for (MC messageCollection : new IteratorListMaker<MC> (this.getMessageCollections()).list()) {

			messageCollection.getContextNode().delete();
		}
	}

	/**
	 * Returns all messages in this message envelope.
	 * @return All messages contained in the envelope.
	 */
	public ReadOnlyIterator<M> getMessages() {

		return new DescendingIterator<MC, M> (this.getMessageCollections()) {

			@Override
			public Iterator<M> descend(MC messageCollection) {

				return messageCollection.getMessages();
			}
		};
	}

	/**
	 * Finds messages with a given sender in this message envelope.
	 * @param senderXDIAddress The sender to look for.
	 * @return The messages.
	 */
	public ReadOnlyIterator<M> getMessages(XDIAddress senderXDIAddress) {

		MC messageCollection = this.getMessageCollection(senderXDIAddress, false);
		if (messageCollection == null) return new EmptyIterator<M> ();

		return messageCollection.getMessages();
	}

	/**
	 * Returns all operations in this message envelope.
	 * @return All messages contained in the envelope.
	 */
	public ReadOnlyIterator<Operation> getOperations() {

		return new DescendingIterator<M, Operation>(this.getMessages()) {

			@Override
			public Iterator<Operation> descend(M item) {

				return item.getOperations();
			}
		};
	}

	/**
	 * Returns the number of message collections in the message envelope.
	 */
	public long getMessageCollectionCount() {

		Iterator<MC> iterator = this.getMessageCollections();

		return new IteratorCounter(iterator).count();
	}

	/**
	 * Returns the number of messages in the message envelope.
	 */
	public long getMessageCount() {

		Iterator<M> iterator = this.getMessages();

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
	public M createMessage(XDIAddress senderXDIAddress) {

		return this.getMessageCollection(senderXDIAddress, true).createMessage();
	}

	/**
	 * Creates a new XDI message in this XDI message envelope for a given sender.
	 * @param senderXDIAddress The sender.
	 * @param index Index in an ordered collection.
	 * @return The newly created XDI message.
	 */
	public M createMessage(XDIAddress senderXDIAddress, long index) {

		return this.getMessageCollection(senderXDIAddress, true).createMessage(index);
	}

	public Class<ME> getME() {

		return this.me;
	}

	public Class<MC> getMC() {

		return this.mc;
	}

	public Class<M> getM() {

		return this.m;
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

		MessageEnvelope<?, ?, ?> other = (MessageEnvelope<?, ?, ?>) object;

		return this.getGraph().equals(other.getGraph());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getGraph().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(MessageEnvelope<?, ?, ?> other) {

		if (other == this || other == null) return 0;

		return this.getGraph().compareTo(other.getGraph());
	}

	/*
	 * Helper classes
	 */

	public static class MappingXdiEntityCollectionMessageCollectionIterator <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> extends NotNullIterator<MC> {

		public MappingXdiEntityCollectionMessageCollectionIterator(final MessageEnvelope<ME, MC, M> messageEnvelope, Iterator<XdiEntityCollection> xdiEntityCollections) {

			super(new MappingIterator<XdiEntityCollection, MC> (xdiEntityCollections) {

				@Override
				public MC map(XdiEntityCollection xdiEntityCollection) {

					return (MC) MessageCollection.fromMessageEnvelopeAndXdiEntityCollection(messageEnvelope, xdiEntityCollection);
				}
			});
		}
	}
}
