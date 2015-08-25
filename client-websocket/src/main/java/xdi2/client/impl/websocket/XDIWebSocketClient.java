package xdi2.client.impl.websocket;

import java.io.StringWriter;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.RemoteEndpoint.Async;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.client.impl.websocket.endpoint.WebSocketClientEndpoint;
import xdi2.client.util.URLURIUtil;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.FutureMessagingResponse;
import xdi2.messaging.response.TransportMessagingResponse;

/**
 * An XDI client that can send XDI messages over WebSocket and receive results.
 * It supports the following parameters (passed to the init method):
 * <ul>
 * <li>endpointUrl - The URL of the XDI endpoint to talk to.</li>
 * <li>sendMimeType - The mime type to use to send the XDI messages to the endpoint.</li>
 * </ul> 
 * 
 * @author markus
 */
public class XDIWebSocketClient extends XDIAbstractClient<FutureMessagingResponse> implements XDIClient<FutureMessagingResponse> {

	public static final String KEY_ENDPOINTURI = "endpointUri";
	public static final String KEY_SENDMIMETYPE = "sendmimetype";

	public static final String DEFAULT_SENDMIMETYPE = "application/xdi+json;implied=0";

	private static final Logger log = LoggerFactory.getLogger(XDIWebSocketClient.class);

	private Session session;
	private URI xdiWebSocketEndpointUri;

	private MimeType sendMimeType;

	private Callback callback;
	private Map<XDIAddress, FutureMessagingResponse> futureMessagingResponses;

	public XDIWebSocketClient(Session session, URI xdiWebSocketEndpointUri, MimeType sendMimeType) {

		super();

		this.session = session;
		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;

		this.sendMimeType = (sendMimeType != null) ? sendMimeType : new MimeType(DEFAULT_SENDMIMETYPE);

		this.callback = null;
		this.futureMessagingResponses = new HashMap<XDIAddress, FutureMessagingResponse> ();
	}

	public XDIWebSocketClient(Session session, URI xdiWebSocketEndpointUri) {

		this(session, xdiWebSocketEndpointUri, null);
	}

	public XDIWebSocketClient(Session session, String xdiWebSocketEndpointUri) {

		this(session, URLURIUtil.URI(xdiWebSocketEndpointUri), null);
	}

	public XDIWebSocketClient(Session session, Properties parameters) {

		this(session, null, null);

		if (parameters != null) {

			if (parameters.containsKey(KEY_ENDPOINTURI)) this.xdiWebSocketEndpointUri = URLURIUtil.URI(parameters.getProperty(KEY_ENDPOINTURI));
			if (parameters.containsKey(KEY_SENDMIMETYPE)) this.sendMimeType = new MimeType(parameters.getProperty(KEY_SENDMIMETYPE));

			if (log.isDebugEnabled()) log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

	public XDIWebSocketClient(Session session) {

		this(session, null, null);
	}

	public XDIWebSocketClient(URI xdiWebSocketEndpointUri, MimeType sendMimeType) {

		this((Session) null, xdiWebSocketEndpointUri, sendMimeType);
	}

	public XDIWebSocketClient(URI xdiWebSocketEndpointUri) {

		this((Session) null, xdiWebSocketEndpointUri);
	}

	public XDIWebSocketClient(String xdiWebSocketEndpointUri) {

		this((Session) null, xdiWebSocketEndpointUri);
	}

	public XDIWebSocketClient(Properties parameters) {

		this((Session) null, parameters);
	}

	public XDIWebSocketClient() {

		this((Session) null);
	}

	@SuppressWarnings("resource")
	@Override
	protected FutureMessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		// find out which XDIWriter we want to use

		MimeType sendMimeType = this.sendMimeType;
		XDIWriter writer = XDIWriterRegistry.forMimeType(sendMimeType);

		if (writer == null) {

			sendMimeType = new MimeType(DEFAULT_SENDMIMETYPE);
			writer = XDIWriterRegistry.forMimeType(sendMimeType);
		}

		if (writer == null) throw new Xdi2ClientException("Cannot find a suitable XDI writer.");
		if (log.isDebugEnabled()) log.debug("Using writer " + writer.getClass().getName() + ".");

		// find out which XDIReader we want to use

		XDIReader reader = XDIReaderRegistry.getAuto();

		if (reader == null) throw new Xdi2ClientException("Cannot find a suitable XDI reader.");
		if (log.isDebugEnabled()) log.debug("Using reader " + reader.getClass().getName() + ".");

		// connect

		Session session = null;

		try {

			session = this.connect();
		} catch (Exception ex) {

			this.disconnect(new CloseReason(CloseCodes.PROTOCOL_ERROR, "Cannot open WebSocket connection: " + ex.getMessage()));

			throw new Xdi2ClientException("Cannot open WebSocket connection: " + ex.getMessage(), ex);
		}

		// send the message envelope

		if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(null, null));

		try {

			Async async = session.getAsyncRemote();
			StringWriter stringWriter = new StringWriter();

			writer.write(messageEnvelope.getGraph(), stringWriter);
			async.sendText(stringWriter.getBuffer().toString());
		} catch (Exception ex) {

			this.disconnect(new CloseReason(CloseCodes.PROTOCOL_ERROR, "Cannot send message envelope: " + ex.getMessage()));

			throw new Xdi2ClientException("Cannot send message envelope: " + ex.getMessage(), ex);
		}

		// we return a future messaging response

		FutureMessagingResponse futureMessagingResponse = FutureMessagingResponse.fromMessageEnvelope(messageEnvelope);

		for (Message message : messageEnvelope.getMessages()) {

			this.putFutureMessagingResponse(message.getXDIAddress(), futureMessagingResponse);
		}

		// done

		return futureMessagingResponse;
	}

	@Override
	public void close() {

		this.disconnect(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Bye."));
	}

	private Session connect() throws Exception {

		if (this.getSession() != null) return this.getSession();

		if (this.getXdiWebSocketEndpointUri() == null) throw new Xdi2ClientException("No URL to connect to.");

		// connect

		if (log.isDebugEnabled()) log.debug("Connecting to " + this.getXdiWebSocketEndpointUri());

		Session session = WebSocketClientEndpoint.connect(this, this.getXdiWebSocketEndpointUri()).getSession();

		// done

		this.setSession(session);
		return session;
	}

	private void disconnect(CloseReason closeReason) {

		try {

			if (this.getSession() != null) {

				if (this.getSession().isOpen()) {

					this.getSession().close(closeReason);
				}
			}
		} catch (Exception ex) {

			log.error("Cannot disconnect: " + ex.getMessage(), ex);
		} finally {

			this.setSession(null);
		}
	}

	/*
	 * Getters and setters
	 */

	public Session getSession() {

		return this.session;
	}

	public void setSession(Session session) {

		this.session = session;
	}

	public URI getXdiWebSocketEndpointUri() {

		return this.xdiWebSocketEndpointUri;
	}

	public void setXdiWebSocketEndpointUri(URI xdiEndpointUri) {

		this.xdiWebSocketEndpointUri = xdiEndpointUri;
	}

	public MimeType getSendMimeType() {

		return this.sendMimeType;
	}

	public void setSendMimeType(MimeType sendMimeType) {

		this.sendMimeType = sendMimeType;
	}

	public Callback getCallback() {

		return this.callback;
	}

	public void setCallback(Callback callback) {

		this.callback = callback;
	}

	public Map<XDIAddress, FutureMessagingResponse> getFutureMessagingResponses() {

		return this.futureMessagingResponses;
	}

	public void putFutureMessagingResponse(XDIAddress messageXDIaddress, FutureMessagingResponse futureMessagingResponse) {

		if (log.isDebugEnabled()) log.debug("Putting future messaging response for message " + messageXDIaddress);

		this.futureMessagingResponses.put(messageXDIaddress, futureMessagingResponse);
	}

	public void removeFutureMessagingResponse(XDIAddress messageXDIaddress) {

		if (log.isDebugEnabled()) log.debug("Removing future messaging response for message " + messageXDIaddress);

		this.futureMessagingResponses.remove(messageXDIaddress);
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getXdiWebSocketEndpointUri().toString();
	}

	/*
	 * Helper classes
	 */

	public static interface Callback {

		public void onMessageEnvelope(MessageEnvelope messageEnvelope);
		public void onMessagingResponse(TransportMessagingResponse messagingResponse);
	}
}
