package xdi2.client.agent.impl;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.XDIClient;
import xdi2.client.agent.XDIAgent;
import xdi2.client.agent.target.AgentConnection;
import xdi2.client.agent.target.AgentTarget;
import xdi2.client.agent.target.impl.DiscoveryAgentTarget;
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
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.GetOperation;
import xdi2.messaging.operations.Operation;
import xdi2.messaging.response.MessagingResponse;

public class XDIBasicAgent implements XDIAgent {

	private static final Logger log = LoggerFactory.getLogger(XDIBasicAgent.class);

	private List<AgentTarget> agentTargets;

	public XDIBasicAgent(List<AgentTarget> agentTargets) {

		this.agentTargets = agentTargets;
	}

	public XDIBasicAgent(AgentTarget agentTarget) {

		this.agentTargets = Collections.singletonList(agentTarget);
	}

	public XDIBasicAgent() {

		this(new DiscoveryAgentTarget(XDIDiscoveryClient.DEFAULT_DISCOVERY_CLIENT));
	}

	@Override
	public AgentConnection connect(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException {

		// let's find out the target peer root of the address

		XDIArc firstXDIArc = XDIaddress.getFirstXDIArc();
		XDIAddress firstXDIArcXDIAddress = XDIAddress.fromComponent(firstXDIArc);

		XDIAddress targetPeerRootXDIAddress = XDIAddressUtil.extractXDIAddress(XDIaddress, XdiPeerRoot.class, false, false);
		CloudNumber targetCloudNumber = CloudNumber.isValid(firstXDIArcXDIAddress) ? CloudNumber.fromXDIAddress(firstXDIArcXDIAddress) : null;
		CloudName targetCloudName = CloudName.isValid(firstXDIArcXDIAddress) ? CloudName.fromXDIAddress(firstXDIArcXDIAddress) : null;

		if (log.isDebugEnabled()) log.debug("Peer root: " + targetPeerRootXDIAddress + ", Cloud Number: " + targetCloudNumber + ", Cloud Name: " + targetCloudName);

		XDIArc targetPeerRootXDIArc = null;
		if (targetPeerRootXDIAddress != null) targetPeerRootXDIArc = targetPeerRootXDIAddress.getLastXDIArc();
		if (targetCloudNumber != null) targetPeerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(targetCloudNumber.getXDIAddress());
		if (targetCloudName != null) targetPeerRootXDIArc = XdiPeerRoot.createPeerRootXDIArc(targetCloudName.getXDIAddress());

		if (log.isDebugEnabled()) log.debug("Determined target peer root: " + targetPeerRootXDIArc);

		if (targetPeerRootXDIArc == null) {

			if (log.isDebugEnabled()) log.debug("Unable to determine target peer root for address " + XDIaddress);
			return null;
		}

		// let's find out the target address

		XDIAddress targetXDIAddress = null;

		if (targetPeerRootXDIAddress != null) 
			targetXDIAddress = XDIAddressUtil.localXDIAddress(XDIaddress, targetPeerRootXDIAddress.getNumXDIArcs());
		else
			targetXDIAddress = XDIaddress;

		if (log.isDebugEnabled()) log.debug("Determined target address: " + targetXDIAddress);

		// let's find a connection

		AgentConnection connection = null;

		for (AgentTarget agentTarget : this.getAgentTargets()) {

			connection = agentTarget.connect(targetPeerRootXDIArc);
			if (connection != null) break;
		}

		return connection;
	}

	@Override
	public ContextNode get(XDIAddress XDIaddress) throws Xdi2AgentException, Xdi2ClientException {

		// connect

		AgentConnection connection = this.connect(XDIaddress);
		if (connection == null) throw new Xdi2AgentException("Unable to obtain a connection for address " + XDIaddress);

		// message construction step

		MessageEnvelope messageEnvelope = connection.constructMessageEnvelope();
		Message message = connection.constructMessage(messageEnvelope);
		Operation operation = message.createGetOperation(XDIaddress);
		operation.setParameter(GetOperation.XDI_ADD_PARAMETER_DEREF, Boolean.TRUE);

		// client step

		XDIClient xdiClient = connection.constructXDIClient();
		MessagingResponse messagingResponse = xdiClient.send(messageEnvelope);
		Graph resultGraph = messagingResponse.getResultGraph();

		// let's look for our target address in the message result

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

	public List<AgentTarget> getAgentTargets() {

		return this.agentTargets;
	}

	public void setAgentTargets(List<AgentTarget> agentTargets) {

		this.agentTargets = agentTargets;
	}
}
