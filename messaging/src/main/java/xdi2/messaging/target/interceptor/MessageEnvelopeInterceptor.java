package xdi2.messaging.target.interceptor;

import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;

/**
 * Interceptor that is executed before and after a message envelope is executed,
 * as well as when an exception occurs while executing the message envelope.
 * 
 * @author markus
 */
public interface MessageEnvelopeInterceptor extends Interceptor {

	/**
	 * Run before a message envelope is executed.
	 * @param messageEnvelope The message envelope to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return True, if the message envelope has been fully handled and the server should stop processing it.
	 */
	public InterceptorResult before(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run after a message envelope is executed.
	 * @param messageEnvelope The message envelope to process.
	 * @param messageResult The message result.
	 * @param executionContext The current execution context.
	 * @return True, if the message envelope has been fully handled and the server should stop processing it.
	 */
	public InterceptorResult after(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;

	/**
	 * Run if an exception occurs while a message envelope is executed.
	 */
	public void exception(MessageEnvelope messageEnvelope, MessageResult messageResult, ExecutionContext executionContext, Exception ex);
}
