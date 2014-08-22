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

	private XDIArc defaultToPeerRootXri;

	public ToInterceptor() {

		this.defaultToPeerRootXri = null;
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
		XDIArc ownerPeerRootXri = messagingTarget.getOwnerPeerRootXri();
		XDIArc toPeerRootXri = message.getToPeerRootXri();

		if (toPeerRootXri == null) toPeerRootXri = this.getDefaultToPeerRootXri();

		if (log.isDebugEnabled()) log.debug("ownerPeerRootXri=" + ownerPeerRootXri + ", toPeerRootXri=" + toPeerRootXri);

		if (toPeerRootXri == null) throw new Xdi2MessagingException("No TO peer root XRI found in message.", null, null);

		if (! toPeerRootXri.equals(ownerPeerRootXri)) throw new Xdi2MessagingException("Invalid TO peer root XRI: " + toPeerRootXri, null, null);

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

	public XDIArc getDefaultToPeerRootXri() {

		return this.defaultToPeerRootXri;
	}

	public void setDefaultToPeerRootXri(XDIArc defaultToPeerRootXri) {

		this.defaultToPeerRootXri = defaultToPeerRootXri;
	}
}
