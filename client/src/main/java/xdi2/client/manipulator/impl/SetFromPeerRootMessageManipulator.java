package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;

public class SetFromPeerRootMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIArc fromPeerRootXDIArc;
	private XDIAddress fromPeerRootXDIAddress;

	public SetFromPeerRootMessageManipulator(XDIArc fromPeerRootXDIArc) {

		this.fromPeerRootXDIArc = fromPeerRootXDIArc;
		this.fromPeerRootXDIAddress = null;
	}

	public SetFromPeerRootMessageManipulator(XDIAddress fromPeerRootXDIAddress) {

		this.fromPeerRootXDIArc = null;
		this.fromPeerRootXDIAddress = fromPeerRootXDIAddress;
	}

	public SetFromPeerRootMessageManipulator() {

		this.fromPeerRootXDIArc = null;
		this.fromPeerRootXDIAddress = null;
	}

	@Override
	public void manipulate(Message message, ManipulationContext manipulationContext) throws Xdi2ClientException {

		if (this.getFromPeerRootXDIArc() != null) {

			message.setFromPeerRootXDIArc(this.getFromPeerRootXDIArc());
		} else if (this.getFromPeerRootXDIAddress() != null) {

			message.setFromXDIAddress(this.getFromPeerRootXDIAddress());
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIArc getFromPeerRootXDIArc() {

		return this.fromPeerRootXDIArc;
	}

	public void setFromPeerRootXDIArc(XDIArc fromPeerRootXDIArc) {

		this.fromPeerRootXDIArc = fromPeerRootXDIArc;
	}

	public XDIAddress getFromPeerRootXDIAddress() {

		return this.fromPeerRootXDIAddress;
	}

	public void setFromPeerRootXDIAddress(XDIAddress fromPeerRootXDIAddress) {

		this.fromPeerRootXDIAddress = fromPeerRootXDIAddress;
	}
}
