package xdi2.messaging.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> ME cloneMessageEnvelope(ME messageEnvelope) {

		Graph clonedGraph = CloneUtil.cloneGraph(messageEnvelope.getGraph());

		ME clonedMessageEnvelope = MessageEnvelope.fromGraph(clonedGraph, messageEnvelope.getME(), messageEnvelope.getMC(), messageEnvelope.getM());
		if (log.isTraceEnabled()) log.trace("Cloned message envelope: " + clonedMessageEnvelope);

		return clonedMessageEnvelope;
	}

	/**
	 * Creates a clone of the given message collection with the same contents.
	 * @param messageCollection The message collection to clone.
	 * @return The cloned message collection.
	 */
	public static <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> MC cloneMessageCollection(MC messageCollection) {

		ME clonedMessageEnvelope = cloneMessageEnvelope(messageCollection.getMessageEnvelope());
		clonedMessageEnvelope.deleteMessageCollections();

		CopyUtil.copyContextNode(messageCollection.getContextNode(), clonedMessageEnvelope.getGraph(), null);

		MC clonedMessageCollection = clonedMessageEnvelope.getMessageCollections().next();
		if (log.isTraceEnabled()) log.trace("Cloned message collection: " + clonedMessageCollection.getMessageEnvelope());

		return clonedMessageCollection;
	}

	/**
	 * Creates a clone of the given message with the same contents.
	 * @param message The message to clone.
	 * @return The cloned message.
	 */
	public static <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> M cloneMessage(M message) {

		MessageCollection<ME, MC, M> clonedMessageCollection = cloneMessageCollection(message.getMessageCollection());
		clonedMessageCollection.deleteMessages();

		XdiEntity xdiEntity = message.getXdiEntity();

		if (xdiEntity instanceof XdiEntityMemberUnordered) {

			XdiEntityMemberUnordered xdiEntityMemberUnordered = clonedMessageCollection.getXdiEntityCollection().setXdiMemberUnordered(null);
			CopyUtil.copyContextNodeContents(message.getContextNode(), xdiEntityMemberUnordered.getContextNode(), null);
		} else if (xdiEntity instanceof XdiEntityMemberOrdered) {

			XdiEntityMemberOrdered xdiEntityMemberOrdered = clonedMessageCollection.getXdiEntityCollection().setXdiMemberOrdered(-1);
			CopyUtil.copyContextNodeContents(message.getContextNode(), xdiEntityMemberOrdered.getContextNode(), null);
		} else {

			throw new Xdi2RuntimeException("Unexpected message entity: " + xdiEntity + " (" + xdiEntity.getClass().getSimpleName() + ")");
		}

		M clonedMessage = clonedMessageCollection.getMessages().next();
		if (log.isTraceEnabled()) log.trace("Cloned message: " + clonedMessage.getMessageEnvelope());

		return clonedMessage;
	}

	/**
	 * Creates a clone of the given operation with the same contents.
	 * @param operation The operation to clone.
	 * @return The cloned operation.
	 */
	@SuppressWarnings("unchecked")
	public static <ME extends MessageEnvelope<ME, MC, M>, MC extends MessageCollection<ME, MC, M>, M extends Message<ME, MC, M>> Operation cloneOperation(Operation operation) {

		M clonedMessage = cloneMessage((M) operation.getMessage());
		clonedMessage.deleteOperations();

		CopyUtil.copyRelation(operation.getRelation(), clonedMessage.getMessageEnvelope().getGraph(), null);

		Operation clonedOperation = clonedMessage.getOperations().next();
		if (log.isTraceEnabled()) log.trace("Cloned operation: " + clonedOperation.getMessageEnvelope());

		return clonedOperation;
	}
}
