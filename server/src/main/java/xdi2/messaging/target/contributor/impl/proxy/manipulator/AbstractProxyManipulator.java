package xdi2.messaging.target.contributor.impl.proxy.manipulator;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractProxyManipulator implements ProxyManipulator {

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

	}

	@Override
	public void shutdown(MessagingTarget messagingTarget) throws Exception {

	}

	@Override
	public void manipulate(MessageEnvelope messageEnvelope, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	@Override
	public void manipulate(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
