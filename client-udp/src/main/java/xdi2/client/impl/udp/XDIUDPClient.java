package xdi2.client.impl.udp;

import java.io.StringWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
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
 * An XDI client that can send XDI messages over UDP and receive results.
 * It supports the following parameters (passed to the init method):
 * <ul>
 * <li>endpointUrl - The URL of the XDI endpoint to talk to.</li>
 * <li>sendMimeType - The mime type to use to send the XDI messages to the endpoint.</li>
 * </ul> 
 * 
 * @author markus
 */
public class XDIUDPClient extends XDIAbstractClient<FutureMessagingResponse> implements XDIClient<FutureMessagingResponse> {

	public static final String KEY_ENDPOINTURI = "endpointUri";
	public static final String KEY_SENDMIMETYPE = "sendmimetype";

	public static final String DEFAULT_SENDMIMETYPE = "application/xdi+json;implied=0";

	private static final Logger log = LoggerFactory.getLogger(XDIUDPClient.class);

	private DatagramSocket datagramSocket;
	private URI xdiWebSocketEndpointUri;

	private MimeType sendMimeType;

	private Callback callback;
	private Map<XDIAddress, FutureMessagingResponse> futureMessagingResponses;

	public XDIUDPClient(Session session, URI xdiWebSocketEndpointUri, MimeType sendMimeType) {

		super();

		this.datagramSocket = session;
		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;

		this.sendMimeType = (sendMimeType != null) ? sendMimeType : new MimeType(DEFAULT_SENDMIMETYPE);

		this.callback = null;
		this.futureMessagingResponses = new HashMap<XDIAddress, FutureMessagingResponse> ();
	}

	public XDIUDPClient(Session session, URI xdiWebSocketEndpointUri) {

		this(session, xdiWebSocketEndpointUri, null);

		DatagramSocket d;
		SocketAddress a;
		InetAddress i;
	}

	public XDIUDPClient(Session session, String xdiWebSocketEndpointUri) {

		this(session, URLURIUtil.URI(xdiWebSocketEndpointUri), null);
	}

	public XDIUDPClient(Session session, Properties parameters) {

		this(session, null, null);

		if (parameters != null) {

			if (parameters.containsKey(KEY_ENDPOINTURI)) this.xdiWebSocketEndpointUri = URLURIUtil.URI(parameters.getProperty(KEY_ENDPOINTURI));
			if (parameters.containsKey(KEY_SENDMIMETYPE)) this.sendMimeType = new MimeType(parameters.getProperty(KEY_SENDMIMETYPE));

			if (log.isDebugEnabled()) log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

	public XDIUDPClient(Session session) {

		this(session, null, null);
	}

	public XDIUDPClient(URI xdiWebSocketEndpointUri, MimeType sendMimeType) {

		this((Session) null, xdiWebSocketEndpointUri, sendMimeType);
	}

	public XDIUDPClient(URI xdiWebSocketEndpointUri) {

		this((Session) null, xdiWebSocketEndpointUri);
	}

	public XDIUDPClient(String xdiWebSocketEndpointUri) {

		this((Session) null, xdiWebSocketEndpointUri);
	}

	public XDIUDPClient(Properties parameters) {

		this((Session) null, parameters);
	}

	public XDIUDPClient() {

		this((Session) null);
	}

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

			this.putFutureMessagingResponse(message.getContextNode().getXDIAddress(), futureMessagingResponse);
		}

		// done

		return futureMessagingResponse;
	}

	@Override
	public void close() {

		this.disconnect(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Bye."));
	}

	private Session connect() throws Exception {

		if (this.getDatagramSocket() != null) return this.getDatagramSocket();

		if (this.getXdiWebSocketEndpointUri() == null) throw new Xdi2ClientException("No URL to connect to.");

		// connect

		if (log.isDebugEnabled()) log.debug("Connecting to " + this.getXdiWebSocketEndpointUri());

		Session session = WebSocketClientEndpoint.connect(this, this.getXdiWebSocketEndpointUri()).getDatagramSocket();

		// done

		if (log.isDebugEnabled()) log.debug("Connected successfully.");

		this.setSession(session);
		return session;
	}

	private void disconnect(CloseReason closeReason) {

		try {

			if (this.getDatagramSocket() != null) {

				if (this.getDatagramSocket().isOpen()) {

					this.getDatagramSocket().close(closeReason);
				}
			}
		} catch (Exception ex) {

			log.error("Cannot disconnect: " + ex.getMessage(), ex);
		} finally {

			this.setSession(null);
		}

		if (log.isDebugEnabled()) log.debug("Disconnected successfully.");
	}

	/*
	 * Getters and setters
	 */

	public Session getDatagramSocket() {

		return this.datagramSocket;
	}

	public void setSession(Session session) {

		this.datagramSocket = session;
	}

	public URI getXdiWebSocketEndpointUri() {

		return this.xdiWebSocketEndpointUri;
	}

	public void setXdiWebSocketEndpointUri(URI xdiWebSocketEndpointUri) {

		this.xdiWebSocketEndpointUri = xdiWebSocketEndpointUri;
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
