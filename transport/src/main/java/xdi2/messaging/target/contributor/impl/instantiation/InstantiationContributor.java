package xdi2.messaging.target.contributor.impl.instantiation;

import xdi2.client.agent.XDIAgent;
import xdi2.client.agent.impl.XDIBasicAgent;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiAbstractVariable;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.util.GraphUtil;
import xdi2.messaging.DoOperation;
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
 * This contributor can instantiate new link contracts.
 */
@ContributorMount(
		contributorAddresses={"{{}}{$do}"},
		operationAddresses={"$do{}"}
		)
public class InstantiationContributor extends AbstractContributor implements Prototype<InstantiationContributor> {

	private Graph targetGraph;

	public InstantiationContributor(Graph targetGraph) {

		this.targetGraph = targetGraph;
	}

	public InstantiationContributor() {

		this(null);
	}

	/*
	 * Prototype
	 */

	@Override
	public InstantiationContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		InstantiationContributor contributor = new InstantiationContributor();

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

		// use agent to obtain link contract template

		ContextNode contextNode;

		try {

			XDIAgent xdiAgent = new XDIBasicAgent();
			contextNode = xdiAgent.get(operation.getTargetXDIAddress(), null);
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Unable to obtain link contract template at address " + operation.getTargetXDIAddress() + ": " + ex.getMessage(), ex, executionContext);
		}

		XdiVariable xdiVariable = XdiAbstractVariable.fromContextNode(contextNode);
		if (xdiVariable == null) throw new Xdi2MessagingException("Invalid link contract template at address " + operation.getTargetXDIAddress(), null, executionContext);

		LinkContractTemplate linkContractTemplate = LinkContractTemplate.fromXdiVariable(xdiVariable);
		if (linkContractTemplate == null) throw new Xdi2MessagingException("Invalid link contract template at address " + operation.getTargetXDIAddress(), null, executionContext);

		// determine requesting authority

		XDIAddress requestingAuthority = operation.getSenderXDIAddress();

		// determine authorizing authority

		XDIAddress authorizingAuthority = GraphUtil.getOwnerXDIAddress(this.getTargetGraph());

		// instantiate link contract

		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation();
		linkContractInstantiation.setRequestingAuthority(requestingAuthority);
		linkContractInstantiation.setAuthorizingAuthority(authorizingAuthority);
		linkContractInstantiation.setLinkContractTemplate(linkContractTemplate);

		linkContractInstantiation.execute(this.getTargetGraph(), true);

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
