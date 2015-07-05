package xdi2.client.exceptions;

import xdi2.core.exceptions.Xdi2Exception;
import xdi2.core.features.error.XdiError;
import xdi2.messaging.response.MessagingResponse;

/**
 * An exception that is thrown when an XDI client receives an error result,
 * or another problem occurs during XDI client operations.
 * 
 * @author markus
 */
public class Xdi2ClientException extends Xdi2Exception {

	private static final long serialVersionUID = 8990812849132689916L;

	private MessagingResponse messagingResponse;

	public Xdi2ClientException(String message, Throwable ex) {

		super(message, ex);

		this.messagingResponse = null;
	}

	public Xdi2ClientException(String message) {

		super(message);

		this.messagingResponse = null;
	}

	public Xdi2ClientException(Throwable ex) {

		super(ex);

		this.messagingResponse = null;
	}

	public Xdi2ClientException() {

		super();

		this.messagingResponse = null;
	}

	public Xdi2ClientException(String message, Throwable ex, MessagingResponse messagingResponse) {

		super(message, ex);

		this.messagingResponse = messagingResponse;
	}

	public Xdi2ClientException(String message, MessagingResponse messagingResponse) {

		super(message);

		this.messagingResponse = messagingResponse;
	}

	public Xdi2ClientException(Throwable ex, MessagingResponse messagingResponse) {

		super(ex);

		this.messagingResponse = messagingResponse;
	}

	public Xdi2ClientException(MessagingResponse messagingResponse) {

		super();

		this.messagingResponse = messagingResponse;
	}

	public MessagingResponse getMessagingResponse() {

		return this.messagingResponse;
	}

	public XdiError getXdiError() {

		return this.getMessagingResponse().getXdiError();
	}
}
