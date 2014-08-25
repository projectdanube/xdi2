package xdi2.client.exceptions;

public class Xdi2AgentException extends Xdi2ClientException {

	private static final long serialVersionUID = 5491268349555733298L;

	public Xdi2AgentException(String message, Throwable cause) {

		super(message, cause);
	}

	public Xdi2AgentException(Throwable cause) {

		super(cause);
	}

	public Xdi2AgentException(String message) {

		super(message);
	}

	public Xdi2AgentException() {

		super();
	}
}
