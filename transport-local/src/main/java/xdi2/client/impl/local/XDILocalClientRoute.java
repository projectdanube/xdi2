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

	public XDILocalClientRoute(MessagingTarget messagingTarget) {

		this(null, messagingTarget);
	}

	public XDILocalClientRoute() {

		this(null, null);
	}

	@Override
	protected XDILocalClient constructXDIClientInternal() {

		return new XDILocalClient(this.messagingTarget);
	}

	/*
	 * Getters and setters
	 */

	public MessagingTarget getMessagingTarget() {

		return this.messagingTarget;
	}

	public void setMessagingTarget(MessagingTarget messagingTarget) {

		this.messagingTarget = messagingTarget;
	}
}
