package xdi2.messaging.target.interceptor.impl;

import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.evaluation.PolicyEvaluationContext;
import xdi2.core.features.linkcontracts.policy.PolicyRoot;
import xdi2.messaging.Message;
import xdi2.messaging.MessageResult;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.exceptions.Xdi2NotAuthorizedException;
import xdi2.messaging.target.ExecutionContext;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;
import xdi2.messaging.target.interceptor.AbstractInterceptor;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor evaluates message policies.
 * 
 * @author markus
 */
public class MessagePolicyInterceptor extends AbstractInterceptor implements MessageInterceptor, Prototype<MessagePolicyInterceptor> {

	private Graph messagePolicyGraph;

	/*
	 * Prototype
	 */

	@Override
	public MessagePolicyInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		MessagePolicyInterceptor interceptor = new MessagePolicyInterceptor();

		// set the XDI message policy graph

		if (this.messagePolicyGraph == null && prototypingContext.getMessagingTarget() instanceof GraphMessagingTarget) {

			interceptor.setMessagePolicyGraph(((GraphMessagingTarget) prototypingContext.getMessagingTarget()).getGraph());
		}

		// done

		return interceptor;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public boolean before(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// evaluate the XDI policy of this message

		PolicyRoot policyRoot = message.getPolicyRoot(false);
		if (policyRoot == null) return false;

		PolicyEvaluationContext policyEvaluationContext = new MessagePolicyEvaluationContext(this.getMessagePolicyGraph(), message);

		if (! Boolean.TRUE.equals(policyRoot.evaluate(policyEvaluationContext))) {

			throw new Xdi2NotAuthorizedException("Message policy violation for message " + message.toString() + ".", null, executionContext);
		}

		// done

		return false;
	}

	@Override
	public boolean after(Message message, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// done

		return false;
	}

	public Graph getMessagePolicyGraph() {

		return this.messagePolicyGraph;
	}

	public void setMessagePolicyGraph(Graph messagePolicyGraph) {

		this.messagePolicyGraph = messagePolicyGraph;
	}
}
