package xdi2.messaging;

import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityCollection;
import xdi2.core.features.nodetypes.XdiEntityInstance;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.features.nodetypes.XdiRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.constants.XDIMessagingConstants;

/**
 * An XDI message, represented as a context node.
 * 
 * @author markus
 */
public final class Message extends MessageBase<XdiEntity> {

	private static final long serialVersionUID = -908172536340407558L;

	private MessageCollection messageCollection;
	private XdiEntity xdiEntity;

	protected Message(MessageCollection messageCollection, XdiEntity xdiEntity) {

		if (messageCollection == null) throw new NullPointerException();
		if (xdiEntity == null) throw new NullPointerException();

		this.messageCollection = messageCollection;
		this.xdiEntity = xdiEntity;
	}

	/*
	 * Static methods
	 */

	/**
	 * Checks if an XDI entity is a valid XDI message.
	 * @param xdiEntity The XDI entity to check.
	 * @return True if the XDI entity is a valid XDI message.
	 */
	public static boolean isValid(XdiEntity xdiEntity) {

		if (xdiEntity instanceof XdiEntitySingleton) {

			if (! xdiEntity.getXDIArc().equals(XDIMessagingConstants.XDI_ADD_MSG)) return false;
		} else if (xdiEntity instanceof XdiEntityInstance) {

			XdiEntityCollection xdiCollection = ((XdiEntityInstance) xdiEntity).getXdiCollection();

			if (! xdiCollection.getXDIArc().equals(XDIMessagingConstants.XDI_ADD_EC_MSG)) return false;
		} else {

			return false;
		}

		if (xdiEntity.getXdiEntitySingleton(XDIMessagingConstants.XDI_ARC_DO, false) == null) return false;

		return true;
	}

	/**
	 * Factory method that creates an XDI message bound to a given XDI entity.
	 * @param messageCollection The XDI message collection to which this XDI message belongs.
	 * @param xdiEntity The XDI entity that is an XDI message.
	 * @return The XDI message.
	 */
	public static Message fromMessageCollectionAndXdiEntity(MessageCollection messageCollection, XdiEntity xdiEntity) {

		if (! isValid(xdiEntity)) return null;

		return new Message(messageCollection, xdiEntity);
	}

	/**
	 * Factory method that creates an XDI message bound to a given XDI entity.
	 * @param xdiEntity The XDI entity that is an XDI message.
	 * @return The XDI message.
	 */
	public static Message fromXdiEntity(XdiEntity xdiEntity) {

		XdiEntityCollection xdiEntityCollection = XdiEntityCollection.fromContextNode(xdiEntity.getContextNode().getContextNode());

		MessageCollection messageCollection = xdiEntityCollection == null ? null : MessageCollection.fromXdiEntityCollection(xdiEntityCollection);
		if (messageCollection == null) return null;

		return fromMessageCollectionAndXdiEntity(messageCollection, xdiEntity);
	}

	/*
	 * Instance methods
	 */

	/**
	 * Returns the underlying XDI entity to which this XDI message is bound.
	 * @return An XDI entity that represents the XDI message.
	 */
	public XdiEntity getXdiEntity() {

		return this.xdiEntity;
	}

	@Override
	public XdiEntity getXdiSubGraph() {

		return this.xdiEntity;
	}

	public XdiInnerRoot getXdiInnerRoot() {

		XdiRoot xdiRoot = this.getXdiEntity().findRoot();
		if (! (xdiRoot instanceof XdiInnerRoot)) return null;

		return (XdiInnerRoot) xdiRoot;
	}
	/**
	 * Returns the XDI message collection to which this XDI message (template) belongs.
	 * @return An XDI message collection.
	 */
	public MessageCollection getMessageCollection() {

		return this.messageCollection;
	}

	/**
	 * Returns the message envelope to which this message belongs.
	 * @return A message envelope.
	 */
	public MessageEnvelope getMessageEnvelope() {

		return this.getMessageCollection().getMessageEnvelope();
	}


	/**
	 * Returns the ID of the message.
	 * @return The ID of the message.
	 */
	public XDIArc getID() {

		return this.getContextNode().getXDIArc();
	}

	/**
	 * Returns the sender of the message (template)'s message collection.
	 * @return The sender of the message (template)'s message collection.
	 */
	public ContextNode getSender() {

		return this.getMessageCollection().getSender();
	}

	/**
	 * Returns the sender address of the message (template)'s message collection.
	 * @return The sender address of the message (template)'s message collection.
	 */
	public XDIAddress getSenderXDIAddress() {

		return this.getMessageCollection().getSenderXDIAddress();
	}

}
