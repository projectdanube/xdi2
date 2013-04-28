package xdi2.client;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;

/**
 * An XDIClient can send XDI message envelopes to an XDI endpoint, and retrieve the results.
 * 
 * @author markus
 */
public interface XDIClient {

	/**
	 * Sends an XDI message envelope to an XDI endpoint and retrieves the results.
	 * @param messageEnvelope The XDI message envelope to send.
	 * @param messageResult The message result that will hold results of the message. If this is null,
	 * a new message result will be created.
	 * @return The message result.
	 */
	public MessageResult send(MessageEnvelope messageEnvelope, MessageResult messageResult) throws Xdi2ClientException;

	/**
	 * Shuts down the client.
	 */
	public void close();

	/**
	 * Add a listener for XDI client events.
	 */
	public void addClientListener(XDIClientListener clientListener);

	/**
	 * Remove a listener for XDI client events.
	 */
	public void removeClientListener(XDIClientListener clientListener);
}
