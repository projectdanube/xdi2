package xdi2.messaging.target.contributor.impl.connection;

import java.util.Map;

import xdi2.agent.XDIAgent;
import xdi2.agent.impl.XDIBasicAgent;
import xdi2.client.manipulator.impl.SetLinkContractMessageManipulator;
import xdi2.core.ContextNode;
import xdi2.core.Graph;
import xdi2.core.constants.XDIDictionaryConstants;
import xdi2.core.features.linkcontracts.instance.GenericLinkContract;
import xdi2.core.features.linkcontracts.instance.PublicLinkContract;
import xdi2.core.features.linkcontracts.instantiation.LinkContractInstantiation;
import xdi2.core.features.linkcontracts.template.LinkContractTemplate;
import xdi2.core.features.nodetypes.XdiEntitySingleton;
import xdi2.core.syntax.XDIAddress;
import xdi2.core.syntax.XDIArc;
import xdi2.core.syntax.XDIStatement;
import xdi2.messaging.operations.DoOperation;
import xdi2.messaging.target.MessagingTarget;
import xdi2.messaging.target.Prototype;
import xdi2.messaging.target.contributor.ContributorMount;
import xdi2.messaging.target.contributor.ContributorResult;
import xdi2.messaging.target.contributor.impl.AbstractContributor;
import xdi2.messaging.target.exceptions.Xdi2MessagingException;
import xdi2.messaging.target.execution.ExecutionContext;
import xdi2.messaging.target.impl.graph.GraphMessagingTarget;

/**
 * This contributor can process connection requests.
 * Warning: This is experimental, do not use for serious applications.
 */
// TODO: fix variable syntax
@ContributorMount(
		contributorXDIAddresses={"{}{$do}"},
		operationXDIAddresses={"$do{}"}
		)
public class ConnectionRequestContributor extends AbstractContributor implements Prototype<ConnectionRequestContributor> {

	private Graph targetGraph;
	private XDIAgent xdiAgent;

	public ConnectionRequestContributor(Graph targetGraph, XDIAgent xdiAgent) {

		this.targetGraph = targetGraph;
		this.xdiAgent = xdiAgent;
	}

	public ConnectionRequestContributor() {

		this(null, new XDIBasicAgent());
	}

	/*
	 * Prototype
	 */

	@Override
	public ConnectionRequestContributor instanceFor(xdi2.messaging.target.Prototype.PrototypingContext prototypingContext) throws Xdi2MessagingException {

		// create new contributor

		ConnectionRequestContributor contributor = new ConnectionRequestContributor();

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

		XDIAddress requestingAuthority = operation.getSenderXDIAddress();
		if (requestingAuthority == null) throw new Xdi2MessagingException("No requesting authority for link contract instantiation.", null, executionContext);

		// determine authorizing authority

		XDIAddress authorizingAuthority = operation.getMessage().getToXDIAddress();
		if (authorizingAuthority == null) throw new Xdi2MessagingException("No authorizing authority for link contract instantiation.", null, executionContext);

		// use agent to obtain link contract template

		ContextNode linkContractTemplateContextNode;

		try {

			linkContractTemplateContextNode = this.getXdiAgent().get(
					linkContractTemplateXDIaddress,
					new SetLinkContractMessageManipulator(PublicLinkContract.class));
		} catch (Exception ex) {

			throw new Xdi2MessagingException("Unable to obtain link contract template at address " + operation.getTargetXDIAddress() + ": " + ex.getMessage(), ex, executionContext);
		}

		if (linkContractTemplateContextNode == null) throw new Xdi2MessagingException("Cannot find link contract template at address " + operation.getTargetXDIAddress(), null, executionContext);

		XdiEntitySingleton.Variable linkContractTemplateXdiVariable = XdiEntitySingleton.Variable.fromContextNode(linkContractTemplateContextNode);
		if (linkContractTemplateXdiVariable == null) throw new Xdi2MessagingException("Invalid link contract template variable at address " + operation.getTargetXDIAddress(), null, executionContext);

		LinkContractTemplate linkContractTemplate = LinkContractTemplate.fromXdiEntitySingletonVariable(linkContractTemplateXdiVariable);
		if (linkContractTemplate == null) throw new Xdi2MessagingException("Invalid link contract template at address " + operation.getTargetXDIAddress(), null, executionContext);

		// read variable values from message

		Map<XDIArc, XDIAddress> variableValues = operation.getVariableValues();

		// instantiate link contract

		LinkContractInstantiation linkContractInstantiation = new LinkContractInstantiation(linkContractTemplate);

		linkContractInstantiation.setRequestingAuthority(requestingAuthority);
		linkContractInstantiation.setAuthorizingAuthority(authorizingAuthority);

		GenericLinkContract genericLinkContract = linkContractInstantiation.execute(
				this.getTargetGraph(), 
				variableValues, 
				true);

		// return link contract instance in result

		operationResultGraph.setStatement(XDIStatement.fromComponents(
				linkContractTemplate.getContextNode().getXDIAddress(),
				XDIDictionaryConstants.XDI_ADD_TYPE, 
				genericLinkContract.getContextNode().getXDIAddress())); 

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
