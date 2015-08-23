package xdi2.messaging.target.interceptor.impl.forwarding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.target.interceptor.TargetInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor can answer requests by forwarding them to another XDI endpoint.
 */
public class ForwardingInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, TargetInterceptor, Prototype<ForwardingInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(ForwardingInterceptor.class);

	public static final boolean DEFAULT_FORWARD_IF_SELF = false;
	public static final XDIAgent DEFAULT_XDI_AGENT = new XDIBasicAgent();

	private boolean forwardIfSelf;
	private XDIAgent xdiAgent;

	private boolean skipSiblingInterceptors;
	private boolean skipMessagingTarget;

	public ForwardingInterceptor() {

		this.forwardIfSelf = DEFAULT_FORWARD_IF_SELF;
		this.xdiAgent = DEFAULT_XDI_AGENT;

		this.skipSiblingInterceptors = false;
		this.skipMessagingTarget = true;
	}

	/*
	 * Prototype
	 */

	@Override
	public ForwardingInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// done

		return this;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		XDIArc toPeerRootXDIArc = message.getToPeerRootXDIArc();

		// is the target our current messaging target (self) ?

		if (! this.isForwardIfSelf()) {

			MessagingTarget messagingTarget = executionContext.getCurrentMessagingTarget();
			XDIArc ownerPeerRootXDIArc = messagingTarget.getOwnerPeerRootXDIArc();

			if (toPeerRootXDIArc == null || toPeerRootXDIArc.equals(ownerPeerRootXDIArc)) {

				if (log.isDebugEnabled()) log.debug("Not setting any forwarding target for self request to " + ownerPeerRootXDIArc);

				return InterceptorResult.DEFAULT;
			}
		}

		// find route to target

		XDIClientRoute<? extends XDIClient> xdiClientRoute;

		try {

			xdiClientRoute = this.getXdiAgent().route(toPeerRootXDIArc);
		} catch (Xdi2AgentException ex) {

			throw new Xdi2MessagingException("Agent problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Client problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		}

		putXdiClientRoute(executionContext, xdiClientRoute, this);

		// done

		return InterceptorResult.DEFAULT;
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * TargetInterceptor
	 */

	@Override
	public XDIAddress targetAddress(XDIAddress targetXDIAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		Message message = operation.getMessage();

		// check route

		XDIClientRoute<? extends XDIClient> xdiClientRoute = getXdiClientRoute(executionContext, this);
		if (xdiClientRoute == null) return targetXDIAddress;

		// prepare the forwarding message envelope

		Message forwardingMessage;

		XdiInnerRoot proxyXdiInnerRoot = message.getXdiEntity().getXdiInnerRoot(XDIAddress.create("$proxy"), false);

		if (log.isDebugEnabled()) log.debug("Preparing message for forwarding " + operation.getOperationXDIAddress() + " operation on " + targetXDIAddress + " (using proxy? " + (proxyXdiInnerRoot != null) + "): " + message);

		XDIAddress senderXDIAddress;

		if (proxyXdiInnerRoot != null) {

			senderXDIAddress = executionContext.getCurrentMessagingTarget().getOwnerXDIAddress();
		} else {

			senderXDIAddress = message.getSenderXDIAddress();
		}

		forwardingMessage = new MessageEnvelope().createMessage(senderXDIAddress);

		if (proxyXdiInnerRoot != null) {

			CopyUtil.copyContextNodeContents(proxyXdiInnerRoot.getContextNode(), forwardingMessage.getContextNode(), null);
		} else {

			CopyUtil.copyContextNodeContents(message.getContextNode(), forwardingMessage.getContextNode(), null);
		}

		XDIArc fromPeerRootXDIArc = executionContext.getCurrentMessagingTarget().getOwnerPeerRootXDIArc();
		XDIArc toPeerRootXDIArc = xdiClientRoute.getToPeerRootXDIArc();

		forwardingMessage.setFromPeerRootXDIArc(fromPeerRootXDIArc);
		forwardingMessage.setToPeerRootXDIArc(toPeerRootXDIArc);

		forwardingMessage.deleteOperations();
		forwardingMessage.createOperation(operation.getOperationXDIAddress(), targetXDIAddress);

		if (log.isDebugEnabled()) log.debug("Prepared message for forwarding: " + forwardingMessage);

		MessageEnvelope forwardingMessageEnvelope = forwardingMessage.getMessageEnvelope();

		// send the forwarding message envelope

		XDIClient xdiClient = xdiClientRoute.constructXDIClient();

		try {

			MessagingResponse forwardingMessagingResponse = xdiClient.send(forwardingMessageEnvelope);
			CopyUtil.copyGraph(forwardingMessagingResponse.getResultGraph(), operationResultGraph, null);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while forwarding operation on XDI address " + targetXDIAddress + ": " + ex.getMessage(), ex, executionContext);
		}

		// done

		return null;
	}

	@Override
	public XDIStatement targetStatement(XDIStatement targetXDIStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext)  throws Xdi2MessagingException {

		Message message = operation.getMessage();

		// check route

		XDIClientRoute<? extends XDIClient> xdiClientRoute = getXdiClientRoute(executionContext, this);
		if (xdiClientRoute == null) return targetXDIStatement;

		// prepare the forwarding message envelope

		Message forwardingMessage;

		XdiInnerRoot proxyXdiInnerRoot = message.getXdiEntity().getXdiInnerRoot(XDIAddress.create("$proxy"), false);

		if (log.isDebugEnabled()) log.debug("Preparing message for forwarding " + operation.getOperationXDIAddress() + " operation on " + targetXDIStatement + " (using proxy? " + (proxyXdiInnerRoot != null) + "): " + message);

		XDIAddress senderXDIAddress;

		if (proxyXdiInnerRoot != null) {

			senderXDIAddress = executionContext.getCurrentMessagingTarget().getOwnerXDIAddress();
		} else {

			senderXDIAddress = message.getSenderXDIAddress();
		}

		forwardingMessage = new MessageEnvelope().createMessage(senderXDIAddress);

		if (proxyXdiInnerRoot != null) {

			CopyUtil.copyContextNodeContents(proxyXdiInnerRoot.getContextNode(), forwardingMessage.getContextNode(), null);
		} else {

			CopyUtil.copyContextNodeContents(message.getContextNode(), forwardingMessage.getContextNode(), null);
		}

		XDIArc fromPeerRootXDIArc = executionContext.getCurrentMessagingTarget().getOwnerPeerRootXDIArc();
		XDIArc toPeerRootXDIArc = xdiClientRoute.getToPeerRootXDIArc();

		forwardingMessage.setFromPeerRootXDIArc(fromPeerRootXDIArc);
		forwardingMessage.setToPeerRootXDIArc(toPeerRootXDIArc);

		forwardingMessage.deleteOperations();
		forwardingMessage.createOperation(operation.getOperationXDIAddress(), targetXDIStatement);

		if (log.isDebugEnabled()) log.debug("Prepared message for forwarding: " + forwardingMessage);

		MessageEnvelope forwardingMessageEnvelope = forwardingMessage.getMessageEnvelope();

		// send the forwarding message envelope

		XDIClient xdiClient = xdiClientRoute.constructXDIClient();

		try {

			MessagingResponse forwardingMessagingResponse = xdiClient.send(forwardingMessageEnvelope);
			CopyUtil.copyGraph(forwardingMessagingResponse.getResultGraph(), operationResultGraph, null);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while forwarding operation on XDI statement " + targetXDIStatement + ": " + ex.getMessage(), ex, executionContext);
		}

		// done

		return null;
	}

	/*
	 * Getters and setters
	 */

	public boolean isForwardIfSelf() {

		return this.forwardIfSelf;
	}

	public void setForwardIfSelf(boolean forwardIfSelf) {

		this.forwardIfSelf = forwardIfSelf;
	}

	public XDIAgent getXdiAgent() {

		return this.xdiAgent;
	}

	public void setXdiAgent(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}

	public boolean isSkipSiblingInterceptors() {

		return this.skipSiblingInterceptors;
	}

	public void setSkipSiblingInterceptors(boolean skipSiblingContributors) {

		this.skipSiblingInterceptors = skipSiblingContributors;
	}

	public boolean isSkipMessagingTarget() {

		return this.skipMessagingTarget;
	}

	public void setSkipMessagingTarget(boolean skipMessagingTarget) {

		this.skipMessagingTarget = skipMessagingTarget;
	}

	/*
	 * ExecutionContext helper methods
	 */

	private static final String EXECUTIONCONTEXT_KEY_XDI_CLIENT_ROUTE_PER_MESSAGE = ForwardingInterceptor.class.getCanonicalName() + "#xdiclientroutepermessage";

	public static XDIClientRoute<? extends XDIClient> getXdiClientRoute(ExecutionContext executionContext, ForwardingInterceptor forwardingContributor) {

		return (XDIClientRoute<?>) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_XDI_CLIENT_ROUTE_PER_MESSAGE + Integer.toString(System.identityHashCode(forwardingContributor)));
	}

	public static void putXdiClientRoute(ExecutionContext executionContext, XDIClientRoute<? extends XDIClient> xdiClientRoute, ForwardingInterceptor forwardingContributor) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_XDI_CLIENT_ROUTE_PER_MESSAGE + Integer.toString(System.identityHashCode(forwardingContributor)), xdiClientRoute);
	}
}
