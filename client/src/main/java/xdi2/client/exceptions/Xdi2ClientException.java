package xdi2.client.exceptions;

import xdi2.core.exceptions.Xdi2Exception;
import xdi2.messaging.response.ErrorMessagingResponse;

/**
 * An exception that is thrown when an XDI client receives an error messaging response,
 * or another problem occurs during XDI client operations.
 * 
 * @author markus
 */
public class Xdi2ClientException extends Xdi2Exception {

	private static final long serialVersionUID = 8990812849132689916L;

	private ErrorMessagingResponse errorMessagingResponse;

	public Xdi2ClientException(String message, Throwable ex) {

		super(message, ex);

		this.errorMessagingResponse = null;
	}

	public Xdi2ClientException(String message) {

		super(message);

		this.errorMessagingResponse = null;
	}

	public Xdi2ClientException(Throwable ex) {

		super(ex);

		this.errorMessagingResponse = null;
	}

	public Xdi2ClientException() {

		super();

		this.errorMessagingResponse = null;
	}

	public Xdi2ClientException(String message, Throwable ex, ErrorMessagingResponse errorMessagingResponse) {

		super(message, ex);

		this.errorMessagingResponse = errorMessagingResponse;
	}

	public Xdi2ClientException(String message, ErrorMessagingResponse errorMessagingResponse) {

		super(message);

		this.errorMessagingResponse = errorMessagingResponse;
	}

	public Xdi2ClientException(Throwable ex, ErrorMessagingResponse errorMessagingResponse) {

		super(ex);

		this.errorMessagingResponse = errorMessagingResponse;
	}

	public Xdi2ClientException(ErrorMessagingResponse errorMessagingResponse) {

		super();

		this.errorMessagingResponse = errorMessagingResponse;
	}

	public ErrorMessagingResponse getErrorMessagingResponse() {

		return this.errorMessagingResponse;
	}
}
