package xdi2.client.impl.websocket;

import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.websocket.CloseReason;
import javax.websocket.CloseReason.CloseCodes;
import javax.websocket.DeploymentException;
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
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.response.FutureMessagingResponse;
import xdi2.messaging.response.MessagingResponse;

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
public class XDIWebSocketClient extends XDIAbstractClient implements XDIClient {

	public static final String KEY_ENDPOINTURL = "endpointUrl";
	public static final String KEY_SENDMIMETYPE = "sendmimetype";

	public static final String DEFAULT_SENDMIMETYPE = "application/xdi+json;implied=0";

	protected static final Logger log = LoggerFactory.getLogger(XDIWebSocketClient.class);

	private Session session;

	protected URL xdiWebSocketEndpointUrl;
	protected MimeType sendMimeType;

	private Callback callback;

	public XDIWebSocketClient(Session session, URL xdiWebSocketEndpointUrl, MimeType sendMimeType) {

		super();

		this.session = session;

		this.xdiWebSocketEndpointUrl = xdiWebSocketEndpointUrl;
		this.sendMimeType = (sendMimeType != null) ? sendMimeType : new MimeType(DEFAULT_SENDMIMETYPE);

		this.callback = null;
	}

	public XDIWebSocketClient(Session session, URL xdiWebSocketEndpointUrl) {

		this(session, xdiWebSocketEndpointUrl, null);
	}

	public XDIWebSocketClient(Session session, String xdiWebSocketEndpointUrl) {

		this(session, URLURIUtil.URL(xdiWebSocketEndpointUrl), null);

		try {

			if (xdiWebSocketEndpointUrl != null) this.xdiWebSocketEndpointUrl = new URL(xdiWebSocketEndpointUrl);
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	public XDIWebSocketClient(Session session, Properties parameters) throws Exception {

		this(session, null, null);

		if (parameters != null) {

			if (parameters.containsKey(KEY_ENDPOINTURL)) this.xdiWebSocketEndpointUrl = new URL(parameters.getProperty(KEY_ENDPOINTURL));
			if (parameters.containsKey(KEY_SENDMIMETYPE)) this.sendMimeType = new MimeType(parameters.getProperty(KEY_SENDMIMETYPE));

			if (log.isDebugEnabled()) log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

	public XDIWebSocketClient(Session session) {

		this(session, null, null);
	}

	public XDIWebSocketClient(URL xdiWebSocketEndpointUrl, MimeType sendMimeType) {

		this((Session) null, xdiWebSocketEndpointUrl, sendMimeType);
	}

	public XDIWebSocketClient(URL xdiWebSocketEndpointUrl) {

		this((Session) null, xdiWebSocketEndpointUrl);
	}

	public XDIWebSocketClient(String xdiWebSocketEndpointUrl) {

		this((Session) null, xdiWebSocketEndpointUrl);
	}

	public XDIWebSocketClient(Properties parameters) throws Exception {

		this((Session) null, parameters);
	}

	public XDIWebSocketClient() {

		this((Session) null);
	}

	@SuppressWarnings("resource")
	@Override
	protected MessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		if (this.xdiWebSocketEndpointUrl == null) throw new Xdi2ClientException("No URI set.");

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

		MessagingResponse messagingResponse = FutureMessagingResponse.create(messageEnvelope);

		// done

		return messagingResponse;
	}

	@Override
	public void close() {

		this.disconnect(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Bye."));
	}

	private Session connect() throws DeploymentException, IOException {

		if (this.getSession() != null) return this.getSession();

		// connect

		if (log.isDebugEnabled()) log.debug("Connecting to " + this.getXdiWebSocketEndpointUrl());

		Session session = WebSocketClientEndpoint.connect(this, this.getXdiWebSocketEndpointUrl()).getSession();

		// done

		this.setSession(session);
		return session;
	}

	private void disconnect(CloseReason closeReason) {

		if (this.getSession() != null) {

			try {

				if (this.getSession().isOpen()) {

					this.getSession().close(closeReason);
				}
			} catch (IOException ex) {

				log.error("Cannot close session: " + ex.getMessage(), ex);
			} finally {

				this.setSession(null);
			}
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

	public URL getXdiWebSocketEndpointUrl() {

		return this.xdiWebSocketEndpointUrl;
	}

	public void setXdiWebSocketEndpointUrl(URL xdiEndpointUrl) {

		this.xdiWebSocketEndpointUrl = xdiEndpointUrl;
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

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getXdiWebSocketEndpointUrl().toString();
	}

	/*
	 * Helper classes
	 */

	public static interface Callback {

		public void onMessageEnvelope(MessageEnvelope messageEnvelope);
		public void onMessagingResponse(MessagingResponse messagingResponse);
	}
}
