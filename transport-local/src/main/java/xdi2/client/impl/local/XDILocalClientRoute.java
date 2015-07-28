package xdi2.client.impl.local;

import xdi2.client.XDIClientRoute;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.target.MessagingTarget;

public class XDILocalClientRoute extends XDIAbstractClientRoute<XDILocalClient> implements XDIClientRoute<XDILocalClient> {

	private MessagingTarget messagingTarget;

	public XDILocalClientRoute(XDIArc toPeerRootXDIArc, MessagingTarget messagingTarget) {

		super(toPeerRootXDIArc);

		this.messagingTarget = messagingTarget;
	}

	@Override
	public XDILocalClient constructXDIClient() {

		return new XDILocalClient(this.messagingTarget);
	}

	/*
	 * Getters and setters
	 */

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}
}
