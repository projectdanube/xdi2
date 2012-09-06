package xdi2.messaging.target.interceptor.impl;

import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor checks if the recipient authority of a message matches the owner of the messaging target.
 * 
 * @author markus
 */
public class CheckOwnerInterceptor extends AbstractInterceptor implements MessageInterceptor {

	/*
	 * Interceptor
	 */

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessagingTarget messagingTarget = executionContext.getMessagingTarget();
		XRI3Segment ownerAuthority = messagingTarget.getOwnerAuthority();
		XRI3Segment recipientAuthority = message.getRecipientAuthority();

		if (ownerAuthority == null) return false;
		if (recipientAuthority == null) throw new Xdi2MessagingException("No recipient authority found in message.", null, null);

		if (! ownerAuthority.equals(recipientAuthority)) throw new Xdi2MessagingException("Unknown recipient authority: " + recipientAuthority, null, null);

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
