package xdi2.messaging.target.interceptor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.util.CopyUtil;
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
 * This interceptor checks if the recipient authority of a message matches the owner of the messaging target.
 * 
 * @author markus
 */
public class CheckOwnerInterceptor extends AbstractInterceptor implements MessageInterceptor, Prototype<CheckOwnerInterceptor> {

	private static Logger log = LoggerFactory.getLogger(CheckOwnerInterceptor.class.getName());

	/*
	 * Prototype
	 */

	@Override
	public CheckOwnerInterceptor instanceFor(PrototypingContext prototypingContext) {

		// done

		return this;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();
		XDI3Segment ownerAuthority = messagingTarget.getOwnerAuthority();
		XDI3Segment recipientAuthority = message.getRecipientAuthority();

		Graph g = MemoryGraphFactory.getInstance().openGraph();
		CopyUtil.copyContextNode(message.getContextNode(), g, null);

		if (log.isDebugEnabled()) log.debug("ownerAuthority=" + ownerAuthority + ", recipientAuthority=" + recipientAuthority);

		if (recipientAuthority == null) throw new Xdi2MessagingException("No recipient authority found in message.", null, null);

		if (! recipientAuthority.equals(ownerAuthority)) throw new Xdi2MessagingException("Invalid recipient authority: " + recipientAuthority, null, null);

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
