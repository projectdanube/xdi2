package xdi2.exceptions;

public class Xdi2GraphException extends Xdi2RuntimeException {

	private static final long serialVersionUID = 8785558094571107674L;

	public Xdi2GraphException() {

		super();
	}

	public Xdi2GraphException(String message, Throwable ex) {

		super(message, ex);
	}

	public Xdi2GraphException(String message) {

		super(message);
	}

	public Xdi2GraphException(Throwable ex) {

		super(ex);
	}
}
