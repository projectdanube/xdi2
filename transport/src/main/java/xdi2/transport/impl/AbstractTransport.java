package xdi2.transport.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.io.XDIWriterRegistry;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.error.ErrorMessageResult;
import xdi2.messaging.target.Extension;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.interceptor.Interceptor;
import xdi2.messaging.target.interceptor.InterceptorList;
import xdi2.transport.Request;
import xdi2.transport.Response;
import xdi2.transport.Transport;
import xdi2.transport.exceptions.Xdi2TransportException;

public abstract class AbstractTransport <REQUEST extends Request, RESPONSE extends Response> implements Transport<REQUEST, RESPONSE> {

	private static final Logger log = LoggerFactory.getLogger(AbstractTransport.class);

	private InterceptorList<Transport<?, ?>> interceptors;

	private boolean initialized;
	private Date startup;

	protected AbstractTransport() {

		this.interceptors = new InterceptorList<Transport<?, ?>> ();

		this.initialized = false;
		this.startup = null;
	}

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

	protected MessageResult execute(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, REQUEST request, RESPONSE response) throws Xdi2TransportException, IOException {

		// create an execution context

		ExecutionContext executionContext = this.createExecutionContext(request, response);

		// create a message result

		MessageResult messageResult = new MessageResult();

		// go

		try {

			// execute interceptors (before)

			InterceptorExecutor.executeTransportInterceptorsBefore(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, messageResult, executionContext);

			// execute the message envelope against the messaging target

			if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));
			messagingTarget.execute(messageEnvelope, messageResult, executionContext);
			if (log.isDebugEnabled()) log.debug("MessageResult: " + messageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));

			// execute interceptors (after)

			InterceptorExecutor.executeTransportInterceptorsAfter(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, messageResult, executionContext);
		} catch (Exception ex) {

			log.error("Exception while executing message envelope: " + ex.getMessage(), ex);
			ErrorMessageResult errorMessageResult = this.handleException(request, response, ex);

			// execute interceptors (exception)

			InterceptorExecutor.executeTransportInterceptorsException(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, errorMessageResult, executionContext, ex);

			return null;
		}

		// done

		if (log.isDebugEnabled()) log.debug("Message(s) successfully executed (" + messageResult.getGraph().getRootContextNode().getAllStatementCount() + " results).");

		return messageResult;
	}

	@Override
	public ExecutionContext createExecutionContext(REQUEST request, RESPONSE response) {

		ExecutionContext executionContext = new ExecutionContext();

		AbstractTransport.putTransport(executionContext, this);
		AbstractTransport.putRequest(executionContext, request);
		AbstractTransport.putResponse(executionContext, response);

		return executionContext;
	}

	protected final ErrorMessageResult handleException(REQUEST request, RESPONSE response, Exception ex) throws IOException {

		// send error result

		ErrorMessageResult errorMessageResult = ErrorMessageResult.fromException(ex);

		if (log.isDebugEnabled()) log.debug("ErrorMessageResult: " + errorMessageResult.getGraph().toString(XDIWriterRegistry.getDefault().getFormat(), null));

		this.handleException(request, response, errorMessageResult);

		return errorMessageResult;
	}

	protected abstract void handleException(REQUEST request, RESPONSE response, ErrorMessageResult errorMessageResult) throws IOException;

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

	/*
	 * Getters and setters
	 */

	public InterceptorList<Transport<?, ?>> getInterceptors() {

		return this.interceptors;
	}

	public void setInterceptors(InterceptorList<Transport<?, ?>> interceptors) {

		this.interceptors = interceptors;
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

	public static Request getRequest(ExecutionContext executionContext) {

		return (Request) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_REQUEST);
	}

	public static void putRequest(ExecutionContext executionContext, Request request) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_REQUEST, request);
	}

	public static Response getResponse(ExecutionContext executionContext) {

		return (Response) executionContext.getExecutionContextAttribute(EXECUTIONCONTEXT_KEY_RESPONSE);
	}

	public static void putResponse(ExecutionContext executionContext, Response response) {

		executionContext.putExecutionContextAttribute(EXECUTIONCONTEXT_KEY_RESPONSE, response);
	}
}
