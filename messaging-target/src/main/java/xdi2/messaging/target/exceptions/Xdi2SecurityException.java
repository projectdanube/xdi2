package xdi2.messaging.target.exceptions;

import xdi2.messaging.target.execution.ExecutionContext;

/**
 * An exception that is thrown by a security problem (e.g. invalid secret token, signature, digest).
 * 
 * @author markus
 */
public class Xdi2SecurityException extends Xdi2MessagingException {

	private static final long serialVersionUID = 6420096054424278953L;

	public Xdi2SecurityException(String message, Throwable ex, ExecutionContext executionContext) {

		super(message, ex, executionContext);
	}
}
