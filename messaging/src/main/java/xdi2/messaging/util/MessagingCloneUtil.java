package xdi2.messaging.util;

import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityMemberOrdered;
import xdi2.core.features.nodetypes.XdiEntityMemberUnordered;
import xdi2.core.util.CloneUtil;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageCollection;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.Operation;

/**
 * Various utility methods for cloning messaging components.
 * 
 * @author markus
 */
public final class MessagingCloneUtil {

	/**
	 * Creates a clone of the given message envelope with the same contents.
	 * @param messageEnvelope The message envelope to clone.
	 * @return The cloned message envelope.
	 */
	public static MessageEnvelope cloneMessageEnvelope(MessageEnvelope messageEnvelope) {

		Graph clonedGraph = CloneUtil.cloneGraph(messageEnvelope.getGraph());

		return MessageEnvelope.fromGraph(clonedGraph);
	}

	/**
	 * Creates a clone of the given message collection with the same contents.
	 * @param messageCollection The message collection to clone.
	 * @return The cloned message collection.
	 */
	public static MessageCollection cloneMessageCollection(MessageCollection messageCollection) {

		MessageEnvelope clonedMessageEnvelope = cloneMessageEnvelope(messageCollection.getMessageEnvelope());
		clonedMessageEnvelope.deleteMessageCollections();

		CopyUtil.copyContextNode(messageCollection.getContextNode(), clonedMessageEnvelope.getGraph(), null);

		return clonedMessageEnvelope.getMessageCollections().next();
	}

	/**
	 * Creates a clone of the given message with the same contents.
	 * @param message The message to clone.
	 * @return The cloned message.
	 */
	public static Message cloneMessage(Message message) {

		MessageCollection clonedMessageCollection = cloneMessageCollection(message.getMessageCollection());
		clonedMessageCollection.deleteMessages();

		XdiEntity xdiEntity = message.getXdiEntity();

		if (xdiEntity instanceof XdiEntityMemberUnordered) {

			XdiEntityMemberUnordered xdiEntityMemberUnordered = clonedMessageCollection.getXdiEntityCollection().setXdiMemberUnordered(((XdiEntityMemberUnordered) xdiEntity).getArcXri());
			CopyUtil.copyContextNodeContents(message.getContextNode(), xdiEntityMemberUnordered.getContextNode(), null);
		} else if (xdiEntity instanceof XdiEntityMemberOrdered) {

			XdiEntityMemberOrdered xdiEntityMemberOrdered = clonedMessageCollection.getXdiEntityCollection().setXdiMemberOrdered(-1);
			CopyUtil.copyContextNodeContents(message.getContextNode(), xdiEntityMemberOrdered.getContextNode(), null);
		} else {

			throw new Xdi2RuntimeException("Unexpected message entity: " + xdiEntity + " (" + xdiEntity.getClass().getSimpleName() + ")");
		}

		return clonedMessageCollection.getMessages().next();
	}

	/**
	 * Creates a clone of the given operation with the same contents.
	 * @param operation The operation to clone.
	 * @return The cloned operation.
	 */
	public static Operation cloneOperation(Operation operation) {

		Message clonedMessage = cloneMessage(operation.getMessage());
		clonedMessage.deleteOperations();

		CopyUtil.copyRelation(operation.getRelation(), clonedMessage.getMessageEnvelope().getGraph(), null);

		return clonedMessage.getOperations().next();
	}
}
