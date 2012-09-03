package xdi2.messaging.exceptions;

import xdi2.messaging.target.ExecutionContext;

/**
 * An exception that is thrown when an operation is not authorized.
 * 
 * @author markus
 */
public class Xdi2NotAuthorizedException extends Xdi2MessagingException {

	private static final long serialVersionUID = 6420096054424278953L;

	public Xdi2NotAuthorizedException(String message, Throwable ex, ExecutionContext executionContext) {

		super(message, ex, executionContext);
	}
}
