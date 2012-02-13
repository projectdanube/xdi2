package xdi2.messaging.util;

import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CloneUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageContainer;
import xdi2.messaging.MessageEnvelope;

/**
 * Various utility methods for cloning graph components.
 * 
 * @author markus
 */
public final class MessageCloneUtil {

	protected static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private MessageCloneUtil() { }

	/**
	 * Creates a copy of the given message envelope containing the same message containers.
	 * @param messageEnvelope The message envelope to clone.
	 * @return The cloned message envelope.
	 */
	public static MessageEnvelope cloneMessageEnvelope(MessageEnvelope messageEnvelope) {

		return MessageEnvelope.fromGraph(CloneUtil.cloneGraph(messageEnvelope.getGraph()));
	}

	/**
	 * Creates a copy of the given message container containing the same messages.
	 * @param messageContainer The message container to clone.
	 * @return The cloned message container.
	 */
	public static MessageContainer cloneMessage(MessageContainer messageContainer) {

		return MessageContainer.fromContextNode(CloneUtil.cloneContextNode(messageContainer.getContextNode()));
	}

	/**
	 * Creates a copy of the given message containing the same operations.
	 * @param message The message to clone.
	 * @return The cloned message.
	 */
	public static Message cloneMessage(Message message) {

		return Message.fromContextNode(CloneUtil.cloneContextNode(message.getContextNode()));
	}
}
