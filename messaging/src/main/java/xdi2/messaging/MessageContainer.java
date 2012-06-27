package xdi2.messaging;

import java.io.Serializable;
import java.util.Iterator;

import xdi2.core.ContextNode;
import xdi2.core.util.XRIUtil;
import xdi2.core.util.iterators.DescendingIterator;
import xdi2.core.util.iterators.IteratorCounter;
import xdi2.core.util.iterators.SelectingMappingIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * An XDI message container, represented as a context node.
 * 
 * @author markus
 */
public final class MessageContainer implements Serializable, Comparable<MessageContainer> {

	private static final long serialVersionUID = -7493408194946194153L;

	private MessageEnvelope messageEnvelope;
	private ContextNode contextNode;

	protected MessageContainer(MessageEnvelope messageEnvelope, ContextNode contextNode) {

		if (messageEnvelope == null || contextNode == null) throw new NullPointerException();

		this.messageEnvelope = messageEnvelope;
		this.contextNode = contextNode;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if a context node is a valid XDI message container.
	 * @param contextNode The context node to check.
	 * @return True if the context node is a valid XDI message container.
	 */
	public static boolean isValid(ContextNode contextNode) {

		return XDIMessagingConstants.XRI_SS_MSG.equals(contextNode.getArcXri());
	}

	/**
	 * Factory method that creates an XDI message container bound to a given context node.
	 * @param messageEnvelope The XDI message envelope to which this XDI message container belongs.
	 * @param contextNode The context node that is an XDI message container.
	 * @return The XDI message container.
	 */
	public static MessageContainer fromMessageEnvelopeAndContextNode(MessageEnvelope messageEnvelope, ContextNode contextNode) {

		if (! isValid(contextNode)) return null;

		return new MessageContainer(messageEnvelope, contextNode);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the XDI message envelope to which this XDI message container belongs.
	 * @return An XDI message envelope.
	 */
	public MessageEnvelope getMessageEnvelope() {

		return this.messageEnvelope;
	}

	/**
	 * Returns the underlying context node to which this XDI message container is bound.
	 * @return A context node that represents the XDI message container.
	 */
	public ContextNode getContextNode() {

		return this.contextNode;
	}

	/**
	 * Returns the sender of the message container.
	 * @return The sender of the message container.
	 */
	public XRI3Segment getSender() {

		return this.getContextNode().getContextNode().getXri();
	}

	/**
	 * Returns an existing XDI message in this XDI message container, or creates a new one.
	 * @param create Whether to create an XDI message if it does not exist.
	 * @return The existing or newly created XDI message.
	 */
	public Message getMessage(boolean create) {

		Iterator<Message> messages = this.getMessages();
		if (messages.hasNext()) return messages.next();

		if (create) {

			ContextNode contextNode = this.getContextNode().createContextNode(XRIUtil.randomHEXXRefSubSegment("$", "!"));
			contextNode.createContextNode(XDIMessagingConstants.XRI_SS_DO);
	
			return new Message(this, contextNode);
		} else {
			
			return null;
		}
	}

	/**
	 * Returns all messages in this message container.
	 * @return All messages contained in the container.
	 */
	public Iterator<Message> getMessages() {

		// get all context nodes that are valid XDI messages

		Iterator<ContextNode> contextNodes = this.getContextNode().getContextNodes();

		return new SelectingMappingIterator<ContextNode, Message> (contextNodes) {

			@Override
			public boolean select(ContextNode contextNode) {

				return Message.isValid(contextNode);
			}

			@Override
			public Message map(ContextNode contextNode) {

				return Message.fromMessageContainerAndContextNode(MessageContainer.this, contextNode);
			}
		};
	}

	/**
	 * Returns all operations in this message container.
	 * @return All operations contained in the container.
	 */
	public Iterator<Operation> getOperations() {

		return new DescendingIterator<Message, Operation> (this.getMessages()) {

			@Override
			public Iterator<Operation> descend(Message message) {

				return message.getOperations();
			}
		};
	}

	/**
	 * Returns the number of messages in the message container.
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

		if (object == null || ! (object instanceof MessageContainer)) return false;
		if (object == this) return true;

		MessageContainer other = (MessageContainer) object;

		return this.getContextNode().equals(other.getContextNode());
	}

	@Override
	public int hashCode() {

		int hashCode = 1;

		hashCode = (hashCode * 31) + this.getContextNode().hashCode();

		return hashCode;
	}

	public int compareTo(MessageContainer other) {

		if (other == this || other == null) return(0);

		return this.getContextNode().compareTo(other.getContextNode());
	}
}
