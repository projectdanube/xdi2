package xdi2.client.impl.local;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.target.MessagingTarget;

public class XDILocalClientRoute extends XDIAbstractClientRoute<XDILocalClient> implements XDIClientRoute<XDILocalClient> {

	private MessagingTarget messagingTarget;

	public XDILocalClientRoute(MessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
	}

	@Override
	public XDILocalClient constructXDIClient() {

		return new XDILocalClient(this.messagingTarget);
	}

	@Override
	public Message constructMessage(MessageEnvelope messageEnvelope) {

		Message message = super.constructMessage(messageEnvelope);
		message.setToPeerRootXDIArc(this.messagingTarget.getOwnerPeerRootXDIArc());

		return message;
	}
}
