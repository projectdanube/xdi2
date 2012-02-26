package xdi2.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
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
import xdi2.core.exceptions.Xdi2ParseException;
import xdi2.core.impl.memory.MemoryGraphFactory;
import xdi2.core.io.XDIReader;
import xdi2.core.io.XDIReaderRegistry;
import xdi2.core.io.XDIWriter;
import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.xri3.impl.XRI3Segment;
import xdi2.core.xri3.impl.parser.ParserException;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.http.AcceptHeader;
import xdi2.messaging.http.AcceptHeader.AcceptEntry;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.util.XDIMessagingConstants;

/**
 * The XDI endpoint servlet.
 * 
 * It reads and installs all XDI messaging targets from a Spring application context.
 * 
 * @author markus
 *
 */
public class EndpointServlet extends HttpServlet implements HttpRequestHandler, ApplicationContextAware {

	private static final long serialVersionUID = -5653921904489832762L;

	private static final Logger log = LoggerFactory.getLogger(EndpointServlet.class);

	private static final MemoryGraphFactory graphFactory = MemoryGraphFactory.getInstance();

	private EndpointRegistry endpointRegistry;

	public EndpointServlet() {

		super();
	}

	public EndpointRegistry getEndpointRegistry() {

		return(this.endpointRegistry);
	}

	private void initEndpointRegistry(ApplicationContext applicationContext) {

		log.info("Initializing...");

		this.endpointRegistry = new EndpointRegistry();
		this.endpointRegistry.init(applicationContext);

		log.info("Initializing complete.");
	}

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		this.service(request, response);
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		this.initEndpointRegistry(applicationContext);
	}

	@Override
	public void init(ServletConfig servletConfig) throws ServletException {

		super.init(servletConfig);

		ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletConfig.getServletContext());
		this.initEndpointRegistry(applicationContext);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.debug("Incoming GET request. Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			this.processGetRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			if (! response.isCommitted()) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception: " + ex.getMessage());
			return;
		}

		log.debug("Successfully processed GET request.");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.debug("Incoming POST request. Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			this.processPostRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			if (! response.isCommitted()) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception: " + ex.getMessage());
			return;
		}

		log.debug("Successfully processed POST request.");
	}

	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.debug("Incoming PUT request. Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			this.processPutRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			if (! response.isCommitted()) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception: " + ex.getMessage());
			return;
		}

		log.debug("Successfully processed PUT request.");
	}

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		log.debug("Incoming DELETE request. Content-Type: " + request.getContentType() + ", Content-Length: " + request.getContentLength());

		try {

			this.processDeleteRequest(request, response);
		} catch (Exception ex) {

			log.error("Unexpected exception: " + ex.getMessage(), ex);
			if (! response.isCommitted()) response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected exception: " + ex.getMessage());
			return;
		}

		log.debug("Successfully processed DELETE request.");
	}

	@Override
	public void destroy() {

		log.debug("Shutting down.");

		this.endpointRegistry.shutdown();

		super.destroy();
	}

	protected void processGetRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// check which messaging target this request applies to

		String messagingTargetPath = this.findMessagingTargetPath(request, response);
		if (messagingTargetPath == null) return;
		MessagingTarget messagingTarget = this.endpointRegistry.getMessagingTarget(messagingTargetPath);

		// prepare messaging target

		this.prepareMessagingTarget(messagingTarget, request, response);

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = this.readFromUrl(messagingTargetPath, XDIMessagingConstants.XRI_S_GET, request, response);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		this.sendResult(messageResult, request, response);
	}

	protected void processPutRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// check which messaging target this request applies to

		String messagingTargetPath = this.findMessagingTargetPath(request, response);
		if (messagingTargetPath == null) return;
		MessagingTarget messagingTarget = this.endpointRegistry.getMessagingTarget(messagingTargetPath);

		// prepare messaging target

		this.prepareMessagingTarget(messagingTarget, request, response);

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = this.readFromUrl(messagingTargetPath, XDIMessagingConstants.XRI_S_ADD, request, response);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		this.sendResult(messageResult, request, response);
	}

	protected void processDeleteRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// check which messaging target this request applies to

		String messagingTargetPath = this.findMessagingTargetPath(request, response);
		if (messagingTargetPath == null) return;
		MessagingTarget messagingTarget = this.endpointRegistry.getMessagingTarget(messagingTargetPath);

		// prepare messaging target

		this.prepareMessagingTarget(messagingTarget, request, response);

		// construct message envelope from url 

		MessageEnvelope messageEnvelope = this.readFromUrl(messagingTargetPath, XDIMessagingConstants.XRI_S_DEL, request, response);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		this.sendResult(messageResult, request, response);
	}

	protected void processPostRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// check which messaging target this request applies to

		String messagingTargetPath = this.findMessagingTargetPath(request, response);
		if (messagingTargetPath == null) return;
		MessagingTarget messagingTarget = this.endpointRegistry.getMessagingTarget(messagingTargetPath);

		// prepare messaging target

		this.prepareMessagingTarget(messagingTarget, request, response);

		// construct message envelope from body

		MessageEnvelope messageEnvelope = this.readFromBody(request, response);
		if (messageEnvelope == null) return;

		// execute the message envelope against our message target, save result

		MessageResult messageResult = this.execute(messageEnvelope, messagingTarget, request, response);
		if (messageResult == null || messageResult.getGraph() == null) return;

		// send out result

		this.sendResult(messageResult, request, response);
	}

	private String findMessagingTargetPath(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// check which messaging target this request applies to

		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath(); 
		String servletPath = request.getServletPath();
		String path = requestUri.substring(contextPath.length() + servletPath.length());
		if (path.startsWith("/")) path = path.substring(1);

		log.debug("requestUri: " + requestUri);
		log.debug("contextPath: " + contextPath);
		log.debug("servletPath: " + servletPath);
		log.debug("path: " + path);
		
		String messagingTargetPath = this.endpointRegistry.findMessagingTargetPath(path);

		if (messagingTargetPath == null) {

			log.warn("No XDI messaging target configured at " + path + ". Sending " + HttpServletResponse.SC_NOT_FOUND + ".");
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "No XDI messaging target configured at " + path);
			return(null);
		}

		return(messagingTargetPath);
	}

	private void prepareMessagingTarget(MessagingTarget messagingTarget, HttpServletRequest request, HttpServletResponse response) {

		// anything needed to prepare the messaging target?
	}

	private MessageEnvelope readFromUrl(String messagingTargetPath, XRI3Segment operationXri, HttpServletRequest request, HttpServletResponse response) throws IOException {

		// parse an XDI address from the request path

		String requestUri = request.getRequestURI();
		String contextPath = request.getContextPath(); 
		String servletPath = request.getServletPath();
		String path = requestUri.substring(contextPath.length() + servletPath.length());
		if (path.startsWith("/")) path = path.substring(1);
		
		String addr = path.substring(messagingTargetPath.length());
		while (addr.length() > 0 && addr.charAt(0) == '/') addr = addr.substring(1);

		log.debug("requestUri: " + requestUri);
		log.debug("contextPath: " + contextPath);
		log.debug("servletPath: " + servletPath);
		log.debug("path: " + path);
		log.debug("addr: " + addr);

		XRI3Segment contextNodeXri;

		if (addr.equals("")) {

			contextNodeXri = null;
		} else {

			try {

				contextNodeXri = new XRI3Segment(addr);
			} catch (ParserException ex) {

				log.warn("Cannot parse XDI address. Sending " + HttpServletResponse.SC_BAD_REQUEST + ".", ex);
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot parse XDI address: " + ex.getMessage());
				return(null);
			}
		}

		// convert address to a mini messaging envelope

		log.debug("Requested XDI context node: " + contextNodeXri + ".");

		MessageEnvelope messageEnvelope = MessageEnvelope.fromXriAndOperationXri(contextNodeXri, XDIMessagingConstants.XRI_S_GET);

		return messageEnvelope;
	}

	private MessageEnvelope readFromBody(HttpServletRequest request, HttpServletResponse response) throws IOException {

		// try to find an appropriate reader for the provided mime type

		XDIReader reader = null;

		String inputMimeType = request.getContentType();
		reader = XDIReaderRegistry.forMimeType(inputMimeType);

		if (reader == null) reader = XDIReaderRegistry.getDefault();

		// read everything into an in-memory XDI graph (a message envelope)

		log.debug("Reading message in " + reader.getFormat() + " format.");

		Graph graph = graphFactory.openGraph();
		MessageEnvelope messageEnvelope;
		int messageCount;

		try {

			InputStream inputStream = request.getInputStream();

			reader.read(graph, inputStream, null);
			messageEnvelope = MessageEnvelope.fromGraph(graph);
			messageCount = messageEnvelope.getMessageCount();
		} catch (Xdi2ParseException ex) {

			log.warn("Cannot parse XDI graph. Sending " + HttpServletResponse.SC_BAD_REQUEST + ".", ex);
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot parse XDI graph: " + ex.getMessage());
			return(null);
		} catch (Exception ex) {

			log.error("Cannot read message envelope. Sending " + HttpServletResponse.SC_INTERNAL_SERVER_ERROR + ".", ex);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cannot read message envelope: " + ex.getMessage());
			return(null);
		}

		log.debug("Message envelope received (" + messageCount + " messages). Executing...");

		return(messageEnvelope);
	}

	private MessageResult execute(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, HttpServletRequest request, HttpServletResponse response) throws IOException {

		// create an execution context

		ExecutionContext executionContext = new ExecutionContext();

		ServletExecutionContext.setEndpointServlet(executionContext, this);
		ServletExecutionContext.setHttpServletRequest(executionContext, request);
		ServletExecutionContext.setHttpServletResponse(executionContext, response);

		// execute the messages and operations against our message target, save result

		MessageResult messageResult = MessageResult.newInstance();

		try {

			if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));
			messagingTarget.execute(messageEnvelope, messageResult, executionContext);
			if (log.isDebugEnabled()) log.debug("MessageResult: " + messageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat()));
		} catch (Exception ex) {

			log.error("Cannot execute message envelope. Sending error document: " + ex.getMessage(), ex);
			this.sendErrorResult(request, response, ex);
			return(null);
		}

		log.debug("Message(s) successfully executed (" + messageResult.getGraph().getRootContextNode().getAllStatementCount() + " results).");

		return(messageResult);
	}

	private void sendResult(MessageResult messageResult, HttpServletRequest request, HttpServletResponse response) throws IOException {

		// find a suitable writer based on accept headers

		XDIWriter writer = null;

		AcceptHeader acceptHeader = new AcceptHeader(request.getHeader("Accept"));

		for (AcceptEntry entry : acceptHeader.getEntries()) {

			if ((writer = XDIWriterRegistry.forMimeType(entry.getMimeType())) != null) break;
		}

		if (writer == null) writer = XDIWriterRegistry.getDefault();

		// send out the message result

		log.debug("Sending result in " + writer.getFormat() + " format.");

		OutputStream outputStream = response.getOutputStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		writer.write(messageResult.getGraph(), buffer, null);
		response.setContentType(writer.getMimeTypes()[0]);
		response.setContentLength(buffer.size());

		if (buffer.size() > 0) {

			outputStream.write(buffer.toByteArray());
			outputStream.flush();
		}

		outputStream.close();

		log.debug("Output complete.");
	}

	private void sendErrorResult(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {

		// make an error result

		Integer errorCode = new Integer(0);

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getName();

		ErrorMessageResult errorMessageResult = ErrorMessageResult.newInstance();
		errorMessageResult.setErrorCode(errorCode);
		errorMessageResult.setErrorString(errorString);

		// and send it

		log.debug("Sending error result: " + errorCode + " (" + errorString + ")");

		this.sendResult(errorMessageResult, request, response);
	}
}
