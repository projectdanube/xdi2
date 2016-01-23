package xdi2.client.exceptions;

public class Xdi2DiscoveryException extends Xdi2ClientException {

	private static final long serialVersionUID = 4500240992794516180L;
	
	public Xdi2DiscoveryException(String message, Throwable cause) {
		
		super(message, cause);
	}
	
	public Xdi2DiscoveryException(Throwable cause) {
		
		super(cause);
	}

	public Xdi2DiscoveryException(String message) {

		super(message);
	}
	
	public Xdi2DiscoveryException() {
		
		super();
	}
}
