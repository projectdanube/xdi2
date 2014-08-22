package xdi2.messaging.target.contributor.impl.instantiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiVariable;
import xdi2.core.xri3.XDI3Segment;
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
		contributorXris={"{{}}$template$do"},
		operationXris={"$do$instantiate"}
)
public class InstantiationContributor extends AbstractContributor implements Prototype<InstantiationContributor> {

	private static final Logger log = LoggerFactory.getLogger(InstantiationContributor.class);

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
	public ContributorResult executeDoOnAddress(XDI3Segment[] contributorXris, XDI3Segment contributorsXri, XDI3Segment relativeTargetAddress, DoOperation operation, MessageResult messageResult, ExecutionContext executionContext) throws Xdi2MessagingException {

		// find link contract template

		ContextNode contextNode = this.getTargetGraph().getDeepContextNode(operation.getTargetAddress());
		XdiVariable xdiVariable = XdiVariable.fromContextNode(contextNode);
		LinkContractTemplate linkContractTemplate = LinkContractTemplate.fromXdiVariable(xdiVariable);

		// instantiate link contract

		XDI3Segment authorizingAuthority = null;
		
		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation();
		linkContractInstantiation.setRequestingAuthority(operation.getSenderXri());
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
