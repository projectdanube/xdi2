package xdi2.messaging.target.interceptor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor checks if the target peer root address of a message matches the owner of the messaging target.
 * 
 * @author markus
 */
public class ToInterceptor extends AbstractInterceptor implements MessageInterceptor, Prototype<ToInterceptor> {

	private static Logger log = LoggerFactory.getLogger(ToInterceptor.class.getName());

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
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();
		XDI3Segment ownerAddress = messagingTarget.getOwnerAddress();
		XDI3Segment toAddress = message.getToAddress();

		if (log.isDebugEnabled()) log.debug("ownerAddress=" + ownerAddress + ", toAddress=" + toAddress);

		if (toAddress == null) throw new Xdi2MessagingException("No TO address found in message.", null, null);

		if (! toAddress.equals(ownerAddress)) throw new Xdi2MessagingException("Invalid TO address: " + toAddress, null, null);

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
