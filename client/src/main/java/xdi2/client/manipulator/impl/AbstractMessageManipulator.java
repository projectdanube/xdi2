package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.messaging.Message;

public abstract class AbstractMessageManipulator extends AbstractManipulator implements MessageManipulator {

	@Override
	public void manipulate(Message message) throws Xdi2ClientException {

	}
}
