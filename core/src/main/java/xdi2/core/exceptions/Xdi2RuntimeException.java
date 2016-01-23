package xdi2.core.exceptions;

public class Xdi2RuntimeException extends RuntimeException {

	private static final long serialVersionUID = -4816901533536077401L;

	public Xdi2RuntimeException() {

		super();
	}

	public Xdi2RuntimeException(String message, Throwable ex) {

		super(message, ex);
	}

	public Xdi2RuntimeException(String message) {

		super(message);
	}

	public Xdi2RuntimeException(Throwable ex) {

		super(ex);
	}
}
