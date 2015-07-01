package xdi2.transport.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.signatures.KeyPairSignature;
import xdi2.core.properties.XDI2Properties;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.iterators.IteratorListMaker;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.response.ErrorMessagingResponse;
import xdi2.messaging.response.MessageEnvelopeMessagingResponse;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.response.ResultGraphMessagingResponse;
import xdi2.messaging.target.Extension;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.contributor.impl.proxy.manipulator.impl.signing.GraphSigner;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
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

	protected MessagingResponse execute(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, REQUEST request, RESPONSE response) throws Xdi2TransportException {

		// create an execution context

		ExecutionContext executionContext = this.createExecutionContext(request, response);

		// create a message result

		MessagingResponse messagingResponse;

		// go

		try {

			// execute interceptors (before)

			InterceptorExecutor.executeTransportInterceptorsBefore(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, executionContext);

			// execute the message envelope against the messaging target

			if (log.isDebugEnabled()) log.debug("We are running: " + VERSION);
			if (log.isDebugEnabled()) log.debug("MessageEnvelope: " + messageEnvelope);
			ExecutionResult executionResult = messagingTarget.execute(messageEnvelope, executionContext);
			if (log.isDebugEnabled()) log.debug("ExecutionResult: " + executionResult);

			// make messaging response

			if (isAsync(messageEnvelope)) {

				messagingResponse = this.makeMessageEnvelopeMessagingResponse(messageEnvelope, messagingTarget, executionResult);
			} else {

				messagingResponse = this.makeResultGraphMessagingResponse(executionResult);
			}

			// execute interceptors (after)

			InterceptorExecutor.executeTransportInterceptorsAfter(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, messagingResponse, executionContext);
		} catch (Exception ex) {

			log.error("Exception while executing message envelope: " + ex.getMessage(), ex);

			// execute interceptors (exception)

			InterceptorExecutor.executeTransportInterceptorsException(this.getInterceptors(), this, request, response, messagingTarget, messageEnvelope, errorMessageResult, executionContext, ex);

			// make messaging response

			messagingResponse = this.makeErrorMessagingResponse(ex);
		}

		// done

		if (log.isDebugEnabled()) log.debug(messagingResponse.getClass().getSimpleName() + ": " + messagingResponse);

		return messagingResponse;
	}

	@Override

	public ExecutionContext createExecutionContext(REQUEST request, RESPONSE response) {
		ExecutionContext executionContext = new ExecutionContext();

		AbstractTransport.putTransport(executionContext, this);
		AbstractTransport.putRequest(executionContext, request);
		AbstractTransport.putResponse(executionContext, response);

		return executionContext;
	}

	protected final ResultGraphMessagingResponse makeResultGraphMessagingResponse(ExecutionResult executionResult) {

		// create messaging response

		ResultGraphMessagingResponse resultGraphMessagingResponse = ResultGraphMessagingResponse.create(executionResult.getResultGraph());

		// done

		return resultGraphMessagingResponse;
	}

	protected final MessageEnvelopeMessagingResponse makeMessageEnvelopeMessagingResponse(MessageEnvelope messageEnvelope, MessagingTarget messagingTarget, ExecutionResult executionResult) throws Xdi2TransportException {

		// create messaging response

		MessageEnvelope responseMessageEnvelope = new MessageEnvelope();

		for (Message message : messageEnvelope.getMessages()) {

			XDIAddress senderXDIAddress = XdiPeerRoot.getXDIAddressOfPeerRootXDIArc(messagingTarget.getOwnerPeerRootXDIArc());
			XDIAddress toXDIAddress = message.getSenderXDIAddress();

			Message responseMessage = responseMessageEnvelope.createMessage(senderXDIAddress);
			responseMessage.setToXDIAddress(toXDIAddress);
			responseMessage.setTimestamp(new Date());
			responseMessage.setCorrelationXDIAddress(message.getXDIAddress());

			for (Operation operation : message.getOperations()) {

				Graph operationResultGraph = executionResult.getOperationResultGraph(operation);
				if (operationResultGraph == null) throw new Xdi2TransportException("No operation result graph for operation " + operation);

				responseMessage.createOperation(operation.getOperationXDIAddress(), operationResultGraph);
			}

			GraphSigner signer = new GraphSigner(((GraphMessagingTarget) messagingTarget).getGraph());
			signer.setDigestAlgorithm(KeyPairSignature.DIGEST_ALGORITHM_SHA);
			signer.setDigestLength(256);
			signer.sign(responseMessage);
		}

		MessageEnvelopeMessagingResponse messageEnvelopeMessagingResponse = MessageEnvelopeMessagingResponse.create(responseMessageEnvelope);

		// done

		return messageEnvelopeMessagingResponse;
	}

	protected final ErrorMessagingResponse makeErrorMessagingResponse(Exception ex) {

		// set error string

		String errorString = ex.getMessage();
		if (errorString == null) errorString = ex.getClass().getName();

		// information specific to certain exceptions

		Operation errorOperation = null;

		if (ex instanceof Xdi2MessagingException) {

			ExecutionContext executionContext = ((Xdi2MessagingException) ex).getExecutionContext();
			errorOperation = executionContext == null ? null : executionContext.getExceptionOperation();
		}

		ErrorMessagingResponse errorMessagingResponse = ErrorMessagingResponse.create(errorString, errorOperation);

		// done

		return errorMessagingResponse;
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

	public static boolean isAsync(MessageEnvelope messageEnvelope) {

		for (Message message : messageEnvelope.getMessages()) {

			Boolean async = message.getParameterBoolean(Message.XDI_ADD_PARAMETER_ASYNC);
			if (Boolean.TRUE.equals(async)) return true;
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
