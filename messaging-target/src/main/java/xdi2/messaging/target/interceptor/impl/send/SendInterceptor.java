package xdi2.messaging.target.interceptor.impl.send;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiInnerRoot;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.CopyUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.operations.SendOperation;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.OperationInterceptor;
import xdi2.messaging.target.interceptor.impl.AbstractInterceptor;

/**
 * This interceptor can process $send operations.
 */
@ContributorMount(operationXDIAddresses={"$send"} )
public class SendInterceptor extends AbstractInterceptor<MessagingTarget> implements OperationInterceptor, Prototype<SendInterceptor> {

	private XDIAgent xdiAgent;

	public SendInterceptor(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}

	public SendInterceptor() {

		this(new XDIBasicAgent());
	}

	/*
	 * Prototype
	 */

	@Override
	public SendInterceptor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		SendInterceptor contributor = new SendInterceptor();

		// set the agent

		contributor.setXdiAgent(this.getXdiAgent());

		// done

		return contributor;
	}

	/*
	 * OperationInterceptor
	 */

	@Override
	public InterceptorResult before(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		// check operation

		if (! (operation instanceof SendOperation)) return InterceptorResult.DEFAULT;

		// prepare the forwarding message envelope

		XdiInnerRoot targetInnerRoot = operation.getTargetInnerRoot();
		if (targetInnerRoot == null) return InterceptorResult.SKIP_MESSAGING_TARGET;

		MessageEnvelope forwardingMessageEnvelope = new MessageEnvelope();
		CopyUtil.copyContextNodeContents(targetInnerRoot.getContextNode(), forwardingMessageEnvelope.getGraph(), null);

		// look at all forwarding messages

		for (Message forwardingMessage : forwardingMessageEnvelope.getMessages()) {

			XDIArc toPeerRootXDIArc = forwardingMessage.getToPeerRootXDIArc();

			// find route to target

			XDIClientRoute<? extends XDIClient> xdiClientRoute;

			try {

				xdiClientRoute = this.getXdiAgent().route(toPeerRootXDIArc);
			} catch (Xdi2AgentException ex) {

				throw new Xdi2MessagingException("Agent problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
			} catch (Xdi2ClientException ex) {

				throw new Xdi2MessagingException("Client problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
			}

			// send the forwarding message envelope

			XDIClient xdiClient = xdiClientRoute.constructXDIClient();

			try {

				MessagingResponse forwardingMessagingResponse = xdiClient.send(forwardingMessageEnvelope);
				CopyUtil.copyGraph(forwardingMessagingResponse.getResultGraph(), operationResultGraph, null);
			} catch (Xdi2ClientException ex) {

				throw new Xdi2MessagingException("Problem while sending message " + forwardingMessage + " to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
			}
		}

		// done

		return InterceptorResult.SKIP_MESSAGING_TARGET;
	}

	@Override
	public InterceptorResult after(Operation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Getters and setters
	 */

	public XDIAgent getXdiAgent() {

		return this.xdiAgent;
	}

	public void setXdiAgent(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}
}
