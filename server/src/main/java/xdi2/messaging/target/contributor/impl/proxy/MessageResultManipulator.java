package xdi2.messaging.target.contributor.impl.proxy;

import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.ExecutionContext;

/**
 * This is used to manipulate message results after they are received
 * from another XDI endpoint by the ProxyContributor.
 * 
 * @author markus
 */
public interface MessageResultManipulator {

	/**
	 * Manipulate a message result.
	 * @param messageResult The message result to manipulate.
	 * @param executionContext The current execution context.
	 */
	public void manipulate(MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException;
}
