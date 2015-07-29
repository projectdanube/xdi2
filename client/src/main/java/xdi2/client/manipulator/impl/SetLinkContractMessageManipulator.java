package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

public class SetLinkContractMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIAddress linkContractXDIAddress;
	private Class<? extends LinkContract> linkContractClass;

	public SetLinkContractMessageManipulator(XDIAddress linkContractXDIAddress) {

		this.linkContractXDIAddress = linkContractXDIAddress;
		this.linkContractClass = null;
	}

	public SetLinkContractMessageManipulator(Class<? extends LinkContract> linkContractClass) {

		this.linkContractXDIAddress = null;
		this.linkContractClass = linkContractClass;
	}

	public SetLinkContractMessageManipulator() {

		this.linkContractXDIAddress = null;
		this.linkContractClass = null;
	}

	@Override
	public void manipulate(Message message) throws Xdi2ClientException {

		if (this.getLinkContractXDIAddress() != null) {

			message.setLinkContractXDIAddress(this.getLinkContractXDIAddress());
		} else if (this.getLinkContractClass() != null) {

			message.setLinkContractClass(this.getLinkContractClass());
		}
	}

	/*
	 * Getters and setters
	 */

	public XDIAddress getLinkContractXDIAddress() {

		return this.linkContractXDIAddress;
	}

	public void setLinkContractXDIAddress(XDIAddress linkContractXDIAddress) {

		this.linkContractXDIAddress = linkContractXDIAddress;
	}

	public Class<? extends LinkContract> getLinkContractClass() {

		return this.linkContractClass;
	}

	public void setLinkContractClass(Class<? extends LinkContract> linkContractClass) {

		this.linkContractClass = linkContractClass;
	}
}
