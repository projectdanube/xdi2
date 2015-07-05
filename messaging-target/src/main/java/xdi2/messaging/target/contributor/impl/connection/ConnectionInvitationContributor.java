package xdi2.messaging.target.contributor.impl.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.client.exceptions.Xdi2ClientException;
import xdi2.client.impl.http.XDIHttpClient;
import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiAbstractVariable.MappingContextNodeXdiVariableIterator;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.CopyUtil;
import xdi2.core.util.GraphUtil;
import xdi2.discovery.XDIDiscoveryClient;
import xdi2.discovery.XDIDiscoveryResult;
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
	private XDIDiscoveryClient xdiDiscoveryClient;

	public ConnectionInvitationContributor(Graph targetGraph, XDIDiscoveryClient xdiDiscoveryClient) {

		this.targetGraph = targetGraph;
		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}

	public ConnectionInvitationContributor() {

		this(null, DEFAULT_DISCOVERY_CLIENT);
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

		// set the discovery client

		contributor.setXdiDiscoveryClient(this.getXdiDiscoveryClient());

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

		// discover authorizing authority

		XDIDiscoveryResult xdiDiscoveryResult;

		try {

			xdiDiscoveryResult = this.getXdiDiscoveryClient().discoverFromRegistry(authorizingAuthority);
		} catch (Xdi2ClientException ex) {

			throw new Xdi2MessagingException("XDI Discovery failed on " + authorizingAuthority + ": " + ex.getMessage(), ex, executionContext);
		}

		if (xdiDiscoveryResult.getCloudNumber() == null) throw new Xdi2MessagingException("Could not discover Cloud Number for authorizing authority at " + authorizingAuthority, null, executionContext);
		if (xdiDiscoveryResult.getXdiEndpointUrl() == null) throw new Xdi2MessagingException("Could not discover XDI endpoint URI for authorizing authority at " + authorizingAuthority, null, executionContext);

		// create connection request

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(requestingAuthority);
		message.setToPeerRootXDIArc(XdiPeerRoot.createPeerRootXDIArc(authorizingAuthority));
		message.setLinkContractXDIAddress(operation.getMessage().getLinkContractXDIAddress());
		message.createOperation(XDIAddress.create("$do{}"), linkContractTemplateXDIaddress);

		MappingContextNodeXdiVariableIterator xdiVariablesIterator = new MappingContextNodeXdiVariableIterator(operation.getMessage().getContextNode().getContextNodes());

		for (XdiVariable<?> xdiVariable : xdiVariablesIterator) {

			if (log.isDebugEnabled()) log.debug("Custom variable in connection invitation: " + xdiVariable.getXDIArc());

			CopyUtil.copyContextNode(xdiVariable.getContextNode(), message.getContextNode(), null);
		}

		// send connection request

		MessagingResponse messagingResponse;

		try {

			messagingResponse = new XDIHttpClient(xdiDiscoveryResult.getXdiEndpointUrl()).send(messageEnvelope);
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

	public XDIDiscoveryClient getXdiDiscoveryClient() {

		return this.xdiDiscoveryClient;
	}

	public void setXdiDiscoveryClient(XDIDiscoveryClient xdiDiscoveryClient) {

		this.xdiDiscoveryClient = xdiDiscoveryClient;
	}
}
