package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

public class SetCorrelationMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIAddress correlationXDIAddress;

	public SetCorrelationMessageManipulator(XDIAddress correlationXDIAddress) {

		this.correlationXDIAddress = correlationXDIAddress;
	}

	public SetCorrelationMessageManipulator() {

		this.correlationXDIAddress = null;
	}

	@Override
	public void manipulate(Message message) throws Xdi2ClientException {

		if (this.getCorrelationXDIAddress() != null) {

			message.setCorrelationXDIAddress(this.getCorrelationXDIAddress());
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIAddress getCorrelationXDIAddress() {

		return this.correlationXDIAddress;
	}

	public void setCorrelationXDIAddress(XDIAddress correlationXDIAddress) {

		this.correlationXDIAddress = correlationXDIAddress;
	}
}
