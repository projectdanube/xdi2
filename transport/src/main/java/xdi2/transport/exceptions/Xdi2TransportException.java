package xdi2.transport.exceptions;

import xdi2.core.exceptions.Xdi2Exception;

/**
 * An exception that is thrown during server operation.
 * 
 * @author markus
 */
public class Xdi2TransportException extends Xdi2Exception {

	private static final long serialVersionUID = -4891083955016301902L;

	public Xdi2TransportException() {

		super();
	}

	public Xdi2TransportException(String message, Throwable ex) {

		super(message, ex);
	}

	public Xdi2TransportException(String message) {

		super(message);
	}

	public Xdi2TransportException(Throwable ex) {

		super(ex);
	}
}
