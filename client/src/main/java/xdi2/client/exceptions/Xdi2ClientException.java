package xdi2.client.exceptions;

import xdi2.core.exceptions.Xdi2Exception;
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

	public Xdi2ClientException(String message, MessagingResponse MessagingResponse) {

		super(message);

		this.messagingResponse = MessagingResponse;
	}

	public Xdi2ClientException(Throwable ex, MessagingResponse MessagingResponse) {

		super(ex);

		this.messagingResponse = MessagingResponse;
	}

	public Xdi2ClientException(MessagingResponse MessagingResponse) {

		super();

		this.messagingResponse = MessagingResponse;
	}

	public MessagingResponse getMessagingResponse() {

		return this.messagingResponse;
	}
}
