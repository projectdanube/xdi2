package xdi2.messaging.response;

import xdi2.core.Graph;

/**
 * A message envelope that will be available in the future.
 * 
 * @author markus
 */
public class FutureMessagingResponse extends AbstractMessagingResponse implements MessagingResponse {

	private static final long serialVersionUID = 6405364186568481142L;

	private FutureMessagingResponse() {

	}

	/*
	 * Static methods
	 */

	public static FutureMessagingResponse create() {

		return new FutureMessagingResponse();
	}

	/*
	 * Overrides
	 */

	@Override
	public Graph getGraph() {

		throw new IllegalStateException("Messaging response is not available yet.");
	}

	@Override
	public Graph getResultGraph() {

		throw new IllegalStateException("Messaging response is not available yet.");
	}

	/*
	 * Instance methods
	 */

	public void somethingHereToNotifyClientMaybe() {

		//TODO
	}
}
