package xdi2.messaging.target.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.AbstractMessagingTarget;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;

public abstract class AbstractInterceptor implements Interceptor {

	private static final Logger log = LoggerFactory.getLogger(AbstractMessagingTarget.class);

	protected void feedback(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();

		if (! (messagingTarget instanceof AbstractMessagingTarget)) throw new Xdi2MessagingException("Cannot feedback on a non-AbstractMessagingTarget", null, executionContext);

		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Initiating Feedback.");
		((AbstractMessagingTarget) messagingTarget).execute(message, messageResult, executionContext);
		if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Feedback completed.");
	}
}
