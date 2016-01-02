package xdi2.messaging.response;

import xdi2.core.Graph;
import xdi2.core.features.error.XdiError;

public interface MessagingResponse {

	/**
	 * Returns the underlying graph of this messaging response.
	 */
	public Graph getGraph();

	/**
	 * Returns the result graph returned in this messaging response.
	 */
	public Graph getResultGraph();

	/**
	 * Returns whether the messaging response has an associated error.
	 */
	public boolean hasXdiError();

	/**
	 * Returns the error associated with this messaging response, if any.
	 */
	public XdiError getXdiError();
}
