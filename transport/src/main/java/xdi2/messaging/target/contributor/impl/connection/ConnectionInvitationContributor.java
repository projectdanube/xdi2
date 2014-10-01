package xdi2.messaging.target.contributor.impl.connection;

import xdi2.core.Graph;
import xdi2.core.features.nodetypes.XdiPeerRoot;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;
import xdi2.messaging.DoOperation;
import xdi2.messaging.Message;
import xdi2.messaging.MessageEnvelope;
import xdi2.messaging.MessageResult;
import xdi2.messaging.context.ExecutionContext;
import xdi2.messaging.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.AbstractContributor;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * This contributor can process connection invitations.
 */
@ContributorMount(
		contributorAddresses={"{{}}{$do}"},
		operationAddresses={"$do$is{}"}
		)
public class ConnectionInvitationContributor extends AbstractContributor implements Prototype<ConnectionInvitationContributor> {

	private Graph targetGraph;

	public ConnectionInvitationContributor(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public ConnectionInvitationContributor() {

		this(null);
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
	public ContributorResult executeDoOnAddress(XDIAddress[] contributorXris, XDIAddress contributorsXri, XDIAddress relativeTargetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		XDIAddress linkContractTemplateXDIaddress = operation.getTargetXDIAddress();

		// determine requesting authority

		XDIAddress requestingAuthority = GraphUtil.getOwnerXDIAddress(this.getTargetGraph());

		// determine authorizing authority

		XDIAddress authorizingAuthority = operation.getSenderXDIAddress();

		// create connection request

		MessageEnvelope messageEnvelope = new MessageEnvelope();
		Message message = messageEnvelope.createMessage(requestingAuthority);
		message.setToPeerRootXDIArc(XdiPeerRoot.createPeerRootXDIArc(authorizingAuthority));
		message.setLinkContractXDIAddress(linkContractTemplateXDIaddress);
		message.createOperation(XDIAddress.create("$do{}"), linkContractTemplateXDIaddress);

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
}
