package xdi2.messaging.response;

import java.io.Serializable;

import xdi2.core.Graph;

public interface MessagingResponse extends Serializable, Comparable<MessagingResponse> {

	/**
	 * Returns the underlying graph of this messaging response.
	 */
	public Graph getGraph();

	/**
	 * Returns the result graph returned in this messaging response.
	 */
	public Graph getResultGraph();
}
