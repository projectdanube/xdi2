package xdi2.agent.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.XDIAgent;
import xdi2.agent.routing.XDIAgentRouter;
import xdi2.agent.routing.impl.XDIHttpDiscoveryAgentRouter;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.CloudName;
import xdi2.core.syntax.CloudNumber;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.response.MessagingResponse;

public class XDIBasicAgent implements XDIAgent {

	private static final Logger log = LoggerFactory.getLogger(XDIBasicAgent.class);

	private List<XDIAgentRouter<?, ?>> agentRouters;

	public XDIBasicAgent(List<XDIAgentRouter<?, ?>> agentRouters) {

		this.agentRouters = agentRouters;
	}

	public XDIBasicAgent(XDIAgentRouter<?, ?> agentRouter) {

		this.agentRouters = new ArrayList<XDIAgentRouter<?, ?>> ();
		this.agentRouters.add(agentRouter);
	}

	public XDIBasicAgent() {

		this(new XDIHttpDiscoveryAgentRouter());
	}

	@Override
	public XDIClientRoute<?> route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// let's find a route

		XDIClientRoute<?> route = null;

		for (XDIAgentRouter<?, ?> agentTarget : this.getAgentRouters()) {

			route = agentTarget.route(toPeerRootXDIArc);
			if (route != null) break;
		}

		if (log.isDebugEnabled()) log.debug("Route for " + toPeerRootXDIArc + " is " + route);

		// done

		return route;
	}

	@Override
	public XDIClientRoute<?> route(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException {

		// let's find out the TO peer root of the address

		XDIArc firstXDIArc = XDIaddress.getFirstXDIArc();
		XDIAddress firstXDIArcXDIAddress = XDIAddress.fromComponent(firstXDIArc);

		XDIAddress toPeerRootXDIAddress = XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, false, false);
		CloudNumber toCloudNumber = CloudNumber.isValid(firstXDIArcXDIAddress) ? CloudNumber.fromXDIAddress(firstXDIArcXDIAddress) : null;
		CloudName toCloudName = CloudName.isValid(firstXDIArcXDIAddress) ? CloudName.fromXDIAddress(firstXDIArcXDIAddress) : null;

		if (log.isDebugEnabled()) log.debug("Peer root: " + toPeerRootXDIAddress + ", Cloud Number: " + toCloudNumber + ", Cloud Name: " + toCloudName);

		XDIArc toPeerRootXDIArc = null;
		if (toPeerRootXDIAddress != null) toPeerRootXDIArc = toPeerRootXDIAddress.getLastXDIArc();
		if (toCloudNumber != null) toPeerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(toCloudNumber.getXDIAddress());
		if (toCloudName != null) toPeerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(toCloudName.getXDIAddress());

		if (log.isDebugEnabled()) log.debug("Determined TO peer root: " + toPeerRootXDIArc);

		if (toPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Unable to determine TO peer root for address " + XDIaddress);
			return null;
		}

		// let's find a route

		return route(toPeerRootXDIArc);
	}

	@Override
	public XDIClientRoute<?> route(MessageEnvelope messageEnvelope) throws Xdi2AgentException {

		// use the TO peer root

		XDIArc toPeerRootXDIArc = null;

		for (Message message : messageEnvelope.getMessages()) {

			if (toPeerRootXDIArc == null) { 

				toPeerRootXDIArc = message.getToPeerRootXDIArc();
				continue;
			}

			if (! toPeerRootXDIArc.equals(message.getToPeerRootXDIArc())) {

				throw new Xdi2AgentException("Cannot route message envelope with multiple messages and different TO peer roots.");
			}
		}

		// let's find a route

		return route(toPeerRootXDIArc);
	}

	@Override
	public XDIClientRoute<?> route(Message message) throws Xdi2AgentException, Xdi2ClientException {

		// use the TO peer root

		XDIArc toPeerRootXDIArc = message.getToPeerRootXDIArc();

		// let's find a route

		return route(toPeerRootXDIArc);
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException {

		// route

		XDIClientRoute<?> route = this.route(XDIaddress);
		if (route == null) throw new Xdi2AgentException("Unable to obtain a route for address " + XDIaddress);

		// client construction step

		XDIClient xdiClient = route.constructXDIClient();

		// message envelope construction step

		MessageEnvelope messageEnvelope = route.constructMessageEnvelope();
		Message message = route.constructMessage(messageEnvelope);
		Operation operation = message.createGetOperation(XDIaddress);
		operation.setParameter(GetOperation.XDI_ADD_PARAMETER_DEREF, Boolean.TRUE);

		// send the message envelope

		MessagingResponse messagingResponse = xdiClient.send(messageEnvelope);
		Graph resultGraph = messagingResponse.getResultGraph();

		// let's look for our XDI address in the message result

		ContextNode contextNode = resultGraph.getDeepContextNode(XDIaddress);

		if (contextNode != null) {

			if (log.isDebugEnabled()) log.debug("Unable to find context node. Giving up for address " + XDIaddress);
			return null;
		}

		// done

		if (log.isDebugEnabled()) log.debug("Found context node in result graph for address " + XDIaddress);
		return contextNode;
	}

	/*
	 * Getters and setters
	 */

	public List<XDIAgentRouter<?, ?>> getAgentRouters() {

		return this.agentRouters;
	}

	public void setAgentRouters(List<XDIAgentRouter<?, ?>> agentRouters) {

		this.agentRouters = agentRouters;
	}
}
