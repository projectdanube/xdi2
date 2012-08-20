package xdi2.server.exceptions;

import xdi2.core.exceptions.Xdi2Exception;

/**
 * An exception that is thrown during server operation.
 * 
 * @author markus
 */
public class Xdi2ServerException extends Xdi2Exception {

	private static final long serialVersionUID = -4891083955016301902L;

	public Xdi2ServerException() {

		super();
	}

	public Xdi2ServerException(String message, Throwable ex) {

		super(message, ex);
	}

	public Xdi2ServerException(String message) {

		super(message);
	}

	public Xdi2ServerException(Throwable ex) {

		super(ex);
	}
}
