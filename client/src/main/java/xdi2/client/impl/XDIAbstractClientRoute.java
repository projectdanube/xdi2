package xdi2.client.impl;

import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;

public abstract class XDIAbstractClientRoute <CLIENT extends XDIClient> implements XDIClientRoute<CLIENT> {

	private XDIArc toPeerRootXDIArc;
	private ManipulatorList manipulators;

	protected XDIAbstractClientRoute(XDIArc toPeerRootXDIArc) {

		this.toPeerRootXDIArc = toPeerRootXDIArc;
		this.manipulators = new ManipulatorList();
	}

	@Override
	public XDIArc getToPeerRootXDIArc() {

		return this.toPeerRootXDIArc;
	}

	@Override
	public final CLIENT constructXDIClient() {

		CLIENT client = this.constructXDIClientInternal();

		if (client instanceof XDIAbstractClient) {

			((XDIAbstractClient) client).setManipulators(this.getManipulators());
		}

		return client;
	}

	protected abstract CLIENT constructXDIClientInternal();

	@Override
	public MessageEnvelope constructMessageEnvelope() {

		MessageEnvelope messageEnvelope = new MessageEnvelope();

		return messageEnvelope;
	}

	@Override
	public Message constructMessage(MessageEnvelope messageEnvelope, XDIAddress senderXDIAddress) {

		Message message = messageEnvelope.createMessage(senderXDIAddress);

		if (this.getToPeerRootXDIArc() != null) message.setToPeerRootXDIArc(this.getToPeerRootXDIArc());

		return message;
	}

	@Override
	public Message constructMessage(MessageEnvelope messageEnvelope) {

		return this.constructMessage(messageEnvelope, XDIMessagingConstants.XDI_ADD_ANONYMOUS);
	}

	/*
	 * Getters and setters
	 */

	public ManipulatorList getManipulators() {

		return this.manipulators;
	}

	public void setManipulators(ManipulatorList manipulators) {

		this.manipulators = manipulators;
	}
}
