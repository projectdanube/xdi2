package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

public class SetMessageTypeMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIAddress messageType;

	public SetMessageTypeMessageManipulator(XDIAddress messageType) {

		this.messageType = messageType;
	}

	public SetMessageTypeMessageManipulator() {

		this.messageType = null;
	}

	@Override
	public void manipulate(Message message, ManipulationContext manipulationContext) throws Xdi2ClientException {

		if (this.getMessageType() != null) {

			message.setMessageType(this.getMessageType());
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIAddress getMessageType() {

		return this.messageType;
	}

	public void setMessageType(XDIAddress messageType) {

		this.messageType = messageType;
	}
}
