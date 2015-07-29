package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

public class SetParameterMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIAddress parameterAddress;
	private Object parameterValue;

	public SetParameterMessageManipulator(XDIAddress parameterAddress, Object parameterValue) {

		this.parameterAddress = parameterAddress;
		this.parameterValue = parameterValue;
	}

	public SetParameterMessageManipulator() {

		this.parameterAddress = null;
		this.parameterValue = null;
	}

	@Override
	public void manipulate(Message message) throws Xdi2ClientException {

		if (this.getParameterAddress() != null && this.getParameterValue() != null) {

			message.setParameter(this.getParameterAddress(), this.getParameterValue());
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIAddress getParameterAddress() {

		return this.parameterAddress;
	}

	public void setParameterAddress(XDIAddress parameterAddress) {

		this.parameterAddress = parameterAddress;
	}

	public Object getParameterValue() {

		return this.parameterValue;
	}

	public void setParameterValue(Object parameterValue) {

		this.parameterValue = parameterValue;
	}
}
