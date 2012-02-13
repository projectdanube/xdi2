package xdi2.core.exceptions;

/**
 * An exception that is thrown on various occasions if a graph cannot be parsed.
 * 
 * @author markus
 */
public class Xdi2ParseException extends Xdi2Exception {

	private static final long serialVersionUID = -81795287039488030L;

	public Xdi2ParseException() {

		super();
	}

	public Xdi2ParseException(String message, Throwable ex) {

		super(message, ex);
	}

	public Xdi2ParseException(String message) {

		super(message);
	}

	public Xdi2ParseException(Throwable ex) {

		super(ex);
	}
}
