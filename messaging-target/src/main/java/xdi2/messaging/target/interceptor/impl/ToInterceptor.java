package xdi2.messaging.target.interceptor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor checks if the target peer root of a message matches the owner peer root of the messaging target.
 * 
 * @author markus
 */
public class ToInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<ToInterceptor> {

	private static Logger log = LoggerFactory.getLogger(ToInterceptor.class.getName());

	public ToInterceptor() {

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
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// check if the owner peer root matches the TO peer root

		MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();
		XDIArc ownerPeerRootXDIArc = messagingTarget.getOwnerPeerRootXDIArc();
		XDIArc toPeerRootXDIArc = message.getToPeerRootXDIArc();

		if (log.isDebugEnabled()) log.debug("ownerPeerRootXDIArc=" + ownerPeerRootXDIArc + ", toPeerRootXDIArc=" + toPeerRootXDIArc);

		if (toPeerRootXDIArc == null) throw new Xdi2MessagingException("No TO peer root found in message.", null, executionContext);

		if (! toPeerRootXDIArc.equals(ownerPeerRootXDIArc)) throw new Xdi2MessagingException("Invalid TO peer root: " + toPeerRootXDIArc, null, executionContext);

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}
}
