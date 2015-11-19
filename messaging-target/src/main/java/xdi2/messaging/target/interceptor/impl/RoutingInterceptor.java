package xdi2.messaging.target.interceptor.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClient;
import xdi2.client.manipulator.Manipulator;
import xdi2.core.syntax.XDIArc;
import xdi2.messaging.Message;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.execution.ExecutionResult;
import xdi2.messaging.target.interceptor.InterceptorResult;
import xdi2.messaging.target.interceptor.MessageInterceptor;

/**
 * This interceptor can route messages using the XDI agent framework.
 * 
 * @author markus
 */
public class RoutingInterceptor extends AbstractInterceptor<MessagingTarget> implements MessageInterceptor, Prototype<RoutingInterceptor> {

	private static final Logger log = LoggerFactory.getLogger(RoutingInterceptor.class);

	private XDIAgent xdiAgent;
	private Collection<Manipulator> manipulators;
	private boolean skipSiblingInterceptors;
	private boolean skipMessagingTarget;

	public RoutingInterceptor(XDIAgent xdiAgent, Collection<Manipulator> manipulators, boolean skipSiblingInterceptors, boolean skipMessagingTarget) {

		this.xdiAgent = xdiAgent;
		this.manipulators = manipulators;
		this.skipSiblingInterceptors = skipSiblingInterceptors;
		this.skipMessagingTarget = skipMessagingTarget;
	}

	public RoutingInterceptor() {

		this(new XDIBasicAgent(), null, true, true);
	}

	/*
	 * Prototype
	 */

	@Override
	public RoutingInterceptor instanceFor(PrototypingContext prototypingContext) {

		// create new interceptor

		RoutingInterceptor interceptor = new RoutingInterceptor();

		// set the agent and manipulators

		interceptor.setXdiAgent(this.getXdiAgent());
		interceptor.setManipulators(this.getManipulators());

		// done

		return interceptor;
	}

	/*
	 * MessageInterceptor
	 */

	@Override
	public InterceptorResult before(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		// send

		this.processRoute(message, executionContext);

		// done

		return new InterceptorResult(this.isSkipSiblingInterceptors(), this.isSkipMessagingTarget());
	}

	@Override
	public InterceptorResult after(Message message, ExecutionContext executionContext, ExecutionResult executionResult) throws Xdi2MessagingException {

		return InterceptorResult.DEFAULT;
	}

	/*
	 * Helper methods
	 */

	private void processRoute(Message routingMessage, ExecutionContext executionContext) throws Xdi2MessagingException {

		if (log.isDebugEnabled()) log.debug("Preparing to route message " + routingMessage);

		// find route for routing message

		XDIArc toPeerRootXDIArc = routingMessage.getToPeerRootXDIArc();

		XDIClientRoute<? extends XDIClient<? extends MessagingResponse>> xdiClientRoute;

		try {

			xdiClientRoute = this.getXdiAgent().route(toPeerRootXDIArc);
		} catch (Xdi2AgentException ex) {

			throw new Xdi2MessagingException("Agent problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Client problem while routing to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		}

		if (xdiClientRoute == null) throw new Xdi2MessagingException("No route for " + toPeerRootXDIArc, null, executionContext);

		// send the routing message

		XDIClient<? extends MessagingResponse> xdiClient = xdiClientRoute.constructXDIClient();

		try {

			// add manipulators

			if (xdiClient instanceof XDIAbstractClient) {

				Collection<Manipulator> manipulators = new ArrayList<Manipulator> ();
				if (this.getManipulators() != null) manipulators.addAll(this.getManipulators());

				((XDIAbstractClient<? extends MessagingResponse>) xdiClient).getManipulators().addManipulators(manipulators);
			}

			// send

			xdiClient.send(routingMessage.getMessageEnvelope());
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while sending routing message " + routingMessage + " to " + toPeerRootXDIArc + ": " + ex.getMessage(), ex, executionContext);
		}
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

	public Collection<Manipulator> getManipulators() {

		return this.manipulators;
	}

	public void setManipulators(Collection<Manipulator> manipulators) {

		this.manipulators = manipulators;
	}

	public boolean isSkipSiblingInterceptors() {

		return this.skipSiblingInterceptors;
	}

	public void setSkipSiblingInterceptors(boolean skipSiblingInterceptors) {

		this.skipSiblingInterceptors = skipSiblingInterceptors;
	}

	public boolean isSkipMessagingTarget() {

		return this.skipMessagingTarget;
	}

	public void setSkipMessagingTarget(boolean skipMessagingTarget) {

		this.skipMessagingTarget = skipMessagingTarget;
	}
}
