package xdi2.messaging.target.interceptor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor checks if the target peer root XRI of a message matches the owner peer root XRI of the messaging target.
 * 
 * @author markus
 */
public class ToInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<ToInterceptor> {

	private static Logger log = LoggerFactory.getLogger(ToInterceptor.class.getName());

	private XDIArc defaultToPeerRootXDIArc;

	public ToInterceptor() {

		this.defaultToPeerRootXDIArc = null;
	}

	/*
	 * Prototype
	 */

	@Override
	public ToInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check if the owner peer root XRI matches the TO peer root XRI

		MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();
		XDIArc ownerPeerRootXDIArc = messagingTarget.getOwnerPeerRootXDIArc();
		XDIArc toPeerRootXDIArc = message.getToPeerRootXDIArc();

		if (toPeerRootXDIArc == null) toPeerRootXDIArc = this.getDefaultToPeerRootXDIArc();

		if (log.isDebugEnabled()) log.debug("ownerPeerRootXDIArc=" + ownerPeerRootXDIArc + ", toPeerRootXDIArc=" + toPeerRootXDIArc);

		if (toPeerRootXDIArc == null) throw new Xdi2MessagingException("No TO peer root found in message.", null, null);

		if (! toPeerRootXDIArc.equals(ownerPeerRootXDIArc)) throw new Xdi2MessagingException("Invalid TO peer root XRI: " + toPeerRootXDIArc, null, null);

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Getters and setters
	 */

	public XDIArc getDefaultToPeerRootXDIArc() {

		return this.defaultToPeerRootXDIArc;
	}

	public void setDefaultToPeerRootXDIArc(XDIArc defaultToPeerRootXDIArc) {

		this.defaultToPeerRootXDIArc = defaultToPeerRootXDIArc;
	}
}
