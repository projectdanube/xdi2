package xdi2.client.impl.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.client.impl.http.ssl.XDI2X509TrustManager;
import xdi2.client.util.URLURIUtil;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.http.AcceptHeader;
import xdi2.messaging.response.TransportMessagingResponse;

/**
 * An XDI client that can send XDI messages over HTTP and receive results.
 * It supports the following parameters (passed to the init method):
 * <ul>
 * <li>endpointUrl - The URL of the XDI endpoint to talk to.</li>
 * <li>sendMimeType - The mime type to use to send the XDI messages to the endpoint. The Content-type header will be set accordingly.</li>
 * <li>recvMimeType - The mime type in which we want to receive the results from the endpoint. The Accept header will be set accordingly.
 * If the endpoint replies in some other mime type than requested, we will still try to read it.</li>
 * <li>useragent - The User-Agent HTTP header to use.</li>
 * </ul> 
 * 
 * @author markus
 */
public class XDIHttpClient extends XDIAbstractClient<TransportMessagingResponse> implements XDIClient<TransportMessagingResponse> {

	public static final String KEY_ENDPOINTURI = "endpointUri";
	public static final String KEY_SENDMIMETYPE = "sendmimetype";
	public static final String KEY_RECVMIMETYPE = "recvmimetype";
	public static final String KEY_USERAGENT = "useragent";
	public static final String KEY_FOLLOWREDIRECTS = "followredirects";

	public static final String DEFAULT_SENDMIMETYPE = "application/xdi+json;implied=0";
	public static final String DEFAULT_RECVMIMETYPE = "application/xdi+json;implied=0";
	public static final String DEFAULT_USERAGENT = "XDI2 Java Library";
	public static final String DEFAULT_FOLLOWREDIRECTS = "false";

	private static final Logger log = LoggerFactory.getLogger(XDIHttpClient.class);

	private HttpURLConnection httpURLConnection;
	private URI xdiEndpointUri;

	private MimeType sendMimeType;
	private MimeType recvMimeType;
	private String userAgent;
	private boolean followRedirects;

	static {

		XDI2X509TrustManager.enable();
	}

	public XDIHttpClient(HttpURLConnection httpURLConnection, URI xdiEndpointUri, MimeType sendMimeType, MimeType recvMimeType, String userAgent, Boolean followRedirects) {

		super();

		this.httpURLConnection = httpURLConnection;
		this.xdiEndpointUri = xdiEndpointUri;

		this.sendMimeType = (sendMimeType != null) ? sendMimeType : new MimeType(DEFAULT_SENDMIMETYPE);
		this.recvMimeType = (recvMimeType != null) ? recvMimeType : new MimeType(DEFAULT_RECVMIMETYPE);
		this.userAgent = (userAgent != null) ? userAgent : DEFAULT_USERAGENT;
		this.followRedirects = (followRedirects != null) ? followRedirects.booleanValue() : Boolean.parseBoolean(DEFAULT_FOLLOWREDIRECTS);
	}

	public XDIHttpClient(HttpURLConnection httpURLConnection, URI xdiEndpointUri, MimeType sendMimeType, MimeType recvMimeType, String userAgent) {

		this(httpURLConnection, xdiEndpointUri, sendMimeType, recvMimeType, userAgent, null);
	}

	public XDIHttpClient(HttpURLConnection httpURLConnection, URI xdiEndpointUri, MimeType sendMimeType, MimeType recvMimeType) {

		this(httpURLConnection, xdiEndpointUri, sendMimeType, recvMimeType, null, null);
	}

	public XDIHttpClient(HttpURLConnection httpURLConnection, URI xdiEndpointUri) {

		this(httpURLConnection, xdiEndpointUri, null, null, null, null);
	}

	public XDIHttpClient(HttpURLConnection httpURLConnection, String xdiEndpointUri) {

		this(httpURLConnection, URLURIUtil.URI(xdiEndpointUri), null, null, null, null);
	}

	public XDIHttpClient(HttpURLConnection httpURLConnection, Properties parameters) {

		this(httpURLConnection, null, null, null, null, null);

		if (parameters != null) {

			if (parameters.containsKey(KEY_ENDPOINTURI)) this.xdiEndpointUri = URLURIUtil.URI(parameters.getProperty(KEY_ENDPOINTURI));
			if (parameters.containsKey(KEY_SENDMIMETYPE)) this.sendMimeType = new MimeType(parameters.getProperty(KEY_SENDMIMETYPE));
			if (parameters.containsKey(KEY_RECVMIMETYPE)) this.recvMimeType = new MimeType(parameters.getProperty(KEY_RECVMIMETYPE));
			if (parameters.containsKey(KEY_USERAGENT)) this.userAgent = parameters.getProperty(KEY_USERAGENT);
			if (parameters.containsKey(KEY_FOLLOWREDIRECTS)) this.followRedirects = Boolean.parseBoolean(parameters.getProperty(KEY_FOLLOWREDIRECTS));

			if (log.isDebugEnabled()) log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

	public XDIHttpClient(HttpURLConnection httpURLConnection) {

		this(httpURLConnection, null, null, null, null, null);
	}

	public XDIHttpClient(URI xdiEndpointUri, MimeType sendMimeType, MimeType recvMimeType, String userAgent, Boolean followRedirects) {

		this((HttpURLConnection) null, xdiEndpointUri, sendMimeType, recvMimeType, userAgent, followRedirects);
	}

	public XDIHttpClient(URI xdiEndpointUri, MimeType sendMimeType, MimeType recvMimeType, String userAgent) {

		this((HttpURLConnection) null, xdiEndpointUri, sendMimeType, recvMimeType, userAgent);
	}

	public XDIHttpClient(URI xdiEndpointUri, MimeType sendMimeType, MimeType recvMimeType) {

		this((HttpURLConnection) null, xdiEndpointUri, sendMimeType, recvMimeType);
	}

	public XDIHttpClient(URI xdiEndpointUri) {

		this((HttpURLConnection) null, xdiEndpointUri);
	}

	public XDIHttpClient(String xdiEndpointUri) {

		this((HttpURLConnection) null, xdiEndpointUri);
	}

	public XDIHttpClient(Properties parameters) {

		this((HttpURLConnection) null, parameters);
	}

	public XDIHttpClient() {

		this((HttpURLConnection) null);
	}

	@Override
	protected TransportMessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

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

		MimeType recvMimeType = this.getRecvMimeType();
		XDIReader reader = XDIReaderRegistry.forMimeType(recvMimeType);

		if (reader == null) {

			recvMimeType = new MimeType(DEFAULT_RECVMIMETYPE);
			reader = XDIReaderRegistry.forMimeType(recvMimeType);
		}

		if (reader == null) throw new Xdi2ClientException("Cannot find a suitable XDI reader.");
		if (log.isDebugEnabled()) log.debug("Using reader " + reader.getClass().getName() + ".");

		// connect

		HttpURLConnection httpURLConnection;

		try {

			httpURLConnection = this.connect();
		} catch (Xdi2ClientException ex) {

			throw ex;
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot open HTTP(S) connection: " + ex.getMessage(), ex);
		}

		// send the message envelope

		if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(null, null));

		int responseCode;
		String responseMessage;

		try {

			OutputStream outputStream = httpURLConnection.getOutputStream();
			writer.write(messageEnvelope.getGraph(), outputStream);
			outputStream.flush();
			outputStream.close();

			responseCode = httpURLConnection.getResponseCode();
			responseMessage = httpURLConnection.getResponseMessage();
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send message envelope: " + ex.getMessage(), ex);
		}

		// check response code

		if (responseCode >= 300) {

			throw new Xdi2ClientException("HTTP code " + responseCode + " received: " + responseMessage);
		}

		// check in which format we receive the result

		String contentType = httpURLConnection.getContentType();
		int contentLength = httpURLConnection.getContentLength();

		if (log.isDebugEnabled()) log.debug("Received result. Content-Type: " + contentType + ", Content-Length: " + contentLength);

		if (contentType != null) {

			reader = XDIReaderRegistry.forMimeType(new MimeType(contentType));

			if (reader == null) {

				log.info("Don't know how to read message result with Content-Type " + contentType + ". Trying to auto-detect format.");
				reader = XDIReaderRegistry.getAuto();
			}
		} else {

			log.info("No Content-Type received. Trying to auto-detect format.");
			reader = XDIReaderRegistry.getAuto();
		}

		// read the messaging response and close connection

		Graph messagingResponseGraph = MemoryGraphFactory.getInstance().openGraph();

		try {

			InputStream inputStream = httpURLConnection.getInputStream();
			reader.read(messagingResponseGraph, inputStream);
			inputStream.close();
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot read message result: " + ex.getMessage(), ex);
		} finally {

			this.disconnect();
		}

		TransportMessagingResponse messagingResponse = TransportMessagingResponse.fromGraph(messagingResponseGraph);

		// done

		return messagingResponse;
	}

	@Override
	public void close() {

		this.disconnect();
	}

	private HttpURLConnection connect() throws Xdi2ClientException, IOException {

		if (this.getHttpURLConnection() != null) return this.getHttpURLConnection();

		if (this.getXdiEndpointUri() == null) throw new Xdi2ClientException("No URL to connect to.");

		// prepare Accept: header

		AcceptHeader acceptHeader = AcceptHeader.create(recvMimeType);

		if (log.isDebugEnabled()) log.debug("Using Accept header " + acceptHeader.toString() + ".");

		// connect

		if (log.isDebugEnabled()) log.debug("Connecting to " + this.getXdiEndpointUri());

		URL url = URLURIUtil.URItoURL(this.getXdiEndpointUri());
		URLConnection URLconnection = url.openConnection();

		if (! (URLconnection instanceof HttpURLConnection)) throw new Xdi2ClientException("Can only work with HTTP(S) URLs: " + this.getXdiEndpointUri());

		HttpURLConnection httpURLConnection = (HttpURLConnection) URLconnection;

		httpURLConnection.setDoInput(true);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setInstanceFollowRedirects(this.getFollowRedirects());
		System.setProperty("http.strictPostRedirect", "true");
		httpURLConnection.setRequestProperty("Content-Type", sendMimeType.toString());
		httpURLConnection.setRequestProperty("Accept", acceptHeader.toString());
		httpURLConnection.setRequestProperty("User-Agent", this.getUserAgent());
		httpURLConnection.setRequestMethod("POST");

		// done

		if (log.isDebugEnabled()) log.debug("Connected successfully.");

		this.setHttpURLConnection(httpURLConnection);
		return httpURLConnection;
	}

	private void disconnect() {

		try {

			if (this.getHttpURLConnection() != null) {

				this.getHttpURLConnection().disconnect();
			}
		} catch (Exception ex) {

			log.error("Cannot disconnect: " + ex.getMessage(), ex);
		} finally {

			this.setHttpURLConnection(null);
		}

		if (log.isDebugEnabled()) log.debug("Disconnected successfully.");
	}

	/*
	 * Getters and setters
	 */

	public HttpURLConnection getHttpURLConnection() {

		return this.httpURLConnection;
	}

	public void setHttpURLConnection(HttpURLConnection httpURLConnection) {

		this.httpURLConnection = httpURLConnection;
	}

	public URI getXdiEndpointUri() {

		return this.xdiEndpointUri;
	}

	public void setXdiEndpointUri(URI xdiEndpointUri) {

		this.xdiEndpointUri = xdiEndpointUri;
	}

	public MimeType getSendMimeType() {

		return this.sendMimeType;
	}

	public void setSendMimeType(MimeType sendMimeType) {

		this.sendMimeType = sendMimeType;
	}

	public MimeType getRecvMimeType() {

		return this.recvMimeType;
	}

	public void setRecvMimeType(MimeType recvMimeFormat) {

		this.recvMimeType = recvMimeFormat;
	}

	public String getUserAgent() {

		return this.userAgent;
	}

	public void setUserAgent(String userAgent) {

		this.userAgent = userAgent;
	}

	public boolean getFollowRedirects() {

		return this.followRedirects;
	}

	public void setFollowRedirects(boolean followRedirects) {

		this.followRedirects = followRedirects;
	}

	/*
	 * Object methods
	 */

	@Override
	public String toString() {

		return this.getXdiEndpointUri().toString();
	}
}
