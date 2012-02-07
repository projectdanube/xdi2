package xdi2.exceptions;

/**
 * An exception that is thrown on various occasions if a graph cannot be parsed.
 * 
 * @author markus
 */
public class ParseException extends Xdi2Exception {

	private static final long serialVersionUID = -81795287039488030L;

	public ParseException() {

		super();
	}

	public ParseException(String message, Throwable ex) {

		super(message, ex);
	}

	public ParseException(String message) {

		super(message);
	}

	public ParseException(Throwable ex) {

		super(ex);
	}
}
