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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.client.impl.websocket.endpoint.WebSocketEndpoint;
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

	protected URL xdiWebSocketEndpointUrl;
	protected MimeType sendMimeType;

	private WebSocketEndpoint webSocketEndpoint;

	public XDIWebSocketClient(URL xdiWebSocketEndpointUrl, MimeType sendMimeType) {

		super();

		this.xdiWebSocketEndpointUrl = xdiWebSocketEndpointUrl;
		this.sendMimeType = (sendMimeType != null) ? sendMimeType : new MimeType(DEFAULT_SENDMIMETYPE);
	}

	public XDIWebSocketClient(URL xdiWebSocketEndpointUrl) {

		this(xdiWebSocketEndpointUrl, null);
	}

	public XDIWebSocketClient(String xdiWebSocketEndpointUrl) {

		this(null, null);

		try {

			if (xdiWebSocketEndpointUrl != null) this.xdiWebSocketEndpointUrl = new URL(xdiWebSocketEndpointUrl);
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	public XDIWebSocketClient() {

		this(null, null);
	}

	public XDIWebSocketClient(Properties parameters) throws Exception {

		this(null, null);

		if (parameters != null) {

			if (parameters.containsKey(KEY_ENDPOINTURL)) this.xdiWebSocketEndpointUrl = new URL(parameters.getProperty(KEY_ENDPOINTURL));
			if (parameters.containsKey(KEY_SENDMIMETYPE)) this.sendMimeType = new MimeType(parameters.getProperty(KEY_SENDMIMETYPE));

			if (log.isDebugEnabled()) log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

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

		// initialize and open connection

		try {

			WebSocketEndpoint webSocketEndpoint = connect();

			webSocketEndpoint.getSession().getBasicRemote().getSendWriter();
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot initialize WebSocket client: " + ex.getMessage(), ex);
		}

		// send the message envelope

		if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(null, null));

		try {

			Async async = this.getWebSocketEndpoint().getSession().getAsyncRemote();
			StringWriter stringWriter = new StringWriter();

			writer.write(messageEnvelope.getGraph(), stringWriter);
			async.sendText(stringWriter.getBuffer().toString());
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send message envelope: " + ex.getMessage(), ex);
		}

		// we return a future messaging response

		MessagingResponse messagingResponse = FutureMessagingResponse.create();

		// done

		return messagingResponse;
	}

	@Override
	public void close() {

		// close the WebSocket session

		if (this.getWebSocketEndpoint() != null) {

			try {

				this.getWebSocketEndpoint().getSession().close(new CloseReason(CloseCodes.NORMAL_CLOSURE, null));
			} catch (IOException ex) {

				throw new RuntimeException(ex.getMessage(), ex);
			}

			this.setWebSocketEndpoint(null);
		}
	}

	private WebSocketEndpoint connect() throws DeploymentException, IOException {

		if (this.getWebSocketEndpoint() == null) {

			if (log.isDebugEnabled()) log.debug("Connecting to " + this.getXdiWebSocketEndpointUrl());

			WebSocketEndpoint webSocketEndpoint = WebSocketEndpoint.connect(this.getXdiWebSocketEndpointUrl());
			this.setWebSocketEndpoint(webSocketEndpoint);
		}

		return this.getWebSocketEndpoint();
	}

	/*
	 * Getters and setters
	 */

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

	public WebSocketEndpoint getWebSocketEndpoint() {

		return this.webSocketEndpoint;
	}

	public void setWebSocketEndpoint(WebSocketEndpoint webSocketEndpoint) {

		this.webSocketEndpoint = webSocketEndpoint;
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getXdiWebSocketEndpointUrl().toString();
	}
}
