package xdi2.client.exceptions;

import xdi2.core.exceptions.Xdi2Exception;
import xdi2.messaging.error.ErrorMessageResult;

/**
 * An exception that is thrown when an XDI client receives an error result,
 * or another problem occurs during XDI client operations.
 * 
 * @author markus
 */
public class Xdi2ClientException extends Xdi2Exception {

	private static final long serialVersionUID = 8990812849132689916L;

	private ErrorMessageResult errorMessageResult;

	public Xdi2ClientException(String message, Throwable ex) {

		super(message, ex);

		this.errorMessageResult = null;
	}

	public Xdi2ClientException(String message) {

		super(message);

		this.errorMessageResult = null;
	}

	public Xdi2ClientException(Throwable ex) {

		super(ex);

		this.errorMessageResult = null;
	}

	public Xdi2ClientException() {

		super();

		this.errorMessageResult = null;
	}

	public Xdi2ClientException(String message, Throwable ex, ErrorMessageResult errorMessageResult) {

		super(message, ex);

		this.errorMessageResult = errorMessageResult;
	}

	public Xdi2ClientException(String message, ErrorMessageResult errorMessageResult) {

		super(message);

		this.errorMessageResult = errorMessageResult;
	}

	public Xdi2ClientException(Throwable ex, ErrorMessageResult errorMessageResult) {

		super(ex);

		this.errorMessageResult = errorMessageResult;
	}

	public Xdi2ClientException(ErrorMessageResult errorMessageResult) {

		super();

		this.errorMessageResult = errorMessageResult;
	}

	public ErrorMessageResult getErrorMessageResult() {

		return this.errorMessageResult;
	}
}
