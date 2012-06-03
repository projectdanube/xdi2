package xdi2.client.http;

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
import xdi2.core.exceptions.Xdi2MessagingException;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.http.AcceptHeader;

/**
 * An XDI client that can send XDI messages over HTTP and receive results.
 * It supports the following parameters (passed to the init method):
 * <ul>
 * <li>url - The URL of the XDI endpoint to talk to.</li>
 * <li>sendformat - The format to use to send the XDI messages to the endpoint (default: XDI/JSON). This can be
 * either the name of the format or the mime type. In any case, the Content-type header will be set accordingly.</li>
 * <li>recvformat - The format in which we want to receive the results from the endpoint (default: XDI/JSON). This
 * can be either the name of the format or the mime type. In any case, The Accept header will be set accordingly.
 * If the endpoint replies in some other format than requested, we will still try to read it.</li>
 * </ul> 
 * Supported formats for both sending and receiving are: XDI/JSON, STATEMENTS
 * The corresponding mime types are: application/xdi+json, text/plain
 * 
 * @author markus
 */
public class XDIHttpClient implements XDIClient {

	public static final String KEY_URL = "url";
	public static final String KEY_SENDMIMETYPE = "sendmimetype";
	public static final String KEY_RECVMIMETYPE = "recvmimetype";
	public static final String KEY_USERAGENT = "useragent";

	public static final String DEFAULT_SENDMIMETYPE = "application/xdi+json";
	public static final String DEFAULT_RECVMIMETYPE = "application/xdi+json";
	public static final String DEFAULT_USERAGENT = "XDI Squared";

	protected static final Logger log = LoggerFactory.getLogger(XDIHttpClient.class);

	protected URL url;
	protected String sendFormat;
	protected String recvFormat;
	protected String userAgent;

	public XDIHttpClient() {

		this.url = null;
		this.sendFormat = DEFAULT_SENDMIMETYPE;
		this.recvFormat = DEFAULT_RECVMIMETYPE;
		this.userAgent = DEFAULT_USERAGENT;
	}

	public XDIHttpClient(String url) {

		try {

			this.url = (url != null) ? new URL(url) : null;
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}

		this.sendFormat = DEFAULT_SENDMIMETYPE;
		this.recvFormat = DEFAULT_RECVMIMETYPE;
		this.userAgent = DEFAULT_USERAGENT;
	}

	public XDIHttpClient(String url, String sendFormat, String recvFormat, String userAgent) {

		try {

			this.url = (url != null) ? new URL(url) : null;
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}

		this.sendFormat = (sendFormat != null) ? sendFormat : DEFAULT_SENDMIMETYPE;
		this.recvFormat = (recvFormat != null) ? recvFormat : DEFAULT_RECVMIMETYPE;
		this.userAgent = (userAgent != null) ? userAgent : DEFAULT_USERAGENT;
	}

	public XDIHttpClient(Properties parameters) throws Exception {

		if (parameters == null) {

			this.url = null;
			this.sendFormat = DEFAULT_SENDMIMETYPE;
			this.recvFormat = DEFAULT_RECVMIMETYPE;
			this.recvFormat = DEFAULT_USERAGENT;
		} else {

			this.url = new URL(parameters.getProperty(KEY_URL, null));
			this.sendFormat = parameters.getProperty(KEY_SENDMIMETYPE, DEFAULT_SENDMIMETYPE);
			this.recvFormat = parameters.getProperty(KEY_RECVMIMETYPE, DEFAULT_RECVMIMETYPE);
			this.userAgent = parameters.getProperty(KEY_RECVMIMETYPE, DEFAULT_USERAGENT);

			log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

	public MessageResult send(MessageEnvelope messageEnvelope, MessageResult messageResult) throws Xdi2MessagingException {

		if (this.url == null) throw new Xdi2MessagingException("No URL set.");

		// find out which XDIWriter we want to use

		XDIWriter writer = XDIWriterRegistry.forFormat(this.sendFormat);
		if (writer == null) writer = XDIWriterRegistry.forMimeType(this.sendFormat);
		if (writer == null) writer = XDIWriterRegistry.forMimeType(DEFAULT_SENDMIMETYPE);
		if (writer == null) writer = XDIWriterRegistry.getDefault();
		if (writer == null) throw new Xdi2MessagingException("Cannot find a suitable XDI writer.");

		log.debug("Using writer " + writer.getClass().getName() + ".");

		// find out which XDIReader we want to use

		XDIReader reader = XDIReaderRegistry.forFormat(this.recvFormat);
		if (reader == null) reader = XDIReaderRegistry.forMimeType(this.recvFormat);
		if (reader == null) reader = XDIReaderRegistry.forMimeType(DEFAULT_RECVMIMETYPE);
		if (reader == null) reader = XDIReaderRegistry.getDefault();
		if (reader == null) throw new Xdi2MessagingException("Cannot find a suitable XDI reader.");

		log.debug("Using reader " + reader.getClass().getName() + ".");

		// prepare Accept: header

		AcceptHeader acceptHeader = AcceptHeader.create(reader.getMimeType());

		log.debug("Using Accept header " + acceptHeader.toString() + ".");

		// initialize and open connection

		log.debug("Connecting...");

		URLConnection connection;

		try {

			connection = this.url.openConnection();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot open connection: " + ex.getMessage(), ex);
		}

		if (! (connection instanceof HttpURLConnection)) throw new Xdi2MessagingException("Can only work with HTTP(S) URLs.");

		HttpURLConnection http = (HttpURLConnection) connection;

		try {

			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestProperty("Content-Type", writer.getMimeType());
			http.setRequestProperty("Accept", acceptHeader.toString());
			http.setRequestProperty("User-Agent", this.userAgent);
			http.setRequestMethod("POST");
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot initialize HTTP transport: " + ex.getMessage(), ex);
		}

		// send the message envelope

		log.debug("Sending message envelope with " + messageEnvelope.getMessageCount() + " messages.");
		if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));

		int responseCode;
		String responseMessage;

		try {

			OutputStream outputStream = http.getOutputStream();
			writer.write(messageEnvelope.getGraph(), outputStream, null);
			outputStream.flush();
			outputStream.close();

			responseCode = http.getResponseCode();
			responseMessage = http.getResponseMessage();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot send message envelope: " + ex.getMessage(), ex);
		}

		// check response code

		if (responseCode >= 300) {

			throw new Xdi2MessagingException("HTTP code " + responseCode + " received: " + responseMessage);
		}

		// check in which format we receive the result

		String contentType = http.getContentType();
		int contentLength = http.getContentLength();

		log.debug("Received result. Content-Type: " + contentType + ", Content-Length: " + contentLength);

		if (contentType != null) {

			reader = XDIReaderRegistry.forMimeType(contentType);

			if (reader == null) {

				log.info("Don't know how to read message result with Content-Type " + contentType + ". Trying to auto-detect format.");
				reader = XDIReaderRegistry.getAuto();
			}
		} else {

			log.info("No Content-Type received. Trying to auto-detect format.");
			reader = XDIReaderRegistry.getAuto();
		}

		// read the message result and close connection

		if (messageResult == null) messageResult = MessageResult.newInstance();

		try {

			InputStream inputStream = http.getInputStream();
			reader.read(messageResult.getGraph(), inputStream, null);
			inputStream.close();
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot read message result: " + ex.getMessage(), ex);
		}

		http.disconnect();

		if (log.isDebugEnabled()) log.debug("MessageResult: " + messageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));

		// see if it is an error message result

		if (ErrorMessageResult.isValid(messageResult.getGraph())) {

			ErrorMessageResult errorMessageResult = ErrorMessageResult.fromGraph(messageResult.getGraph());

			log.debug("Error message result received: " + "(" + errorMessageResult.getErrorCode().toString() + ") " + errorMessageResult.getErrorString());

			throw new Xdi2MessagingException("Error message result received: " + "(" + errorMessageResult.getErrorCode().toString() + ") " + errorMessageResult.getErrorString());
		}

		// done

		log.debug("Successfully received result, " + messageResult.getGraph().getRootContextNode().getAllStatementCount() + " result statements.");

		return(messageResult);
	}

	public void close() {

	}

	public URL getUrl() {

		return(this.url);
	}

	public void setUrl(URL url) {

		this.url = url;
	}

	public String getSendFormat() {

		return(this.sendFormat);
	}

	public void setSendFormat(String sendFormat) {

		this.sendFormat = sendFormat;
	}

	public String getRecvFormat() {

		return(this.recvFormat);
	}

	public void setRecvFormat(String recvFormat) {

		this.recvFormat = recvFormat;
	}
}
