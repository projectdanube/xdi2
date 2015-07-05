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
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.http.AcceptHeader;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.transport.exceptions.Xdi2TransportException;
import xdi2.transport.impl.AbstractTransport;
import xdi2.transport.registry.impl.uri.UriMessagingTargetMount;
import xdi2.transport.registry.impl.uri.UriMessagingTargetRegistry;

public class HttpTransport extends AbstractTransport<HttpTransportRequest, HttpTransportResponse> {

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

	private UriMessagingTargetRegistry uriMessagingTargetRegistry;
	private Map<String, String> headers;
	private Map<String, String> headersGet;
	private Map<String, String> headersPost;
	private Map<String, String> headersPut;
	private Map<String, String> headersDelete;
	private Map<String, String> headersOptions;

	public HttpTransport(UriMessagingTargetRegistry uriMessagingTargetRegistry) {

		this.uriMessagingTargetRegistry = uriMessagingTargetRegistry;
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

	@Override
	public void execute(HttpTransportRequest request, HttpTransportResponse response) throws IOException {

		if (log.isInfoEnabled()) log.info("Incoming " + request.getMethod() + " request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

		try {

			UriMessagingTargetMount uriMessagingTargetMount = this.getUriMessagingTargetRegistry().lookup(request.getRequestPath());

			if (HttpTransportRequest.METHOD_GET.equals(request.getMethod())) this.processGetRequest(request, response, uriMessagingTargetMount);
			else if (HttpTransportRequest.METHOD_POST.equals(request.getMethod())) this.processPostRequest(request, response, uriMessagingTargetMount);
			else if (HttpTransportRequest.METHOD_PUT.equals(request.getMethod())) this.processPutRequest(request, response, uriMessagingTargetMount);
			else if (HttpTransportRequest.METHOD_DELETE.equals(request.getMethod())) this.processDeleteRequest(request, response, uriMessagingTargetMount);
			else if (HttpTransportRequest.METHOD_OPTIONS.equals(request.getMethod())) this.processOptionsRequest(request, response);
			else throw new Xdi2TransportException("Invalid HTTP method: " + request.getMethod());
		} catch (IOException ex) {

			throw ex;
		} catch (Exception ex) {

			sendErrorInternalServer(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed " + request.getMethod() + " request.");
	}

	protected void processGetRequest(HttpTransportRequest request, HttpTransportResponse response, UriMessagingTargetMount uriMessagingTargetMount) throws Xdi2TransportException, IOException {

		final MessagingTarget messagingTarget = uriMessagingTargetMount == null ? null : uriMessagingTargetMount.getMessagingTarget();
		MessageEnvelope messageEnvelope;
		MessagingResponse messagingResponse;

		// execute interceptors

		boolean result = InterceptorExecutor.executeHttpTransportInterceptorsGet(this.getInterceptors(), this, request, response, uriMessagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (GET).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			sendErrorNotFound(request, response);
			return;
		}

		// construct message envelope from url 

		try {

			messageEnvelope = readFromUrl(uriMessagingTargetMount, request, response, XDIMessagingConstants.XDI_ADD_GET);
			if (messageEnvelope == null) throw new Xdi2TransportException("No messaging request.");
		} catch (IOException ex) {

			throw new Xdi2TransportException("Invalid message envelope: " + ex.getMessage(), ex);
		}

		// execute the messaging request against our messaging target, save messaging response

		messagingResponse = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messagingResponse == null || messagingResponse.getGraph() == null) throw new Xdi2TransportException("No messaging response.");

		// done

		this.sendOk(request, response, messagingResponse);
	}

	protected void processPostRequest(HttpTransportRequest request, HttpTransportResponse response, UriMessagingTargetMount uriMessagingTargetMount) throws Xdi2TransportException, IOException {

		final MessagingTarget messagingTarget = uriMessagingTargetMount == null ? null : uriMessagingTargetMount.getMessagingTarget();
		MessageEnvelope messageEnvelope;
		MessagingResponse messagingResponse;

		// execute interceptors

		boolean result = InterceptorExecutor.executeHttpTransportInterceptorsPost(this.getInterceptors(), this, request, response, uriMessagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (POST).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			sendErrorNotFound(request, response);
			return;
		}

		// construct messaging request from body

		try {

			messageEnvelope = readFromBody(request, response);
			if (messageEnvelope == null) throw new Xdi2TransportException("No messaging request.");
		} catch (IOException ex) {

			throw new Xdi2TransportException("Invalid message envelope: " + ex.getMessage(), ex);
		}

		// execute the messaging request against our messaging target, save messaging response

		messagingResponse = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messagingResponse == null || messagingResponse.getGraph() == null) throw new Xdi2TransportException("No messaging response.");

		// done

		this.sendOk(request, response, messagingResponse);
	}

	protected void processPutRequest(HttpTransportRequest request, HttpTransportResponse response, UriMessagingTargetMount uriMessagingTargetMount) throws Xdi2TransportException, IOException {

		final MessagingTarget messagingTarget = uriMessagingTargetMount == null ? null : uriMessagingTargetMount.getMessagingTarget();
		MessageEnvelope messageEnvelope;
		MessagingResponse messagingResponse;

		// execute interceptors

		boolean result = InterceptorExecutor.executeHttpTransportInterceptorsPut(this.getInterceptors(), this, request, response, uriMessagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (PUT).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			sendErrorNotFound(request, response);
			return;
		}

		// construct message envelope from url 

		try {

			messageEnvelope = readFromUrl(uriMessagingTargetMount, request, response, XDIMessagingConstants.XDI_ADD_SET);
			if (messageEnvelope == null) throw new Xdi2TransportException("No messaging request.");
		} catch (IOException ex) {

			throw new Xdi2TransportException("Invalid message envelope: " + ex.getMessage(), ex);
		}

		// execute the messaging request against our messaging target, save messaging response

		messagingResponse = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messagingResponse == null || messagingResponse.getGraph() == null) throw new Xdi2TransportException("No messaging response.");

		// done

		this.sendOk(request, response, messagingResponse);
	}

	protected void processDeleteRequest(HttpTransportRequest request, HttpTransportResponse response, UriMessagingTargetMount uriMessagingTargetMount) throws Xdi2TransportException, IOException {

		final MessagingTarget messagingTarget = uriMessagingTargetMount == null ? null : uriMessagingTargetMount.getMessagingTarget();
		MessageEnvelope messageEnvelope;
		MessagingResponse messagingResponse;

		// execute interceptors

		boolean result = InterceptorExecutor.executeHttpTransportInterceptorsDelete(this.getInterceptors(), this, request, response, uriMessagingTargetMount);

		if (result) {

			if (log.isDebugEnabled()) log.debug("Skipping request according to HTTP transport interceptor (DELETE).");
			return;
		}

		// no messaging target?

		if (messagingTarget == null) {

			sendErrorNotFound(request, response);
			return;
		}

		// construct message envelope from url 

		try {

			messageEnvelope = readFromUrl(uriMessagingTargetMount, request, response, XDIMessagingConstants.XDI_ADD_DEL);
			if (messageEnvelope == null) throw new Xdi2TransportException("No messaging request.");
		} catch (IOException ex) {

			throw new Xdi2TransportException("Invalid message envelope: " + ex.getMessage(), ex);
		}

		// execute the messaging request against our messaging target, save messaging response

		messagingResponse = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messagingResponse == null || messagingResponse.getGraph() == null) throw new Xdi2TransportException("No messaging response.");

		// done

		this.sendOk(request, response, messagingResponse);
	}

	protected void processOptionsRequest(HttpTransportRequest request, HttpTransportResponse response) throws Xdi2TransportException, IOException {

		// send out response

		this.sendOk(request, response, null);
		response.setContentLength(0);
	}

	/*
	 * Helper methods
	 */

	private static MessageEnvelope readFromUrl(UriMessagingTargetMount messagingTargetMount, HttpTransportRequest request, HttpTransportResponse response, XDIAddress operationAddress) throws IOException {

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
				throw new IOException("Cannot parse XDI graph: " + ex.getMessage(), ex);
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

	private static MessageEnvelope readFromBody(HttpTransportRequest request, HttpTransportResponse response) throws IOException {

		InputStream inputStream = request.getBodyInputStream();

		// try to find an appropriate reader for the provided mime type

		XDIReader xdiReader = null;

		String contentType = request.getContentType();
		MimeType recvMimeType = contentType != null ? new MimeType(contentType) : null;
		xdiReader = recvMimeType != null ? XDIReaderRegistry.forMimeType(recvMimeType) : null;

		if (xdiReader == null) xdiReader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		if (log.isDebugEnabled()) log.debug("Reading message in " + recvMimeType + " with reader " + xdiReader.getClass().getSimpleName() + ".");

		MessageEnvelope messageEnvelope;

		try {

			Graph graph = MemoryGraphFactory.getInstance().openGraph();

			xdiReader.read(graph, inputStream);
			messageEnvelope = MessageEnvelope.fromGraph(graph);
		} catch (IOException ex) {

			throw ex;
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			throw new IOException("Cannot parse XDI graph: " + ex.getMessage(), ex);
		} finally {

			inputStream.close();
		}

		if (log.isDebugEnabled()) log.debug("Message envelope received (" + messageEnvelope.getMessageCount() + " messages). Executing...");

		// done

		return messageEnvelope;
	}

	private void sendOk(HttpTransportRequest request, HttpTransportResponse response, MessagingResponse messagingResponse) throws IOException {

		response.setStatus(HttpTransportResponse.SC_OK);

		Map<String, String> headers = new HashMap<String, String> ();
		headers.putAll(this.getHeaders());

		if (HttpTransportRequest.METHOD_GET.equals(request.getMethod())) headers.putAll(this.getHeadersGet());
		if (HttpTransportRequest.METHOD_POST.equals(request.getMethod())) headers.putAll(this.getHeadersPost());
		if (HttpTransportRequest.METHOD_PUT.equals(request.getMethod())) headers.putAll(this.getHeadersPut());
		if (HttpTransportRequest.METHOD_DELETE.equals(request.getMethod())) headers.putAll(this.getHeadersDelete());
		if (HttpTransportRequest.METHOD_OPTIONS.equals(request.getMethod())) headers.putAll(this.getHeadersOptions());

		for (Map.Entry<String, String> header : headers.entrySet()) {

			response.setHeader(header.getKey(), header.getValue());
		}

		if (messagingResponse != null) {

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
			writer.write(messagingResponse.getGraph(), buffer);

			response.setContentType(writer.getMimeType().toString());
			response.setContentLength(buffer.size());

			if (buffer.size() > 0) {

				OutputStream outputStream = response.getBodyOutputStream();

				outputStream.write(buffer.toByteArray());
				outputStream.flush();

				outputStream.close();
			}
		}

		if (log.isDebugEnabled()) log.debug("Output complete.");
	}

	private static void sendErrorNotFound(HttpTransportRequest request, HttpTransportResponse response) throws IOException {

		log.warn("Not found: " + request.getRequestPath() + ". Sending " + HttpTransportResponse.SC_NOT_FOUND + ".");
		response.sendError(HttpTransportResponse.SC_NOT_FOUND, "Not found: " + request.getRequestPath());
	}

	private static void sendErrorInternalServer(HttpTransportRequest request, HttpTransportResponse response, Exception ex) throws IOException {

		log.error("Internal server error: " + ex.getMessage() + ". Sending " + HttpTransportResponse.SC_NOT_FOUND + ".", ex);
		response.sendError(HttpTransportResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error: " + ex.getMessage());
	}

	/*
	 * Getters and setters
	 */

	public UriMessagingTargetRegistry getUriMessagingTargetRegistry() {

		return this.uriMessagingTargetRegistry;
	}

	public void setUriMessagingTargetRegistry(UriMessagingTargetRegistry uriMessagingTargetRegistry) {

		this.uriMessagingTargetRegistry = uriMessagingTargetRegistry;
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
