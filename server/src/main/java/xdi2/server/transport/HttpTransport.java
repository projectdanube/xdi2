package xdi2.server.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.XDI3Segment;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.http.AcceptHeader;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.server.exceptions.Xdi2ServerException;
import xdi2.server.registry.HttpMessagingTargetRegistry;

public class HttpTransport {

	private static final Logger log = LoggerFactory.getLogger(HttpTransport.class);

	private static final String[] HEADER_ALLOW = new String[] { "Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS" };
	private static final String[][] HEADERS_CORS = new String[][] {
		new String[] { "Access-Control-Allow-Origin", "*" },
		new String[] { "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept" },
		new String[] { "Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS" }
	};

	private HttpMessagingTargetRegistry httpMessagingTargetRegistry;
	private InterceptorList interceptors;

	private boolean initialized;
	private Date startup;

	public HttpTransport(HttpMessagingTargetRegistry httpMessagingTargetRegistry) {

		this.httpMessagingTargetRegistry = httpMessagingTargetRegistry;
		this.interceptors = new InterceptorList();
		this.initialized = false;
		this.startup = null;
	}

	public HttpTransport() {

		this(null);
	}

	public void init() throws Xdi2ServerException {

		if (this.isInitialized()) {

			if (log.isDebugEnabled()) log.debug("Already initialized.");
			return;
		}

		log.info("Initializing...");

		// execute interceptors

		this.getInterceptors().executeHttpTransportInterceptorsInit(this);

		// remember startup time

		this.startup = new Date();

		// done

		this.initialized = true;

		log.info("Initializing complete.");
	}

	public void shutdown() {

		if (! this.isInitialized()) {

			if (log.isDebugEnabled()) log.debug("Not initialized.");
			return;
		}

		log.info("Shutting down.");

		// execute interceptors

		this.getInterceptors().executeHttpTransportInterceptorsDestroy(this);

		// done

		this.initialized = false;

		log.info("Shutting down complete.");
	}

	public void doGet(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isDebugEnabled()) log.debug("Incoming GET request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			request.lookup(this.getHttpMessagingTargetRegistry());
			this.processGetRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed GET request.");
	}

	public void doPost(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isDebugEnabled()) log.debug("Incoming POST request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			request.lookup(this.getHttpMessagingTargetRegistry());
			this.processPostRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed POST request.");
	}

	public void doPut(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isDebugEnabled()) log.debug("Incoming PUT request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			request.lookup(this.getHttpMessagingTargetRegistry());
			this.processPutRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed PUT request.");
	}

	public void doDelete(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isDebugEnabled()) log.debug("Incoming DELETE request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			request.lookup(this.getHttpMessagingTargetRegistry());
			this.processDeleteRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed DELETE request.");
	}

	public void doOptions(HttpRequest request, HttpResponse response) throws IOException {

		if (log.isDebugEnabled()) log.debug("Incoming OPTIONS request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			response.setStatus(HttpResponse.SC_OK);
			response.setHeader(HEADER_ALLOW[0], HEADER_ALLOW[1]);
			for (String[] HEADER_CORS : HEADERS_CORS) response.setHeader(HEADER_CORS[0], HEADER_CORS[1]);
			response.setContentLength(0);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		if (log.isDebugEnabled()) log.debug("Successfully processed OPTIONS request.");
	}

	protected void processGetRequest(HttpRequest request, HttpResponse response) throws Xdi2ServerException, IOException {

		MessagingTarget messagingTarget = request.getMessagingTarget();

		// execute interceptors

		if (this.getInterceptors().executeHttpTransportInterceptorsGet(this, request, response, messagingTarget)) return;

		// no messaging target?

		if (messagingTarget == null) {

			log.warn("No XDI messaging target configured at " + request.getRequestPath() + ". Sending " + HttpResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + request.getRequestPath());

			return;
		}

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(request, response, messagingTarget, XDIMessagingConstants.XRI_S_GET);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
	}

	protected void processPostRequest(HttpRequest request, HttpResponse response) throws Xdi2ServerException, IOException {

		MessagingTarget messagingTarget = request.getMessagingTarget();

		// execute interceptors

		if (this.getInterceptors().executeHttpTransportInterceptorsPost(this, request, response, messagingTarget)) return;

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

		sendResult(messageResult, request, response);
	}

	protected void processPutRequest(HttpRequest request, HttpResponse response) throws Xdi2ServerException, IOException {

		MessagingTarget messagingTarget = request.getMessagingTarget();

		// execute interceptors

		if (this.getInterceptors().executeHttpTransportInterceptorsPut(this, request, response, messagingTarget)) return;

		// no messaging target?

		if (messagingTarget == null) {

			log.warn("No XDI messaging target configured at " + request.getRequestPath() + ". Sending " + HttpResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + request.getRequestPath());

			return;
		}

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(request, response, messagingTarget, XDIMessagingConstants.XRI_S_SET);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
	}

	protected void processDeleteRequest(HttpRequest request, HttpResponse response) throws Xdi2ServerException, IOException {

		MessagingTarget messagingTarget = request.getMessagingTarget();

		// execute interceptors

		if (this.getInterceptors().executeHttpTransportInterceptorsDelete(this, request, response, messagingTarget)) return;

		// no messaging target?

		if (messagingTarget == null) {

			log.warn("No XDI messaging target configured at " + request.getRequestPath() + ". Sending " + HttpResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + request.getRequestPath());

			return;
		}

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(request, response, messagingTarget, XDIMessagingConstants.XRI_S_DEL);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
	}

	private static MessageEnvelope readFromUrl(HttpRequest request, HttpResponse response, MessagingTarget messagingTarget, XDI3Segment operationXri) throws IOException {

		// parse an XDI address from the request path

		String addr = request.getRequestPath().substring(request.getMessagingTargetPath().length());
		while (addr.length() > 0 && addr.charAt(0) == '/') addr = addr.substring(1);

		if (log.isDebugEnabled()) log.debug("XDI address: " + addr);

		XDI3Segment targetAddress;

		if (addr.equals("")) {

			targetAddress = null;
		} else {

			try {

				targetAddress = XDI3Segment.create(addr);
			} catch (Exception ex) {

				log.error("Cannot parse XDI address: " + ex.getMessage(), ex);
				handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
				return null;
			}
		}

		// convert address to a mini messaging envelope

		if (log.isDebugEnabled()) log.debug("Requested XDI context node: " + targetAddress + ".");

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndTargetAddress(XDIMessagingConstants.XRI_S_GET, targetAddress);

		// set the TO authority to the owner authority of the messaging target

		XDI3Segment ownerAuthority = messagingTarget.getOwnerAuthority();

		if (ownerAuthority != null) {

			Message message = messageEnvelope.getMessages().next();
			message.setToAuthority(ownerAuthority);
		}

		// done

		return messageEnvelope;
	}

	private static MessageEnvelope readFromBody(HttpRequest request, HttpResponse response) throws IOException {

		// try to find an appropriate reader for the provided mime type

		XDIReader reader = null;

		String contentType = request.getContentType();
		MimeType recvMimeType = contentType != null ? new MimeType(contentType) : null;
		reader = recvMimeType != null ? XDIReaderRegistry.forMimeType(recvMimeType) : null;

		if (reader == null) reader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		if (log.isDebugEnabled()) log.debug("Reading message in " + recvMimeType + " with reader " + reader.getClass().getSimpleName() + ".");

		Graph graph = MemoryGraphFactory.getInstance().openGraph();
		MessageEnvelope messageEnvelope;
		long messageCount;

		try {

			InputStream inputStream = request.getBodyInputStream();

			reader.read(graph, inputStream);
			messageEnvelope = MessageEnvelope.fromGraph(graph);
			messageCount = messageEnvelope.getMessageCount();
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
			return null;
		}

		if (log.isDebugEnabled()) log.debug("Message envelope received (" + messageCount + " messages). Executing...");

		return messageEnvelope;
	}

	protected MessageResult execute(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, HttpRequest request, HttpResponse response) throws IOException {

		// create an execution context

		ExecutionContext executionContext = new ExecutionContext();

		putHttpTransport(executionContext, this);
		putHttpRequest(executionContext, request);
		putHttpResponse(executionContext, response);

		// execute the messages and operations against our message target, save result

		MessageResult messageResult = new MessageResult();

		try {

			if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));
			messagingTarget.execute(messageEnvelope, messageResult, executionContext);
			if (log.isDebugEnabled()) log.debug("MessageResult: " + messageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));
		} catch (Exception ex) {

			log.error("Exception while executing message envelope: " + ex.getMessage(), ex);
			handleException(request, response, ex);
			return null;
		}

		if (log.isDebugEnabled()) log.debug("Message(s) successfully executed (" + messageResult.getGraph().getRootContextNode().getAllStatementCount() + " results).");

		return messageResult;
	}

	private static void sendResult(MessageResult messageResult, HttpRequest request, HttpResponse response) throws IOException {

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

		response.setStatus(HttpResponse.SC_OK);
		for (String[] HEADER_CORS : HEADERS_CORS) response.setHeader(HEADER_CORS[0], HEADER_CORS[1]);
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

	private static void handleException(HttpRequest request, HttpResponse response, Exception ex) throws IOException {

		// send error result

		ErrorMessageResult errorMessageResult = ErrorMessageResult.fromException(ex);

		if (log.isDebugEnabled()) log.debug("ErrorMessageResult: " + errorMessageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));

		sendResult(errorMessageResult, request, response);
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_HTTPTRANSPORT = HttpTransport.class.getCanonicalName() + "#httptransport";
	private static final String EXECUTIONCONTEXT_KEY_HTTPREQUEST = HttpTransport.class.getCanonicalName() + "#httprequest";
	private static final String EXECUTIONCONTEXT_KEY_HTTPRESPONSE = HttpTransport.class.getCanonicalName() + "#httpresponse";

	public static HttpTransport getHttpTransport(ExecutionContext executionContext) {

		return (HttpTransport) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPTRANSPORT);
	}

	public static void putHttpTransport(ExecutionContext executionContext, HttpTransport httpTransport) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPTRANSPORT, httpTransport);
	}	

	public static HttpRequest getHttpRequest(ExecutionContext executionContext) {

		return (HttpRequest) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPREQUEST);
	}

	public static void putHttpRequest(ExecutionContext executionContext, HttpRequest request) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPREQUEST, request);
	}	

	public static HttpResponse getHttpResponse(ExecutionContext executionContext) {

		return (HttpResponse) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPRESPONSE);
	}

	public static void putHttpResponse(ExecutionContext executionContext, HttpResponse response) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_HTTPRESPONSE, response);
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

	public InterceptorList getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(InterceptorList interceptors) {

		this.interceptors = interceptors;
	}

	public void setInterceptors(List<Interceptor> interceptors) {

		this.interceptors.clear();
		this.interceptors.addAll(interceptors);
	}

	public Date getStartup() {

		return this.startup;
	}

	public String getStartupAsString() {

		return new SimpleDateFormat().format(this.getStartup());
	}

	public boolean isInitialized() {

		return this.initialized;
	}
}
