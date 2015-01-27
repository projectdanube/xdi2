package xdi2.transport.impl.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.http.AcceptHeader;
import xdi2.messaging.target.MessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.AbstractTransport;
import xdi2.transport.impl.http.registry.HttpMessagingTargetRegistry;
import xdi2.transport.impl.http.registry.MessagingTargetMount;

public class HttpTransport extends AbstractTransport<HttpRequest, HttpResponse> {

	private static final Logger log = LoggerFactory.getLogger(HttpTransport.class);

	private static final Map<String, String> DEFAULT_HEADERS;
	private static final Map<String, String> DEFAULT_HEADERS_GET;
	private static final Map<String, String> DEFAULT_HEADERS_POST;
	private static final Map<String, String> DEFAULT_HEADERS_PUT;
	private static final Map<String, String> DEFAULT_HEADERS_DELETE;
	private static final Map<String, String> DEFAULT_HEADERS_OPTIONS;

	static {

		DEFAULT_HEADERS = new HashMap<String, String> ();
		DEFAULT_HEADERS.put("Access-Control-Allow-Origin", "*");
		DEFAULT_HEADERS.put("Access-Control-Allow-Credentials", "true");
		DEFAULT_HEADERS.put("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Cache-Control, Expires, X-Cache, X-HTTP-Method-Override, Accept");
		DEFAULT_HEADERS.put("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
		DEFAULT_HEADERS_GET = new HashMap<String, String> ();
		DEFAULT_HEADERS_POST = new HashMap<String, String> ();
		DEFAULT_HEADERS_PUT = new HashMap<String, String> ();
		DEFAULT_HEADERS_DELETE = new HashMap<String, String> ();
		DEFAULT_HEADERS_OPTIONS = new HashMap<String, String> ();
		DEFAULT_HEADERS_OPTIONS.put("Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS");
	}

	private HttpMessagingTargetRegistry httpMessagingTargetRegistry;
	private Map<String, String> headers;
	private Map<String, String> headersGet;
	private Map<String, String> headersPost;
	private Map<String, String> headersPut;
	private Map<String, String> headersDelete;
	private Map<String, String> headersOptions;

	public HttpTransport(HttpMessagingTargetRegistry httpMessagingTargetRegistry) {

		this.httpMessagingTargetRegistry = httpMessagingTargetRegistry;
		this.headers = DEFAULT_HEADERS;
		this.headersGet = DEFAULT_HEADERS_GET;
		this.headersPost = DEFAULT_HEADERS_POST;
		this.headersPut = DEFAULT_HEADERS_PUT;
		this.headersDelete = DEFAULT_HEADERS_DELETE;
		this.headersOptions = DEFAULT_HEADERS_OPTIONS;
	}

	public HttpTransport() {

		this(null);
	}

	@Override
	public void init() throws Exception {

		super.init();
	}

	@Override
	public void shutdown() throws Exception {

		super.shutdown();
	}

	public void doGet(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming GET request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());
		
		try {

			MessagingTargetMount messagingTargetMount = this.getHttpMessagingTargetRegistry().lookup(request.getRequestPath());

			this.processGetRequest(request, response, messagingTargetMount);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed GET request.");
	}

	public void doPost(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming POST request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

		try {

			MessagingTargetMount messagingTargetMount = this.getHttpMessagingTargetRegistry().lookup(request.getRequestPath());

			this.processPostRequest(request, response, messagingTargetMount);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed POST request.");
	}

	public void doPut(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming PUT request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

		try {

			MessagingTargetMount messagingTargetMount = this.getHttpMessagingTargetRegistry().lookup(request.getRequestPath());

			this.processPutRequest(request, response, messagingTargetMount);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed PUT request.");
	}

	public void doDelete(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming DELETE request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

		try {

			MessagingTargetMount messagingTargetMount = this.getHttpMessagingTargetRegistry().lookup(request.getRequestPath());

			this.processDeleteRequest(request, response, messagingTargetMount);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed DELETE request.");
	}

	public void doOptions(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming OPTIONS request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

		try {

			this.processOptionsRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed OPTIONS request.");
	}

	protected void processGetRequest(HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		MessagingTarget messagingTarget = messagingTargetMount == null ? null : messagingTargetMount.getMessagingTarget();

		// execute interceptors

		boolean result = InterceptorExecutor.executeHttpTransportInterceptorsGet(this.getInterceptors(), this, request, response, messagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (GET).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			log.warn("No XDI messaging target configured at " + request.getRequestPath() + ". Sending " + HttpResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + request.getRequestPath());

			return;
		}

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(messagingTargetMount, request, response, XDIMessagingConstants.XDI_ADD_GET);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		this.sendStatusAndHeaders(request, response);
		sendMessageResult(messageResult, request, response);
	}

	protected void processPostRequest(HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		MessagingTarget messagingTarget = messagingTargetMount == null ? null : messagingTargetMount.getMessagingTarget();

		// execute interceptors

		boolean result = InterceptorExecutor.executeHttpTransportInterceptorsPost(this.getInterceptors(), this, request, response, messagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (POST).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			log.warn("No XDI messaging target configured at " + request.getRequestPath() + ". Sending " + HttpResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + request.getRequestPath());

			return;
		}

		// construct message envelope from body

		MessageEnvelope messageEnvelope = readFromBody(request, response);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		this.sendStatusAndHeaders(request, response);
		sendMessageResult(messageResult, request, response);
	}

	protected void processPutRequest(HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		MessagingTarget messagingTarget = messagingTargetMount == null ? null : messagingTargetMount.getMessagingTarget();

		// execute interceptors

		boolean result = InterceptorExecutor.executeHttpTransportInterceptorsPut(this.getInterceptors(), this, request, response, messagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (PUT).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			log.warn("No XDI messaging target configured at " + request.getRequestPath() + ". Sending " + HttpResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + request.getRequestPath());

			return;
		}

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(messagingTargetMount, request, response, XDIMessagingConstants.XDI_ADD_SET);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		this.sendStatusAndHeaders(request, response);
		sendMessageResult(messageResult, request, response);
	}

	protected void processDeleteRequest(HttpRequest request, HttpResponse response, MessagingTargetMount messagingTargetMount) throws Xdi2TransportException, IOException {

		MessagingTarget messagingTarget = messagingTargetMount == null ? null : messagingTargetMount.getMessagingTarget();

		// execute interceptors

		boolean result = InterceptorExecutor.executeHttpTransportInterceptorsDelete(this.getInterceptors(), this, request, response, messagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (DELETE).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			log.warn("No XDI messaging target configured at " + request.getRequestPath() + ". Sending " + HttpResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + request.getRequestPath());

			return;
		}

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(messagingTargetMount, request, response, XDIMessagingConstants.XDI_ADD_DEL);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		this.sendStatusAndHeaders(request, response);
		sendMessageResult(messageResult, request, response);
	}

	protected void processOptionsRequest(HttpRequest request, HttpResponse response) throws Xdi2TransportException, IOException {

		// send out result

		this.sendStatusAndHeaders(request, response);
		response.setContentLength(0);
	}

	private MessageEnvelope readFromUrl(MessagingTargetMount messagingTargetMount, HttpRequest request, HttpResponse response, XDIAddress operationAddress) throws IOException {

		if (messagingTargetMount == null) throw new NullPointerException();

		// parse an XDI address from the request path

		String addr = request.getRequestPath().substring(messagingTargetMount.getMessagingTargetPath().length());
		while (addr.length() > 0 && addr.charAt(0) == '/') addr = addr.substring(1);

		if (log.isDebugEnabled()) log.debug("XDI address: " + addr);

		XDIAddress targetAddress;

		if (addr.equals("")) {

			targetAddress = null;
		} else {

			try {

				targetAddress = XDIAddress.create(addr);
			} catch (Exception ex) {

				log.error("Cannot parse XDI address: " + ex.getMessage(), ex);
				this.handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
				return null;
			}
		}

		// convert address to a mini messaging envelope

		if (log.isDebugEnabled()) log.debug("Requested XDI context node: " + targetAddress + ".");

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXDIAddressAndTargetXDIAddress(XDIMessagingConstants.XDI_ADD_GET, targetAddress);

		// set the TO peer root XRI to the owner peer root XRI of the messaging target

		XDIArc ownerPeerRootXDIArc = messagingTargetMount.getMessagingTarget().getOwnerPeerRootXDIArc();

		if (ownerPeerRootXDIArc != null) {

			Message message = messageEnvelope.getMessages().next();
			message.setToPeerRootXDIArc(ownerPeerRootXDIArc);
		}

		// done

		return messageEnvelope;
	}

	private MessageEnvelope readFromBody(HttpRequest request, HttpResponse response) throws IOException {

		// try to find an appropriate reader for the provided mime type

		XDIReader xdiReader = null;

		String contentType = request.getContentType();
		MimeType recvMimeType = contentType != null ? new MimeType(contentType) : null;
		xdiReader = recvMimeType != null ? XDIReaderRegistry.forMimeType(recvMimeType) : null;

		if (xdiReader == null) xdiReader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		if (log.isDebugEnabled()) log.debug("Reading message in " + recvMimeType + " with reader " + xdiReader.getClass().getSimpleName() + ".");

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		MessageEnvelope messageEnvelope;
		long messageCount;

		InputStream inputStream = request.getBodyInputStream();

		try {

			xdiReader.read(graph, inputStream);
			messageEnvelope = MessageEnvelope.fromGraph(graph);
			messageCount = messageEnvelope.getMessageCount();
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			this.handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
			return null;
		} finally {

			inputStream.close();
		}

		if (log.isDebugEnabled()) log.debug("Message envelope received (" + messageCount + " messages). Executing...");

		return messageEnvelope;
	}

	/*
	 * Helper methods
	 */

	private void sendStatusAndHeaders(HttpRequest request, HttpResponse response) {

		response.setStatus(HttpResponse.SC_OK);

		Map<String, String> headers = new HashMap<String, String> ();
		headers.putAll(this.getHeaders());

		if ("GET".equals(request.getMethod())) headers.putAll(this.getHeadersGet());
		if ("POST".equals(request.getMethod())) headers.putAll(this.getHeadersPost());
		if ("PUT".equals(request.getMethod())) headers.putAll(this.getHeadersPut());
		if ("DELETE".equals(request.getMethod())) headers.putAll(this.getHeadersDelete());
		if ("OPTIONS".equals(request.getMethod())) headers.putAll(this.getHeadersOptions());
		
		for (Map.Entry<String, String> header : headers.entrySet()) {
			
			response.setHeader(header.getKey(), header.getValue());
		}
	}

	private static void sendMessageResult(MessageResult messageResult, HttpRequest request, HttpResponse response) throws IOException {

		// find a suitable writer based on accept headers

		if (log.isDebugEnabled()) log.debug("Accept: " + request.getHeader("Accept"));

		XDIWriter writer = null;

		String acceptHeader = request.getHeader("Accept");
		MimeType sendMimeType = acceptHeader != null ? AcceptHeader.parse(acceptHeader).bestMimeType(false, true) : null;
		writer = sendMimeType != null ? XDIWriterRegistry.forMimeType(sendMimeType) : null;

		if (writer == null) writer = XDIWriterRegistry.getDefault();

		// send out the message result

		if (log.isDebugEnabled()) log.debug("Sending result in " + sendMimeType + " with writer " + writer.getClass().getSimpleName() + ".");

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		writer.write(messageResult.getGraph(), buffer);

		response.setContentType(writer.getMimeType().toString());
		response.setContentLength(buffer.size());

		if (buffer.size() > 0) {

			OutputStream outputStream = response.getBodyOutputStream();

			outputStream.write(buffer.toByteArray());
			outputStream.flush();

			outputStream.close();
		}

		if (log.isDebugEnabled()) log.debug("Output complete.");
	}

	private static void handleInternalException(HttpRequest request, HttpResponse response, Exception ex) throws IOException {

		response.sendError(HttpResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception: " + ex.getMessage());
	}

	@Override
	protected void handleException(HttpRequest request, HttpResponse response, ErrorMessageResult errorMessageResult) throws IOException {

		// send error result

		this.sendStatusAndHeaders(request, response);
		sendMessageResult(errorMessageResult, request, response);
	}

	/*
	 * Getters and setters
	 */

	public HttpMessagingTargetRegistry getHttpMessagingTargetRegistry() {

		return this.httpMessagingTargetRegistry;
	}

	public void setHttpMessagingTargetRegistry(HttpMessagingTargetRegistry httpMessagingTargetRegistry) {

		this.httpMessagingTargetRegistry = httpMessagingTargetRegistry;
	}

	public Map<String, String> getHeaders() {

		return this.headers;
	}

	public void setHeaders(Map<String, String> headers) {

		this.headers = headers;
	}

	public Map<String, String> getHeadersGet() {
		return headersGet;
	}

	public void setHeadersGet(Map<String, String> headersGet) {

		this.headersGet = headersGet;
	}

	public Map<String, String> getHeadersPost() {

		return this.headersPost;
	}

	public void setHeadersPost(Map<String, String> headersPost) {

		this.headersPost = headersPost;
	}

	public Map<String, String> getHeadersPut() {

		return this.headersPut;
	}

	public void setHeadersPut(Map<String, String> headersPut) {

		this.headersPut = headersPut;
	}

	public Map<String, String> getHeadersDelete() {

		return this.headersDelete;
	}

	public void setHeadersDelete(Map<String, String> headersDelete) {

		this.headersDelete = headersDelete;
	}

	public Map<String, String> getHeadersOptions() {

		return this.headersOptions;
	}

	public void setHeadersOptions(Map<String, String> headersOptions) {

		this.headersOptions = headersOptions;
	}
}
