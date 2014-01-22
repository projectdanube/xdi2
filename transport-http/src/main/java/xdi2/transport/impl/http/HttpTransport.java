package xdi2.transport.impl.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import xdi2.core.xri3.XDI3SubSegment;
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

	private static final String[] HEADER_ALLOW = new String[] { "Allow", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS" };
	private static final String[][] HEADERS_CORS = new String[][] {
		new String[] { "Access-Control-Allow-Origin", "*" },
		new String[] { "Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept" },
		new String[] { "Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS" }
	};

	private HttpMessagingTargetRegistry httpMessagingTargetRegistry;

	public HttpTransport(HttpMessagingTargetRegistry httpMessagingTargetRegistry) {

		this.httpMessagingTargetRegistry = httpMessagingTargetRegistry;
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

		if (log.isDebugEnabled()) log.debug("Incoming GET request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

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

		if (log.isDebugEnabled()) log.debug("Incoming POST request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

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

		if (log.isDebugEnabled()) log.debug("Incoming PUT request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

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

		if (log.isDebugEnabled()) log.debug("Incoming DELETE request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

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

		if (log.isDebugEnabled()) log.debug("Incoming OPTIONS request to " + request.getRequestPath() + ". Content-Type: " + request.getContentType());

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

		MessageEnvelope messageEnvelope = readFromUrl(messagingTargetMount, request, response, XDIMessagingConstants.XRI_S_GET);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
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

		sendResult(messageResult, request, response);
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

		MessageEnvelope messageEnvelope = readFromUrl(messagingTargetMount, request, response, XDIMessagingConstants.XRI_S_SET);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
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

		MessageEnvelope messageEnvelope = readFromUrl(messagingTargetMount, request, response, XDIMessagingConstants.XRI_S_DEL);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
	}

	private MessageEnvelope readFromUrl(MessagingTargetMount messagingTargetMount, HttpRequest request, HttpResponse response, XDI3Segment operationXri) throws IOException {

		if (messagingTargetMount == null) throw new NullPointerException();

		// parse an XDI address from the request path

		String addr = request.getRequestPath().substring(messagingTargetMount.getMessagingTargetPath().length());
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
				this.handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
				return null;
			}
		}

		// convert address to a mini messaging envelope

		if (log.isDebugEnabled()) log.debug("Requested XDI context node: " + targetAddress + ".");

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndTargetAddress(XDIMessagingConstants.XRI_S_GET, targetAddress);

		// set the TO peer root XRI to the owner peer root XRI of the messaging target

		XDI3SubSegment ownerPeerRootXri = messagingTargetMount.getMessagingTarget().getOwnerPeerRootXri();

		if (ownerPeerRootXri != null) {

			Message message = messageEnvelope.getMessages().next();
			message.setToPeerRootXri(ownerPeerRootXri);
		}

		// done

		return messageEnvelope;
	}

	private MessageEnvelope readFromBody(HttpRequest request, HttpResponse response) throws IOException {

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
			this.handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
			return null;
		}

		if (log.isDebugEnabled()) log.debug("Message envelope received (" + messageCount + " messages). Executing...");

		return messageEnvelope;
	}

	/*
	 * Helper methods
	 */

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

	protected void handleException(HttpRequest request, HttpResponse response, ErrorMessageResult errorMessageResult) throws IOException {

		// send error result

		sendResult(errorMessageResult, request, response);
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
}
