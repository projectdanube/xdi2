package xdi2.client.manipulator.impl;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.ManipulationContext;
import xdi2.client.manipulator.MessageManipulator;
import xdi2.core.features.linkcontracts.instance.LinkContract;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;

public class SetLinkContractMessageManipulator extends AbstractMessageManipulator implements MessageManipulator {

	private XDIAddress linkContractXDIAddress;
	private LinkContract linkContract;
	private Class<? extends LinkContract> linkContractClass;

	public SetLinkContractMessageManipulator(XDIAddress linkContractXDIAddress) {

		this.linkContractXDIAddress = linkContractXDIAddress;
		this.linkContract = null;
		this.linkContractClass = null;
	}

	public SetLinkContractMessageManipulator(LinkContract linkContract) {

		this.linkContractXDIAddress = null;
		this.linkContract = linkContract;
		this.linkContractClass = null;
	}

	public SetLinkContractMessageManipulator(Class<? extends LinkContract> linkContractClass) {

		this.linkContractXDIAddress = null;
		this.linkContract = null;
		this.linkContractClass = linkContractClass;
	}

	public SetLinkContractMessageManipulator() {

		this.linkContractXDIAddress = null;
		this.linkContract = null;
		this.linkContractClass = null;
	}

	@Override
	public void manipulate(Message message, ManipulationContext manipulationContext) throws Xdi2ClientException {

		if (this.getLinkContractXDIAddress() != null) {

			message.setLinkContractXDIAddress(this.getLinkContractXDIAddress());
		} else if (this.getLinkContract() != null) {

			message.setLinkContract(this.getLinkContract());
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

	public LinkContract getLinkContract() {

		return this.linkContract;
	}

	public void setLinkContract(LinkContract linkContract) {

		this.linkContract = linkContract;
	}

	public Class<? extends LinkContract> getLinkContractClass() {

		return this.linkContractClass;
	}

	public void setLinkContractClass(Class<? extends LinkContract> linkContractClass) {

		this.linkContractClass = linkContractClass;
	}
}
