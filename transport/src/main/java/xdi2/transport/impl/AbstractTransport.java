package xdi2.transport.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.properties.XDI2Properties;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.constants.XDIMessagingConstants;
import xdi2.messaging.response.TransportMessagingResponse;
import xdi2.messaging.target.Extension;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.messaging.target.interceptor.InterceptorList;
import xdi2.transport.Transport;
import xdi2.transport.TransportRequest;
import xdi2.transport.TransportResponse;
import xdi2.transport.exceptions.Xdi2TransportException;

public abstract class AbstractTransport <REQUEST extends TransportRequest, RESPONSE extends TransportResponse> implements Transport<REQUEST, RESPONSE> {

	private static final Logger log = LoggerFactory.getLogger(AbstractTransport.class);

	private static String VERSION;

	private boolean initialized;
	private Date startup;

	private InterceptorList<Transport<?, ?>> interceptors;

	static {

		VERSION = "XDI2 Version: " + XDI2Properties.properties.get("project.version") + " (" + XDI2Properties.properties.get("project.build.timestamp") + "). ";
		VERSION += "Git Commit: " + XDI2Properties.properties.get("git.branch") + " " + XDI2Properties.properties.get("git.commit.id") + " (" + XDI2Properties.properties.get("git.commit.time") + ").";
	}

	protected AbstractTransport() {

		this.interceptors = new InterceptorList<Transport<?, ?>> ();

		this.initialized = false;
		this.startup = null;
	}

	@Override
	public void init() throws Exception {

		if (this.isInitialized()) {

			if (log.isDebugEnabled()) log.debug("Already initialized.");
			return;
		}

		log.info("Initializing...");

		// init interceptors

		List<Extension<Transport<?, ?>>> extensions = new ArrayList<Extension<Transport<?, ?>>> ();
		extensions.addAll(new IteratorListMaker<Interceptor<Transport<?, ?>>> (this.getInterceptors().iterator()).list());

		Collections.sort(extensions, new Extension.InitPriorityComparator());

		for (Extension<Transport<?, ?>> extension : extensions) {

			if (log.isDebugEnabled()) log.debug("Initializing interceptor " + extension.getClass().getSimpleName() + ".");

			extension.init(this);
		}

		// remember startup time

		this.startup = new Date();

		// done

		this.initialized = true;

		log.info("Initializing complete.");
	}

	@Override
	public void shutdown() throws Exception {

		if (! this.isInitialized()) {

			if (log.isDebugEnabled()) log.debug("Not initialized.");
			return;
		}

		log.info("Shutting down.");

		// shutdown interceptors and contributors

		List<Extension<Transport<?, ?>>> extensions = new ArrayList<Extension<Transport<?, ?>>> ();
		extensions.addAll(new IteratorListMaker<Interceptor<Transport<?, ?>>> (this.getInterceptors().iterator()).list());

		Collections.sort(extensions, new Extension.ShutdownPriorityComparator());

		for (Extension<Transport<?, ?>> extension : extensions) {

			if (log.isDebugEnabled()) log.debug("Shutting down interceptor " + extension.getClass().getSimpleName() + ".");

			extension.shutdown(this);
		}

		// done

		this.initialized = false;

		log.info("Shutting down complete.");
	}

	protected TransportMessagingResponse execute(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, REQUEST request, RESPONSE response) throws Xdi2TransportException {

		// create an execution context and execution result

		final ExecutionContext executionContext = this.createExecutionContext(request, response);
		final ExecutionResult executionResult = ExecutionResult.createExecutionResult(messageEnvelope);

		// execution result and messaging response

		TransportMessagingResponse messagingResponse;

		// go

		try {

			// execute interceptors (before)

			InterceptorExecutor.executeTransportInterceptorsBefore(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, executionContext);

			// execute the message envelope against the messaging target

			if (log.isDebugEnabled()) log.debug("" + this.getClass().getSimpleName() + ": We are running: " + VERSION);
			if (log.isDebugEnabled()) log.debug("" + this.getClass().getSimpleName() + ": MessageEnvelope: " + messageEnvelope);
			messagingTarget.execute(messageEnvelope, executionContext, executionResult);
			if (log.isDebugEnabled()) log.debug("" + this.getClass().getSimpleName() + ": ExecutionResult: " + executionResult);

			// make messaging response

			messagingResponse = this.makeMessagingResponse(messageEnvelope, messagingTarget, executionResult);

			// execute interceptors (after)

			InterceptorExecutor.executeTransportInterceptorsAfter(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, messagingResponse, executionContext);
		} catch (Exception ex) {

			log.error("Exception while executing message envelope: " + ex.getMessage(), ex);

			// make messaging response

			messagingResponse = this.makeMessagingResponse(messageEnvelope, messagingTarget, executionResult);

			// execute interceptors (exception)

			InterceptorExecutor.executeTransportInterceptorsException(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, messagingResponse, ex, executionContext);
		}

		// done

		if (log.isDebugEnabled()) log.debug(messagingResponse.getClass().getSimpleName() + ": " + messagingResponse);

		return messagingResponse;
	}

	@Override
	public ExecutionContext createExecutionContext(REQUEST request, RESPONSE response) {

		ExecutionContext executionContext = ExecutionContext.createExecutionContext();

		AbstractTransport.putTransport(executionContext, this);
		AbstractTransport.putRequest(executionContext, request);
		AbstractTransport.putResponse(executionContext, response);

		return executionContext;
	}

	private final TransportMessagingResponse makeMessagingResponse(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, ExecutionResult executionResult) throws Xdi2TransportException {

		TransportMessagingResponse messagingResponse;

		if (isFull(messageEnvelope)) {

			messagingResponse = executionResult.makeFullMessagingResponse(messageEnvelope, messagingTarget);
		} else {

			messagingResponse = executionResult.makeLightMessagingResponse();
		}

		return messagingResponse;
	}

	/*
	 * Getters and setters
	 */

	public Date getCurrent() {

		return new Date();
	}

	public String getCurrentAsString() {

		return new SimpleDateFormat().format(this.getCurrent());
	}

	public Date getStartup() {

		return this.startup;
	}

	public String getStartupAsString() {

		return new SimpleDateFormat().format(this.getStartup());
	}

	public long getStartupAsSeconds() {

		return (new Date().getTime() - this.getStartup().getTime()) / 1000;
	}

	public boolean isInitialized() {

		return this.initialized;
	}

	public InterceptorList<Transport<?, ?>> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(InterceptorList<Transport<?, ?>> interceptors) {

		this.interceptors = interceptors;
	}

	/*
	 * Helper methods
	 */

	public static boolean isFull(MessageEnvelope messageEnvelope) {

		for (Message message : messageEnvelope.getMessages()) {

			Boolean full = message.getParameterBoolean(XDIMessagingConstants.XDI_ADD_MESSAGE_PARAMETER_FULL);
			if (Boolean.TRUE.equals(full)) return true;
		}

		return false;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_TRANSPORT = AbstractTransport.class.getCanonicalName() + "#transport";
	private static final String EXECUTIONCONTEXT_KEY_REQUEST = AbstractTransport.class.getCanonicalName() + "#request";
	private static final String EXECUTIONCONTEXT_KEY_RESPONSE = AbstractTransport.class.getCanonicalName() + "#response";

	public static Transport<?, ?> getTransport(ExecutionContext executionContext) {

		return (Transport<?, ?>) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_TRANSPORT);
	}

	public static void putTransport(ExecutionContext executionContext, Transport<?, ?> transport) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_TRANSPORT, transport);
	}

	public static TransportRequest getRequest(ExecutionContext executionContext) {

		return (TransportRequest) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_REQUEST);
	}

	public static void putRequest(ExecutionContext executionContext, TransportRequest request) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_REQUEST, request);
	}

	public static TransportResponse getResponse(ExecutionContext executionContext) {

		return (TransportResponse) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_RESPONSE);
	}

	public static void putResponse(ExecutionContext executionContext, TransportResponse response) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_RESPONSE, response);
	}
}
