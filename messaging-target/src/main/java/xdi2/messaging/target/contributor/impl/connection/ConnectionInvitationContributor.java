package xdi2.messaging.target.contributor.impl.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.XDIClient;
import xdi2.client.XDIClientRoute;
import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiAbstractVariable.MappingContextNodeXdiVariableIterator;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.GraphUtil;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.response.MessagingResponse;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.contributor.impl.AbstractContributor;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * This contributor can process connection invitations.
 * Warning: This is experimental, do not use for serious applications.
 */
//TODO: fix variable syntax
@ContributorMount(
		contributorXDIAddresses={"{}{$do}"},
		operationXDIAddresses={"$do$is{}"}
		)
public class ConnectionInvitationContributor extends AbstractContributor implements Prototype<ConnectionInvitationContributor> {

	private static final Logger log = LoggerFactory.getLogger(ConnectionInvitationContributor.class);

	public static final XDIDiscoveryClient DEFAULT_DISCOVERY_CLIENT = XDIDiscoveryClient.DEFAULT_DISCOVERY_CLIENT;

	private Graph targetGraph;
	private XDIAgent xdiAgent;

	public ConnectionInvitationContributor(Graph targetGraph, XDIAgent xdiAgent) {

		this.targetGraph = targetGraph;
		this.xdiAgent = xdiAgent;
	}

	public ConnectionInvitationContributor() {

		this(null, new XDIBasicAgent());
	}

	/*
	 * Prototype
	 */

	@Override
	public ConnectionInvitationContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		ConnectionInvitationContributor contributor = new ConnectionInvitationContributor();

		// set the graph

		contributor.setTargetGraph(this.getTargetGraph());

		// set the agent

		contributor.setXdiAgent(this.getXdiAgent());

		// done

		return contributor;
	}

	/*
	 * Init and shutdown
	 */

	@Override
	public void init(MessagingTarget messagingTarget) throws Exception {

		super.init(messagingTarget);

		if (this.getTargetGraph() == null && messagingTarget instanceof GraphMessagingTarget) this.setTargetGraph(((GraphMessagingTarget) messagingTarget).getGraph()); 
		if (this.getTargetGraph() == null) throw new Xdi2MessagingException("No target graph.", null, null);
	}

	/*
	 * Contributor methods
	 */

	@Override
	public ContributorResult executeDoOnAddress(XDIAddress[] contributorXris, XDIAddress contributorsXri, XDIAddress relativeTargetAddress, DoOperation operation, Graph operationResultGraph, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress linkContractTemplateXDIaddress = operation.getTargetXDIAddress();

		// determine requesting authority

		XDIAddress requestingAuthority = GraphUtil.getOwnerXDIAddress(this.getTargetGraph());

		// determine authorizing authority

		XDIAddress authorizingAuthority = operation.getSenderXDIAddress();

		// find route to authorizing authority

		XDIClientRoute<? extends XDIClient> route;

		try {

			route = this.getXdiAgent().route(authorizingAuthority);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("XDI routing failed on " + authorizingAuthority + ": " + ex.getMessage(), ex, executionContext);
		}

		if (route == null) throw new Xdi2MessagingException("Could not find route to authorizing authority at " + authorizingAuthority, null, executionContext);

		// create connection request

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(requestingAuthority);
		message.setToPeerRootXDIArc(route.getToPeerRootXDIArc());
		message.setLinkContractXDIAddress(operation.getMessage().getLinkContractXDIAddress());
		message.createOperation(XDIAddress.create("$do{}"), linkContractTemplateXDIaddress);

		MappingContextNodeXdiVariableIterator xdiVariablesIterator = new MappingContextNodeXdiVariableIterator(operation.getMessage().getContextNode().getContextNodes());

		for (XdiVariable<?> xdiVariable : xdiVariablesIterator) {

			if (log.isDebugEnabled()) log.debug("Custom variable in connection invitation: " + xdiVariable.getXDIArc());

			CopyUtil.copyContextNode(xdiVariable.getContextNode(), message.getContextNode(), null);
		}

		// send connection request

		XDIClient xdiClient = route.constructXDIClient();
		MessagingResponse messagingResponse;

		try {

			messagingResponse = xdiClient.send(messageEnvelope);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("Problem while sending connection request: " + ex.getMessage(), ex, executionContext);
		}

		// copy messaging response to our result graph

		CopyUtil.copyGraph(messagingResponse.getResultGraph(), operationResultGraph, null);

		// done

		return ContributorResult.SKIP_MESSAGING_TARGET;
	}

	/*
	 * Getters and setters
	 */

	public Graph getTargetGraph() {

		return this.targetGraph;
	}

	public void setTargetGraph(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public XDIAgent getXdiAgent() {

		return this.xdiAgent;
	}

	public void setXdiAgent(XDIAgent xdiAgent) {

		this.xdiAgent = xdiAgent;
	}
}
