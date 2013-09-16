package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityMemberUnordered;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * An XDI message collection, represented as a context node.
 * 
 * @author markus
 */
public final class MessageCollection implements Serializable, Comparable<MessageCollection> {

	private static final long serialVersionUID = -7493408194946194153L;

	private MessageEnvelope messageEnvelope;
	private XdiEntityCollection xdiEntityCollection;

	protected MessageCollection(MessageEnvelope messageEnvelope, XdiEntityCollection xdiEntityCollection) {

		if (messageEnvelope == null || xdiEntityCollection == null) throw new NullPointerException();

		this.messageEnvelope = messageEnvelope;
		this.xdiEntityCollection = xdiEntityCollection;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity class is a valid XDI message collection.
	 * @param xdiEntityCollection The XDI entity class to check.
	 * @return True if the XDI entity class is a valid XDI message collection.
	 */
	public static boolean isValid(XdiEntityCollection xdiEntityCollection) {

		return xdiEntityCollection.getContextNode().getArcXri().equals(XdiEntityCollection.createArcXri(XDIMessagingConstants.XRI_SS_MSG));
	}

	/**
	 * Factory method that creates an XDI message collection bound to a given XDI entity class.
	 * @param messageEnvelope The XDI message envelope to which this XDI message collection belongs.
	 * @param xdiEntityCollection The XDI entity class that is an XDI message collection.
	 * @return The XDI message collection.
	 */
	public static MessageCollection fromMessageEnvelopeAndXdiEntityClass(MessageEnvelope messageEnvelope, XdiEntityCollection xdiEntityCollection) {

		if (! isValid(xdiEntityCollection)) return null;

		return new MessageCollection(messageEnvelope, xdiEntityCollection);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the XDI message envelope to which this XDI message collection belongs.
	 * @return An XDI message envelope.
	 */
	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	/**
	 * Returns the underlying XDI entity class to which this XDI message collection is bound.
	 * @return An XDI entity class that represents the XDI message collection.
	 */
	public XdiEntityCollection getXdiEntityClass() {

		return this.xdiEntityCollection;
	}

	/**
	 * Returns the underlying context node to which this XDI message collection is bound.
	 * @return A context node that represents the XDI message collection.
	 */
	public ContextNode getContextNode() {

		return this.getXdiEntityClass().getContextNode();
	}

	/**
	 * Returns the sender of the message collection.
	 * @return The sender of the message collection.
	 */
	public XDI3Segment getSender() {

		return this.getContextNode().getContextNode().getXri();
	}

	/**
	 * Returns an existing XDI message in this XDI message collection, or creates a new one.
	 * @param create Whether to create an XDI message if it does not exist.
	 * @return The existing or newly created XDI message.
	 */
	public Message getMessage(boolean create) {

		Iterator<Message> messages = this.getMessages();
		if (messages.hasNext()) return messages.next();

		if (create) {

			XdiEntityMemberUnordered xdiEntityMember = this.xdiEntityCollection.setXdiMemberUnordered(null);
			xdiEntityMember.getXdiEntitySingleton(XDIMessagingConstants.XRI_SS_DO, true);

			return new Message(this, xdiEntityMember);
		} else {

			return null;
		}
	}

	/**
	 * Returns all messages in this message collection.
	 * @return All messages contained in the collection.
	 */
	public ReadOnlyIterator<Message> getMessages() {

		return new MappingXdiEntityMessageIterator(this, this.getXdiEntityClass().getXdiMembers(true));
	}

	/**
	 * Deletes all messages from this message collection.
	 */
	public void deleteMessages() {

		for (Message message : new IteratorListMaker<Message> (this.getMessages()).list()) {

			message.getContextNode().delete();
		}
	}

	/**
	 * Returns all operations in this message collection.
	 * @return All operations contained in the collection.
	 */
	public ReadOnlyIterator<Operation> getOperations() {

		return new DescendingIterator<Message, Operation> (this.getMessages()) {

			@Override
			public Iterator<Operation> descend(Message message) {

				return message.getOperations();
			}
		};
	}

	/**
	 * Returns the number of messages in the message collection.
	 */
	public long getMessageCount() {

		return new IteratorCounter(this.getMessages()).count();
	}

	/**
	 * Returns the number of operations in all messages of the message envelope.
	 */
	public long getOperationCount() {

		return new IteratorCounter(this.getOperations()).count();
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getContextNode().toString();
	}

	@Override
	public boolean equals(Object object) {

		if (object == null || ! (object instanceof MessageCollection)) return false;
		if (object == this) return true;

		MessageCollection other = (MessageCollection) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(MessageCollection other) {

		if (other == this || other == null) return(0);

		return this.getContextNode().compareTo(other.getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingXdiEntityMessageIterator extends NotNullIterator<Message> {

		public MappingXdiEntityMessageIterator(final MessageCollection messageCollection, Iterator<? extends XdiEntity> xdiEntities) {

			super(new MappingIterator<XdiEntity, Message> (xdiEntities) {

				@Override
				public Message map(XdiEntity xdiEntity) {

					return Message.fromMessageCollectionAndXdiEntity(messageCollection, xdiEntity);
				}
			});
		}
	}
}
