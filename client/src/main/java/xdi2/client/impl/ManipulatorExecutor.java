package xdi2.client.impl;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.MessageEnvelopeManipulator;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public class ManipulatorExecutor {

	private static final Logger log = LoggerFactory.getLogger(ManipulatorExecutor.class);

	private ManipulatorExecutor() {

	}

	/*
	 * Methods for executing manipulators
	 */

	public static void executeMessageEnvelopeManipulators(ManipulatorList manipulatorList, MessageEnvelope messageEnvelope, ManipulationContext manipulationContext) throws Xdi2ClientException {

		for (Iterator<MessageEnvelopeManipulator> messageEnvelopeManipulators = findMessageEnvelopeManipulators(manipulatorList); messageEnvelopeManipulators.hasNext(); ) {

			MessageEnvelopeManipulator messageEnvelopeManipulator = messageEnvelopeManipulators.next();

			if (log.isDebugEnabled()) log.debug("Executing message envelope manipulator " + messageEnvelopeManipulator.getClass().getSimpleName() + " (before).");

			messageEnvelopeManipulator.manipulate(messageEnvelope, manipulationContext);
		}
	}

	public static void executeMessageManipulators(ManipulatorList manipulatorList, Message message, ManipulationContext manipulationContext) throws Xdi2ClientException {

		for (Iterator<MessageManipulator> messageManipulators = findMessageManipulators(manipulatorList); messageManipulators.hasNext(); ) {

			MessageManipulator messageManipulator = messageManipulators.next();

			if (log.isDebugEnabled()) log.debug("Executing message manipulator " + messageManipulator.getClass().getSimpleName() + " (before).");

			messageManipulator.manipulate(message, manipulationContext);
		}
	}

	/*
	 * Methods for finding manipulators
	 */

	public static Iterator<MessageEnvelopeManipulator> findMessageEnvelopeManipulators(ManipulatorList manipulatorList) {

		return manipulatorList.findManipulators(MessageEnvelopeManipulator.class);
	}

	public static Iterator<MessageManipulator> findMessageManipulators(ManipulatorList manipulatorList) {

		return manipulatorList.findManipulators(MessageManipulator.class);
	}
}
