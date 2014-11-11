package xdi2.messaging.request;

import xdi2.core.Graph;

public interface MessagingRequest {

	/**
	 * Returns the underlying graph of this messaging request.
	 */
	public Graph getGraph();
}
