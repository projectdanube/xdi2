package xdi2.client;

import java.io.Closeable;

import xdi2.client.events.XDIDiscoverEvent;
import xdi2.client.events.XDISendEvent;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.MessagingResponse;

/**
 * An XDIClient can send XDI message envelopes to an XDI endpoint, and retrieve the results.
 * 
 * @author markus
 */
public interface XDIClient extends Closeable {

	/**
	 * Sends an XDI messaging request to an XDI endpoint.
	 * @param messageEnvelope The XDI messaging envelope to send.
	 * @return The messaging response.
	 */
	public MessagingResponse send(MessageEnvelope messageEnvelope) throws Xdi2ClientException;

	/**
	 * Shuts down the client.
	 */
	@Override
	public void close();

	/**
	 * Add a listener for XDI client events.
	 */
	public void addClientListener(XDIClientListener clientListener);

	/**
	 * Remove a listener for XDI client events.
	 */
	public void removeClientListener(XDIClientListener clientListener);

	/**
	 * Fire a send event.
	 */
	public void fireSendEvent(XDISendEvent sendEvent);

	/**
	 * Fire a discover event.
	 */
	public void fireDiscoverEvent(XDIDiscoverEvent discoveryEvent);
}
