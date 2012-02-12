package xdi2.client.http;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import xdi2.client.XDIClient;
import xdi2.exceptions.Xdi2MessagingException;
import xdi2.io.XDIReader;
import xdi2.io.XDIReaderRegistry;
import xdi2.io.XDIWriter;
import xdi2.io.XDIWriterRegistry;
import xdi2.messaging.ErrorMessageResult;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.http.AcceptHeader;
import xdi2.messaging.http.AcceptHeader.AcceptEntry;

/**
 * An XDI client that can send XDI messages over HTTP and receive results.
 * It supports the following parameters (passed to the init method):
 * <ul>
 * <li>url - The URL of the XDI endpoint to talk to.</li>
 * <li>sendformat - The format to use to send the XDI messages to the endpoint (default: X3 Standard). This can be
 * either the name of the format or the mime type. In any case, the Content-type header will be set accordingly.</li>
 * <li>recvformat - The format in which we want to receive the results from the endpoint (default: X3 Standard). This
 * can be either the name of the format or the mime type. In any case, The Accept header will be set accordingly.
 * If the endpoint replies in some other format than requested, we will still try to read it.</li>
 * </ul> 
 * Supported formats for both sending and receiving are: XDI/XML, X3, X-TRIPLES.
 * The corresponding mime types are: application/xdi+xml, text/xdi+x3, text/plain
 * 
 * @author markus
 */
public class XDIHttpClient implements XDIClient {

	public static final String KEY_URL = "url";
	public static final String KEY_SENDFORMAT = "sendformat";
	public static final String KEY_RECVFORMAT = "recvformat";
	public static final String KEY_USERAGENT = "useragent";

	public static final String DEFAULT_SENDFORMAT = "X3 Standard";
	public static final String DEFAULT_RECVFORMAT = "X3 Standard";
	public static final String DEFAULT_USERAGENT = "XDI4j";

	protected static final Log log = LogFactory.getLog(XDIHttpClient.class);

	protected URL url;
	protected String sendFormat;
	protected String recvFormat;
	protected String userAgent;

	public XDIHttpClient() {

		this.url = null;
		this.sendFormat = DEFAULT_SENDFORMAT;
		this.recvFormat = DEFAULT_RECVFORMAT;
		this.userAgent = DEFAULT_USERAGENT;
	}

	public XDIHttpClient(String url) {

		try {

			this.url = (url != null) ? new URL(url) : null;
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}

		this.sendFormat = DEFAULT_SENDFORMAT;
		this.recvFormat = DEFAULT_RECVFORMAT;
		this.userAgent = DEFAULT_USERAGENT;
	}

	public XDIHttpClient(String url, String sendFormat, String recvFormat, String userAgent) {

		try {

			this.url = (url != null) ? new URL(url) : null;
		} catch (MalformedURLException ex) {

			throw new IllegalArgumentException(ex.getMessage(), ex);
		}

		this.sendFormat = (sendFormat != null) ? sendFormat : DEFAULT_SENDFORMAT;
		this.recvFormat = (recvFormat != null) ? recvFormat : DEFAULT_RECVFORMAT;
		this.userAgent = (userAgent != null) ? userAgent : DEFAULT_USERAGENT;
	}

	public XDIHttpClient(Properties parameters) throws Exception {

		if (parameters == null) {

			this.url = null;
			this.sendFormat = DEFAULT_SENDFORMAT;
			this.recvFormat = DEFAULT_RECVFORMAT;
			this.recvFormat = DEFAULT_USERAGENT;
		} else {

			this.url = new URL(parameters.getProperty(KEY_URL, null));
			this.sendFormat = parameters.getProperty(KEY_SENDFORMAT, DEFAULT_SENDFORMAT);
			this.recvFormat = parameters.getProperty(KEY_RECVFORMAT, DEFAULT_RECVFORMAT);
			this.userAgent = parameters.getProperty(KEY_RECVFORMAT, DEFAULT_USERAGENT);

			log.debug("Initialized with " + parameters.toString() + ".");
		}
	}

	public MessageResult send(MessageEnvelope messageEnvelope, MessageResult messageResult) throws Xdi2MessagingException {

		if (this.url == null) throw new Xdi2MessagingException("No URL set.");

		// find out which XDIWriter we want to use

		XDIWriter writer = XDIWriterRegistry.forFormat(this.sendFormat);
		if (writer == null) writer = XDIWriterRegistry.forMimeType(this.sendFormat);
		if (writer == null) writer = XDIWriterRegistry.forFormat(DEFAULT_SENDFORMAT);
		if (writer == null) writer = XDIWriterRegistry.getDefault();
		if (writer == null) throw new Xdi2MessagingException("Cannot find a suitable XDI writer.");

		log.debug("Using writer " + writer.getClass().getName() + ".");

		// find out which XDIReader we want to use

		XDIReader reader = XDIReaderRegistry.forFormat(this.recvFormat);
		if (reader == null) reader = XDIReaderRegistry.forMimeType(this.recvFormat);
		if (reader == null) reader = XDIReaderRegistry.forFormat(DEFAULT_RECVFORMAT);
		if (reader == null) reader = XDIReaderRegistry.getDefault();
		if (reader == null) throw new Xdi2MessagingException("Cannot find a suitable XDI reader.");

		log.debug("Using reader " + reader.getClass().getName() + ".");

		// prepare Accept: header

		AcceptHeader accept = new AcceptHeader();

		mimeTypes: for (String mimeType : XDIReaderRegistry.getMimeTypes()) {

			for (String readerMimeType : reader.getMimeTypes()) {

				if (mimeType.equals(readerMimeType)) continue mimeTypes;
			}

			accept.addEntry(new AcceptEntry(0.5f, mimeType));
		}

		for (String readerMimeType : reader.getMimeTypes()) {

			accept.addEntry(new AcceptEntry(1, readerMimeType));
		}

		log.debug("Using Accept header " + accept.toString() + ".");

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
			http.setRequestProperty("Content-Type", writer.getMimeTypes()[0]);
			http.setRequestProperty("Accept", accept.toString());
			http.setRequestProperty("User-Agent", this.userAgent);
			http.setRequestMethod("POST");
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Cannot initialize HTTP transport: " + ex.getMessage(), ex);
		}

		// send the message envelope

		log.debug("Sending message envelope with " + messageEnvelope.getMessageCount() + " messages.");

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

		// see if it is an error message result

		if (ErrorMessageResult.isValid(messageResult.getGraph())) {

			messageResult = ErrorMessageResult.fromGraph(messageResult.getGraph());

			log.debug("Error message result received: " + ((ErrorMessageResult) messageResult).getErrorString() + " (" + ((ErrorMessageResult) messageResult).getErrorCode().toString() + ")");
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
