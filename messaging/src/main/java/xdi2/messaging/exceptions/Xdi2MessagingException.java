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

	public Xdi2MessagingException(String message, Operation operation) {

		super(message);

		this.operation = operation;
	}

	public Xdi2MessagingException(Throwable ex, Operation operation) {

		super(ex);

		this.operation = operation;
	}

	public Operation getOperation() {

		return this.operation;
	}

	/**
	 * Checks if an Operation can be found somewhere in a chain of throwables.
	 * @param ex The throwable to look at.
	 * @return The Operation, or null.
	 */
	public static Operation findOperation(Throwable ex) {

		Operation operation = null;

		while (true) {

			if (ex instanceof Xdi2MessagingException && ((Xdi2MessagingException) ex).getOperation() != null) {

				operation = ((Xdi2MessagingException) ex).getOperation();
				break;
			}

			ex = ex.getCause();
			if (ex == null) break;
		}

		return operation;
	}
}
