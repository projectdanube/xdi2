package xdi2.messaging.target.impl.n;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;

public class NullMessagingTarget implements MessagingTarget {

	public NullMessagingTarget() {

	}

	@Override
	public void init() throws Exception {

	}

	@Override
	public void shutdown() {

	}

	@Override
	public boolean execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
