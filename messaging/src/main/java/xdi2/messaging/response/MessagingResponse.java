package xdi2.messaging.response;

import java.io.Serializable;

import xdi2.core.Graph;
import xdi2.messaging.error.MessagingError;

public interface MessagingResponse extends Serializable, Comparable<MessagingResponse> {

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
	public boolean hasMessagingError();

	/**
	 * Returns the error associated with this messaging response, if any.
	 */
	public MessagingError getMessagingError();
}
