package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.features.multiplicity.EntityCollection;
import xdi2.core.features.multiplicity.EntitySingleton;
import xdi2.core.features.multiplicity.Multiplicity;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * An XDI message collection, represented as a context node.
 * 
 * @author markus
 */
public final class MessageCollection implements Serializable, Comparable<MessageCollection> {

	private static final long serialVersionUID = -7493408194946194153L;

	private MessageEnvelope messageEnvelope;
	private EntityCollection entityCollection;

	protected MessageCollection(MessageEnvelope messageEnvelope, EntityCollection entityCollection) {

		if (messageEnvelope == null || entityCollection == null) throw new NullPointerException();

		this.messageEnvelope = messageEnvelope;
		this.entityCollection = entityCollection;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an entity collection is a valid XDI message collection.
	 * @param entityCollection The entity collection to check.
	 * @return True if the entity collection is a valid XDI message collection.
	 */
	public static boolean isValid(EntityCollection entityCollection) {

		return Multiplicity.entityCollectionArcXri(XDIMessagingConstants.XRI_SS_MSG.toString()).equals(entityCollection.getContextNode().getArcXri());
	}

	/**
	 * Factory method that creates an XDI message collection bound to a given entity collection.
	 * @param messageEnvelope The XDI message envelope to which this XDI message collection belongs.
	 * @param entityCollection The entity collection that is an XDI message collection.
	 * @return The XDI message collection.
	 */
	public static MessageCollection fromMessageEnvelopeAndEntityCollection(MessageEnvelope messageEnvelope, EntityCollection entityCollection) {

		if (! isValid(entityCollection)) return null;

		return new MessageCollection(messageEnvelope, entityCollection);
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
	 * Returns the underlying entity collection to which this XDI message collection is bound.
	 * @return An entity collection that represents the XDI message collection.
	 */
	public EntityCollection getEntityCollection() {

		return this.entityCollection;
	}

	/**
	 * Returns the underlying context node to which this XDI message collection is bound.
	 * @return A context node that represents the XDI message collection.
	 */
	public ContextNode getContextNode() {

		return this.entityCollection.getContextNode();
	}

	/**
	 * Returns the sender of the message collection.
	 * @return The sender of the message collection.
	 */
	public XRI3Segment getSender() {

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

			EntitySingleton entitySingleton = this.entityCollection.createEntitySingleton();
			entitySingleton.getContextNode().createContextNode(XDIMessagingConstants.XRI_SS_DO);

			return new Message(this, entitySingleton);
		} else {

			return null;
		}
	}

	/**
	 * Returns all messages in this message collection.
	 * @return All messages contained in the collection.
	 */
	public ReadOnlyIterator<Message> getMessages() {

		// get all context nodes that are valid XDI messages

		Iterator<EntitySingleton> messages = this.entityCollection.iterator();

		return new SelectingMappingIterator<EntitySingleton, Message> (messages) {

			@Override
			public boolean select(EntitySingleton entitySingleton) {

				return Message.isValid(entitySingleton);
			}

			@Override
			public Message map(EntitySingleton entitySingleton) {

				return Message.fromMessageCollectionAndEntitySingleton(MessageCollection.this, entitySingleton);
			}
		};
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
	public int getMessageCount() {

		return new IteratorCounter(this.getMessages()).count();
	}

	/**
	 * Returns the number of operations in all messages of the message envelope.
	 */
	public int getOperationCount() {

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
}
