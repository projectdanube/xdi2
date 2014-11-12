package xdi2.client.impl.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.client.impl.http.ssl.XDI2X509TrustManager;
import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.http.AcceptHeader;
import xdi2.messaging.response.AbstractMessagingResponse;
import xdi2.messaging.response.MessagingResponse;

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
public class XDIHttpClient extends XDIAbstractClient implements XDIClient {

	public static final String KEY_endpointUrl = "endpointUrl";
	public static final String KEY_SENDMIMETYPE = "sendmimetype";
	public static final String KEY_RECVMIMETYPE = "recvmimetype";
	public static final String KEY_USERAGENT = "useragent";
	public static final String KEY_FOLLOWREDIRECTS = "followredirects";

	public static final String DEFAULT_SENDMIMETYPE = "application/xdi+json;implied=0";
	public static final String DEFAULT_RECVMIMETYPE = "application/xdi+json;implied=0";
	public static final String DEFAULT_USERAGENT = "XDI2 Java Library";
	public static final String DEFAULT_FOLLOWREDIRECTS = "false";

	protected static final Logger log = LoggerFactory.getLogger(XDIHttpClient.class);

	protected URL xdiEndpointUrl;
	protected MimeType sendMimeType;
	protected MimeType recvMimeType;
	protected String userAgent;
	protected boolean followRedirects;

	static {

		XDI2X509TrustManager.enable();
	}

	public XDIHttpClient() {

		super();

		this.xdiEndpointUrl = null;
		this.sendMimeType = new MimeType(DEFAULT_SENDMIMETYPE);
		this.recvMimeType = new MimeType(DEFAULT_RECVMIMETYPE);
		this.userAgent = DEFAULT_USERAGENT;
		this.followRedirects = Boolean.parseBoolean(DEFAULT_FOLLOWREDIRECTS);
	}

	public XDIHttpClient(URL xdiEndpointUrl, MimeType sendMimeType, MimeType recvMimeType, String userAgent, Boolean followRedirects) {

		super();

		this.xdiEndpointUrl = xdiEndpointUrl;
		this.sendMimeType = (sendMimeType != null) ? sendMimeType : new MimeType(DEFAULT_SENDMIMETYPE);
		this.recvMimeType = (recvMimeType != null) ? recvMimeType : new MimeType(DEFAULT_RECVMIMETYPE);
		this.userAgent = (userAgent != null) ? userAgent : DEFAULT_USERAGENT;
		this.followRedirects = (followRedirects != null) ? followRedirects.booleanValue() : Boolean.parseBoolean(DEFAULT_FOLLOWREDIRECTS);
	}

	public XDIHttpClient(URL xdiEndpointUrl, MimeType sendMimeType, MimeType recvMimeType, String userAgent) {

		this(xdiEndpointUrl, sendMimeType, recvMimeType, userAgent, null);
	}

	public XDIHttpClient(URL xdiEndpointUrl, MimeType sendMimeType, MimeType recvMimeType) {

		this(xdiEndpointUrl, sendMimeType, recvMimeType, null, null);
	}

	public XDIHttpClient(URL xdiEndpointUrl) {

		this(xdiEndpointUrl, null, null, null, null);
	}

	public XDIHttpClient(String xdiEndpointUrl) {

		this(null, null, null, null, null);

		try {

			this.setXdiEndpointUrl(new URL(xdiEndpointUrl));
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}
	}

	public XDIHttpClient(Properties parameters) throws Exception {

		super();

		if (parameters == null) {

			this.xdiEndpointUrl = null;
			this.sendMimeType = new MimeType(DEFAULT_SENDMIMETYPE);
			this.recvMimeType = new MimeType(DEFAULT_RECVMIMETYPE);
			this.userAgent = DEFAULT_USERAGENT;
			this.followRedirects = Boolean.parseBoolean(DEFAULT_FOLLOWREDIRECTS);
		} else {

			this.xdiEndpointUrl = new URL(parameters.getProperty(KEY_endpointUrl, null));
			this.sendMimeType = new MimeType(parameters.getProperty(KEY_SENDMIMETYPE, DEFAULT_SENDMIMETYPE));
			this.recvMimeType = new MimeType(parameters.getProperty(KEY_RECVMIMETYPE, DEFAULT_RECVMIMETYPE));
			this.userAgent = parameters.getProperty(KEY_USERAGENT, DEFAULT_USERAGENT);
			this.followRedirects = Boolean.parseBoolean(parameters.getProperty(KEY_FOLLOWREDIRECTS, DEFAULT_FOLLOWREDIRECTS));

			if (log.isDebugEnabled()) log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

	@Override
	protected MessagingResponse sendInternal(MessageEnvelope messageEnvelope) throws Xdi2ClientException {

		if (this.xdiEndpointUrl == null) throw new Xdi2ClientException("No URI set.");

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

		// prepare Accept: header

		AcceptHeader acceptHeader = AcceptHeader.create(recvMimeType);

		if (log.isDebugEnabled()) log.debug("Using Accept header " + acceptHeader.toString() + ".");

		// initialize and open connection

		if (log.isDebugEnabled()) log.debug("Connecting to " + this.xdiEndpointUrl);

		URLConnection connection;

		try {

			connection = this.getXdiEndpointUrl().openConnection();
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot open connection: " + ex.getMessage(), ex);
		}

		if (! (connection instanceof HttpURLConnection)) throw new Xdi2ClientException("Can only work with HTTP(S) URLs.");

		HttpURLConnection http = (HttpURLConnection) connection;

		try {

			http.setDoInput(true);
			http.setDoOutput(true);
			http.setInstanceFollowRedirects(this.getFollowRedirects());
			System.setProperty("http.strictPostRedirect", "true");
			http.setRequestProperty("Content-Type", sendMimeType.toString());
			http.setRequestProperty("Accept", acceptHeader.toString());
			http.setRequestProperty("User-Agent", this.getUserAgent());
			http.setRequestMethod("POST");
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot initialize HTTP transport: " + ex.getMessage(), ex);
		}

		// send the message envelope

		int responseCode;
		String responseMessage;

		try {

			OutputStream outputStream = http.getOutputStream();
			writer.write(messageEnvelope.getGraph(), outputStream);
			outputStream.flush();
			outputStream.close();

			responseCode = http.getResponseCode();
			responseMessage = http.getResponseMessage();
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot send message envelope: " + ex.getMessage(), ex);
		}

		// check response code

		if (responseCode >= 300) {

			throw new Xdi2ClientException("HTTP code " + responseCode + " received: " + responseMessage);
		}

		// check in which format we receive the result

		String contentType = http.getContentType();
		int contentLength = http.getContentLength();

		if (log.isDebugEnabled()) log.debug("Received result. Content-Type: " + contentType + ", Content-Length: " + contentLength);

		if (contentType != null) {

			reader = XDIReaderRegistry.forMimeType(new MimeType(contentType));

			if (reader == null) {

				if (log.isInfoEnabled()) log.info("Don't know how to read message result with Content-Type " + contentType + ". Trying to auto-detect format.");
				reader = XDIReaderRegistry.getAuto();
			}
		} else {

			if (log.isInfoEnabled()) log.info("No Content-Type received. Trying to auto-detect format.");
			reader = XDIReaderRegistry.getAuto();
		}

		// read the messaging response and close connection

		Graph messagingResponseGraph = MemoryGraphFactory.getInstance().openGraph();

		try {

			InputStream inputStream = http.getInputStream();
			reader.read(messagingResponseGraph, inputStream);
			inputStream.close();
		} catch (Exception ex) {

			throw new Xdi2ClientException("Cannot read message result: " + ex.getMessage(), ex);
		} finally {

			http.disconnect();
		}

		MessagingResponse messagingResponse = AbstractMessagingResponse.fromGraph(messagingResponseGraph);

		// done

		return messagingResponse;
	}

	@Override
	public void close() {

	}

	/*
	 * Getters and setters
	 */

	public URL getXdiEndpointUrl() {

		return this.xdiEndpointUrl;
	}

	public void setXdiEndpointUrl(URL xdiEndpointUrl) {

		this.xdiEndpointUrl = xdiEndpointUrl;
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

		return this.getXdiEndpointUrl().toString();
	}
}
