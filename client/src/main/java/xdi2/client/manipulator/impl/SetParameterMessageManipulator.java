package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.impl.AbstractLiteralNode;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

public class SetParameterMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIAddress parameterAddress;
	private String parameterValue;

	public SetParameterMessageManipulator(XDIAddress parameterAddress, String parameterValue) {

		this.parameterAddress = parameterAddress;
		this.parameterValue = parameterValue;
	}

	public SetParameterMessageManipulator() {

		this.parameterAddress = null;
		this.parameterValue = null;
	}

	@Override
	public void manipulate(Message message, ManipulationContext manipulationContext) throws Xdi2ClientException {

		if (this.getParameterAddress() != null && this.getParameterValue() != null) {

			message.setParameter(this.getParameterAddress(), AbstractLiteralNode.stringToLiteralData(this.getParameterValue()));
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

	public String getParameterValue() {

		return this.parameterValue;
	}

	public void setParameterValue(String parameterValue) {

		this.parameterValue = parameterValue;
	}
}
