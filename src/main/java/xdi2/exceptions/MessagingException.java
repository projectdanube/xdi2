package xdi2.exceptions;

/**
 * An exception that is thrown when messages are processed.
 * 
 * @author markus
 */
public class MessagingException extends Xdi2Exception {

	private static final long serialVersionUID = -1108199426288138128L;

	public MessagingException() {

		super();
	}

	public MessagingException(String message, Throwable ex) {

		super(message, ex);
	}

	public MessagingException(String message) {

		super(message);
	}

	public MessagingException(Throwable ex) {

		super(ex);
	}
}
