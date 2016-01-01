package xdi2.agent.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.XDIAgent;
import xdi2.agent.routing.XDIAgentRouter;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2AgentException;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.XDIAbstractClientRoute;
import xdi2.client.manipulator.Manipulator;
import xdi2.core.ContextNode;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.util.XDIAddressUtil;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;

public class XDIBasicAgent implements XDIAgent {

	private static final Logger log = LoggerFactory.getLogger(XDIBasicAgent.class);

	private LinkedList<XDIAgentRouter<?, ?>> agentRouters;
	private Collection<Manipulator> manipulators;

	public XDIBasicAgent(Collection<XDIAgentRouter<?, ?>> agentRouters) {

		this.agentRouters = new LinkedList<XDIAgentRouter<?, ?>> (agentRouters);
		this.manipulators = new ArrayList<Manipulator> ();
	}

	public XDIBasicAgent(XDIAgentRouter<?, ?>[] agentRouters) {

		this.agentRouters = new LinkedList<XDIAgentRouter<?, ?>> (Arrays.asList(agentRouters));
		this.manipulators = new ArrayList<Manipulator> ();
	}

	public XDIBasicAgent(XDIAgentRouter<?, ?> agentRouter) {

		this.agentRouters = new LinkedList<XDIAgentRouter<?, ?>> ();
		this.agentRouters.add(agentRouter);
		this.manipulators = new ArrayList<Manipulator> ();
	}

	public XDIBasicAgent() {

		this.agentRouters = new LinkedList<XDIAgentRouter<?, ?>> ();
		this.manipulators = new ArrayList<Manipulator> ();
	}

	@Override
	public XDIClientRoute<?> route(XDIArc toPeerRootXDIArc) throws Xdi2AgentException {

		// let's find a route

		XDIClientRoute<?> foundRoute = null;
		XDIAgentRouter<?, ?> foundAgentRouter = null;

		for (XDIAgentRouter<?, ?> agentRouter : this.getAgentRouters()) {

			if (log.isDebugEnabled()) log.debug("Trying router " + agentRouter.getClass().getSimpleName() + " to route to " + toPeerRootXDIArc);

			foundRoute = agentRouter.route(toPeerRootXDIArc);
			if (foundRoute != null) foundAgentRouter = agentRouter;
			if (foundRoute != null) break;
		}

		if (foundRoute == null) {

			if (log.isDebugEnabled()) log.debug("No route found for " + toPeerRootXDIArc);
			return null;
		}

		if (log.isDebugEnabled()) log.debug("Route for " + toPeerRootXDIArc + " is " + foundRoute + " via router " + foundAgentRouter.getClass().getSimpleName());

		// add manipulators if supported

		if (foundRoute instanceof XDIAbstractClientRoute && this.getManipulators() != null) {

			((XDIAbstractClientRoute<?>) foundRoute).getManipulators().addAll(this.getManipulators());
		}

		// done

		return foundRoute;
	}

	@Override
	public XDIClientRoute<?> route(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException {

		// let's find out the TO peer root of the address

		XDIAddress peerRootXDIAddress = XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, false, false, false, false);
		XDIArc peerRootFirstXDIArc = peerRootXDIAddress == null ? null : peerRootXDIAddress.getFirstXDIArc();

		XDIArc firstXDIArc = XDIaddress.getFirstXDIArc();

		if (log.isDebugEnabled()) log.debug("Peer root first arc: " + peerRootFirstXDIArc + ", First arc: " + firstXDIArc);

		XDIArc toPeerRootXDIArc = null;
		if (toPeerRootXDIArc == null && peerRootFirstXDIArc != null) toPeerRootXDIArc = peerRootFirstXDIArc;
		if (toPeerRootXDIArc == null && firstXDIArc != null) toPeerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(XDIAddress.fromComponent(firstXDIArc));

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

	/*
	 * $get helper methods
	 */

	@Override
	public ContextNode get(XDIAddress XDIaddress, XDIAddress senderXDIAddress, Collection<Manipulator> manipulators) throws Xdi2AgentException, Xdi2ClientException {

		XDIClientRoute<?> xdiClientRoute = this.route(XDIaddress);
		if (xdiClientRoute == null) return null;

		return xdiClientRoute.get(XDIaddress, senderXDIAddress, manipulators);
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress, XDIAddress senderXDIAddress, Manipulator[] manipulators) throws Xdi2AgentException, Xdi2ClientException {

		XDIClientRoute<?> xdiClientRoute = this.route(XDIaddress);
		if (xdiClientRoute == null) return null;

		return xdiClientRoute.get(XDIaddress, senderXDIAddress, manipulators);
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress, XDIAddress senderXDIAddress, Manipulator manipulator) throws Xdi2AgentException, Xdi2ClientException {

		XDIClientRoute<?> xdiClientRoute = this.route(XDIaddress);
		if (xdiClientRoute == null) return null;

		return xdiClientRoute.get(XDIaddress, senderXDIAddress, manipulator);
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress, XDIAddress senderXDIAddress) throws Xdi2AgentException, Xdi2ClientException {

		XDIClientRoute<?> xdiClientRoute = this.route(XDIaddress);
		if (xdiClientRoute == null) return null;

		return xdiClientRoute.get(XDIaddress, senderXDIAddress);
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress, Collection<Manipulator> manipulators) throws Xdi2AgentException, Xdi2ClientException {

		XDIClientRoute<?> xdiClientRoute = this.route(XDIaddress);
		if (xdiClientRoute == null) return null;

		return xdiClientRoute.get(XDIaddress, manipulators);
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress, Manipulator[] manipulators) throws Xdi2AgentException, Xdi2ClientException {

		XDIClientRoute<?> xdiClientRoute = this.route(XDIaddress);
		if (xdiClientRoute == null) return null;

		return xdiClientRoute.get(XDIaddress, manipulators);
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress, Manipulator manipulator) throws Xdi2AgentException, Xdi2ClientException {

		XDIClientRoute<?> xdiClientRoute = this.route(XDIaddress);
		if (xdiClientRoute == null) return null;

		return xdiClientRoute.get(XDIaddress, manipulator);
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException {

		XDIClientRoute<?> xdiClientRoute = this.route(XDIaddress);
		if (xdiClientRoute == null) return null;

		return xdiClientRoute.get(XDIaddress);
	}

	/*
	 * Getters and setters
	 */

	public LinkedList<XDIAgentRouter<?, ?>> getAgentRouters() {

		return this.agentRouters;
	}

	public void setAgentRouters(LinkedList<XDIAgentRouter<?, ?>> agentRouters) {

		this.agentRouters = agentRouters;
	}

	public Collection<Manipulator> getManipulators() {

		return this.manipulators;
	}

	public void setManipulators(Collection<Manipulator> manipulators) {

		this.manipulators = manipulators;
	}
}
