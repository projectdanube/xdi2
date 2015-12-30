package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.manipulator.MessageEnvelopeManipulator;
import xdi2.messaging.MessageEnvelope;

public abstract class AbstractMessageEnvelopeManipulator extends AbstractManipulator implements MessageEnvelopeManipulator {

	@Override
	public void manipulate(MessageEnvelope messageEnvelope, ManipulationContext manipulationContext) throws Xdi2ClientException {

	}
}
