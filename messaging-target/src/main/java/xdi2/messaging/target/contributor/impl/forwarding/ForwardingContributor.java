package xdi2.messaging.target.contributor.impl.forwarding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.XDIAddressUtil;
import xdi2.core.util.XDIStatementUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.contributor.impl.AbstractContributor;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;
import xdi2.messaging.util.MessagingCloneUtil;

/**
 * This contributor can answer requests by forwarding them to another XDI endpoint.
 */
@ContributorMount(
		contributorXDIAddresses={""}
		)
public class ForwardingContributor extends AbstractContributor implements MessageInterceptor, Prototype<ForwardingContributor> {

	private static final Logger log = LoggerFactory.getLogger(ForwardingContributor.class);

	public static final boolean DEFAULT_FORWARD_IF_SELF = false;
	public static final XDIAgent DEFAULT_XDI_AGENT = new XDIBasicAgent();

	private boolean forwardIfSelf;
	private XDIAgent xdiAgent;

	private boolean skipParentContributors;
	private boolean skipSiblingContributors;
	private boolean skipMessagingTarget;

	public ForwardingContributor() {

		this.forwardIfSelf = DEFAULT_FORWARD_IF_SELF;
		this.xdiAgent = DEFAULT_XDI_AGENT;

		this.skipParentContributors = false;
		this.skipSiblingContributors = false;
		this.skipMessagingTarget = true;
	}

	/*
	 * Prototype
	 */

	@Override
	public ForwardingContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

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
	public InterceptorResult before(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIArc toPeerRootXDIArc = message.getToPeerRootXDIArc();

		// is the target our current messaging target (self) ?

		if (this.isForwardIfSelf()) {

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
	public InterceptorResult after(Message message, ExecutionResult executionResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Contributor methods
	 */

	@Override
	public ContributorResult executeOnAddress(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIAddress relativeTargetAddress, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check route

		XDIClientRoute<? extends XDIClient> xdiClientRoute = getXdiClientRoute(executionContext, this);
		if (xdiClientRoute == null) return ContributorResult.DEFAULT;

		// prepare the forwarding message envelope

		Message message = operation.getMessage();
		if (log.isDebugEnabled()) log.debug("Preparing message for forwarding: " + message);

		XDIAddress targetXDIAddress = XDIAddressUtil.concatXDIAddresses(contributorsAddress, relativeTargetAddress);

		Message forwardingMessage = MessagingCloneUtil.cloneMessage(message);
		forwardingMessage.setToPeerRootXDIArc(xdiClientRoute.getToPeerRootXDIArc());
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

		return new ContributorResult(this.isSkipParentContributors(), this.isSkipSiblingContributors(), this.isSkipMessagingTarget());
	}

	@Override
	public ContributorResult executeOnStatement(XDIAddress[] contributorAddresses, XDIAddress contributorsAddress, XDIStatement relativeTargetStatement, Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check route

		XDIClientRoute<? extends XDIClient> xdiClientRoute = getXdiClientRoute(executionContext, this);
		if (xdiClientRoute == null) return ContributorResult.DEFAULT;

		// prepare the forwarding message envelope

		Message message = operation.getMessage();
		if (log.isDebugEnabled()) log.debug("Preparing message for forwarding: " + message);

		XDIStatement targetXDIStatement = XDIStatementUtil.concatXDIStatement(contributorsAddress, relativeTargetStatement);

		Message forwardingMessage = new MessageEnvelope().createMessage(message.getSenderXDIAddress());
		forwardingMessage.setToPeerRootXDIArc(xdiClientRoute.getToPeerRootXDIArc());
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

		return new ContributorResult(this.isSkipParentContributors(), this.isSkipSiblingContributors(), this.isSkipMessagingTarget());
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

	public boolean isSkipParentContributors() {

		return this.skipParentContributors;
	}

	public void setSkipParentContributors(boolean skipParentContributors) {

		this.skipParentContributors = skipParentContributors;
	}

	public boolean isSkipSiblingContributors() {

		return this.skipSiblingContributors;
	}

	public void setSkipSiblingContributors(boolean skipSiblingContributors) {

		this.skipSiblingContributors = skipSiblingContributors;
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

	private static final String EXECUTIONCONTEXT_KEY_XDI_CLIENT_ROUTE_PER_MESSAGE = ForwardingContributor.class.getCanonicalName() + "#xdiclientroutepermessage";

	public static XDIClientRoute<? extends XDIClient> getXdiClientRoute(ExecutionContext executionContext, ForwardingContributor forwardingContributor) {

		return (XDIClientRoute<?>) executionContext.getMessageAttribute(EXECUTIONCONTEXT_KEY_XDI_CLIENT_ROUTE_PER_MESSAGE + Integer.toString(System.identityHashCode(forwardingContributor)));
	}

	public static void putXdiClientRoute(ExecutionContext executionContext, XDIClientRoute<? extends XDIClient> xdiClientRoute, ForwardingContributor forwardingContributor) {

		executionContext.putMessageAttribute(EXECUTIONCONTEXT_KEY_XDI_CLIENT_ROUTE_PER_MESSAGE + Integer.toString(System.identityHashCode(forwardingContributor)), xdiClientRoute);
	}
}
