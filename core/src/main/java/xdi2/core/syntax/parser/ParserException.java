package xdi2.core.syntax.parser;

public class ParserException extends RuntimeException {

	private static final long serialVersionUID = 8893304430054033855L;

	public ParserException() {

		super();
	}

	public ParserException(String message, Throwable cause) {

		super(message, cause);
	}

	public ParserException(String message) {

		super(message);
	}

	public ParserException(Throwable cause) {

		super(cause);
	}
}
