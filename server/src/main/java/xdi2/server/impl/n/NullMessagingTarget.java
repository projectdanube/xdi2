package xdi2.server.impl.n;

import xdi2.exceptions.Xdi2MessagingException;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.server.EndpointRegistry;
import xdi2.server.ExecutionContext;
import xdi2.server.MessagingTarget;

public class NullMessagingTarget implements MessagingTarget {

	public NullMessagingTarget() {

	}

	public void init(EndpointRegistry endpointRegistry) throws Exception {

	}

	public void shutdown() {

	}

	public boolean execute(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return false;
	}
}
