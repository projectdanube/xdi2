package xdi2.messaging.target.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractMessagingTarget;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractInterceptor implements Interceptor {

	private static final Logger log = LoggerFactory.getLogger(AbstractMessagingTarget.class);

	protected void feedback(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessagingTarget messagingTarget = executionContext.getMessagingTarget();

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Initiating Feedback.");
		messagingTarget.execute(messageEnvelope, messageResult, executionContext);
		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Feedback completed.");
	}
}
