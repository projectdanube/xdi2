package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;

public class SetToPeerRootMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIArc toPeerRootXDIArc;
	private XDIAddress toPeerRootXDIAddress;

	public SetToPeerRootMessageManipulator(XDIArc toPeerRootXDIArc) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.toPeerRootXDIAddress = null;
	}

	public SetToPeerRootMessageManipulator(XDIAddress toPeerRootXDIAddress) {

		this.toPeerRootXDIArc = null;
		this.toPeerRootXDIAddress = toPeerRootXDIAddress;
	}

	public SetToPeerRootMessageManipulator() {

		this.toPeerRootXDIArc = null;
		this.toPeerRootXDIAddress = null;
	}

	@Override
	public void manipulate(Message message, ManipulationContext manipulationContext) throws Xdi2ClientException {

		if (this.getToPeerRootXDIArc() != null) {

			message.setToPeerRootXDIArc(this.getToPeerRootXDIArc());
		} else if (this.getToPeerRootXDIAddress() != null) {

			message.setToXDIAddress(this.getToPeerRootXDIAddress());
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIArc getToPeerRootXDIArc() {

		return this.toPeerRootXDIArc;
	}

	public void setToPeerRootXDIArc(XDIArc toPeerRootXDIArc) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
	}

	public XDIAddress getToPeerRootXDIAddress() {

		return this.toPeerRootXDIAddress;
	}

	public void setToPeerRootXDIAddress(XDIAddress toPeerRootXDIAddress) {

		this.toPeerRootXDIAddress = toPeerRootXDIAddress;
	}
}
