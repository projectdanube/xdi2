package xdi2.server;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.support.WebApplicationContextUtils;

import xdi2.core.Graph;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.MimeType;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.iterators.SelectingClassIterator;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.http.AcceptHeader;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.server.interceptor.EndpointServletInterceptor;

/**
 * The XDI endpoint servlet.
 * 
 * It reads and installs all XDI messaging targets from a Spring application context.
 * 
 * @author markus
 *
 */
public final class EndpointServlet extends HttpServlet implements HttpRequestHandler, ApplicationContextAware {

	private static final long serialVersionUID = -5653921904489832762L;

	private static final Logger log = LoggerFactory.getLogger(EndpointServlet.class);

	private static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private EndpointRegistry endpointRegistry;
	private List<Interceptor> interceptors;
	private boolean supportGet, supportPost, supportPut, supportDelete;

	public EndpointServlet() {

		super();

		this.endpointRegistry = null;
		this.interceptors = new ArrayList<Interceptor> ();
		this.supportGet = true;
		this.supportPost = true;
		this.supportPut = true;
		this.supportDelete = true;
	}

	private void initEndpointRegistry(ApplicationContext applicationContext) {

		this.endpointRegistry = new EndpointRegistry();
		this.endpointRegistry.init(applicationContext);
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		this.service(request, response);
	}

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		log.debug("Setting application context.");

		this.applicationContext = applicationContext;
	}

	@Override
	public void init() throws ServletException {

		log.info("Initializing...");
		log.debug("supportGet=" + this.supportGet + ", supportPost=" + this.supportPost + ", supportPut=" + this.supportPut + ", supportDelete=" + this.supportDelete);

		if (this.applicationContext == null) {

			log.debug("Setting application context using servlet context.");

			ServletContext servletContext = this.getServletContext();
			this.applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
		}

		this.initEndpointRegistry(this.applicationContext);

		log.info("Initializing complete.");
	}

	@Override
	public void destroy() {

		log.info("Shutting down.");

		this.endpointRegistry.shutdown();

		super.destroy();

		log.info("Shutting down complete.");
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (! this.supportGet) {

			super.doGet(request, response);
			return;
		}

		log.debug("Incoming GET request. Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			this.processGetRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		log.debug("Successfully processed GET request.");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (! this.supportPost) {

			super.doPost(request, response);
			return;
		}

		log.debug("Incoming POST request. Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			this.processPostRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		log.debug("Successfully processed POST request.");
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (! this.supportPut) {

			super.doPut(request, response);
			return;
		}

		log.debug("Incoming PUT request. Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			this.processPutRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		log.debug("Successfully processed PUT request.");
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		if (! this.supportDelete) {

			super.doDelete(request, response);
			return;
		}

		log.debug("Incoming DELETE request. Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			this.processDeleteRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			handleInternalException(request, response, ex);
			return;
		}

		log.debug("Successfully processed DELETE request.");
	}

	protected void processGetRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// check which messaging target this request applies to

		String path = findPath(request);
		String messagingTargetPath = this.findMessagingTargetPath(request, response, path);
		if (messagingTargetPath == null) return;
		MessagingTarget messagingTarget = this.endpointRegistry.getMessagingTarget(messagingTargetPath);

		// check interceptors

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.getEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (GET).");

			if (endpointServletInterceptor.processGetRequest(request, response, path, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": GET request has been fully handled by interceptor " + endpointServletInterceptor.getClass().getSimpleName() + ".");
				return;
			}
		}

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(request, response, path, messagingTargetPath, XDIMessagingConstants.XRI_S_GET);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
	}

	protected void processPostRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// check which messaging target this request applies to

		String path = findPath(request);
		String messagingTargetPath = this.findMessagingTargetPath(request, response, path);
		if (messagingTargetPath == null) return;
		MessagingTarget messagingTarget = this.endpointRegistry.getMessagingTarget(messagingTargetPath);

		// check interceptors

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.getEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (POST).");

			if (endpointServletInterceptor.processPostRequest(request, response, path, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": POST request has been fully handled by interceptor " + endpointServletInterceptor.getClass().getSimpleName() + ".");
				return;
			}
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

	protected void processPutRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// check which messaging target this request applies to

		String path = findPath(request);
		String messagingTargetPath = this.findMessagingTargetPath(request, response, path);
		if (messagingTargetPath == null) return;
		MessagingTarget messagingTarget = this.endpointRegistry.getMessagingTarget(messagingTargetPath);

		// check interceptors

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.getEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (PUT).");

			if (endpointServletInterceptor.processPutRequest(request, response, path, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": PUT request has been fully handled by interceptor " + endpointServletInterceptor.getClass().getSimpleName() + ".");
				return;
			}
		}
		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(request, response, path, messagingTargetPath, XDIMessagingConstants.XRI_S_ADD);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
	}

	protected void processDeleteRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// check which messaging target this request applies to

		String path = findPath(request);
		String messagingTargetPath = this.findMessagingTargetPath(request, response, path);
		if (messagingTargetPath == null) return;
		MessagingTarget messagingTarget = this.endpointRegistry.getMessagingTarget(messagingTargetPath);

		// check interceptors

		for (Iterator<EndpointServletInterceptor> endpointServletInterceptors = this.getEndpointServletInterceptors(); endpointServletInterceptors.hasNext(); ) {

			EndpointServletInterceptor endpointServletInterceptor = endpointServletInterceptors.next();

			if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": Executing endpoint servlet interceptor " + endpointServletInterceptor.getClass().getSimpleName() + " (DELETE).");

			if (endpointServletInterceptor.processDeleteRequest(request, response, path, messagingTarget)) {

				if (log.isDebugEnabled()) log.debug(this.getClass().getSimpleName() + ": DELETE request has been fully handled by interceptor " + endpointServletInterceptor.getClass().getSimpleName() + ".");
				return;
			}
		}

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = readFromUrl(request, response, path, messagingTargetPath, XDIMessagingConstants.XRI_S_DEL);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		sendResult(messageResult, request, response);
	}

	protected static String findPath(HttpServletRequest request) {

		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath(); 
		String servletPath = request.getServletPath();
		String path = requestUri.substring(contextPath.length() + servletPath.length());
		if (path.startsWith("/")) path = path.substring(1);

		log.debug("requestUri: " + requestUri);
		log.debug("contextPath: " + contextPath);
		log.debug("servletPath: " + servletPath);
		log.debug("path: " + path);

		return path;
	}

	protected String findMessagingTargetPath(HttpServletRequest request, HttpServletResponse response, String path) throws IOException {

		// check which messaging target this request applies to

		String messagingTargetPath = this.endpointRegistry.findMessagingTargetPath(path);

		if (messagingTargetPath == null) {

			log.warn("No XDI messaging target configured at " + path + ". Sending " + HttpServletResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + path);
			return null;
		}

		return messagingTargetPath;
	}

	private static MessageEnvelope readFromUrl(HttpServletRequest request, HttpServletResponse response, String path, String messagingTargetPath, XRI3Segment operationXri) throws IOException {

		// parse an XDI address from the request path

		String addr = path.substring(messagingTargetPath.length());
		while (addr.length() > 0 && addr.charAt(0) == '/') addr = addr.substring(1);

		log.debug("XDI address: " + addr);

		XRI3Segment contextNodeXri;

		if (addr.equals("")) {

			contextNodeXri = null;
		} else {

			try {

				contextNodeXri = new XRI3Segment(addr);
			} catch (Exception ex) {

				log.error("Cannot parse XDI address: " + ex.getMessage(), ex);
				handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
				return null;
			}
		}

		// convert address to a mini messaging envelope

		log.debug("Requested XDI context node: " + contextNodeXri + ".");

		MessageEnvelope messageEnvelope = MessageEnvelope.fromOperationXriAndTargetXri(XDIMessagingConstants.XRI_S_GET, contextNodeXri);

		return messageEnvelope;
	}

	private static MessageEnvelope readFromBody(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// try to find an appropriate reader for the provided mime type

		XDIReader reader = null;

		String contentType = request.getContentType();
		MimeType recvMimeType = contentType != null ? new MimeType(contentType) : null;
		reader = recvMimeType != null ? XDIReaderRegistry.forMimeType(recvMimeType) : null;

		if (reader == null) reader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		log.debug("Reading message in " + recvMimeType + " with reader " + reader.getClass().getSimpleName() + ".");

		Graph graph = graphFactory.openGraph();
		MessageEnvelope messageEnvelope;
		int messageCount;

		try {

			InputStream inputStream = request.getInputStream();

			reader.read(graph, inputStream, null);
			messageEnvelope = MessageEnvelope.fromGraph(graph);
			messageCount = messageEnvelope.getMessageCount();
		} catch (Exception ex) {

			log.error("Cannot parse XDI graph: " + ex.getMessage(), ex);
			handleException(request, response, new Exception("Cannot parse XDI graph: " + ex.getMessage(), ex));
			return null;
		}

		log.debug("Message envelope received (" + messageCount + " messages). Executing...");

		return messageEnvelope;
	}

	private MessageResult execute(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, HttpServletRequest request, HttpServletResponse response) throws IOException {

		// create an execution context

		ExecutionContext executionContext = new ExecutionContext();

		ServletExecutionContext.putEndpointServlet(executionContext, this);
		ServletExecutionContext.putHttpServletRequest(executionContext, request);
		ServletExecutionContext.putHttpServletResponse(executionContext, response);

		// execute the messages and operations against our message target, save result

		MessageResult messageResult = new MessageResult();

		try {

			if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));
			messagingTarget.execute(messageEnvelope, messageResult, executionContext);
			if (log.isDebugEnabled()) log.debug("MessageResult: " + messageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));
		} catch (Exception ex) {

			log.error("Exception: " + ex.getMessage(), ex);
			handleException(request, response, ex);
			return null;
		}

		log.debug("Message(s) successfully executed (" + messageResult.getGraph().getRootContextNode().getAllStatementCount() + " results).");

		return messageResult;
	}

	private static void sendResult(MessageResult messageResult, HttpServletRequest request, HttpServletResponse response) throws IOException {

		// find a suitable writer based on accept headers

		log.debug("Accept: " + request.getHeader("Accept"));

		XDIWriter writer = null;

		String acceptHeader = request.getHeader("Accept");
		MimeType sendMimeType = acceptHeader != null ? AcceptHeader.parse(acceptHeader).bestMimeType() : null;
		writer = sendMimeType != null ? XDIWriterRegistry.forMimeType(sendMimeType) : null;

		if (writer == null) writer = XDIWriterRegistry.getDefault();

		// send out the message result

		log.debug("Sending result in " + sendMimeType + " with writer " + writer.getClass().getSimpleName() + ".");

		OutputStream outputStream = response.getOutputStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		writer.write(messageResult.getGraph(), buffer, null);
		response.setContentType(writer.getMimeTypes()[0].toString());
		response.setContentLength(buffer.size());

		if (buffer.size() > 0) {

			outputStream.write(buffer.toByteArray());
			outputStream.flush();
		}

		outputStream.close();

		log.debug("Output complete.");
	}

	protected static void handleInternalException(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {

		if (! response.isCommitted()) {

			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception: " + ex.getMessage());
		}
	}

	protected static void handleException(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {

		// send error result

		ErrorMessageResult errorMessageResult = ErrorMessageResult.fromException(ex);

		if (log.isDebugEnabled()) log.debug("ErrorMessageResult: " + errorMessageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));

		sendResult(errorMessageResult, request, response);
	}

	/*
	 * Getters and setters
	 */

	public EndpointRegistry getEndpointRegistry() {

		return this.endpointRegistry;
	}

	public void setEndpointRegistry(EndpointRegistry endpointRegistry) {

		this.endpointRegistry = endpointRegistry;
	}

	public List<Interceptor> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(List<Interceptor> interceptors) {

		this.interceptors = interceptors;
	}

	public Iterator<EndpointServletInterceptor> getEndpointServletInterceptors() {

		return new SelectingClassIterator<Interceptor, EndpointServletInterceptor> (this.interceptors.iterator(), EndpointServletInterceptor.class);
	}

	public boolean getSupportGet() {

		return this.supportGet;
	}

	public void setSupportGet(boolean supportGet) {

		this.supportGet = supportGet;
	}

	public boolean getSupportPost() {

		return this.supportPost;
	}

	public void setSupportPost(boolean supportPost) {

		this.supportPost = supportPost;
	}

	public boolean getSupportPut() {

		return this.supportPut;
	}

	public void setSupportPut(boolean supportPut) {

		this.supportPut = supportPut;
	}

	public boolean getSupportDelete() {

		return this.supportDelete;
	}

	public void setSupportDelete(boolean supportDelete) {

		this.supportDelete = supportDelete;
	}
}
