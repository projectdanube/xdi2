package xdi2.messaging.exceptions;

import xdi2.messaging.Operation;

/**
 * An exception that is thrown when an operation is not authorized.
 * 
 * @author markus
 */
public class Xdi2NotAuthorizedException extends Xdi2MessagingException {

	private static final long serialVersionUID = 6420096054424278953L;

	public Xdi2NotAuthorizedException(String message, Throwable ex, Operation operation) {

		super(message, ex, operation);
	}
}
