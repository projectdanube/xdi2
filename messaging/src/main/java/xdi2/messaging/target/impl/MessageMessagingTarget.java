package xdi2.messaging.target.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.exceptions.Xdi2RuntimeException;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
import xdi2.messaging.target.ExecutionContext;

/**
 * The MessageMessagingTarget allows subclasses to register completely specified
 * types of XDI messages that are understood by the endpoint.
 * 
 * The MessageMessagingTarget executes an XDI message by executing a matching
 * MessageHandler in a single step.
 * 
 * Subclasses must do the following:
 * - Register Message classes and MessageHandlers that can handle them.
 * 
 * @author markus
 */
public class MessageMessagingTarget extends AbstractMessagingTarget {

	private static final Logger log = LoggerFactory.getLogger(MessageMessagingTarget.class);

	private Map<Class<? extends Message>, MessageHandler> messageHandlers;
	private Map<Class<? extends Message>, Method> isValidMethods;
	private Map<Class<? extends Message>, Method> fromContextNodeMethods;

	public MessageMessagingTarget() {

		super();

		this.messageHandlers = new HashMap<Class<? extends Message>, MessageHandler> ();
	}

	@Override
	public void init() throws Exception {

		super.init();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	public void registerMessageHandler(Class<? extends Message> messageClass, MessageHandler messageHandler) {

		// find the message class' isValid() and fromContextNode() methods
		
		Method isValidMethod;
		Method fromContextNodeMethod;

		try {

			isValidMethod = messageClass.getMethod("isValid", ContextNode.class);
			if (isValidMethod == null) throw new NullPointerException();

			fromContextNodeMethod = messageClass.getMethod("fromContextNode", ContextNode.class);
			if (fromContextNodeMethod == null) throw new NullPointerException();
		} catch (NoSuchMethodException ex) {

			throw new Xdi2RuntimeException("Message class without correct isValid() or fromContextNode() method.", ex);
		}
		
		// register the message class

		this.messageHandlers.put(messageClass, messageHandler);
		this.isValidMethods.put(messageClass, isValidMethod);
		this.fromContextNodeMethods.put(messageClass, fromContextNodeMethod);
	}

	@Override
	public final boolean execute(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		boolean handled = false;

		// find out which message handler can handle this message

		for (Map.Entry<Class<? extends Message>, MessageHandler> entry : this.messageHandlers.entrySet()) {

			MessageHandler messageHandler = entry.getValue();
			Method isValidMethod = this.isValidMethods.get(entry.getKey());
			Method fromContextNodeMethod = this.fromContextNodeMethods.get(entry.getKey());

			// check if the message is a valid instance

			Boolean valid;

			try {

				valid = (Boolean) isValidMethod.invoke(null, message.getContextNode());
				if (valid == null) throw new NullPointerException();
			} catch (Exception ex) {

				MessageMessagingTarget.log.warn("Warning: Cannot call isValid() method.", ex);
				continue;
			}

			if (! valid.equals(Boolean.TRUE)) continue;

			// create an instance of that particular message class

			Message newMessage;

			try {

				newMessage = (Message) fromContextNodeMethod.invoke(null, message.getContextNode());
				if (newMessage == null) throw new NullPointerException();
			} catch (Exception ex) {

				MessageMessagingTarget.log.warn("Warning: Cannot call fromContextNode() method.", ex);
				continue;
			}

			// invoke the message handler

			if (messageHandler.execute(newMessage, messageResult, executionContext)) handled = true;
			break;
		}

		return handled;
	}

	@Override
	public boolean execute(Operation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		throw new Xdi2MessagingException("Cannot execute individual operations on this messaging target.");
	}
}
