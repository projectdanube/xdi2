package xdi2.exceptions;

/**
 * An exception that is thrown when messages are processed.
 * 
 * @author markus
 */
public class Xdi2MessagingException extends Xdi2Exception {

	private static final long serialVersionUID = -1108199426288138128L;

	public Xdi2MessagingException() {

		super();
	}

	public Xdi2MessagingException(String message, Throwable ex) {

		super(message, ex);
	}

	public Xdi2MessagingException(String message) {

		super(message);
	}

	public Xdi2MessagingException(Throwable ex) {

		super(ex);
	}
}
