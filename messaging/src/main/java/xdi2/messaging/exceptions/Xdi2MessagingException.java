package xdi2.messaging.exceptions;

import xdi2.core.exceptions.Xdi2Exception;
import xdi2.messaging.target.ExecutionContext;

/**
 * An exception that is thrown when messages are processed.
 * 
 * @author markus
 */
public class Xdi2MessagingException extends Xdi2Exception {

	private static final long serialVersionUID = -1108199426288138128L;

	private ExecutionContext executionContext;

	public Xdi2MessagingException(String message, Throwable ex, ExecutionContext executionContext) {

		super(message, ex);

		this.executionContext = executionContext;
	}

	public ExecutionContext getExecutionContext() {

		return this.executionContext;
	}

	public void setExecutionContext(ExecutionContext operation) {

		this.executionContext = operation;
	}
}
