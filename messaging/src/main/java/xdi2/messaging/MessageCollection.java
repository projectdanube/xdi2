package xdi2.messaging;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityMemberOrdered;
import xdi2.core.features.nodetypes.XdiEntityMemberUnordered;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.core.util.iterators.MappingIterator;
import xdi2.core.util.iterators.NotNullIterator;
import xdi2.core.util.iterators.ReadOnlyIterator;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.operations.Operation;

/**
 * An XDI message collection, represented as a context node.
 * 
 * @author markus
 */
public class MessageCollection <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> implements Serializable, Comparable<MessageCollection<?, ?, ?>> {

	private static final long serialVersionUID = -7493408194946194153L;

	private ME messageEnvelope;
	private XdiEntityCollection xdiEntityCollection;

	protected MessageCollection(ME messageEnvelope, XdiEntityCollection xdiEntityCollection) {

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

		return xdiEntityCollection.getContextNode().getXDIArc().equals(XdiEntityCollection.createEntityCollectionXDIArc(XDIMessagingConstants.XDI_ARC_MSG));
	}

	/**
	 * Factory method that creates an XDI message collection bound to a given XDI entity collection.
	 * @param messageEnvelope The XDI message envelope to which this XDI message collection belongs.
	 * @param xdiEntityCollection The XDI entity collection that is an XDI message collection.
	 * @return The XDI message collection.
	 */
	@SuppressWarnings("unchecked")
	public static <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> MC fromMessageEnvelopeAndXdiEntityCollection(MessageEnvelope<ME, MC, M> messageEnvelope, XdiEntityCollection xdiEntityCollection) {

		try {

			Method method = messageEnvelope.getMC().getMethod("fromMessageEnvelopeAndXdiEntityCollection", messageEnvelope.getME(), XdiEntityCollection.class);
			return (MC) method.invoke(null, messageEnvelope, xdiEntityCollection);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the XDI message envelope to which this XDI message collection belongs.
	 * @return An XDI message envelope.
	 */
	public ME getMessageEnvelope() {

		return this.messageEnvelope;
	}

	/**
	 * Returns the underlying XDI entity class to which this XDI message collection is bound.
	 * @return An XDI entity class that represents the XDI message collection.
	 */
	public XdiEntityCollection getXdiEntityCollection() {

		return this.xdiEntityCollection;
	}

	/**
	 * Returns the underlying context node to which this XDI message collection is bound.
	 * @return A context node that represents the XDI message collection.
	 */
	public ContextNode getContextNode() {

		return this.getXdiEntityCollection().getContextNode();
	}

	/**
	 * Returns the sender of the message collection.
	 * @return The sender of the message collection.
	 */
	public ContextNode getSender() {

		return this.getContextNode().getContextNode();
	}

	/**
	 * Returns the sender address of the message collection.
	 * @return The sender adddress of the message collection.
	 */
	public XDIAddress getSenderXDIAddress() {

		return this.getSender().getXDIAddress();
	}

	/**
	 * Creates a new XDI message in this XDI message collection.
	 * @return The newly created XDI message.
	 */
	@SuppressWarnings("unchecked")
	public M createMessage() {

		XdiEntityMemberUnordered xdiEntityMember = this.xdiEntityCollection.setXdiMemberUnordered(null);
		xdiEntityMember.getXdiEntitySingleton(XDIMessagingConstants.XDI_ARC_DO, true);

		try {

			Method method = this.getMessageEnvelope().getM().getMethod("fromMessageCollectionAndXdiEntity", this.getMessageEnvelope().getMC(), XdiEntity.class);
			return (M) method.invoke(null, this, xdiEntityMember);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/**
	 * Creates a new XDI message in this XDI message collection.
	 * @param index Index in an ordered collection.
	 * @return The newly created XDI message.
	 */
	public M createMessage(long index) {

		XdiEntityMemberOrdered xdiEntityMember = this.xdiEntityCollection.setXdiMemberOrdered(index);
		xdiEntityMember.getXdiEntitySingleton(XDIMessagingConstants.XDI_ARC_DO, true);

		try {

			Constructor<M> constructor = this.getMessageEnvelope().getM().getConstructor(this.getMessageEnvelope().getMC(), XdiEntity.class);
			return constructor.newInstance(this, xdiEntityMember);
		} catch (Exception ex) {

			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/**
	 * Returns all messages in this message collection.
	 * @return All messages contained in the collection.
	 */
	public ReadOnlyIterator<M> getMessages() {

		return new MappingXdiEntityMessageIterator<ME, MC, M> (this, this.getXdiEntityCollection().getXdiMembersDeref());
	}

	/**
	 * Deletes all messages from this message collection.
	 */
	public void deleteMessages() {

		for (M message : new IteratorListMaker<M> (this.getMessages()).list()) {

			message.getContextNode().delete();
		}
	}

	/**
	 * Returns all operations in this message collection.
	 * @return All operations contained in the collection.
	 */
	public ReadOnlyIterator<Operation> getOperations() {

		return new DescendingIterator<M, Operation> (this.getMessages()) {

			@Override
			public Iterator<Operation> descend(M message) {

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

		MessageCollection<?, ?, ?> other = (MessageCollection<?, ?, ?>) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	@Override
	public int compareTo(MessageCollection<?, ?, ?> other) {

		if (other == this || other == null) return(0);

		return this.getContextNode().compareTo(other.getContextNode());
	}

	/*
	 * Helper classes
	 */

	public static class MappingXdiEntityMessageIterator <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> extends NotNullIterator<M> {

		public MappingXdiEntityMessageIterator(final MessageCollection<ME, MC, M> messageCollection, Iterator<? extends XdiEntity> xdiEntities) {

			super(new MappingIterator<XdiEntity, M> (xdiEntities) {

				@Override
				public M map(XdiEntity xdiEntity) {

					return Message.fromMessageCollectionAndXdiEntity(messageCollection, xdiEntity);
				}
			});
		}
	}
}
