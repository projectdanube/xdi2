package xdi2.messaging.target.contributor.impl.proxy.manipulator;

import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.Operation;
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

		for (Message message : messageEnvelope.getMessages()) {

			this.manipulate(message, executionContext);
		}
	}

	public void manipulate(Message message, ExecutionContext executionContext) throws Xdi2MessagingException {

		for (Operation operation : message.getOperations()) {

			this.manipulate(operation, executionContext);
		}
	}

	public void manipulate(Operation operation, ExecutionContext executionContext) throws Xdi2MessagingException {

	}

	@Override
	public void manipulate(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

	}
}
