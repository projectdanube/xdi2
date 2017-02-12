package xdi2.messaging.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.core.features.nodetypes.XdiAbstractEntity;
import xdi2.core.features.nodetypes.XdiEntity;
import xdi2.core.features.nodetypes.XdiEntityInstanceOrdered;
import xdi2.core.features.nodetypes.XdiEntityInstanceUnordered;
import xdi2.core.util.CloneUtil;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageCollection;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;

/**
 * Various utility methods for cloning messaging components.
 * 
 * @author markus
 */
public final class MessagingCloneUtil {

	private static Logger log = LoggerFactory.getLogger(MessagingCloneUtil.class.getName());

	/**
	 * Creates a clone of the given message envelope with the same contents.
	 * @param messageEnvelope The message envelope to clone.
	 * @return The cloned message envelope.
	 */
	public static MessageEnvelope cloneMessageEnvelope(MessageEnvelope messageEnvelope) {

		Graph clonedGraph = CloneUtil.cloneGraph(messageEnvelope.getGraph());

		MessageEnvelope clonedMessageEnvelope = MessageEnvelope.fromGraph(clonedGraph);
		if (log.isTraceEnabled()) log.trace("Cloned message envelope: " + clonedMessageEnvelope);

		return clonedMessageEnvelope;
	}

	/**
	 * Creates a clone of the given message with the same contents.
	 * @param message The message to clone.
	 * @return The cloned message.
	 */
	public static Message cloneMessage(Message message, boolean newId) {

		MessageCollection clonedMessageCollection = new MessageEnvelope().getMessageCollection(message.getSenderXDIAddress(), true);

		XdiEntity xdiEntity = message.getXdiEntity();

		ContextNode copiedContextNode;

		if (newId) {

			if (xdiEntity instanceof XdiEntityInstanceUnordered) {

				copiedContextNode = clonedMessageCollection.getXdiEntityCollection().setXdiInstanceUnordered(true, false).getContextNode();
				CopyUtil.copyContextNodeContents(message.getContextNode(), copiedContextNode, null);
			} else if (xdiEntity instanceof XdiEntityInstanceOrdered) {

				copiedContextNode = clonedMessageCollection.getXdiEntityCollection().setXdiInstanceOrdered(false, true).getContextNode();
				CopyUtil.copyContextNodeContents(message.getContextNode(), copiedContextNode, null);
			} else {

				throw new Xdi2RuntimeException("Unexpected message entity: " + xdiEntity + " (" + xdiEntity.getClass().getSimpleName() + ")");
			}
		} else {

			copiedContextNode = CopyUtil.copyContextNode(message.getContextNode(), clonedMessageCollection.getContextNode(), null);
		}

		XdiEntity clonedXdiEntity = XdiAbstractEntity.fromContextNode(copiedContextNode);
		Message clonedMessage = Message.fromMessageCollectionAndXdiEntity(clonedMessageCollection, clonedXdiEntity);
		if (log.isTraceEnabled()) log.trace("Cloned message: " + clonedMessage.getMessageEnvelope());

		return clonedMessage;
	}

	/**
	 * Creates a clone of the given operation with the same contents.
	 * @param operation The operation to clone.
	 * @return The cloned operation.
	 */
	public static Operation cloneOperation(Operation operation, boolean newId) {

		Message clonedMessage = cloneMessage(operation.getMessage(), newId);
		clonedMessage.deleteOperations();

		CopyUtil.copyRelation(operation.getRelation(), clonedMessage.getMessageEnvelope().getGraph(), null);

		Operation clonedOperation = clonedMessage.getOperations().next();
		if (log.isTraceEnabled()) log.trace("Cloned operation: " + clonedOperation.getMessage().getMessageEnvelope());

		return clonedOperation;
	}
}
