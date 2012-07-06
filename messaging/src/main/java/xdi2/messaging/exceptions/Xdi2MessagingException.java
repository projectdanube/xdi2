package xdi2.messaging.exceptions;

import xdi2.core.exceptions.Xdi2Exception;
import xdi2.messaging.Operation;

/**
 * An exception that is thrown when messages are processed.
 * 
 * @author markus
 */
public class Xdi2MessagingException extends Xdi2Exception {

	private static final long serialVersionUID = -1108199426288138128L;

	private Operation operation;

	public Xdi2MessagingException(String message, Throwable ex, Operation operation) {

		super(message, ex);

		this.operation = operation;
	}

	public Operation getOperation() {

		return this.operation;
	}

	public void setOperation(Operation operation) {

		this.operation = operation;
	}
}
