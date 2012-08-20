package xdi2.core.exceptions;

public abstract class Xdi2Exception extends Exception {

	private static final long serialVersionUID = 8960240020914987608L;

	public Xdi2Exception() {

		super();
	}

	public Xdi2Exception(String message, Throwable ex) {

		super(message, ex);
	}

	public Xdi2Exception(String message) {

		super(message);
	}

	public Xdi2Exception(Throwable ex) {

		super(ex);
	}
}
